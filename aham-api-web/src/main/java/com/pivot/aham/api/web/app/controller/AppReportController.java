/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.api.web.app.controller;

import com.google.common.collect.Lists;
import com.pivot.aham.api.web.app.dto.reqdto.PdfStatementReqDTO;
import com.pivot.aham.api.web.app.dto.reqdto.UserStatementReqDTO;
import com.pivot.aham.api.web.app.dto.resdto.UserStatementDetailsDTO;
import com.pivot.aham.api.web.app.dto.resdto.UserStatementListResDTO;
import com.pivot.aham.api.web.app.febase.AppResultCode;
import com.pivot.aham.api.web.app.service.AppService;
import com.pivot.aham.api.web.app.vo.res.UserStatementListResVo;
import com.pivot.aham.api.web.web.vo.req.UserStatementReqVo;
import com.pivot.aham.common.core.base.AbstractController;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.support.cache.RedissonHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/")
@Api(value = "用户下载月报接口", description = "用户下载月报接口")
@Slf4j
public class AppReportController extends AbstractController {

    @Resource
    private AppService appService;

    @Resource
    private RedissonHelper redissonHelper;

    @RequestMapping("app/statementlist")
    @ApiOperation(value = "用户下载月报资料", produces = MediaType.APPLICATION_JSON_VALUE, notes = "下载月报资料接口需要以下参数：\n" + "1.用户clientId\n")

    public Message<List<UserStatementListResVo>> getStatementList(@RequestBody @Valid UserStatementReqVo userStatementReqVo, HttpServletResponse response, HttpServletRequest request) throws Exception {
        if (!checkLogin(userStatementReqVo.getClientId())) {
            return Message.error(AppResultCode.UNAUTHORIZED.value(), AppResultCode.UNAUTHORIZED.msg());
        }
        UserStatementReqDTO userStatementReqDTO = new UserStatementReqDTO();
        userStatementReqDTO.setClientId(userStatementReqVo.getClientId());

        UserStatementListResDTO userStatementResDTO = appService.getStatementList(userStatementReqDTO);
        if (userStatementResDTO != null) {
            if (userStatementResDTO.getResultCode().equals(String.valueOf(AppResultCode.OK))) {                
                List<UserStatementListResVo> resVoList = Lists.newArrayList();
                List<UserStatementDetailsDTO> userStatementDetailsDTOList = userStatementResDTO.getStatmentList();
                userStatementDetailsDTOList.stream().map((userStatementDetailsDTO) -> {
                    UserStatementListResVo userStatementListResVo = new UserStatementListResVo();
                    userStatementListResVo.setFileId(userStatementDetailsDTO.getFileId());
                    userStatementListResVo.setFileName(userStatementDetailsDTO.getFileName());
                    userStatementListResVo.setClientId(userStatementDetailsDTO.getClientId());
                    userStatementListResVo.setFileDownloadedDate(userStatementDetailsDTO.getFileDownloadedDate());
                    return userStatementListResVo;
                }).forEachOrdered((userStatementListResVo) -> {
                    resVoList.add(userStatementListResVo);
                });
                return Message.success(resVoList);
            } else {
                return Message.error("Api failures on getStatementList");
            }
        } else {
            return Message.error("System failures on getStatementList");
        }
    }

    @GetMapping(value = "app/statement")
    @ApiOperation(value = "用户下载月报", notes = "下载月报接口需要以下参数：\n" + "1.用户clientId\n2.fileId\n")
    public void getPdfStatement(
            @RequestParam("clientId") String clientId, @RequestParam("fileId") String fileId, @RequestParam("date") String date,
            @RequestParam("token") String token, HttpServletResponse response, HttpServletRequest request) throws Exception {

        // Add the end, the file will be deleted after DL
        String fileName = "/data/" + clientId + "_custstatement_" + date + ".pdf";
        String responseStr = "";
        try {
            if (!checkLogin(clientId, token)) {
                log.info("fail checkLogin , clientId {} ", clientId);
                return;
            }
            PdfStatementReqDTO pdfStatementReqDTO = new PdfStatementReqDTO();
            pdfStatementReqDTO.setClientId(clientId);
            pdfStatementReqDTO.setFileId(fileId);
            responseStr = appService.getPdfStatement(pdfStatementReqDTO);
            if (!responseStr.isEmpty()) {
                try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.ISO_8859_1)) {
                    writer.write(responseStr);
                }
                File file = new File(fileName);
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));
                response.setContentLength((int) file.length());
                InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
                FileCopyUtils.copy(inputStream, response.getOutputStream());
            }
        } catch (IOException e) {
        } finally {
            if (!responseStr.isEmpty()) {
                try {
                    File file = new File(fileName);
                    file.delete();
                } catch (Exception ex) {

                }
            }
        }
    }
}
