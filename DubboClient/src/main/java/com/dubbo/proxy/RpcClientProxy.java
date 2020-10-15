package com.dubbo.proxy;

import com.dubbo.bean.RpcRequest;
import com.dubbo.proxy.RpcProxyHandler;
import com.dubbo.registry.IServiceDiscovery;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RpcClientProxy{
    private IServiceDiscovery serviceDiscovery;

    public RpcClientProxy(IServiceDiscovery serviceDiscovery){
        this.serviceDiscovery = serviceDiscovery;
    }

    public <T> T create (final Class<T> interfaceClass){
        System.out.println("开始事务1");
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader()  ,
                    new Class<?>[]{interfaceClass}, new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        RpcRequest request = new RpcRequest();
                        request.setClassName(method.getDeclaringClass().getName());
                        request.setMethodName(method.getName());
                        request.setTypes(method.getParameterTypes());
                        request.setParams(args);
                        System.out.println(" methodName：" + request.toString() );
                        String serviceName = interfaceClass.getName();
                        String serviceAddress = serviceDiscovery.discover(serviceName);
                        String[] arrs = serviceAddress.split(":");
                        String host = arrs[0];
                        int port = Integer.parseInt(arrs[1]);
                        final RpcProxyHandler rpcProxyHandler = new RpcProxyHandler();
                        EventLoopGroup group  = new NioEventLoopGroup();
                        try{
                            Bootstrap b = new Bootstrap();
                            b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY,true)
                                    .handler(new ChannelInitializer<SocketChannel>() {
                                      @Override
                                      protected void initChannel(SocketChannel ch) throws Exception {
                                        // 自定义处理类
                                          ChannelPipeline pipeline = ch.pipeline();
                                          pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                                          pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));

                                          pipeline.addLast("Eecoder",new ObjectEncoder());
                                          pipeline.addLast("Decoder",new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));

                                          ch.pipeline().addLast(rpcProxyHandler);
                                      }
                            });
                            // 绑定端口，开始接收进来的连接
                            ChannelFuture f = b.connect(host,port).sync();
                            f.channel().writeAndFlush(request);
                            System.out.println("HttpClient已经连接到服务 ：" + host +":" + port  );
                            // 等待服务器 socket 关闭 。
                            f.channel().closeFuture().sync();
                        }catch (Exception e){
                            group.shutdownGracefully();
                        }
                        return  rpcProxyHandler.getResponse();
                    }

                }
                );

    }
}