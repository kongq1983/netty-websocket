package com.kq.netty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author kq
 * @date 2021-04-21 16:34
 * @since 2020-0630
 */

@SpringBootApplication
public class NettyWebSocketApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(NettyWebSocketApplication.class, args);

    }

}
