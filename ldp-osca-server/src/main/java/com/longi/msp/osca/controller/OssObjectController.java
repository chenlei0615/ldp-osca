package com.longi.msp.osca.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.longi.msp.osca.constants.RouterConstant;
import com.longi.msp.osca.model.dto.PutObjectVo;
import com.longi.msp.osca.model.dto.UploaderInfoDTO;
import com.longi.msp.osca.model.entity.ObjectEntity;
import com.longi.msp.osca.model.request.BatchUploadRequest;
import com.longi.msp.osca.model.request.CopyRequest;
import com.longi.msp.osca.model.request.ObjectListRequest;
import com.longi.msp.osca.model.request.UploadObjectRequest;
import com.longi.msp.osca.service.ObjectService;
import com.meicloud.paas.common.ReturnT;
import com.meicloud.paas.common.constants.ErrorCode;
import com.meicloud.paas.common.constants.StorageClassConstant;
import com.meicloud.paas.common.error.ErrorCodeException;
import com.meicloud.paas.common.utils.FilePathUtils;
import com.meicloud.paas.osca.console.model.response.PutObjectDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * @author chenlei140
 * @className OssObjectController
 * @description 对象操作控制层
 * @date 2022/6/23 17:31
 */
@Slf4j
@Tag(name = "对象操作接口")
@RestController
@RequestMapping("v1")
@RequiredArgsConstructor
public class OssObjectController {

    private final ObjectService objectService;

    @Operation(summary = "文件列表")
    @PostMapping(value = RouterConstant.OBJECT_LIST_PATH)
    public ReturnT<PageInfo<ObjectEntity>> list(@RequestBody @Validated ObjectListRequest request) {
        PageMethod.startPage(request.getPageNo(), request.getPageSize());
        return ReturnT.succeed(new PageInfo<>(objectService.listObjects(request)));
    }

    @Operation(summary = "单个上传")
    @PostMapping(value = RouterConstant.OBJECT_UPLOAD_PATH)
    public ReturnT<PutObjectDTO> upload(UploadObjectRequest uploadObjectRequest) {
        return ReturnT.succeed(objectService.putObject(uploadObjectRequest));
    }

    @Operation(summary = "批量上传")
    @PostMapping(value = RouterConstant.OBJECT_BATCH_UPLOAD_PATH)
    public ReturnT<PutObjectVo> uploads(BatchUploadRequest batchUploadRequest) {
        return ReturnT.succeed(objectService.putObjects(batchUploadRequest));
    }

    @Operation(summary = "下载")
    @GetMapping(value = RouterConstant.OBJECT_DOWN_PATH, produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public void down(HttpServletResponse response, @RequestParam("id") String id,
                     @RequestParam(value = "customPath", required = false) String customPath,
                     @RequestParam(value = "watermarkContent", required = false) String watermarkContent,
                     @RequestParam(value = "watermarkPicUrl", required = false) String watermarkPicUrl) {
        ServletOutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start("下载任务");
            ObjectEntity objectEntity = objectService.getOne(id);
            outputStream = response.getOutputStream();
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename*=utf-8'zh_cn'" + URLEncoder.encode((CharSequenceUtil.isEmpty(customPath) ? FilePathUtils.parseAndGetSrcName(objectEntity.getObjectName()) : customPath), "UTF-8"));
            inputStream = objectService.getObject(outputStream, objectEntity, watermarkContent, watermarkPicUrl);
            IoUtil.copy(inputStream, outputStream);
            stopWatch.stop();
            log.info("下载总耗时 :{} ", stopWatch.getTotalTimeSeconds());
        } catch (IOException e) {
            throw new ErrorCodeException(ErrorCode.GET_OBJECT_ERROR, "下载异常 ： " + e.getMessage());
        } finally {
            IoUtil.close(outputStream);
            IoUtil.close(inputStream);
        }
    }

    @Operation(summary = "删除")
    @PostMapping(value = RouterConstant.OBJECT_DELETE_PATH)
    public ReturnT<String> deleteObject(@PathVariable("id") String id) {
        objectService.delete(id);
        return ReturnT.succeed();
    }

    @Operation(summary = "拷贝")
    @PostMapping(value = RouterConstant.OBJECT_COPY_PATH)
    public ReturnT<String> copy(CopyRequest request) {
        objectService.copy(request);
        return ReturnT.succeed();
    }

    @Operation(summary = "获取文件详情")
    @GetMapping(value = RouterConstant.OBJECT_DETAIL_PATH)
    public ReturnT<ObjectEntity> statObject(@PathVariable("id") String id) {
        return ReturnT.succeed(objectService.getOne(id));
    }

    @Operation(summary = "重命名")
    @PostMapping(value = RouterConstant.OBJECT_RENAME_PATH)
    public ReturnT<String> rename(@PathVariable("id") String id, String targetObjectName) {
        objectService.rename(id, targetObjectName);
        return ReturnT.succeed();
    }

    @Operation(summary = "获取存储商存储类型")
    @GetMapping(value = RouterConstant.OBJECT_STORAGE_LIST_PATH)
    @Parameter(name = "supplier", description = "存储商类型")
    public ReturnT<List<Map<String, String>>> getStorages(@RequestParam String supplier) {
        List<Map<String, String>> list = StorageClassConstant.getStorageList(supplier);
        return ReturnT.succeed(list);
    }

    @Operation(summary = "获取上传人列表")
    @GetMapping(value = RouterConstant.OBJECT_UPLOADER_LIST_PATH)
    public ReturnT<List<UploaderInfoDTO>> uploader() {
        List<UploaderInfoDTO> list = objectService.getUploader();
        return ReturnT.succeed(list);
    }

}
