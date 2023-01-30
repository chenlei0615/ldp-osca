package com.longi.msp.osca.model.response;

import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.net.URLEncoder;

/**
 * @author chenlei140
 * @className ResponseGenerator
 * @description 响应流生成器
 * @date 2021/12/22 9:07
 */
public class HeaderGenerator {

    private HeaderGenerator() {
    }

    /**
     * @param fileName
     * @param mediaType
     * @return
     */
    @SneakyThrows
    public static HttpHeaders headers(String fileName, MediaType mediaType) {
        HttpHeaders headers = new HttpHeaders();
        String previewFileName = URLEncoder.encode(fileName, "UTF-8");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + previewFileName);
        headers.setContentType(mediaType);
        return headers;
    }

    /**
     * default pdf
     *
     * @param fileName
     * @return
     */
    public static HttpHeaders headers(String fileName) {
        return headers(fileName, MediaType.APPLICATION_PDF);
    }

}
