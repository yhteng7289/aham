package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.AccountAssetMapper;
import com.pivot.aham.api.service.mapper.AhamReconMapper;
import com.pivot.aham.api.service.mapper.model.AccountAssetPO;
import com.pivot.aham.api.service.mapper.model.AccountRechargePO;
import com.pivot.aham.api.service.mapper.model.AhamReconPO;
import com.pivot.aham.api.service.service.AccountAssetService;
import com.pivot.aham.api.service.service.AccountRedeemService;
import com.pivot.aham.api.service.service.AhamReconService;
import com.pivot.aham.common.core.Constants;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.enums.ProductAssetStatusEnum;
import com.pivot.aham.common.enums.analysis.AssetSourceEnum;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author addison
 */
@Service
public class AhamReconServiceImpl extends BaseServiceImpl<AhamReconPO, AhamReconMapper> implements AhamReconService {

	@Override
	public void add(AhamReconPO ahamRecon) {
		mapper.save(ahamRecon);
	}

	@Override
	public List<AhamReconPO> findAhamRecon(AhamReconPO ahamRecon) {
		return mapper.findAhamRecon(ahamRecon);
	}

}
