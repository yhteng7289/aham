package com.pivot.aham.api.service.job.wrapperbean;

import lombok.Data;

import java.util.List;

/**
 * Created by luyang.li on 18/12/9.
 */
@Data
public class EtfListBean {
    private List<EtfBean> mainEtf;
    private List<EtfBean> subEtf;
}
