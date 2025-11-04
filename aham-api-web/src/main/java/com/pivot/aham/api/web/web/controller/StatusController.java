package com.pivot.aham.api.web.web.controller;

import com.google.common.collect.Maps;
import com.pivot.aham.common.core.base.AbstractController;
import com.pivot.aham.common.core.monitor.PrometheusMethodMonitor;
import com.pivot.aham.common.core.support.email.Email;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.core.util.EmailUtil;
import com.pivot.aham.common.core.util.PropertiesUtil;
import io.lettuce.core.ReadFrom;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.codec.Utf8StringCodec;
import io.lettuce.core.masterslave.MasterSlave;
import io.lettuce.core.masterslave.StatefulRedisMasterSlaveConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("Status")
@Slf4j
public class StatusController extends AbstractController {

    private static ExecutorService executorService = new ThreadPoolExecutor(10, 20, 60, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(20), new ThreadPoolExecutor.DiscardOldestPolicy());

    @RequestMapping(value = "Version")
    @ResponseBody
    @PrometheusMethodMonitor
    public Map version(HttpServletRequest request, HttpServletResponse response) throws IOException {
//本机IP
        InetAddress ip = InetAddress.getLocalHost();
        //获取网络接口
        NetworkInterface network = NetworkInterface.getByInetAddress(ip);
        StringBuilder macBuilder = new StringBuilder();
        if (network != null) {
            byte[] mac = network.getHardwareAddress();
            for (byte b : mac) {
                long tmp = 0x000000FF & (long) b;
                macBuilder.append(Long.toHexString(tmp));
                macBuilder.append(":");
            }
        }
        Sequence sequence = new Sequence();
        Map<String, String> info = Maps.newHashMap();
        info.put("mac", macBuilder.toString());
        info.put("timemillis", System.currentTimeMillis() + "");
        info.put("dataCenterId", sequence.watchDatacenterId() + "");
        info.put("workerId", sequence.watchWorkerId() + "");
        info.put("env1", PropertiesUtil.getString("env.remark"));
        info.put("datetime", "2019-06-28");
        return info;
    }

    @RequestMapping(value = "Mail")
    @ResponseBody
    @PrometheusMethodMonitor
    public String mail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Email email = new Email();
        email.setBody("本地发送测试" + PropertiesUtil.getBoolean("env.error.log.send"));
        email.setTopic("ERROR " + PropertiesUtil.getString("email.env.name"));
        email.setSendTo("wooitatt.khor@ezyit.asia,clientservices@aham.com.sg");
        email.setSSL(true);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                EmailUtil.sendEmail(email);
            }
        });

        EmailUtil.sendEmail(email);
//        ErrorLogAndMailUtil.logError(log, "测试发邮件啊啊啊啊啊啊");

        return "success";
    }

    @RequestMapping(value = "TestAWSRedis")
    @ResponseBody
    @PrometheusMethodMonitor
    public String testRedis() throws IOException {
//        GenericObjectPoolConfig redisPoolConfig = new GenericObjectPoolConfig();
//        redisPoolConfig.setMinIdle(PropertiesUtil.getInt("redis.minIdle"));
//        redisPoolConfig.setMaxIdle(PropertiesUtil.getInt("redis.maxIdle"));
//        redisPoolConfig.setMaxTotal(PropertiesUtil.getInt("redis.maxTotal"));
//        redisPoolConfig.setMaxWaitMillis(PropertiesUtil.getInt("redis.maxWaitMillis"));
//        redisPoolConfig.setTestOnBorrow(true);
//        redisPoolConfig.setTestOnReturn(true);
//        // Idle时进行连接扫描
//        redisPoolConfig.setTestWhileIdle(true);
//        // 表示idle object evitor两次扫描之间要sleep的毫秒数
//        redisPoolConfig.setTimeBetweenEvictionRunsMillis(30000);
//        // 表示idle object evitor每次扫描的最多的对象数
//        redisPoolConfig.setNumTestsPerEvictionRun(10);
//        // 表示一个对象至少停留在idle状态的最短时间，然后才能被idle object evitor扫描并驱逐
//        // 这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
//        redisPoolConfig.setMinEvictableIdleTimeMillis(60000);
//
//        Duration commandTimeout = Duration.ofMillis(PropertiesUtil.getInt("redis.commandTimeout", 60000));
//        Duration shutdownTimeout = Duration.ofMillis(PropertiesUtil.getInt("redis.shutdownTimeout", 5000));
//
//        ClientResources clientResources= DefaultClientResources.create();
//        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder = LettucePoolingClientConfiguration.builder()
//                .poolConfig(redisPoolConfig).commandTimeout(commandTimeout).shutdownTimeout(shutdownTimeout)
//                .clientResources(clientResources);
//        builder.useSsl();
//        LettuceClientConfiguration clientConfiguration = builder.build();
//
//
//
//        RedisStaticMasterReplicaConfiguration redisStaticMasterReplicaConfiguration
//                = new RedisStaticMasterReplicaConfiguration("pivot-redis01-001.pivot-redis01.s3mqxv.apse1.cache.amazonaws.com");
//        redisStaticMasterReplicaConfiguration.setPassword("kbfdg^luwkl4Hh0I");
//        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(redisStaticMasterReplicaConfiguration, clientConfiguration);
//
//        connectionFactory.getConnection().serverCommands().dbSize();

        RedisClient redisClient = RedisClient.create();
        RedisURI redisURI = RedisURI.Builder.redis("pivot-redis01-001.pivot-redis01.s3mqxv.apse1.cache.amazonaws.com")
                .withSsl(true)
                .withPassword("kbfdg^luwkl4Hh0I")
                .build();
        List<RedisURI> nodes = Arrays.asList(redisURI);

        StatefulRedisMasterSlaveConnection<String, String> connection = MasterSlave
                .connect(redisClient, new Utf8StringCodec(), nodes);
        connection.setReadFrom(ReadFrom.MASTER_PREFERRED);
        RedisCommands<String, String> commands = connection.sync();
        commands.multi();
        commands.set("keytest", "addison");
        String s = commands.get("keytest");
        System.out.println("Connected to Redis" + s);
        commands.del("keytest");
        commands.exec();
        connection.close();
        redisClient.shutdown();

        return "success";
    }
}
