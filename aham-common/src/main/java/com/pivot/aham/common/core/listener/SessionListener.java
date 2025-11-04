package com.pivot.aham.common.core.listener;

import com.pivot.aham.common.core.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;


/**
 * shiro的session监听，统计在线用户数
 *
 * @author addison
 * @since 2018年11月15日
 */
public class SessionListener implements org.apache.shiro.session.SessionListener {
    private Logger logger = LogManager.getLogger();

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public void onStart(Session session) {
        session.setAttribute(Constants.WEBTHEME, "default");
        logger.info("创建了一个Session连接:[" + session.getId() + "]");
        //放入集合，统计用
        redisTemplate.opsForSet().add(Constants.ALLUSER_NUMBER, session.getId());
    }

    @Override
    public void onStop(Session session) {
        if (getAllUserNumber() > 0) {
            logger.info("销毁了一个Session连接:[" + session.getId() + "]");
        }
        session.removeAttribute(Constants.CURRENT_USER);
        //移除集合，统计用
        redisTemplate.opsForSet().remove(Constants.ALLUSER_NUMBER, session.getId());
    }

    @Override
    public void onExpiration(Session session) {
        onStop(session);
    }

    /**
     * 从redis获取在线用户数量
     *
     * */
    public Integer getAllUserNumber() {
        return redisTemplate.opsForSet().size(Constants.ALLUSER_NUMBER).intValue();
    }
}
