package com.longi.msp.osca.controller;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.longi.msp.osca.constants.RouterConstant;
import com.longi.msp.osca.model.entity.ServerConfigEntity;
import com.longi.msp.osca.model.request.InsertConfigRequest;
import com.longi.msp.osca.model.request.UpdateConfigRequest;
import com.longi.msp.osca.service.ConfigService;
import com.meicloud.paas.common.ReturnT;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author chenlei140
 * @className OssConfigController
 * @description 对象存储配置控制层
 * @date 2022/6/23 17:30
 */
@RestController
@RequestMapping("v1")
@RequiredArgsConstructor
public class ServerConfigController {
    private static final Logger logger = LoggerFactory.getLogger(ServerConfigController.class);

    private final ConfigService configService;


    @GetMapping(RouterConstant.CONFIG_LIST_PATH)
    public ReturnT<PageInfo<ServerConfigEntity>> page(@RequestParam(defaultValue = "1") @Parameter(name = "页码") int pageNum,
                                                      @RequestParam(defaultValue = "10") @Parameter(name = "页码") int pageSize,
                                                      @RequestParam(required = false) @Parameter(name = "服务商类型") String type) {
        logger.info(" 分页列表查询参数 : {} {} {}", type, pageNum, pageSize);
        PageMethod.startPage(pageNum, pageSize);
        return ReturnT.succeed(new PageInfo<>(configService.list(type)));
    }

    @PostMapping(RouterConstant.CONFIG_INSERT_PATH)
    public ReturnT<Integer> insert(@RequestBody @Validated InsertConfigRequest request) {
        logger.info(" insert params :  {}", request);
        return ReturnT.succeed(configService.insert(request));
    }

    @PutMapping(RouterConstant.CONFIG_UPDATE_PATH)
    public ReturnT<Integer> update(@RequestBody @Validated UpdateConfigRequest request) {
        logger.info(" update params : {}", request);
        return ReturnT.succeed(configService.update(request));
    }

    @DeleteMapping(RouterConstant.CONFIG_DELETE_PATH)
    public ReturnT<Integer> delete(@PathVariable String id) {
        return ReturnT.succeed(configService.delete(id));
    }

    @GetMapping(RouterConstant.CONFIG_DETAIL_PATH)
    public ReturnT<ServerConfigEntity> detail(@PathVariable String id) {
        return ReturnT.succeed(configService.getOne(id));
    }

    @GetMapping(RouterConstant.CONFIG_USERS_PATH)
    public ReturnT<List<ServerConfigEntity>> users(@PathVariable String type) {
        return ReturnT.succeed(configService.getAll(type));
    }

    @PostMapping(RouterConstant.CONFIG_AUTH_PATH)
    public ReturnT<ServerConfigEntity> grant(@PathVariable String id, @RequestBody String policy) {
        return ReturnT.succeed(configService.grant(id, policy));
    }

}
