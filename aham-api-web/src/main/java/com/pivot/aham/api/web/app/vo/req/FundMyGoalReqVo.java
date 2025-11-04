package com.pivot.aham.api.web.app.vo.req;

import com.alibaba.fastjson.JSON;
import com.pivot.aham.api.web.app.dto.reqdto.FundMyGoalDTO;
import com.pivot.aham.api.web.app.dto.reqdto.GoalReqDTO;
import com.pivot.aham.common.core.util.DateUtils;
import lombok.Data;
import lombok.experimental.Accessors;
import org.assertj.core.util.Lists;

import java.util.List;


/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class FundMyGoalReqVo {

    private List<MyGoalReqVo> myGoalReqVos;

    private List<String> myGoalStrReqVos;

    public FundMyGoalDTO convertToDto(FundMyGoalReqVo fundMyGoalReqVo) {
        FundMyGoalDTO fundMyGoalDTO = new FundMyGoalDTO();
        fundMyGoalDTO.setGoalReqDTOS(buildDto(fundMyGoalReqVo.getMyGoalReqVos()));
        return fundMyGoalDTO;
    }

    private List<GoalReqDTO> buildDto(List<MyGoalReqVo> myGoalReqVos) {
        List<GoalReqDTO> list = Lists.newArrayList();
        for (MyGoalReqVo myGoalReqVo : myGoalReqVos) {
            GoalReqDTO goalReqDTO = new GoalReqDTO();
            goalReqDTO.setApplyMoney(myGoalReqVo.getApplyMoney())
                    .setClientId(myGoalReqVo.getClientId())
                    .setDate(DateUtils.formatDate(DateUtils.now(), "dd/MM/yyyy"))
                    .setGoalId(myGoalReqVo.getGoalId())
                    .setPortfolioId(myGoalReqVo.getPortfolioId())
                    .setGoalNo(myGoalReqVo.getGoalNo())
                    .setGoalName(myGoalReqVo.getGoalName())
                    .setTime(DateUtils.formatDate(DateUtils.now(), "HH:mm"));
            list.add(goalReqDTO);
        }
        return list;
    }


    public static void main(String[] args) {

        List<MyGoalReqVo> list = Lists.newArrayList();
        MyGoalReqVo myGoalReqVo1 = new MyGoalReqVo();
        myGoalReqVo1.setClientId("string").setApplyMoney("string").setDate("string")
                .setGoalId("string").setGoalName("string").setTime("string");
        list.add(myGoalReqVo1);
        MyGoalReqVo myGoalReqVo2 = new MyGoalReqVo();
        myGoalReqVo2.setClientId("string").setApplyMoney("string").setDate("string")
                .setGoalId("string").setGoalName("string").setTime("string");
        list.add(myGoalReqVo2);
        System.out.println(JSON.toJSON(list));

    }
}
