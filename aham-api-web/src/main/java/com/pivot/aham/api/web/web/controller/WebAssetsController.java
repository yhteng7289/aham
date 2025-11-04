package com.pivot.aham.api.web.web.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.fastjson.JSON;
import com.pivot.aham.api.server.dto.UserAssetDTO;
import com.pivot.aham.api.server.dto.UserAssetWapperDTO;
import com.pivot.aham.api.server.dto.res.SysConfigResDTO;
import com.pivot.aham.api.server.remoteservice.AssetServiceRemoteService;
import com.pivot.aham.api.server.remoteservice.SysConfigRemoteService;
import com.pivot.aham.api.web.web.vo.req.UserAssetsReqVo;
import com.pivot.aham.api.web.web.vo.res.UserAssetsResVo;
import com.pivot.aham.api.web.core.ExceptionUtil;
import com.pivot.aham.common.core.base.AbstractController;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.base.RpcMessageStandardCode;
import com.pivot.aham.common.core.exception.BusinessException;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 *
 * @author luyang.li
 * @date 18/12/9
 * <p>
 * 提供给FE的用户资产接口
 */
@RestController
@RequestMapping("/app/")
@Api(value = "用户资产", description = "提供给FE的用户资产接口")
public class WebAssetsController extends AbstractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebAssetsController.class);
    @Resource
    private AssetServiceRemoteService assetServiceRemoteService;

    @Resource
    private SysConfigRemoteService sysConfigRemoteService;

    @PostMapping("user/assets.api")
    @ApiOperation(value = "用户资产", produces = MediaType.APPLICATION_JSON_VALUE, notes
            = "注册接口需要以下1个参数：\n" + "1.用户clientId\n")
    @SentinelResource(value = "qpsFlow", blockHandler = "handleException", blockHandlerClass = {ExceptionUtil.class})
    public Message<UserAssetsResVo> userBaseInfo(@RequestBody UserAssetsReqVo userAssetsReqVo) throws Exception {

        RpcMessage<SysConfigResDTO> rpcMessageSysConfigResDTO = sysConfigRemoteService.getStatus("assets.api");
        LOGGER.info("rpcMessageSysConfigResDTO {} ", rpcMessageSysConfigResDTO);
        if (rpcMessageSysConfigResDTO.isSuccess()) {
            Boolean status = rpcMessageSysConfigResDTO.getContent().getStatus();
            if (!status) {
                throw new BusinessException("Service unavailable");
            }
        }

        LOGGER.info("用户资产,请求参数userAssetsReqVo:{}", JSON.toJSON(userAssetsReqVo));
        UserAssetDTO userAssetDTO = userAssetsReqVo.convertToDto(userAssetsReqVo);
        UserAssetsResVo userAssetsResVo = new UserAssetsResVo();
        RpcMessage<UserAssetWapperDTO> assetWapperDTORpcMessage = assetServiceRemoteService.queryUserAssets(userAssetDTO);
        if (RpcMessageStandardCode.OK.value() == assetWapperDTORpcMessage.getResultCode()) {
            UserAssetWapperDTO userAssetWapperDTO = assetWapperDTORpcMessage.getContent();
            userAssetsResVo = BeanMapperUtils.map(userAssetWapperDTO, UserAssetsResVo.class);
        }
        LOGGER.info("用户资产,完成,UserAssetsResVo:{}", JSON.toJSON(userAssetsResVo));
        return Message.success(userAssetsResVo);
    }
}
