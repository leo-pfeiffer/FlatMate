package cs5031.groupc.practical3.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import cs5031.groupc.practical3.model.Group;
import cs5031.groupc.practical3.model.User;
import cs5031.groupc.practical3.testutils.SqlFileReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
public class DataAccessObjectTest {

    @Autowired
    DataAccessObject dao;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        try {
            SqlFileReader reader = new SqlFileReader();
            String schemaScript = reader.readFile("src/main/resources/db/delete.sql");
            String demoScript = reader.readFile("src/main/resources/db/demo_data.sql");

            for (String query : schemaScript.split(";")) {
                jdbcTemplate.execute(query);
            }

            for (String query : demoScript.split(";")) {
                jdbcTemplate.execute(query);
            }

        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed setup: " + e.getMessage());
        }
    }

    @Test
    public void testGetGroup() {
        Group group = dao.getGroup(1L);
        assertNotNull(group);
        assertEquals(1L, group.getGroupId());
    }

    @Test
    public void testGetGroupByName() {
        Group group = dao.getGroup("gannochy");
        assertNotNull(group);
        assertEquals("gannochy", group.getName());
    }

    @Test
    public void testCreateGroup() {
        ArrayList<Group> groupsPre = dao.getAllGroups();
        int numRows = dao.createGroup("testgroup");
        assertEquals(1, numRows);
        Group group = dao.getGroup("testgroup");
        assertNotNull(group);
    }

    @Test
    public void testGetAllGroups() {
        ArrayList<Group> groups = dao.getAllGroups();
        assertTrue(groups.size() > 0);
        int numRows = dao.createGroup("testgroup");
        assertEquals(1, numRows);
        ArrayList<Group> groupsPost = dao.getAllGroups();
        assertEquals(groupsPost.size() - 1, groups.size());
    }

    @Test
    public void testGetUser() {
        User user = dao.getUser("leopold");
        assertEquals("leopold", user.getUsername());
    }

    @Test
    public void testCreateUser() {
        int numRows = dao.createUser("testuser", "pass");
        assertEquals(1, numRows);
        User user = dao.getUser("testuser");
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
    }

    @Test
    public void testGetAllUsers() {
        ArrayList<User> users = dao.getAllUsers();
        assertTrue(users.size() > 0);
        int numRows = dao.createUser("testuser", "pass");
        assertEquals(1, numRows);
        ArrayList<User> usersPost = dao.getAllUsers();
        assertEquals(usersPost.size() - 1, users.size());
    }

    @Test
    public void testGetGroupForUser() {
        dao.createUser("nogroupuser", "pass");
        dao.createUser("groupuser", "pass");
        dao.addUserToGroup("groupuser", 1L);
        Group group = dao.getUser("groupuser").getGroup();
        assertNotNull(group);
        assertEquals(1L, group.getGroupId());
        Group noGroup = dao.getUser("nogroupuser").getGroup();
        assertNull(noGroup);
    }
}
