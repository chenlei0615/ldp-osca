package com.longi.msp.osca.model.dto;

import com.meicloud.paas.osca.console.model.response.ErrorObjectDTO;
import com.meicloud.paas.osca.console.model.response.PutObjectDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 上传结果
 *
 * @author chenlei140
 * @date 2022/12/14 21:35
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PutObjectVo {
    @Schema(description = "成功上传对象集合")
    private List<PutObjectDTO> objects;
    @Schema(description = "失败集合")
    private List<ErrorObjectDTO> errors;
}
