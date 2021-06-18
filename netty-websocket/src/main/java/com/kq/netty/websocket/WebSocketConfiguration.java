package com.kq.netty.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author kq
 * @date 2021-04-21 18:07
 * @since 2020-0630
 */

@Configuration
public class WebSocketConfiguration {

    @Autowired
    private WebSocketServer webSocketServer;

    @Bean
    public ApplicationRunner applicationRunner() {

        return applicationArguments -> {
            long startTime = System.currentTimeMillis();
            System.out.println(Thread.currentThread().getName() + "：开始调用异步业务");

            //无返回值
            webSocketServer.start();

            long endTime = System.currentTimeMillis();
            System.out.println(Thread.currentThread().getName() + "：调用异步业务结束，耗时：" + (endTime - startTime));
        };
    }

}
