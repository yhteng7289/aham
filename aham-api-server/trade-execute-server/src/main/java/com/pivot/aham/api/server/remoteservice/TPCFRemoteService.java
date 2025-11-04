package com.pivot.aham.api.server.remoteservice;

import java.util.List;
import java.math.BigDecimal;

/**
 * Created by dexter on 12/5/2020
 */
public interface TPCFRemoteService {

    public BigDecimal getTPCF(String checkDate);

    public BigDecimal getTNCF(String checkDate);

    public List<BigDecimal> getRechargeAmount(String checkDate);

    public List<BigDecimal> getRedeemAmount(String checkDate);

    public List<String> getRechargeClient(String checkDate);

    public List<String> getRedeemClient(String checkDate);

    
}
