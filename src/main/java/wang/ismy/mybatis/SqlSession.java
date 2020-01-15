package wang.ismy.mybatis;

import wang.ismy.mybatis.handle.MapperInvocationHandler;

import java.lang.reflect.Proxy;

/**
 * @author MY
 * @date 2020/1/15 15:36
 */
public class SqlSession {

    public static  <T>T  getMapper(Class<T> klass){
        return klass.cast(Proxy.newProxyInstance(klass.getClassLoader(),new Class[]{klass},new MapperInvocationHandler()));
    }
}
