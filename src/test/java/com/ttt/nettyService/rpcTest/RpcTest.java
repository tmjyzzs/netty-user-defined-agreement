package com.ttt.nettyService.rpcTest;

import com.ttt.message.RpcRequestMessage;
import com.ttt.server.service.HelloService;
import com.ttt.server.service.ServicesFactory;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

public class RpcTest {

    @Test
    public void testRpc() throws Exception {

        RpcRequestMessage message = new RpcRequestMessage(
                1,
                "com.ttt.server.service.HelloService",
                "sayHello",
                String.class,
                new Class[]{String.class},
                new Object[]{"张三"}
        );

        HelloService service = (HelloService)
                ServicesFactory.getService(Class.forName(message.getInterfaceName()));

        Method method = service.getClass().getMethod(message.getMethodName(), message.getParameterTypes());
        Object invoke = method.invoke(service, message.getParameterValue());
        System.out.println(invoke);
    }
}
