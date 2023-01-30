package com.longi.msp.osca.controller;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.longi.msp.osca.constants.RouterConstant;
import com.longi.msp.osca.model.entity.ObjectRecordEntity;
import com.longi.msp.osca.model.request.InsertRecordRequest;
import com.longi.msp.osca.service.RecordService;
import com.meicloud.paas.common.ReturnT;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 对象记录控制层
 *
 * @author chenlei140
 * @date 2022/10/26 15:18
 **/
@Tag(name = "对象记录接口")
@Slf4j
@RestController
@RequestMapping("v1")
@RequiredArgsConstructor
public class ObjectRecordController {

    private final RecordService recordService;

    @Operation(summary = "记录分页列表")
    @GetMapping(RouterConstant.RECORD_LIST_PATH)
    public ReturnT<PageInfo<ObjectRecordEntity>> page(@RequestParam(defaultValue = "1") @Parameter(name = "页码") int pageNum,
                                                      @RequestParam(defaultValue = "10") @Parameter(name = "页码") int pageSize,
                                                      @RequestParam(required = false) @Parameter(name = "对象id") String oid) {
        log.info(" 记录分页列表查询参数 : {} {} {}", oid, pageNum, pageSize);
        PageMethod.startPage(pageNum, pageSize);
        return ReturnT.succeed(new PageInfo<>(recordService.list(oid)));
    }

    @Operation(summary = "上报记录")
    @PostMapping(RouterConstant.RECORD_INSERT_PATH)
    public ReturnT<String> insert(@RequestBody @Validated InsertRecordRequest request) {
        log.info(" insert params :  {}", request);
        recordService.insert(request);
        return ReturnT.succeed();
    }
}
