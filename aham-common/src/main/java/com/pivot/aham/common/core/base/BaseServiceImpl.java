package com.pivot.aham.common.core.base;

import com.baomidou.mybatisplus.enums.SqlMethod;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.SqlHelper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.toolkit.ReflectionKit;
import com.google.common.collect.Lists;
import com.pivot.aham.common.core.exception.BusinessException;
import com.pivot.aham.common.core.util.InstanceUtil;
import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * 服务基类
 *
 * @author addison
 * @param <T>
 * @param <M>
 * @since 2018年11月15日
 */
public class BaseServiceImpl<T extends BaseModel, M extends BaseMapper<T>> implements BaseService<T> {

    protected Logger logger = LogManager.getLogger();
    @Autowired
    protected M mapper;

    /**
     * 逻辑批量删除
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void del(List<Long> ids, Long userId) {
        ids.forEach(id -> del(id, userId));
    }

    /**
     * 逻辑删除
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void del(Long id, Long userId) {
        try {
            T record = this.getById(id);
            record.setUseEnable(0);
            record.setUpdateTime(new Date());
            mapper.updateById(record);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 物理删除
     *
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void delete(Long id) {
        try {
            mapper.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 物理删除
     *
     */
    @Override
    @Transactional
    public Integer deleteByEntity(T t) {
        Wrapper<T> wrapper = new EntityWrapper<>(t);
        return mapper.delete(wrapper);
    }

    /**
     * 物理删除
     *
     */
    @Override
    @Transactional
    public Integer deleteByMap(Map<String, Object> columnMap) {
        return mapper.deleteByMap(columnMap);
    }

    /**
     * 根据实体参数分页查询
     *
     * @param entity
     * @param rowBounds
     * @return
     */
    @Override
    public Page<T> queryPageList(T entity, Page<T> rowBounds) {
        EntityWrapper<T> entityWrapper = new EntityWrapper<>(entity);
        List<T> ts = mapper.selectPage(rowBounds, entityWrapper);
        rowBounds.setRecords(ts);
        return rowBounds;
    }

    @Override
    public Integer count(T entity) {
        Wrapper<T> wrapper = new EntityWrapper<>(entity);
        return mapper.selectCount(wrapper);
    }

    @Override
    public List<T> queryList(T params) {
        Wrapper<T> wrapper = new EntityWrapper<>(params);
        List<T> list = mapper.selectList(wrapper);
        return list;
    }

    /**
     * 根据Id查询(默认类型T)
     *
     * @param ids
     * @return
     */
    @Override
    public List<T> queryList(final List<Long> ids) {
        List<T> list = mapper.selectBatchIds(ids);
        return list;
    }

    /**
     * 根据Id查询(cls返回类型Class)
     *
     * @param <K>
     * @param ids
     * @param cls
     * @return
     */
    @Override
    public <K> List<K> queryList(final List<Long> ids, final Class<K> cls) {
        final List<K> list = Lists.newArrayList();
        List<T> lists = mapper.selectBatchIds(ids);
        for (T t : lists) {
            K k = InstanceUtil.to(t, cls);
            list.add(k);
        }
        return list;
    }

    @Override
    /**
     * 根据实体参数查询一条记录
     */
    public T selectOne(T entity) {
        T t = mapper.selectOne(entity);
        return t;
    }

    @Override
    public T queryById(Long id) {
        T record = null;
        record = mapper.selectById(id);
        return record;
    }

    /**
     * 修改/新增
     */
    @Override
    @Transactional
    public T updateOrInsert(T record) {
        try {
            record.setUpdateTime(new Date());
            if (record.getId() == null) {
                record.setCreateTime(new Date());
                mapper.insert(record);
            } else {
                mapper.updateById(record);
            }
            record = mapper.selectById(record.getId());
        } catch (DuplicateKeyException e) {
            logger.error("重复主键", e);
            throw new BusinessException("已经存在相同的记录.");
        } catch (Exception e) {
            logger.error("插入异常：", e);
            throw new RuntimeException(e);
        }
        return record;
    }

    /**
     * 批量修改/新增
     *
     * @param entityList
     * @return
     */
    @Override
    public boolean updateBatch(List<T> entityList) {
        return updateBatch(entityList, 50);
    }

    /**
     * 批量修改/新增
     *
     * @param entityList
     * @param batchSize
     * @return
     */
    @Override
    public boolean updateBatch(List<T> entityList, int batchSize) {
        if (CollectionUtils.isEmpty(entityList)) {
            throw new IllegalArgumentException("更新集合为空");
        }
        try (SqlSession batchSqlSession = sqlSessionBatch()) {
            IntStream.range(0, entityList.size()).forEach(i -> {
                updateOrInsert(entityList.get(i));
                if (i >= 1 && i % batchSize == 0) {
                    batchSqlSession.flushStatements();
                }
            });
            batchSqlSession.flushStatements();
        } catch (Throwable e) {
            throw new RuntimeException("执行批量更新异常", e);
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    protected Class<T> currentModelClass() {
        return ReflectionKit.getSuperClassGenricType(getClass(), 0);
    }

    /**
     * <p>
     * 批量操作 SqlSession
     * </p>
     *
     * @return
     */
    protected SqlSession sqlSessionBatch() {
        return SqlHelper.sqlSessionBatch(currentModelClass());
    }

    /**
     * 获取SqlStatement
     *
     * @param sqlMethod
     * @return
     */
    protected String sqlStatement(SqlMethod sqlMethod) {
        return SqlHelper.table(currentModelClass()).getSqlStatement(sqlMethod.getMethod());
    }

    /**
     * 根据Id查询(默认类型T)
     */
    private T getById(Long id) {
        T record = mapper.selectById(id);
        return record;
    }

    /**
     * 根据实体参数分页查询
     *
     * @param entity
     * @param rowBounds
     * @param feeType
     * @param operateType
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public Page<T> queryTotalAccountFeePage(T entity, Page<T> rowBounds, Integer feeType, Integer operateType, Date startTime, Date endTime) {
        EntityWrapper<T> entityWrapper = new EntityWrapper<>(entity);
        if (startTime != null) {
            entityWrapper.gt("create_time", startTime);
        }
        if (endTime != null) {
            entityWrapper.lt("create_time", endTime);
        }
        entityWrapper.eq("fee_type", feeType);
        entityWrapper.eq("operate_type", operateType);
        List<T> ts = mapper.selectPage(rowBounds, entityWrapper);
        rowBounds.setRecords(ts);
        return rowBounds;
    }

    @Override
    public Page<T> queryPageListByTimeRange(T entity, Page<T> rowBounds, String column, Date startTime, Date endTime) {
        EntityWrapper<T> entityWrapper = new EntityWrapper<>(entity);
        if (startTime != null) {
            //entityWrapper.gt(column, startTime);
            entityWrapper.ge(column, startTime); //Edited By WooiTatt
        }
        if (endTime != null) {
            //entityWrapper.lt(column, endTime);
            entityWrapper.le(column, endTime); //Edited By WooiTatt
        }
        List<T> ts = mapper.selectPage(rowBounds, entityWrapper);
        rowBounds.setRecords(ts);
        return rowBounds;
    }
    
    //Added by WooiTatt
    @Override 
    public Page<T> querySaxoReconciliation(T entity, Page<T> rowBounds, Date startTime, Date endTime) {
        EntityWrapper<T> entityWrapper = new EntityWrapper<>(entity);
        
        if (startTime != null) {
             entityWrapper.ge("compare_time", startTime);
        }
        if (endTime != null) {
            entityWrapper.le("compare_time", endTime);
        }

        List<T> ts = mapper.selectPage(rowBounds, entityWrapper);
        rowBounds.setRecords(ts);
        return rowBounds;
    }

}
