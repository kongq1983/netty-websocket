package com.kq.netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * * Netty Reactor模型 -
 * * 1.单线程模型:一个用户一个线程来处理，线程有极限
 * * 2.多线程模型：加入线程池,线程池线程轮询执行任务
 * * 3.主从多线程模型：俩个线程池，一个线程池接收请求，一个线程池处理IO（推荐，适用高并发环境）
 * *
 * * 以下代码为主从多线程模型
 *
 * @author kq
 * @date 2021-04-21 17:55
 * @since 2020-0630
 */
@Slf4j
@Component
public class WebSocketServer {

    private int nettyPort = 9090;

    @PostConstruct
    public void init(){
//        this.start();
    }

//    @Async
    public void start() {

        log.info(" begin to start netty server ");

        NioEventLoopGroup mainGrp = new NioEventLoopGroup();//主线程池
        NioEventLoopGroup subGrp = new NioEventLoopGroup();//从线程池

        try {
            //1.创建netty服务器启动对象
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            //2.初始化
            serverBootstrap
                    //指定使用的线程池
                    .group(mainGrp, subGrp)
                    //指定netty通道类型
                    .channel(NioServerSocketChannel.class)
                    // 指定通道初始化器用来加载当Channel收到事件消息后，
                    // 如何进行业务处理
                    .childHandler(new WebSocketChannelInitializer());

            //3.绑定端口，以同步的方式启动
            ChannelFuture future = serverBootstrap.bind(nettyPort).sync();
//            ChannelFuture future = serverBootstrap.bind().sync();

            log.info(" netty server lister on port {} ",nettyPort);

            // 和springboot集成，这里会阻塞，会导致springboot没启动完成

            //4.等待服务关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();

            //5.异常关闭服务器
            mainGrp.shutdownGracefully();
            subGrp.shutdownGracefully();
        }

    }

}
