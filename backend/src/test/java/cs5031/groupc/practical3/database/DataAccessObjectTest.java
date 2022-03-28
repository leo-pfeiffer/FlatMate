package cs5031.groupc.practical3.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import cs5031.groupc.practical3.model.Bill;
import cs5031.groupc.practical3.model.Group;
import cs5031.groupc.practical3.model.User;
import cs5031.groupc.practical3.testutils.SqlFileReader;
import cs5031.groupc.practical3.vo.UserRole;
import org.junit.jupiter.api.AfterEach;
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

    final static String DELETE_SCRIPT = "src/test/resources/db/delete.sql";
    final static String DEMO_SCRIPT = "src/test/resources/db/demo_data.sql";


    @BeforeEach
    public void setUp() {
        try {
            String delete = SqlFileReader.readFile(DELETE_SCRIPT);
            String demo = SqlFileReader.readFile(DEMO_SCRIPT);

            for (String query : delete.split(";")) {
                jdbcTemplate.execute(query);
            }

            for (String query : demo.split(";")) {
                jdbcTemplate.execute(query);
            }

        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed setup: " + e.getMessage());
        }
    }

    @AfterEach
    public void tearDown() {
        try {
            String delete = SqlFileReader.readFile(DELETE_SCRIPT);

            for (String query : delete.split(";")) {
                jdbcTemplate.execute(query);
            }

        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed teardown: " + e.getMessage());
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
        ArrayList<Group> groupsPost = dao.getAllGroups();
        assertEquals(groupsPre.size() + 1, groupsPost.size());
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
    public void testAddUserToGroupByName() {
        dao.createUser("testuser", "pass");
        dao.createGroup("testgroup");
        dao.addUserToGroup("testuser", "testgroup");
        Group group = dao.getUser("testuser").getGroup();
        assertEquals("testgroup", group.getName());
    }

    @Test
    public void testAddUserToGroupById() {
        dao.createUser("testuser", "pass");
        dao.addUserToGroup("testuser", 1L);
        Group group = dao.getUser("testuser").getGroup();
        assertEquals(1L, group.getGroupId());
    }

    @Test
    public void testRemoveUserFromGroup() {
        dao.createUser("testuser", "pass");
        dao.createGroup("testgroup");
        dao.addUserToGroup("testuser", "testgroup");
        assertNotNull(dao.getUser("testuser").getGroup());
        dao.removeUserFromGroup("testuser");
        assertNull(dao.getUser("testuser").getGroup());
    }

    @Test
    public void testSetRoleToAdmin() {
        dao.createUser("testuser", "pass");
        dao.setRoleToAdmin("testuser");
        User user = dao.getUser("testuser");
        assertEquals(user.getRole(), UserRole.ADMIN);
    }

    @Test
    public void testSetRoleToUser() {
        dao.createUser("testuser", "pass");
        dao.setRoleToAdmin("testuser");
        User user = dao.getUser("testuser");
        assertEquals(user.getRole(), UserRole.ADMIN);
        dao.setRoleToUser("testuser");
        user = dao.getUser("testuser");
        assertEquals(user.getRole(), UserRole.USER);
    }

    @Test
    public void testGetBill() {
        Bill bill = dao.getBill(1L);
        assertNotNull(bill);
        assertEquals(1L, bill.getBillId());
    }

    @Test
    public void testCreateBill() {
        User user = dao.getUser("leopold");
        Bill bill = new Bill();
        bill.setName("testbill");
        bill.setDescription("testdescription");
        bill.setAmount(12.12d);
        bill.setPaymentMethod("Cash");
        bill.setOwner(user);

        int numRows = dao.createBill(bill);
        assertEquals(1, numRows);
    }

    @Test
    public void testGetBillsForGroup() {
        // todo this is based on the demo data, maybe add data programmatically instead
        ArrayList<Bill> bills = dao.getBillsForGroup(1L);
        assertEquals(4, bills.size());
    }

    @Test
    public void testGetBillsForUser() {
        // todo this is based on the demo data, maybe add data programmatically instead
        ArrayList<Bill> bills = dao.getBillsForUser("leopold");
        assertEquals(3, bills.size());
    }


}
