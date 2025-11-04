package com.pivot.aham.api.web.h5.controller;

import com.alibaba.fastjson.JSON;
import com.pivot.aham.api.server.dto.app.reqdto.AppUserStatementReqDTO;
import com.pivot.aham.api.server.dto.app.resdto.AppUserStatementResDTO;
import com.pivot.aham.api.server.dto.app.resdto.StatementResDTO;
import com.pivot.aham.api.server.remoteservice.AppRemoteService;
import com.pivot.aham.api.web.h5.vo.req.AppUserStatementReqVo;
import com.pivot.aham.api.web.h5.vo.res.AppUserStatementResVo;
import com.pivot.aham.api.web.h5.vo.res.StatementResVo;
import com.pivot.aham.common.core.base.AbstractController;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.base.RpcMessageStandardCode;
import com.pivot.aham.common.core.support.cache.RedissonHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * @author YYYz
 */
@RestController
@CrossOrigin(value = "*")
@RequestMapping("/api/v1/")
@Api(value = "App月报接口", description = "App月报接口")
@Slf4j
@Deprecated
public class AppStatementController extends AbstractController {

    @Resource
    private AppRemoteService appRemoteService;

    @Resource
    private RedissonHelper redissonHelper;

    @RequestMapping("h5/clientStatements")
    @ApiOperation(value = "用户月报列表", produces = MediaType.APPLICATION_JSON_VALUE)
    public Message<List<AppUserStatementResVo>> clientStatement(@RequestBody @Valid AppUserStatementReqVo userStatementReqVo, HttpServletRequest request) throws Exception {

        String token = request.getHeader("token");
        String clientId = redissonHelper.get(token);
        userStatementReqVo.setClientId(clientId);
        log.info("App月报接口,请求参数userStatementReqVo:{},token:{}", JSON.toJSON(userStatementReqVo));
        AppUserStatementReqDTO appUserStatementReqDTO = userStatementReqVo.convertToDto(userStatementReqVo);

        RpcMessage<List<AppUserStatementResDTO>> rpcMessage = appRemoteService.getUserStatementList(appUserStatementReqDTO);

        if (RpcMessageStandardCode.OK.value() == rpcMessage.getResultCode()) {
            List<AppUserStatementResVo> appUserStatementResVos = Lists.newArrayList();
            for (AppUserStatementResDTO appUserStatementResDTO : rpcMessage.getContent()) {
                AppUserStatementResVo appUserStatementResVo = new AppUserStatementResVo();
                appUserStatementResVo.setYear(appUserStatementResDTO.getYear())
                        .setStatements(buildStatements(appUserStatementResDTO.getStatements()));
                appUserStatementResVos.add(appUserStatementResVo);
            }
            return Message.success(appUserStatementResVos);
        } else {
            return Message.error(rpcMessage.getErrMsg());
        }

    }

    private List<StatementResVo> buildStatements(List<StatementResDTO> statements) throws Exception {
        List<StatementResVo> statementResVos = Lists.newArrayList();
        for (StatementResDTO statementResDTO : statements) {
            StatementResVo statementResVo = new StatementResVo();
            statementResVo.setMonth(getMonthToEn(statementResDTO.getMonth())).setStatementUrl(statementResDTO.getStatementUrl());
            statementResVos.add(statementResVo);
        }
        return statementResVos;
    }

    public String getMonthToEn(String month) throws Exception {
        String EnMonth = "";
        switch (month) {
            case "01":
                EnMonth = "January";
                return EnMonth;
            case "02":
                EnMonth = "February";
                return EnMonth;
            case "03":
                EnMonth = "March";
                return EnMonth;
            case "04":
                EnMonth = "April";
                return EnMonth;
            case "05":
                EnMonth = "May";
                return EnMonth;
            case "06":
                EnMonth = "June";
                return EnMonth;
            case "07":
                EnMonth = "July";
                return EnMonth;
            case "08":
                EnMonth = "August";
                return EnMonth;
            case "09":
                EnMonth = "September";
                return EnMonth;
            case "10":
                EnMonth = "October";
                return EnMonth;
            case "11":
                EnMonth = "November";
                return EnMonth;
            case "12":
                EnMonth = "December";
                return EnMonth;
        }
        return EnMonth;
    }

}
