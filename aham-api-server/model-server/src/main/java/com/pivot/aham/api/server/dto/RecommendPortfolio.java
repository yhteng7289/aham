package com.pivot.aham.api.server.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * Created by luyang.li on 19/1/22.
 */
@Data
@Accessors(chain = true)
public class RecommendPortfolio implements Serializable {

    private List<ProductWeight> productWeights;
    private String classfiyName;
}
