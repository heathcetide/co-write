package com.cowrite.project.utils;

import com.cowrite.project.model.entity.User;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JsonUtilsTest {

    @Test
    public void testToJsonAndFromJson() {
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");

        String json = JsonUtils.toJson(user);
        assertNotNull(json);
        assertTrue(json.contains("testUser"));

        User parsedUser = JsonUtils.fromJson(json, User.class);
        assertEquals("testUser", parsedUser.getUsername());
        assertEquals("test@example.com", parsedUser.getEmail());
    }

    @Test
    public void testFromJsonToObjectNode() {
        String json = "{\"key\":\"value\"}";
        ObjectNode node = JsonUtils.fromJsonToObjectNode(json);
        assertNotNull(node);
        assertEquals("value", node.get("key").asText());
    }

    @Test
    public void testCreateObjectNode() {
        ObjectNode node = JsonUtils.createObjectNode();
        assertNotNull(node);
        node.put("foo", "bar");
        assertEquals("bar", node.get("foo").asText());
    }

    @Test
    public void testToObjectNode() {
        User user = new User();
        user.setUsername("testObject");
        ObjectNode node = JsonUtils.toObjectNode(user);
        assertEquals("testObject", node.get("username").asText());
    }
}
