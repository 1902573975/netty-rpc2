package com.api.grp.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Properties;

@Component
@Intercepts({
        @Signature(type = StatementHandler.class,method = "prepare",args = {Connection.class,Integer.class})
})
public class StatementInterceptor implements Interceptor {

    public static final String TENANT_ID = "tenantId";
    public static final String TENANT_ID_VALUE ="1";
    public static final String SQL_TENANT_ID="tenant_id";

    private boolean hasTenantId;
    private boolean tenantIdEmpty;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        System.out.println("Statement Intercept");
        StatementHandler handler = (StatementHandler)invocation.getTarget();
        ParameterHandler parameterHandler = handler.getParameterHandler();
        MetaObject metaObject = SystemMetaObject.forObject(handler);
        MappedStatement statement = (MappedStatement)metaObject.getValue("delegate.mappedStatement");
        BoundSql boundSql =(BoundSql) metaObject.getValue("delegate.boundSql");
        String sql = boundSql.getSql().trim();
        if(SqlCommandType.INSERT.equals(statement.getSqlCommandType())){
            tenantIdParameter(parameterHandler);
            String[] values = sql.toLowerCase().split("values");
            if(tenantIdEmpty & hasTenantId && values[0].trim().endsWith(")") && values[0].indexOf(SQL_TENANT_ID) == -1){
                sql = sql.replaceFirst("\\)",","+SQL_TENANT_ID+")");
                sql  =sql.substring(0,sql.length()-1)  +",'"+TENANT_ID_VALUE+"')";
            }
            metaObject.setValue("delegate.boundSql.sql",sql);
//            System.out.println(sql);
        }else if(SqlCommandType.SELECT.equals(statement.getSqlCommandType())){
            tenantIdParameter(parameterHandler);
        }
        return invocation.proceed();
    }

    private void tenantIdParameter(ParameterHandler parameterHandler){
        Object parameterObject = parameterHandler.getParameterObject();
        try{
            Field tenantIdField = parameterObject.getClass().getDeclaredField(TENANT_ID);
            tenantIdField.setAccessible(true);
            Object tenantId = tenantIdField.get(parameterObject);
            Class<?> tenantIdClazz = tenantIdField.getType();
            if(tenantIdClazz.equals(String.class) && (tenantId == null || StringUtils.isEmpty((String)tenantId)) ){
                hasTenantId =true;
                tenantIdEmpty = true;
            }
        }catch (Exception e){
        }
    }

    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o,this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
