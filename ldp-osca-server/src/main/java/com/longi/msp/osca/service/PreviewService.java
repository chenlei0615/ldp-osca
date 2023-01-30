package com.longi.msp.osca.service;

import com.longi.msp.osca.model.response.DataTranslator;

import java.io.InputStream;

/**
 * @author chenlei140
 */
public interface PreviewService {

    /**
     * io convert by libreoffice for preview
     *
     * @param inputStream
     * @param sourceFileName
     * @return
     */
    byte[] convert(InputStream inputStream, String sourceFileName);

    /**
     * preview by oss objectKey
     *
     * @param objectName
     * @param inputStream
     * @param picInputStream   图片流
     * @param watermarkContent
     * @param translator
     * @return
     */
    byte[] preview(String objectName, InputStream inputStream, InputStream picInputStream, String watermarkContent, DataTranslator translator);
}
