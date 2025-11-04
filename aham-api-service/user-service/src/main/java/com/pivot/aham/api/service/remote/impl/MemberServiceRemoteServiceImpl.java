package com.pivot.aham.api.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.server.dto.MemberDTO;
import com.pivot.aham.api.server.remoteservice.MemberServiceRemoteService;
import com.pivot.aham.api.service.mapper.model.TMember;
import com.pivot.aham.api.service.service.MemberService;
import com.pivot.aham.common.core.exception.BusinessException;
import com.pivot.aham.common.core.exception.ValidateException;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年11月29日
 */
@Service(interfaceClass = MemberServiceRemoteService.class)
public class MemberServiceRemoteServiceImpl implements MemberServiceRemoteService{
    @Autowired
    private MemberService memberService;
    

    @Override
    public MemberDTO updateOrInsert(MemberDTO record) throws BusinessException, ValidateException {
        TMember tMember = new TMember();
        BeanMapperUtils.copy(record, tMember);
        tMember = memberService.updateOrInsert(tMember);
        MemberDTO sysUserDTO = new MemberDTO();
        BeanMapperUtils.copy(tMember, sysUserDTO);
        return sysUserDTO;
    }

    @Override
    public void del(List<Long> ids, Long userId) throws BusinessException, ValidateException {
        memberService.del(ids,userId);

    }

    @Override
    public void del(Long id, Long userId) throws BusinessException, ValidateException {
        memberService.del(id,userId);
    }

    @Override
    public void delete(Long id) throws BusinessException, ValidateException {
        memberService.delete(id);
    }

    @Override
    public Integer deleteByEntity(MemberDTO sysUserDTO) throws BusinessException, ValidateException {
        TMember tMember = new TMember();
        BeanMapperUtils.copy(sysUserDTO, tMember);
        return memberService.deleteByEntity(tMember);
    }

    @Override
    public Integer deleteByMap(Map<String, Object> columnMap) throws BusinessException, ValidateException {
        return memberService.deleteByMap(columnMap);
    }

    @Override
    public MemberDTO queryById(Long id) {
        TMember tMember = memberService.queryById(id);
        MemberDTO sysUserDTO = new MemberDTO();
        BeanMapperUtils.copy(tMember,sysUserDTO);
        return sysUserDTO;
    }

//    @Override
//    public Page<MemberDTO> query(Map<String, Object> params) {
//        Page<MemberDTO> pagination = new Page<>();
//        Page<TMember> sysUserPagination = memberService.queryPageList(params);
//        BeanMapperUtils.copy(sysUserPagination,pagination);
//        return pagination;
//    }

    @Override
    public Page<MemberDTO> query(MemberDTO entity, Page<MemberDTO> rowBounds) {
        Page<TMember> pagination = new Page<>();
        TMember tMember = new TMember();
        BeanMapperUtils.copy(entity,tMember);
        BeanMapperUtils.copy(rowBounds,pagination);
        Page<TMember> sysUserPagination = memberService.queryPageList(tMember,pagination);

        Page<MemberDTO> sysUserDTOPagination = new Page<>();
        BeanMapperUtils.copy(sysUserPagination,sysUserDTOPagination);
        return sysUserDTOPagination;
    }

//    @Override
//    public List<MemberDTO> queryList(Map<String, Object> params) {
//
//        List<TMember> sysUserList= memberService.queryList(params);
//        List<MemberDTO> sysUserDTOList = BeanMapperUtils.mapList(sysUserList,MemberDTO.class);
//        return sysUserDTOList;
//    }

    @Override
    public List<MemberDTO> queryList(List<Long> ids) {
        List<TMember> sysUserList= memberService.queryList(ids);
        List<MemberDTO> sysUserDTOList = BeanMapperUtils.mapList(sysUserList,MemberDTO.class);
        return sysUserDTOList;
    }

    @Override
    public <K> List<K> queryList(List<Long> ids, Class<K> cls) {
        return  null;
    }

    @Override
    public List<MemberDTO> queryList(MemberDTO entity) {
        TMember tMember = new TMember();
        BeanMapperUtils.copy(entity, tMember);
        List<TMember> sysUserList= memberService.queryList(tMember);
        List<MemberDTO> sysUserDTOList = BeanMapperUtils.mapList(sysUserList,MemberDTO.class);
        return sysUserDTOList;
    }

    @Override
    public MemberDTO selectOne(MemberDTO entity) {
        TMember tMember = new TMember();
        BeanMapperUtils.copy(entity,tMember);
        tMember = memberService.selectOne(tMember);
        MemberDTO sysUserDTO = new MemberDTO();
        BeanMapperUtils.copy(tMember,sysUserDTO);
        return sysUserDTO;
    }

    @Override
    public Integer count(MemberDTO entity) throws BusinessException, ValidateException {
        TMember tMember = new TMember();
        BeanMapperUtils.copy(entity,tMember);
        Integer count = memberService.count(tMember);

        return count;
    }

    @Override
    public boolean updateBatch(List<MemberDTO> entityList) throws BusinessException, ValidateException {
        List<TMember> sysUserList = BeanMapperUtils.mapList(entityList,TMember.class);

        return memberService.updateBatch(sysUserList);
    }

    @Override
    public boolean updateBatch(List<MemberDTO> entityList, int batchSize) throws BusinessException, ValidateException {
        List<TMember> sysUserList = BeanMapperUtils.mapList(entityList,TMember.class);

        return memberService.updateBatch(sysUserList,batchSize);
    }
}
