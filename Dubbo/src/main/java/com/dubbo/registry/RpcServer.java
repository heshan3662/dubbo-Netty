package com.dubbo.registry;

import com.sun.org.omg.CORBA.Initializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.HashMap;
import java.util.Map;

public class RpcServer{

    private static Map<String, Object> handlerMap = new HashMap<String, Object> ();

    private IRegisterCenter registerCenter ;

    private String serviceAddress  ;

    public   RpcServer(IRegisterCenter registerCenter,String serviceAddress){
        this.registerCenter= registerCenter;
        this.serviceAddress= serviceAddress;
    }
    public void piblisher (){
        for(String serviceName : handlerMap.keySet()){
            registerCenter.register(serviceName,serviceAddress);
        }
        System.out.println("handlerMap1:"+ handlerMap.size());
        EventLoopGroup bossGroup = new NioEventLoopGroup(1); // boss
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // worker

        try {
            // 启动NIO服务的引导程序类
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup) // 设置EventLoopGroup
                    .channel(NioServerSocketChannel.class) // 指明新的Channel的类型
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 自定义处理类
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                            pipeline.addLast("Eecoder",new ObjectEncoder());
                            pipeline.addLast("Decoder",new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                            RpcServerHandler rpcServerHandler = new RpcServerHandler(handlerMap);
                            ch.pipeline().addLast(new RpcServerHandler(handlerMap));
                            System.out.println("handlerMap2:"+ handlerMap.size());
                        }
                    }) // 指定ChannelHandler
                    .option(ChannelOption.SO_BACKLOG, 128) // 设置的ServerChannel的一些选项
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // 设置的ServerChannel的子Channel的选项
             String[] addrs = serviceAddress.split(":");
             String ip = addrs[0];
            int port = Integer.parseInt(addrs[1]);
            // 绑定端口，开始接收进来的连接
            ChannelFuture f = b.bind(ip, port).sync();
             System.out.println("HttpServer已启动，端口：" + port);
            // 等待服务器 socket 关闭 。
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();

        }
    }


    public void bind(Object... services){
        for(Object service:services ){
             RpcAnnotation rpcAnnotation =  service.getClass().getAnnotation(RpcAnnotation.class);
             String serviceName  = rpcAnnotation.value().getName();
             handlerMap.put(serviceName, service);
        }

    }
}