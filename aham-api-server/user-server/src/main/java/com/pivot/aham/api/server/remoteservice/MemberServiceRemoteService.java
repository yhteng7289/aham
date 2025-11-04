package com.pivot.aham.api.server.remoteservice;


import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.server.dto.MemberDTO;
import com.pivot.aham.common.core.base.BaseRemoteService;
import com.pivot.aham.common.core.exception.BusinessException;
import com.pivot.aham.common.core.exception.ValidateException;

import java.util.List;
import java.util.Map;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年11月29日
 */
public interface MemberServiceRemoteService extends BaseRemoteService {

    /**
     * 修改
     * @param record
     * @return
     */
    MemberDTO updateOrInsert(MemberDTO record) throws BusinessException, ValidateException;

    /**
     * 逻辑删除批量
     * @param ids
     * @param userId
     */
    void del(List<Long> ids, Long userId) throws BusinessException, ValidateException;

    /**
     * 逻辑删除单条
     * @param id
     * @param userId
     */
    void del(Long id, Long userId) throws BusinessException, ValidateException;

    /**
     * 物理删除
     * @param id
     */
    void delete(Long id) throws BusinessException, ValidateException;

    /**
     * 物理删除
     * @param t
     * @return
     */
    Integer deleteByEntity(MemberDTO t) throws BusinessException, ValidateException;

    /**
     * 物理删除
     * @param columnMap
     * @return
     */
    Integer deleteByMap(Map<String, Object> columnMap) throws BusinessException, ValidateException;

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    MemberDTO queryById(Long id);


//    Pagination<MemberDTO> query(Map<String, Object> params);

    Page<MemberDTO> query(MemberDTO entity, Page<MemberDTO> rowBounds);

//    List<MemberDTO> queryList(Map<String, Object> params);

    List<MemberDTO> queryList(List<Long> ids);

    <K> List<K> queryList(List<Long> ids, Class<K> cls);

    List<MemberDTO> queryList(MemberDTO entity);

    MemberDTO selectOne(MemberDTO entity);

    Integer count(MemberDTO entity) throws BusinessException, ValidateException;

    boolean updateBatch(List<MemberDTO> entityList) throws BusinessException, ValidateException;

    boolean updateBatch(List<MemberDTO> entityList, int batchSize) throws BusinessException, ValidateException;
}
