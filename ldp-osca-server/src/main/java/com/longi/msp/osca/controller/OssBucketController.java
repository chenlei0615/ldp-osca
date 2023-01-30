package com.longi.msp.osca.controller;


import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.longi.msp.osca.constants.RouterConstant;
import com.longi.msp.osca.model.entity.BucketEntity;
import com.longi.msp.osca.model.request.BucketConfigRequest;
import com.longi.msp.osca.model.request.BucketEditRequest;
import com.longi.msp.osca.model.request.BucketEnterRequest;
import com.longi.msp.osca.model.response.BucketVO;
import com.longi.msp.osca.service.BucketService;
import com.meicloud.paas.common.ReturnT;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description: 桶操作接口
 * @author: zhangxz
 * @create: 2021/10/13
 */
@Tag(name = "桶操作接口")
@RestController
@RequestMapping("v1")
public class OssBucketController {

    private final BucketService bucketService;

    public OssBucketController(BucketService bucketService) {
        this.bucketService = bucketService;
    }

    @Operation(summary = "桶列表分页")
    @GetMapping(value = RouterConstant.BUCKET_LIST_PATH)
    public ReturnT<PageInfo<BucketVO>> bucket(@RequestParam(defaultValue = "1") @Parameter(name = "页码") int pageNum,
                                              @RequestParam(defaultValue = "10") @Parameter(name = "页码") int pageSize,
                                              @RequestParam(required = false) @Parameter(name = "服务商类型") String type,
                                              @RequestParam(required = false) @Parameter(name = "桶名称") String bucketName) {
        PageMethod.startPage(pageNum, pageSize);
        return ReturnT.succeed(new PageInfo<>(bucketService.listBucket(type,bucketName)));
    }

    @Operation(summary = "桶列表全量")
    @GetMapping(value = RouterConstant.BUCKET_LIST_ALL)
    public ReturnT<List<BucketEntity>> all(@RequestParam(required = false) @Parameter(name = "桶类型") String type) {
        return ReturnT.succeed(bucketService.findAllByType(type));
    }

    @PostMapping(value = RouterConstant.BUCKET_INSERT_PATH)
    public ReturnT<String> create(@RequestParam("cid") @Parameter(name = "存储商配置id") String cid,
                                  @RequestParam("bucket") @Parameter(name = "桶名") String bucket,
                                  @RequestParam(value = "description", required = false) @Parameter(name = "描述") String description,
                                  @RequestParam("acl") @Parameter(name = "PUBLIC_READ_WRITE:公有读写,PUBLIC_READ:公有读,PRIVATE:私有读写 PRIVATE_READ:私有读") String acl) {
        bucketService.create(cid, bucket, description, acl);
        return ReturnT.succeed();
    }

    @Operation(summary = "桶录入")
    @PostMapping(value = RouterConstant.BUCKET_ENTER_PATH)
    public ReturnT<BucketEntity> enter(BucketEnterRequest bucketEnterRequest) {
        return ReturnT.succeed(bucketService.enter(bucketEnterRequest));
    }

    @Operation(summary = "桶删除")
    @PostMapping(value = RouterConstant.BUCKET_DELETE_PATH)
    public ReturnT<Integer> delete(@PathVariable String id) {
        return ReturnT.succeed(bucketService.delete(id));
    }

    @Operation(summary = "桶配置")
    @PutMapping(value = RouterConstant.BUCKET_CONFIG_PATH)
    public ReturnT<String> update(@RequestBody @Validated BucketConfigRequest request) {
        bucketService.updateBucket(request);
        return ReturnT.succeed();
    }

    @Operation(summary = "桶编辑")
    @PostMapping(value = RouterConstant.BUCKET_EDIT_PATH)
    public ReturnT<BucketEntity> edit(BucketEditRequest request) {
        return ReturnT.succeed(bucketService.editBucket(request));
    }

    @Operation(summary = "桶详情")
    @GetMapping(value = RouterConstant.BUCKET_DETAIL_PATH)
    public ReturnT<BucketEntity> detail(@Parameter(name = "桶id") String bid) {
        return ReturnT.succeed(bucketService.getOne(bid));
    }

    @GetMapping(value = RouterConstant.BUCKET_SYNC_PATH)
    public ReturnT<String> sync() {
        bucketService.sync();
        return ReturnT.succeed();
    }
}
