package com.dubbo.registry;


import com.alibaba.fastjson.JSONObject;
import com.dubbo.bean.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class RpcServerHandler extends ChannelInboundHandlerAdapter {
 private Map<String,Object> handlerMap = new HashMap<String, Object>();
 public RpcServerHandler(Map<String,Object> handlerMap){
     this.handlerMap = handlerMap;
     System.out.println("handlerMap3:"+JSONObject.toJSONString(handlerMap));
 }

    @Override
    public void  channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcRequest rpcRequest = (RpcRequest) msg;
        System.out.println(JSONObject.toJSONString(rpcRequest));
        Object result = new Object();

        if (handlerMap .containsKey(rpcRequest.getClassName())){
            Object clazz = handlerMap.get(rpcRequest.getClassName());
            Method method = clazz.getClass().getMethod(rpcRequest.getMethodName(),java.lang.String.class);
            result = method.invoke(clazz,rpcRequest.getParams());
        }
        ctx.write(result);
        ctx.flush();
        ctx.close();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().id() + "closed !");
    }
}