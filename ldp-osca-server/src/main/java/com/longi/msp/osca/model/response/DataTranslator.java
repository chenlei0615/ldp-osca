package com.longi.msp.osca.model.response;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.text.StrPool;
import com.meicloud.paas.common.constants.ExtensionConstant;
import lombok.Data;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


/**
 * @author chenlei140
 * @className DataTranslator
 * @description 数据转化模型
 * @date 2022/1/5 15:50
 */
@Data
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DataTranslator {

    /**
     * 不带后缀的名字
     */
    private String fileName;
    /**
     * 源文件名字带后缀
     */
    private String sourceFileName;
    private String sourceFileSuffix;

    private String targetFilename;
    private HttpHeaders httpHeader;


    public DataTranslator(String url) {
        sourceFileName = FileNameUtil.getName(url);
        fileName = sourceFileName.substring(0, sourceFileName.lastIndexOf(StrPool.DOT));
        sourceFileSuffix = FileNameUtil.getSuffix(url);
        translate();
    }

    private void translate() {
        if (ExtensionConstant.include(sourceFileSuffix, ExtensionConstant.EXCEL_2_HTML)) {
            targetFilename = String.format("%s%s", fileName, ExtensionConstant.HTML_EXTENSION);
            httpHeader = HeaderGenerator.headers(targetFilename, MediaType.TEXT_HTML);
        } else if (ExtensionConstant.include(sourceFileSuffix, ExtensionConstant.PIC_EXTENSION)) {
            targetFilename = String.format("%s%s%s", fileName, StrPool.DOT, sourceFileSuffix);
            httpHeader = HeaderGenerator.headers(targetFilename, MediaType.IMAGE_PNG);
        } else if (sourceFileSuffix.equalsIgnoreCase(ExtensionConstant.GIF)) {
            targetFilename = String.format("%s%s", fileName, ExtensionConstant.GIF_EXTENSION);
            httpHeader = HeaderGenerator.headers(targetFilename, MediaType.IMAGE_GIF);
        } else {
            targetFilename = String.format("%s%s", fileName, ExtensionConstant.PDF_EXTENSION);
            httpHeader = HeaderGenerator.headers(targetFilename);
        }
    }

}
