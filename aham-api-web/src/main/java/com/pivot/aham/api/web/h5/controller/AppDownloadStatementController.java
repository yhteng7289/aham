package com.pivot.aham.api.web.h5.controller;

import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSON;
import com.amazonaws.services.s3.model.S3Object;
import com.pivot.aham.common.core.base.AbstractController;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.util.AwsUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author YYYz
 */
@Controller
@CrossOrigin(value = "*")
@RequestMapping("/api/v1/")
@Api(value = "App月报接口", description = "App月报接口")
@Slf4j
public class AppDownloadStatementController extends AbstractController {

    @RequestMapping("h5/downLoadStatement")
    @ApiOperation(value = "App下载月报", produces = MediaType.APPLICATION_JSON_VALUE)
    public Message<Void> clientStatement(@RequestParam String url, HttpServletResponse response, HttpServletRequest request) throws Exception {
        log.info("App下载月报接口,请求参数userStatementReqVo:{}", JSON.toJSON(url));
        try {
            S3Object s3Object = AwsUtil.downloadFile(url);
            log.info("测试文件下载路径{}", url);
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/pdf");
            IoUtil.copy(s3Object.getObjectContent(), response.getOutputStream());
        } catch (Exception e) {
            log.error("用户{},下载月报文件失败", url, e);
            return Message.error("下载月报文件失败" + e.getMessage());
        }
        log.info("用户下载月报接口,完成,userStatementReqVo:{}", JSON.toJSON(url));
        return Message.success();
    }

}
