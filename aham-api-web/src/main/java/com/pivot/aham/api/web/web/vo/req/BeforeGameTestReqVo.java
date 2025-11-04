package com.pivot.aham.api.web.web.vo.req;

import com.pivot.aham.api.server.dto.BeforeGameTestDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class BeforeGameTestReqVo {
    @NotBlank(message = "年龄不能为空")
    private String age;
    private String skill;
    private String basis;
    private String horizon;
    private String toleRance;
    private String stability;
    private List<Integer> questionsAndAnswers;

    public BeforeGameTestDTO convertToDto(BeforeGameTestReqVo beforeGameTestReqVo) {
        BeforeGameTestDTO beforeGameTestDTO = new BeforeGameTestDTO();
        beforeGameTestDTO.setAge(beforeGameTestReqVo.getAge());
        beforeGameTestDTO.setSkill(beforeGameTestReqVo.getSkill());
        beforeGameTestDTO.setBasis(beforeGameTestReqVo.getBasis());
        beforeGameTestDTO.setHorizon(beforeGameTestReqVo.getHorizon());
        beforeGameTestDTO.setToleRance(beforeGameTestReqVo.getToleRance());
        beforeGameTestDTO.setStability(beforeGameTestReqVo.getStability());
        beforeGameTestDTO.setQuestionsAndAnswers(beforeGameTestReqVo.getQuestionsAndAnswers());
        return beforeGameTestDTO;
    }

}
