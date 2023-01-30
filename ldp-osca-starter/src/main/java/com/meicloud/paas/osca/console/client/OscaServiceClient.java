package com.meicloud.paas.osca.console.client;

import com.meicloud.paas.common.ReturnT;
import com.meicloud.paas.osca.console.model.request.UploadFileRequest;
import com.meicloud.paas.osca.console.model.response.BucketInfoDTO;
import com.meicloud.paas.osca.console.model.response.ObjectDTO;
import com.meicloud.paas.osca.console.model.response.PutObjectDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

/**
 * object server feign client
 *
 * @author chenlei140
 * @date 2022/10/25 11:24
 **/
@FeignClient(
        value = "${feign.client.config.default.schema:http}" + "://ldp-osca")
public interface OscaServiceClient {

    /**
     * 获取bucket
     *
     * @param bid
     * @return
     */
    @GetMapping(value = "v1/bucket/detail")
    ReturnT<BucketInfoDTO> getBucket(@RequestParam("bid") String bid);

    /**
     * 上传
     *
     * @param request
     * @return
     */
    @PostMapping(value = "v1/object/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ReturnT<PutObjectDTO> putObject(UploadFileRequest request);


    /**
     * 下载
     *
     * @param id object id
     * @return
     */
    @GetMapping(value = "v1/object/down")
    void getObject(HttpServletResponse response, @RequestParam("id") String id, @RequestParam(value = "customPath", required = false) String customPath);


    /**
     * 详情
     *
     * @param id
     * @return
     */
    @GetMapping(value = "v1/object/detail/{id}")
    ReturnT<ObjectDTO> statObject(@PathVariable("id") String id);


}
