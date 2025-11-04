package com.pivot.aham.api.web.web.controller;

import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSON;
import com.amazonaws.services.s3.model.S3Object;
import com.pivot.aham.api.web.web.vo.req.UserStatementReqVo;
import com.pivot.aham.common.core.base.AbstractController;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.util.AwsUtil;
import com.pivot.aham.common.core.util.DateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;


@Controller
@RequestMapping("/app/")
@Api(value = "用户下载月报接口", description = "用户下载月报接口")
@Slf4j
public class UserStatementController extends AbstractController {

    @RequestMapping("user/clientStatement.api")
    @ApiOperation(value = "用户下载月报", produces = MediaType.APPLICATION_JSON_VALUE, notes =
            "下载月报接口需要以下参数：\n" + "1.用户clientId\n")
    public Message<Void> clinetStatement(@RequestBody @Valid UserStatementReqVo userStatementReqVo, HttpServletResponse response, HttpServletRequest request) throws Exception {
        log.info("用户下载月报接口,请求参数userStatementReqVo:{}", JSON.toJSON(userStatementReqVo));

        String stateMonth = DateUtils.formatDate(new Date(), "yyyy-MM");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int day = cal.get(Calendar.DAY_OF_MONTH);
        if (day <= 2) {
            Date lastMonth = DateUtils.addMonths(new Date(), -1);
            stateMonth = DateUtils.formatDate(lastMonth, "yyyy-MM");
        }

        String fileName = "statement/" + userStatementReqVo.getClientId() + "_custstatement_" + stateMonth + ".pdf";
        try {
            S3Object s3Object = AwsUtil.downloadFile(fileName);
            log.info("测试文件下载路径{}", fileName);
            response.setCharacterEncoding("utf-8");
            response.setContentType("multipart/form-data");
            response.setHeader("Content-Disposition", "attachment;fileName=" + setFileDownloadHeader(request, fileName));
            IoUtil.copy(s3Object.getObjectContent(), response.getOutputStream());
        } catch (Exception e) {
            log.error("用户{},下载月报文件失败", userStatementReqVo.getClientId(), e);
            return Message.error("下载月报文件失败" + e.getMessage());
        }
        log.info("用户下载月报接口,完成,userStatementReqVo:{}", JSON.toJSON(userStatementReqVo));
        return Message.success();
    }

    public String setFileDownloadHeader(HttpServletRequest request, String fileName) throws UnsupportedEncodingException {
        final String agent = request.getHeader("USER-AGENT");
        String filename = fileName;
        if(agent == null){
            filename = URLEncoder.encode(filename, "utf-8");
        }else {
            if (agent.contains("MSIE")) {
                // IE浏览器
                filename = URLEncoder.encode(filename, "utf-8");
                filename = filename.replace("+", " ");
            } else if (agent.contains("Firefox")) {
                // 火狐浏览器
                filename = new String(fileName.getBytes(), "ISO8859-1");
            } else if (agent.contains("Chrome")) {
                // google浏览器
                filename = URLEncoder.encode(filename, "utf-8");
            } else {
                // 其它浏览器
                filename = URLEncoder.encode(filename, "utf-8");
            }
        }
        return filename;
    }



}
