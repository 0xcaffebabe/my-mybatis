package wang.ismy.mybatis.handle;

import com.alibaba.druid.pool.DruidDataSource;

import wang.ismy.mybatis.annotation.Param;
import wang.ismy.mybatis.annotation.Select;
import wang.ismy.mybatis.annotation.Update;


import javax.sql.DataSource;
import java.lang.reflect.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author MY
 * @date 2020/1/15 15:34
 */
public class MapperInvocationHandler implements InvocationHandler {

    private DataSource dataSource;

    public MapperInvocationHandler() {
        init();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Map<String,Object> paramsMap = new HashMap<>();
        Parameter[] parameters = method.getParameters();
        String sql = "";
        for (int i = 0; i < parameters.length; i++) {
            Param paramAnnotation = parameters[i].getAnnotation(Param.class);
            if (paramAnnotation != null){
                paramsMap.put(paramAnnotation.value(),args[i]);
            }
        }
        Select select = method.getAnnotation(Select.class);
        Update update = method.getAnnotation(Update.class);
        if (select != null){
            sql = select.value();
            return select(sql,paramsMap,args,method.getGenericReturnType());
        }else if (update != null){
            sql = update.value();
            int ret = update(sql,paramsMap,args);
            if (method.getReturnType().equals(Integer.class) || method.getReturnType().equals(int.class)){

                return ret;
            }else {
                return null;
            }

        }else {
            throw new IllegalStateException("无法进行");
        }


    }

    private void init(){
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("123");
        druidDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        druidDataSource.setUrl("jdbc:mysql:///test");
        dataSource = druidDataSource;
    }

    private Object select(String sql,Map<String,Object> map,Object[] args,Type returnType) throws SQLException, IllegalAccessException, InstantiationException, NoSuchFieldException {
        PreparedStatement prst = processParam(sql, map, args);
        ResultSet rs = prst.executeQuery();
        // 将结果集转为对象
        // 如果是List，获取其泛型对象作为bean类型
        // 否则returnType作为bean类型
        Class<?> beanType = null;

        if (returnType instanceof ParameterizedType){
            if (((ParameterizedType) returnType).getRawType().equals(List.class)){
                ParameterizedType aClass = (ParameterizedType) returnType;
                beanType = (Class<?>) aClass.getActualTypeArguments()[0];
                List ret = new ArrayList();
                while (rs.next()){
                    Object bean = beanType.newInstance();

                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                        Object value = rs.getObject(rs.getMetaData().getColumnName(i));
                        Field field = bean.getClass().getDeclaredField(rs.getMetaData().getColumnName(i));
                        field.setAccessible(true);
                        field.set(bean,value);
                    }
                    ret.add(bean);
                }
                return ret;
            }
        } else{
            beanType = (Class<?>) returnType;
            if (rs.next()){
                Object bean = beanType.newInstance();

                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    Object value = rs.getObject(rs.getMetaData().getColumnName(i));
                    Field field = bean.getClass().getDeclaredField(rs.getMetaData().getColumnName(i));
                    field.setAccessible(true);
                    field.set(bean,value);
                }
                return bean;
            }

        }

        return null;
    }

    private int update(String sql,Map<String,Object> map,Object[] args) throws SQLException {
        PreparedStatement prst = processParam(sql, map, args);
        return prst.executeUpdate();
    }

    private PreparedStatement processParam(String sql, Map<String, Object> map, Object[] args) throws SQLException {
        // 占位符替换为?
        for (String s : map.keySet()) {
            sql = sql.replace("#{"+s+"}","?");
        }
        // 预编译SQL并执行
        Connection conn = dataSource.getConnection();
        PreparedStatement prst = conn.prepareStatement(sql);
        if (args != null){
            for (int i = 1; i <= args.length; i++) {
                prst.setObject(i,args[i-1]);
            }
        }
        return prst;
    }


}
