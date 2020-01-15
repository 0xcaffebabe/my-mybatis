package wang.ismy.mybatis;

import wang.ismy.mybatis.mapper.UserMapper;

/**
 *
 * @author MY
 * @date 2020/1/15 15:07
 */
public class Main {
    public static void main(String[] args) {
        UserMapper mapper = SqlSession.getMapper(UserMapper.class);
        System.out.println(mapper.find("mht", 18));
//        System.out.println(mapper.insert("mht", 18));
    }
}
