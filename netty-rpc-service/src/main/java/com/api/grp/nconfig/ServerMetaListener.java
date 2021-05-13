package com.api.grp.nconfig;

import com.api.grp.comm.RPCService;
import com.api.grp.comm.SignUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

@Order(-1)
@Component
@ConditionalOnBean(NettyConfiguration.class)
public class ServerMetaListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private MethodMetaMaps methodMetaMaps;

    @Value("${spring.application.name}")
    private String applicationName;

    private HashSet<String> duplicateInterfaceImpl =new HashSet<>();


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        Map<String, Object> beans = contextRefreshedEvent.getApplicationContext().getBeansWithAnnotation(RPCService.class);

        Method[] declaredMethods;
        String methodName;
        Class<?>[] parameterTypes;
        Class<?> returnType;

        if(StringUtils.isEmpty(applicationName)){
            throw new RuntimeException("应用名称不能为空");
        }

        //实例化后的接口集合
        HashSet<Class> instanceIntSet =new HashSet<>();


        for(Map.Entry<String,Object> entry : beans.entrySet()){
            Object bean = entry.getValue();//实现类实例
            Class<?> targetClass = bean.getClass();
            Class<?>[] interfaces = targetClass.getInterfaces();


            for(Class iClazz :interfaces){
                RPCService annotation = (RPCService)iClazz.getAnnotation(RPCService.class);
                if(annotation != null){

                    String serName = annotation.serviceName();
                    if(StringUtils.isEmpty(serName)){
                        throw new RuntimeException("服务名称不能为空:"+targetClass.getName());
                    }
                    if(serName.equals(applicationName)){
                        instanceIntSet.add(iClazz);
                    }else {
                        continue;
                    }

                    String iClazzName = iClazz.getName();//接口全名
                    //检测接口是否有多个实现类
                    if(duplicateInterfaceImpl.contains(iClazzName)){
                        throw new RuntimeException(iClazzName+"有多个实现类");
                    }
                    duplicateInterfaceImpl.add(iClazzName);

//                    declaredMethods = iClazz.getDeclaredMethods();//只会获取当前类声明的方法
                    declaredMethods = iClazz.getMethods();//获取当前类和子类声明的方法
                    for(Method m : declaredMethods){
                        methodName = m.getName();
                        parameterTypes = m.getParameterTypes();
                        returnType = m.getReturnType();

                        String uuid = SignUtils.sign(m.toGenericString());
                        RpcMetadata metadata =new RpcMetadata(bean,iClazzName,m,methodName,parameterTypes,returnType);
//                        System.out.println(iClazzName+","+methodName);
                        methodMetaMaps.addMetadata(uuid,metadata);
                    }

                }
            }
        }
        methodMetaMaps.setInstanceIntSet(instanceIntSet);
    }
}
