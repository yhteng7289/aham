/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.api.web.in.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.server.dto.req.TotalFeeAccountReqDTO;
import com.pivot.aham.api.server.dto.res.TotalFeeAccountResDTO;
import com.pivot.aham.api.web.in.vo.TotalFeeAccountExportResVO;
import com.pivot.aham.api.web.in.vo.TotalFeeAccountReqVo;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.base.RpcMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.pivot.aham.api.server.remoteservice.PivotFeeDetailRemoteService;
import com.pivot.aham.common.core.support.file.excel.ExportExcel;
import com.pivot.aham.common.core.util.DateUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author HP
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/in")
@Api(value = "Pivot Account - Total Fee Account")
public class TotalFeeAccountController {

    @Resource
    private PivotFeeDetailRemoteService pivotFeeDetailRemoteService;

    @ApiOperation(value = "Retrieve Total Amount (Header)")
    @PostMapping("/totalFeeAccount/money/{feeType}")
    @RequiresPermissions("in:pivotAccountAsset:*")
    public Message<BigDecimal> totalMoney(@PathVariable String feeType) {
        RpcMessage<BigDecimal> totalMoneyRpcMessage = pivotFeeDetailRemoteService.getTotalMoneyByFeeType(Integer.valueOf(feeType));
        if (totalMoneyRpcMessage.isSuccess()) {
            return Message.success(totalMoneyRpcMessage.getContent().setScale(6, BigDecimal.ROUND_HALF_UP));
        } else {
            return Message.success();
        }
    }

    @ApiOperation(value = "Retrieve Total Fee Account (Page)")
    @PostMapping("/totalFeeAccount/page/{feeType}")
    @RequiresPermissions("in:pivotAccountAsset:*")
    public Message<Page<TotalFeeAccountResDTO>> pageListing(@RequestBody TotalFeeAccountReqVo totalFeeAccountReqVo, @PathVariable String feeType) {
        TotalFeeAccountReqDTO totalFeeAccountReqDTO = new TotalFeeAccountReqDTO();
        totalFeeAccountReqDTO.setStartCreateTime(totalFeeAccountReqVo.getStartCreateTime());
        totalFeeAccountReqDTO.setEndCreateTime(totalFeeAccountReqVo.getEndCreateTime());
        totalFeeAccountReqDTO.setPageNo(totalFeeAccountReqVo.getPageNo());
        totalFeeAccountReqDTO.setPageSize(totalFeeAccountReqVo.getPageSize());
        RpcMessage<Page<TotalFeeAccountResDTO>> rpcMessage = pivotFeeDetailRemoteService.getTotalFeePageByFeeType(totalFeeAccountReqDTO, Integer.valueOf(feeType));
        if (rpcMessage.isSuccess()) {
            return Message.success(rpcMessage.getContent());
        } else {
            return Message.success();
        }
    }

    @ApiOperation(value = "Retrieve Total Fee Account (Page)")
    @GetMapping("/totalFeeAccount/export/{feeType}")
    @RequiresPermissions("in:pivotAccountAsset:*")
    public Message exportListing(@RequestParam(name = "startCreateTime", required = false) Date startCreateDate,
            @RequestParam(name = "endCreateDate", required = false) Date endCreateDate, @PathVariable String feeType,
            HttpServletResponse response) {
        TotalFeeAccountReqDTO totalFeeAccountReqDTO = new TotalFeeAccountReqDTO();
        totalFeeAccountReqDTO.setStartCreateTime(startCreateDate);
        totalFeeAccountReqDTO.setEndCreateTime(endCreateDate);
        totalFeeAccountReqDTO.setPageNo(null);
        totalFeeAccountReqDTO.setPageSize(null);
        RpcMessage<Page<TotalFeeAccountResDTO>> rpcMessage = pivotFeeDetailRemoteService.getTotalFeePageByFeeType(totalFeeAccountReqDTO, Integer.valueOf(feeType));
        if (rpcMessage.isSuccess()) {
            List<TotalFeeAccountResDTO> totalFeeAccountResDTOList = rpcMessage.getContent().getRecords();
            if (totalFeeAccountResDTOList.isEmpty()) {
                Message.error("无内容");
            }

            List<TotalFeeAccountExportResVO> totalFeeAccountExportResVOList = new ArrayList();
            totalFeeAccountResDTOList.stream().map((totalFeeAccountResDTO) -> {
                TotalFeeAccountExportResVO totalFeeAccountExportResVO = new TotalFeeAccountExportResVO();
                totalFeeAccountExportResVO.setAccountId(totalFeeAccountResDTO.getAccountId());
                totalFeeAccountExportResVO.setClientId(totalFeeAccountResDTO.getClientId());
                totalFeeAccountExportResVO.setFeeType(totalFeeAccountResDTO.getFeeType().getDesc());
                totalFeeAccountExportResVO.setGoalId(totalFeeAccountResDTO.getGoalId());
                totalFeeAccountExportResVO.setMoney(totalFeeAccountResDTO.getMoney());
                totalFeeAccountExportResVO.setOperateDate(totalFeeAccountResDTO.getOperateDate());
                totalFeeAccountExportResVO.setOperateType(totalFeeAccountResDTO.getOperateType().getDesc());
                return totalFeeAccountExportResVO;
            }).forEachOrdered((totalFeeAccountExportResVO) -> {
                totalFeeAccountExportResVOList.add(totalFeeAccountExportResVO);
            });

            ExportExcel exportExcel = new ExportExcel(null, TotalFeeAccountExportResVO.class);
            exportExcel.setDataList(totalFeeAccountExportResVOList);

            String dateStr = DateUtils.formatDate(new Date(), "yyyyMMdd");
            String fileName = "ManagementFee_" + dateStr + ".xlsx";
            try {
                exportExcel.write(response, fileName);
                exportExcel.dispose();
            } catch (IOException e) {
                Message.error("下载文件失败" + e.getMessage());
            }
        } else {
            Message.error("无内容");
        }
        return null;
    }
}
