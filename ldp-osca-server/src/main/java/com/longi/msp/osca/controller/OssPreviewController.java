package com.longi.msp.osca.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.longi.msp.osca.constants.RouterConstant;
import com.longi.msp.osca.model.entity.ObjectEntity;
import com.longi.msp.osca.model.response.DataTranslator;
import com.longi.msp.osca.service.ObjectService;
import com.longi.msp.osca.service.PreviewService;
import com.meicloud.paas.common.constants.ErrorCode;
import com.meicloud.paas.common.error.ErrorCodeException;
import com.meicloud.paas.common.utils.AssertUtils;
import com.meicloud.paas.common.utils.InputStreamUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;

/**
 * @author chenlei140
 * @className OssPreviewController
 * @description 预览控制器
 * @date 2022/6/28 10:02
 */
@Tag(name = "OSS预览控制器")
@RestController
@RequestMapping("v1")
@RequiredArgsConstructor
public class OssPreviewController {
    private static final Logger logger = LoggerFactory.getLogger(OssPreviewController.class);

    private final PreviewService previewService;
    private final ObjectService objectService;

    @Value("${lmsp.osca.max-preview-file-size}")
    private Integer limitSize;

    @Operation(summary = "通过文件网络地址预览")
    @GetMapping(RouterConstant.PREVIEW_PATH)
    public ResponseEntity<byte[]> previewByUrl(@RequestParam("url") String url) {
        logger.info(" \n 预览文件地址 ：【{}】", url);
        AssertUtils.isTrue(StringUtils.isNotBlank(url), ErrorCode.FILE_URL_NOT_EXIST);
        InputStream inputStream = InputStreamUtil.getInputStreamByUrl(url);
        AssertUtils.notNull(inputStream, ErrorCode.PARSE_FILE_URL_FAILED);
        StopWatch clock = new StopWatch();
        clock.start("文件链接：数据转化任务开始");
        DataTranslator translator = new DataTranslator(url);
        byte[] convertedFileBytes = previewService.convert(inputStream, translator.getSourceFileName());
        clock.stop();
        logger.info(" \n 通过url地址预览：任务耗时 【{}】秒", clock.getTotalTimeSeconds());
        return ResponseEntity.ok().headers(translator.getHttpHeader()).body(convertedFileBytes);
    }

    @Operation(summary = "通过文件对象存储地址预览")
    @GetMapping(RouterConstant.PREVIEW_OSS_PATH)
    public ResponseEntity<byte[]> preview(@PathVariable("id") String id,
                                          @RequestParam(value = "watermarkPicUrl", required = false) @Parameter(name = "水印图片网络地址") String watermarkPicUrl,
                                          @RequestParam(value = "watermarkContent", required = false) @Parameter(name = "水印文字") String watermarkContent) {
        logger.info(" \n 预览参数 ：{}  {} {}", id, watermarkPicUrl, watermarkContent);
        ObjectEntity objectEntity = objectService.getOne(id);
        if (objectEntity.getObjectSize() > limitSize * 1024 * 1024) {
            throw new ErrorCodeException(ErrorCode.PREVIEW_EXCEPTION, "文件预览大小不超过 " + limitSize + "M");
        }
        InputStream inputStream = null;
        try {
            StopWatch clock = new StopWatch();
            clock.start("预览任务开始");
            String objectName = objectEntity.getObjectKey();
            inputStream = objectService.getSourceInputStream(objectEntity.getBid(), objectName);
            DataTranslator translator = new DataTranslator(objectName);
            byte[] convertedFileBytes;
            InputStream picIo = CharSequenceUtil.isNotBlank(watermarkPicUrl) ? InputStreamUtil.getInputStreamByUrl(watermarkPicUrl) : null;
            convertedFileBytes = previewService.preview(objectName, inputStream, picIo, watermarkContent, translator);
            clock.stop();
            logger.info(" \n 预览：任务总耗时 【{}】秒  文件大小：{} ", clock.getTotalTimeSeconds(), convertedFileBytes.length);
            return ResponseEntity.ok().headers(translator.getHttpHeader()).body(convertedFileBytes);
        } catch (Exception e) {
            throw new ErrorCodeException(ErrorCode.PREVIEW_EXCEPTION, e.getMessage());
        } finally {
            IoUtil.close(inputStream);
        }
    }

}
