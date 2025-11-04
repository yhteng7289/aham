package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class BeforeGameTestDTO extends BaseDTO {

    private String age;
    private String skill;
    private String basis;
    private String horizon;
    private String toleRance;
    private String stability;
    private List<Integer> questionsAndAnswers;
}
