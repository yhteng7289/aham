package com.pivot.aham.api.web.web.controller;

import com.alibaba.fastjson.JSON;
import com.pivot.aham.api.server.dto.BeforeGameTestDTO;
import com.pivot.aham.api.server.dto.VersionInfoDTO;
import com.pivot.aham.api.server.dto.VersionInfoResDTO;
import com.pivot.aham.api.server.remoteservice.UserTestRemoteService;
import com.pivot.aham.api.web.web.vo.req.BeforeGameTestReqVo;
import com.pivot.aham.api.web.web.vo.req.VersionInfoReqVo;
import com.pivot.aham.api.web.web.vo.res.UserTestResVo;
import com.pivot.aham.api.web.web.vo.res.VersionInfoResVo;
import com.pivot.aham.common.core.base.AbstractController;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.base.RpcMessageStandardCode;
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
import javax.validation.Valid;

/**
 *
 * @author senyang.zheng
 * @date 19/04/12
 * <p>D
 * 提供给用户测评接口
 */
@RestController
@RequestMapping("/app/")
@Api(value = "用户测评", description = "提供给用户测评接口")
public class UserTestController extends AbstractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserTestController.class);

    @Resource
    private UserTestRemoteService userTestRemoteService;

    @PostMapping("test/gameTest")
    @ApiOperation(value = "游戏前测评接口", produces = MediaType.APPLICATION_JSON_VALUE, notes = "游戏前测评接口")
    public Message beforeGameTest(@RequestBody @Valid BeforeGameTestReqVo beforeGameTestReqVo) throws Exception {

        LOGGER.info("游戏前测评接口,请求参数,data:{}", JSON.toJSON(beforeGameTestReqVo));
        BeforeGameTestDTO beforeGameTestDTO = beforeGameTestReqVo.convertToDto(beforeGameTestReqVo);
        RpcMessage<String> rpcMessage = userTestRemoteService.getPortfolioId(beforeGameTestDTO);
        if (RpcMessageStandardCode.OK.value() == rpcMessage.getResultCode()) {
            UserTestResVo userTestResVo = new UserTestResVo();
            userTestResVo.setPortfolioId(rpcMessage.getContent());
            return Message.success(userTestResVo);
        } else {
            return Message.error(rpcMessage.getErrMsg());
        }
    }


    @PostMapping("version/info")
    @ApiOperation(value = "游戏前测评接口", produces = MediaType.APPLICATION_JSON_VALUE, notes = "游戏前测评接口")
    public Message getVersionInfo(@RequestBody @Valid VersionInfoReqVo versionInfoReqVo) throws Exception {

        LOGGER.info("游戏前测评接口,请求参数,data:{}", JSON.toJSON(versionInfoReqVo));
        VersionInfoDTO versionInfoDTO = versionInfoReqVo.convertToDto(versionInfoReqVo);
        RpcMessage<VersionInfoResDTO> rpcMessage = userTestRemoteService.getVersionInfo(versionInfoDTO);
        if (RpcMessageStandardCode.OK.value() == rpcMessage.getResultCode()) {
            VersionInfoResVo versionInfoResVo = new VersionInfoResVo();
            versionInfoResVo.setDownloadUrl(rpcMessage.getContent().getDownloadUrl());
            versionInfoResVo.setForcedUpdate(rpcMessage.getContent().getForcedUpdate());
            versionInfoResVo.setNewVersion(rpcMessage.getContent().getNewVersion());
            versionInfoResVo.setUpdateMessage(rpcMessage.getContent().getUpdateMessage());
            return Message.success(versionInfoResVo);
        } else {
            return Message.error(rpcMessage.getErrMsg());
        }
    }
}
