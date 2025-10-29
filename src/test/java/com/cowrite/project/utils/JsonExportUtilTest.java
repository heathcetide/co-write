package com.cowrite.project.utils;

import com.cowrite.project.model.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JsonExportUtilTest {

    private List<User> sampleUsers;

    @BeforeEach
    public void setup() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        sampleUsers = Collections.singletonList(user);
    }

    @Test
    public void testReadUsersFromJson() throws Exception {
        String json = "[{\"username\":\"testuser\",\"email\":\"test@example.com\"}]";
        MockMultipartFile file = new MockMultipartFile("file", "users.json", "application/json", json.getBytes());
        List<User> users = JsonExportUtil.readUsersFromJson(file);

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("testuser", users.get(0).getUsername());
    }

    @Test
    public void testExportToJson() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        JsonExportUtil.exportToJson(response, sampleUsers);

        String content = response.getContentAsString();
        assertTrue(content.contains("testuser"));
        assertEquals("application/json", response.getContentType());
    }
}
