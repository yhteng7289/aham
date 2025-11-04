package com.pivot.aham.admin.service.mapper;

import java.util.List;

import com.pivot.aham.admin.service.mapper.model.SysMenu;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SysAuthorizeMapper {

	void deleteUserMenu(@Param("userId") Long userId);

	void deleteUserRole(@Param("userId") Long userId);

	void deleteRoleMenu(@Param("roleId") Long roleId);

	List<String> queryPermissionByUserId(@Param("userId") Long userId);

	List<String> queryPermissionByRoleId(@Param("roleId") Long roleId);

	List<Long> queryMenuIdsByUserId(Long userId);

	List<SysMenu> queryMenusByRoleId(@Param("roleId") Long roleId);

}
