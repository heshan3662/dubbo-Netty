package com.dubbo.proxy;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public  class RpcProxyHandler extends ChannelInboundHandlerAdapter {
    private final Logger logger = LoggerFactory.getLogger(RpcProxyHandler.class);

    private Object response;
     public Object getResponse (){
         return response;
     }
    @Override
    public void  channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        response = msg ;

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {


        //删除自定义链接池中的channal信息，
        Channel incoming = ctx.channel();
        logger.warn( incoming.remoteAddress()+" 断开连接  ");

    }

}