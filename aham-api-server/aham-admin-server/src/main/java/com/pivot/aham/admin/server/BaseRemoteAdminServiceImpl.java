package com.pivot.aham.admin.server;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.core.base.BaseService;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

/**
 * 服务基类
 *
 * @author addison
 * @since 2018年11月15日
 */
public class BaseRemoteAdminServiceImpl<K extends BaseModel, T extends BaseDTO, S extends BaseService<K>> implements BaseAdminService<T> {

    private Class<K> clazzK;
    private Class<T> clazzT;
    protected Logger logger = LogManager.getLogger();

    @SuppressWarnings("unchecked")
    public BaseRemoteAdminServiceImpl() {
        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.clazzK = (Class<K>) type.getActualTypeArguments()[0];
        this.clazzT = (Class<T>) type.getActualTypeArguments()[1];
    }

    @Autowired
    protected S service;

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
        service.del(id, userId);
    }

    /**
     * 物理删除
     *
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void delete(Long id) {
        service.delete(id);
    }

    /**
     * 物理删除
     *
     */
    @Override
    @Transactional
    public Integer deleteByEntity(T t) {
        K k = BeanMapperUtils.map(t, this.clazzK);
        return service.deleteByEntity(k);
    }

    /**
     * 物理删除
     *
     */
    @Override
    @Transactional
    public Integer deleteByMap(Map<String, Object> columnMap) {
        return service.deleteByMap(columnMap);
    }

    /**
     * 根据实体参数分页查询
     *
     */
    @Override
    @SuppressWarnings("unchecked")
    public Page<T> queryPageList(T entity, Page<T> rowBounds) {
        K k = BeanMapperUtils.map(entity, this.clazzK);

        Page<K> ks = new Page<>();
        Page<K> kp = BeanMapperUtils.map(rowBounds, ks.getClass());
        Page<K> kPagination = service.queryPageList(k, kp);

        Page<T> ts = new Page<>();
//        Page<T> tp = BeanMapperUtils.map(kPagination,ts.getClass());

        List<T> lastList = BeanMapperUtils.mapList(kPagination.getRecords(), this.clazzT);
        ts.setRecords(lastList);
        ts.setTotal(kPagination.getTotal());
        ts.setCondition(kPagination.getCondition());
//        ts.setAsc(kPagination.getAscs());
        ts.setAscs(kPagination.getAscs());
        ts.setCurrent(kPagination.getCurrent());
        ts.setDescs(kPagination.getDescs());
//        ts.setOpenSort(kPagination);
//        ts.setOptimizeCountSql(kPagination.get);
        ts.setOrderByField(kPagination.getOrderByField());
//        ts.setSearchCount(kPagination.getser);
        ts.setSize(kPagination.getSize());
        return ts;
    }

    @Override
    public Integer count(T entity) {
        K k = BeanMapperUtils.map(entity, this.clazzK);
        return service.count(k);
    }

    @Override
    public List<T> queryList(T params) {
        K k = BeanMapperUtils.map(params, this.clazzK);
        List<K> list = service.queryList(k);
        return BeanMapperUtils.mapList(list, this.clazzT);
    }

    /**
     * 根据Id查询(默认类型T)
     */
    @Override
    public List<T> queryList(final List<Long> ids) {
        List<K> list = service.queryList(ids);
        return BeanMapperUtils.mapList(list, this.clazzT);
    }

    /**
     * 根据Id查询(cls返回类型Class)
     */
    @Override
    public <K> List<K> queryList(final List<Long> ids, final Class<K> cls) {
        return service.queryList(ids, cls);
    }

    @Override
    /**
     * 根据实体参数查询一条记录
     */
    public T selectOne(T entity) {
        K k = BeanMapperUtils.map(entity, this.clazzK);
        K kRes = service.selectOne(k);
        if (kRes == null) {
            return null;
        }
        return BeanMapperUtils.map(kRes, this.clazzT);
    }

    @Override
    public T queryById(Long id) {
        K k = service.queryById(id);
        return BeanMapperUtils.map(k, this.clazzT);
    }

    /**
     * 修改/新增
     */
    @Override
    @Transactional
    public T updateOrInsert(T record) {
        K k = BeanMapperUtils.map(record, this.clazzK);
        K kRes = service.updateOrInsert(k);
        return BeanMapperUtils.map(kRes, this.clazzT);
    }

    /**
     * 批量修改/新增
     */
    @Override
    public boolean updateBatch(List<T> entityList) {
        return updateBatch(entityList, 50);
    }

    /**
     * 批量修改/新增
     */
    @Override
    public boolean updateBatch(List<T> entityList, int batchSize) {
        List<K> listK = BeanMapperUtils.mapList(entityList, this.clazzK);
        return service.updateBatch(listK, batchSize);
    }

}
