package com.longi.msp.osca.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ObjectUtil;
import com.google.common.base.Enums;
import com.longi.msp.osca.constants.DocumentFormatEnum;
import com.longi.msp.osca.model.response.DataTranslator;
import com.longi.msp.osca.properties.OfficeProperties;
import com.longi.msp.osca.service.PreviewService;
import com.meicloud.paas.common.constants.ErrorCode;
import com.meicloud.paas.common.constants.ExtensionConstant;
import com.meicloud.paas.common.error.ErrorCodeException;
import com.meicloud.paas.common.utils.HtmlParseUtil;
import com.meicloud.paas.core.watermark.WatermarkOperations;
import com.meicloud.paas.core.watermark.WatermarkType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jodconverter.core.document.DocumentFormat;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.local.JodConverter;
import org.jodconverter.local.office.LocalOfficeManager;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static com.meicloud.paas.common.constants.ExtensionConstant.*;

/**
 * 预览服务
 *
 * @author chenlei140
 * @date 2022/10/13 18:19
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class PreviewServiceImpl implements PreviewService {

    private static final String DEFAULT_PATH = FileUtil.getTmpDirPath().concat(StrPool.SLASH).concat("preview");

    private OfficeManager officeManager;

    private final OfficeProperties officeProperties;

    @PostConstruct
    public void init() {
        if (officeProperties.isEnabled()) {
            String[] portNumbers = officeProperties.getPortNumbers().split(StrPool.COMMA);
            int[] ports = new int[portNumbers.length];
            for (int i = 0; i < portNumbers.length; i++) {
                ports[i] = Integer.parseInt(portNumbers[i]);
            }
            LocalOfficeManager.Builder builder = LocalOfficeManager.builder().install();
            builder.officeHome(officeProperties.getOfficeHome());
            builder.portNumbers(ports);
            builder.taskExecutionTimeout(officeProperties.getTaskExecutionTimeout() * 1000 * 60);
            builder.taskQueueTimeout(officeProperties.getTaskQueueTimeout() * 1000 * 60 * 60);
            builder.workingDir(officeProperties.getWorkDir());
            builder.maxTasksPerProcess(officeProperties.getMaxTaskPerProcess());
            builder.startFailFast(true);
            officeManager = builder.build();
            officeManagerStart();
        }
    }

    private void officeManagerStart() {
        if (officeManager.isRunning()) {
            return;
        }
        try {
            log.info(" \n    *****   OFFICE MANAGER START RUNNING  ***** ");
            officeManager.start();
            log.info(" \n    *****   OFFICE MANAGER INIT FINISHED  ***** ");
        } catch (Exception e) {
            log.error("Libreoffice start exception :{} ", e.getMessage());
            throw new ErrorCodeException(ErrorCode.INTERNAL_ERROR);
        }
    }

    @Override
    public byte[] convert(InputStream inputStream, String sourceFileName) {
        return convert(inputStream, sourceFileName, DEFAULT_PATH);
    }

    @Override
    public byte[] preview(String objectName, InputStream inputStream, InputStream picInputStream, String watermarkContent, DataTranslator translator) {
        String suffix = FileNameUtil.getSuffix(objectName);
        if (ExtensionConstant.include(suffix, PIC_EXTENSION) || suffix.equals(GIF) || suffix.equals(PDF)) {
            log.info("current file type is {} ,need not to convert,direct response", suffix);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            if (CharSequenceUtil.isNotBlank(watermarkContent) || ObjectUtil.isNotNull(picInputStream)) {
                WatermarkOperations watermarkOperations = WatermarkType.of(FileNameUtil.getSuffix(objectName)).instance();
                watermarkOperations.addWatermark(FileNameUtil.getSuffix(objectName), inputStream, byteArrayOutputStream, picInputStream, watermarkContent);
            } else {
                IoUtil.copy(inputStream, byteArrayOutputStream);
            }
            return byteArrayOutputStream.toByteArray();
        } else {
            log.info("current file type is {} , need to convert to some other type", suffix);
            return this.convert(inputStream, translator.getSourceFileName());
        }
    }

    /**
     * @param inputStream
     * @param sourceFileName
     * @return
     */
    private byte[] convert(InputStream inputStream, String sourceFileName, String storePath) {
        String suffix = FilenameUtils.getExtension(sourceFileName);
        DocumentFormatEnum documentFormatEnum = Enums.getIfPresent(DocumentFormatEnum.class, suffix.toUpperCase()).orNull();
        DocumentFormat sourceFormat;
        DocumentFormat targetFormat;
        if (Objects.isNull(documentFormatEnum)) {
            throw new ErrorCodeException(ErrorCode.PREVIEW_EXCEPTION, "不支持类型为【{}】的文件预览", suffix);
        } else {
            sourceFormat = documentFormatEnum.getFormFormat();
            targetFormat = documentFormatEnum.getTargetFormat();
            inputStream = documentFormatEnum.getInputStream(inputStream);
        }
        if (ExtensionConstant.include(suffix, ExtensionConstant.EXCEL_2_HTML)) {
            String fileName = FilenameUtils.getBaseName(sourceFileName);
            File targetFile = new File(storePath, fileName + ExtensionConstant.HTML_EXTENSION);
            return convert(inputStream, sourceFormat, targetFormat, targetFile, storePath);
        }
        return convert(inputStream, sourceFormat, targetFormat);
    }

    /**
     * @param inputStream
     * @param sourceFormat
     * @param targetFormat
     * @return byte[]
     */
    private byte[] convert(InputStream inputStream, DocumentFormat sourceFormat, DocumentFormat targetFormat,
                           File targetFile, String storePath) {
        log.info("  \n   >>> 待转换的文档类型：【{}】   \n   >>> 转换的目标文档类型：【{}】 ", sourceFormat, targetFormat);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("转化任务");
        byte[] bytes;
        try {
            JodConverter.convert(inputStream).as(sourceFormat).to(targetFile).as(targetFormat).execute();
            String editHtml = HtmlParseUtil.editHtml(targetFile, storePath);
            if (CharSequenceUtil.isEmpty(editHtml)) {
                bytes = FileUtils.readFileToByteArray(targetFile);
            } else {
                bytes = editHtml.getBytes(StandardCharsets.UTF_8);
            }
            stopWatch.stop();
            log.info("  \n  转化任务完成 {}s ", stopWatch.getTotalTimeSeconds());
            return bytes;
        } catch (OfficeException e) {
            log.error("转化流异常 OfficeException: {} " + e);
            throw new ErrorCodeException(ErrorCode.PREVIEW_EXCEPTION, e.getMessage());
        } catch (IOException ex) {
            log.error("转化流异常 IOException: {} " + ex);
            throw new ErrorCodeException(ErrorCode.PREVIEW_EXCEPTION, " read File error: " + ex.getMessage());
        } finally {
            IoUtil.close(inputStream);
        }

    }

    /**
     * @param inputStream
     * @param sourceFormat
     * @param targetFormat
     * @return
     */
    private byte[] convert(InputStream inputStream, DocumentFormat sourceFormat, DocumentFormat targetFormat) {
        log.info("  \n   >>> 待转换的文档类型：【{}】   \n   >>> 转换的目标文档类型：【{}】 ", sourceFormat, targetFormat);
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            JodConverter.convert(inputStream).as(sourceFormat).to(byteArrayOutputStream).as(targetFormat).execute();
            log.info("转换流任务完成");
            return byteArrayOutputStream.toByteArray();
        } catch (OfficeException e) {
            log.error(" office转化流异常: \n {} " + e);
            throw new ErrorCodeException(ErrorCode.PREVIEW_EXCEPTION, " 转化流异常: " + e.getMessage());
        } finally {
            IoUtil.close(inputStream);
        }
    }

}
