package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.TransOrderPO;
import com.pivot.aham.common.core.base.BaseMapper;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年01月22日
 */
public interface TransOrderMapper extends BaseMapper<TransOrderPO> {

    List<TransOrderPO> queryTransOrder(RowBounds rowBounds,TransOrderPO transOrderPO);

    List<TransOrderPO> queryTransOrderList(TransOrderPO transOrderPO);


}
