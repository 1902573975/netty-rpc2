package com.api.grp.interceptor;

import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * type 指定接口，方法指定方法名称， 参数指定方法的参数列表。
 * select 查询会调用query方法，其他update/delete/insert 会调用 update方法
 */
@Component
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "update",args = {MappedStatement.class, Object.class})
})
public class ExecutorInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        System.out.println("intercept...."+invocation.getMethod().getName());
        MappedStatement statement =(MappedStatement) invocation.getArgs()[0];
        SqlSource sqlSource = statement.getSqlSource();
        if(sqlSource instanceof StaticSqlSource){
            StaticSqlSource staticSqlSource = (StaticSqlSource)sqlSource;
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o,this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
