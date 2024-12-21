package com.example.blog.repository.user;

import com.example.blog.service.user.UserEntity;
import org.apache.ibatis.annotations.*;

import java.util.Optional;

@Mapper
public interface UserRepository {

    default Optional<UserEntity> selectByUsername(String username) {
        return Optional.ofNullable(username)
                .flatMap(this::selectByUsernameInternal);
    }

    @Select("""
            SELECT
             id
            ,u.username
            ,u.password
            ,u.enabled
            FROM users u
            WHERE u.username = #{username}
            """)
    Optional<UserEntity> selectByUsernameInternal(@Param("username") String username);


    @Insert("""
            INSERT INTO users(username, password, enabled)
            VALUES(#{username}, #{password}, #{enabled})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(UserEntity entity
    );

    @Delete("""
            DELETE FROM users u
            WHERE u.username = #{username}
            """)
    void deleteByUsername(@Param("username") String username);
}
