package com.pivot.aham.common.core.base;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.common.core.exception.BusinessException;
import com.pivot.aham.common.core.exception.ValidateException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 业务基类，泛型T为model
 *
 * @author addison
 * @param <T>
 * @since 2018年11月15日
 */
public interface BaseService<T extends BaseModel> {

    /**
     * 修改
     *
     * @param record
     * @return
     */
    @Transactional
    T updateOrInsert(T record) throws BusinessException, ValidateException;

    /**
     * 逻辑删除批量
     *
     * @param ids
     * @param userId
     */
    @Transactional
    void del(List<Long> ids, Long userId) throws BusinessException, ValidateException;

    /**
     * 逻辑删除单条
     *
     * @param id
     * @param userId
     */
    @Transactional
    void del(Long id, Long userId) throws BusinessException, ValidateException;

    /**
     * 物理删除
     *
     * @param id
     */
    @Transactional
    void delete(Long id) throws BusinessException, ValidateException;

    /**
     * 物理删除
     *
     * @param t
     * @return
     */
    @Transactional
    Integer deleteByEntity(T t) throws BusinessException, ValidateException;

    /**
     * 物理删除
     *
     * @param columnMap
     * @return
     */
    @Transactional
    Integer deleteByMap(Map<String, Object> columnMap) throws BusinessException, ValidateException;

    Page<T> queryPageList(T entity, Page<T> rowBounds);

    Page<T> queryTotalAccountFeePage(T entity, Page<T> rowBounds, Integer feeType, Integer operateType, Date startTime, Date endTime);

    Page<T> queryPageListByTimeRange(T entity, Page<T> rowBounds, String column, Date startTime, Date endTime);
    
    Page<T> querySaxoReconciliation(T entity, Page<T> rowBounds, Date startTime, Date endTime); // Added By WooiTatt

    T queryById(Long id);

    List<T> queryList(List<Long> ids);

    <K> List<K> queryList(List<Long> ids, Class<K> cls);

    List<T> queryList(T entity);

    T selectOne(T entity);

    Integer count(T entity) throws BusinessException, ValidateException;

    boolean updateBatch(List<T> entityList) throws BusinessException, ValidateException;

    boolean updateBatch(List<T> entityList, int batchSize) throws BusinessException, ValidateException;
}
