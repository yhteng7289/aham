/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.UobNotificationPO;
import com.pivot.aham.common.core.base.BaseMapper;

/**
 *
 * Created by Howey Teng
 */
public interface UobNotificationMapper extends BaseMapper {

    void saveUobNotification(UobNotificationPO uobNotificationPO);

}
