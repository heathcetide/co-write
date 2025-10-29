package com.cowrite.project.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cowrite.project.model.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.cowrite.project.common.constants.UserConstants.BuildNewUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @BeforeEach
    public void addPreFunction(){
        // Builder User
        User user = BuildNewUser("Default-User","19511899044@163.com","https://cowrite.com/default.png");

        // Insert User
        int result = userMapper.insert(user);
        assertEquals(1, result);
        assertNotNull(user.getId());

        // Select User
        User selected = userMapper.selectById(user.getId());
        assertNotNull(selected);
        assertEquals("19511899044@163.com", selected.getEmail());
    }

    @AfterEach
    public void deletePreFunction(){
        userMapper.delete(new LambdaQueryWrapper<User>().eq(User::getEmail, "19511899044@163.com"));
    }

    @Test
    public void TestExistsByUsername(){
        boolean b = userMapper.existsByUsername("Default-User");
        assertEquals(true, b);
    }

    @Test
    public void TestExistsByEmail(){
        boolean b = userMapper.existsByEmail("19511899044@163.com");
        assertEquals(true, b);
    }

    @Test
    public void TestSelectByEmail(){
        User user = userMapper.selectByEmail("19511899044@163.com");
        assertNotNull(user, "用户不应该为 null，可能未正确插入或查询");
        assertEquals("19511899044@163.com", user.getEmail());
    }

    @Test
    public void TestSelectByUsername(){
        User user = userMapper.selectByUsername("Default-User");
        assertNotNull(user, "用户不应该为 null，可能未正确插入或查询");
        assertEquals("Default-User", user.getUsername());
    }
}
