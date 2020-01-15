package wang.ismy.mybatis.mapper;

import wang.ismy.mybatis.annotation.Param;
import wang.ismy.mybatis.annotation.Select;
import wang.ismy.mybatis.annotation.Update;
import wang.ismy.mybatis.entity.User;

import java.util.List;

/**
 * @author MY
 * @date 2020/1/15 15:28
 */
public interface UserMapper {

    @Select("SELECT * FROM user WHERE name = #{username} AND age = #{age}")
    List<User> find(@Param("username") String name, @Param("age") Integer age);

    @Update("INSERT INTO user VALUES(#{username},#{age})")
    int insert(@Param("username")String username,@Param("age") int age);
}
