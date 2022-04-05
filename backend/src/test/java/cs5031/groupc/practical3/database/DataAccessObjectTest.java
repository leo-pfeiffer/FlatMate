package cs5031.groupc.practical3.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import cs5031.groupc.practical3.model.Bill;
import cs5031.groupc.practical3.model.Group;
import cs5031.groupc.practical3.model.List;
import cs5031.groupc.practical3.model.ListItem;
import cs5031.groupc.practical3.model.User;
import cs5031.groupc.practical3.model.UserBill;
import cs5031.groupc.practical3.testutils.SqlFileReader;
import cs5031.groupc.practical3.vo.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.UncategorizedSQLException;
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
    public void testGetGroupById() {
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
        bill.setCreateTime(1648727482L);

        int numRows = dao.createBill(bill);
        assertEquals(1, numRows);
    }

    @Test
    public void testGetBillsForGroup() {
        ArrayList<Bill> bills = dao.getBillsForGroup(1L);
        assertEquals(4, bills.size());
    }

    @Test
    public void testGetBillsForUser() {
        ArrayList<Bill> bills = dao.getBillsForUser("leopold");
        assertEquals(3, bills.size());
    }

    @Test
    public void testGetList() {
        List list = dao.getList(1L);
        assertNotNull(list);
        assertEquals(1L, list.getListId());
    }

    @Test
    public void testCreateList() {
        User user = dao.getUser("leopold");
        List list = new List();
        list.setName("testlist");
        list.setDescription("testdescription");
        list.setOwner(user);
        list.setCreateTime(1648727482L);
        int numRows = dao.createList(list);
        assertEquals(1, numRows);
    }

    @Test
    public void testGetListsForGroup() {
        ArrayList<List> lists = dao.getListsForGroup(1L);
        assertEquals(3, lists.size());
    }

    @Test
    public void testGetListItem() {
        ListItem item = dao.getListItem(1L);
        assertNotNull(item);
        assertEquals(1L, item.getListItemId());
    }

    @Test
    public void testGetListItemsForList() {
        ArrayList<ListItem> items = dao.getListItemsForList(1L);
        assertEquals(4, items.size());
    }

    @Test
    public void testCreateListItem() {
        List list = dao.getList(1L);
        ListItem item = new ListItem();
        item.setName("testitem");
        item.setList(list);
        int numRows = dao.createListItem(item);
        assertEquals(1, numRows);
    }

    @Test
    public void testCreateUserBill() {
        Bill bill = dao.getBill(1L);
        User user = dao.getUser("leopold");
        UserBill userBill = new UserBill();
        userBill.setBill(bill);
        userBill.setUser(user);
        userBill.setPercentage(0.5d);
        userBill.setPaid(false);
        int numRows = dao.createUserBill(userBill);
        assertEquals(1, numRows);
    }

    @Test
    public void testGetUserBill() {
        UserBill userBill = dao.getUserBill(1L);
        assertNotNull(userBill);
        assertEquals(1L, userBill.getUserBillId());
    }

    @Test
    public void testGetUserBillsForBill() {
        // todo this is based on the demo data, maybe add data programmatically instead
        ArrayList<UserBill> userBills = dao.getUserBillsForBill(1L);
        assertEquals(4, userBills.size());
    }

    @Test
    public void testSetUserBillToPaid() {
        dao.setUserBillToPaid(2L, "leopold");

        ArrayList<UserBill> billsPre = dao.getUserBillsForBill(2L);

        for (UserBill userBill : billsPre) {
            assertFalse(userBill.isPaid());
            dao.setUserBillToPaid(userBill.getUserBillId(), userBill.getUser().getUsername());
        }

        ArrayList<UserBill> billsPost = dao.getUserBillsForBill(2L);

        for (UserBill userBill : billsPost) {
            assertTrue(userBill.isPaid());
        }
    }


    @Test
    public void testGetUserBillsForUser() {
        ArrayList<UserBill> userBills = dao.getUserBillsForUser("leopold");
        assertEquals(3, userBills.size());
    }

    @Test
    public void testAddBillToList() {
        dao.addBillToList(3L, 2L);
        List gottenList = dao.getList(3L);
        Bill gottenBill =  gottenList.getBill();
        assertEquals(2L,gottenBill.getBillId());


    }

    @Test
    public void testCreateBillAndReturnBill() {
        User user = dao.getUser("lucas");
        Bill bill = new Bill();
        bill.setName("testbill");
        bill.setDescription("testdescription");
        bill.setAmount(12.12d);
        bill.setPaymentMethod("Cash");
        bill.setOwner(user);
        bill.setCreateTime(1648727482L);

        Bill billReturned = dao.createBillAndReturnBill(bill);

        assertEquals(bill.getName(), billReturned.getName());
        assertEquals(bill.getDescription(), billReturned.getDescription());
        assertEquals(bill.getAmount(), billReturned.getAmount());
        assertEquals(bill.getOwner().getUsername(), billReturned.getOwner().getUsername());
    }

    @Test
    public void testCreateListAndReturnList() {
        User user = dao.getUser("lucas");
        List list = new List();
        list.setName("testlist");
        list.setDescription("testdescription");
        list.setOwner(user);
        list.setCreateTime(1648727482L);
        List listReturned = dao.createListAndReturnList(list);
        assertEquals(list.getName(), listReturned.getName());
        assertEquals(list.getDescription(), listReturned.getDescription());
        assertEquals(list.getOwner().getUsername(), listReturned.getOwner().getUsername());
    }

    @Test
    public void testCreateDuplicateUser() {
        assertThrows(UncategorizedSQLException.class, () -> dao.createUser("lucas", "1234"));
    }

    @Test
    public void testCreateDuplicateBill() {
        Bill b = new Bill();
        b.setName("test");
        b.setAmount(10.20);
        b.setOwner(dao.getUser("lucas"));
        b.setCreateTime(1648727482L);
        b.setDescription("testdescription");
        b.setPaymentMethod("Cash");

        Bill b1 = dao.createBillAndReturnBill(b);
        Bill b2 = dao.createBillAndReturnBill(b);
        assertNotEquals(b1.getBillId(), b2.getBillId());
    }

    @Test
    public void testCreateBillNoOwner() {
        Bill b = new Bill();
        b.setName("test");
        b.setAmount(10.20);
        b.setCreateTime(1648727482L);
        b.setDescription("testdescription");
        b.setPaymentMethod("Cash");
        assertThrows(NullPointerException.class, () -> dao.createBill(b));
    }

    @Test
    public void testCreateDuplicateList() {
        User user = dao.getUser("lucas");
        List list = new List();
        list.setName("testlist");
        list.setDescription("testdescription");
        list.setOwner(user);
        list.setCreateTime(1648727482L);
        List l1 = dao.createListAndReturnList(list);
        List l2 = dao.createListAndReturnList(list);
        assertNotEquals(l1.getListId(), l2.getListId());
    }

    @Test
    public void testCreateDuplicateListItem() {
        User user = dao.getUser("lucas");
        List list = new List();
        list.setName("testlist");
        list.setDescription("testdescription");
        list.setOwner(user);
        list.setCreateTime(1648727482L);
        List listReturned = dao.createListAndReturnList(list);
        ListItem li = new ListItem();
        li.setList(listReturned);
        li.setName("test");
        dao.createListItem(li);
        dao.createListItem(li);
        assertEquals(2, dao.getListItemsForList(listReturned.getListId()).size());
    }

    @Test
    public void testCreateDuplicateGroup() {
        ArrayList<Group> groupsPre = dao.getAllGroups();
        int numRows = dao.createGroup("testgroup");
        assertEquals(1, numRows);
        assertThrows(UncategorizedSQLException.class, () -> dao.createGroup("testgroup"));
        Group group = dao.getGroup("testgroup");
        assertNotNull(group);
        ArrayList<Group> groupsPost = dao.getAllGroups();
        assertEquals(groupsPre.size() + 1, groupsPost.size());
    }

    @Test
    public void testSetAdminToAdmin() {
        User user = dao.getUser("lucas");
        user.setRole(UserRole.ADMIN);
        user.setRole(UserRole.ADMIN);
        assertEquals(UserRole.ADMIN, user.getRole());
    }

    @Test
    public void testSetUserToUser() {
        User user = dao.getUser("lucas");
        user.setRole(UserRole.USER);
        user.setRole(UserRole.USER);
        assertEquals(UserRole.USER, user.getRole());
    }

    @Test
    public void testGetNonExistentGroup() {
        assertThrows(EmptyResultDataAccessException.class, () -> dao.getGroup("nonexistent"));
    }

    @Test
    public void testGetNonExistentUser() {
        assertThrows(EmptyResultDataAccessException.class, () -> dao.getUser("nonexistent"));
    }

    @Test
    public void testGetNonExistentBill() {
        assertThrows(EmptyResultDataAccessException.class, () -> dao.getBill(123456789L));
    }

    @Test
    public void testGetNonExistentList() {
        assertThrows(EmptyResultDataAccessException.class, () -> dao.getList(123456789L));
    }

    @Test
    public void testGetNonExistentListItem() {
        assertThrows(EmptyResultDataAccessException.class, () -> dao.getListItem(123456789L));
    }

    @Test
    public void testGetNonExistentUserBill() {
        assertThrows(EmptyResultDataAccessException.class, () -> dao.getUserBill(123456789L));
    }

    @Test
    public void testRemoveUserFromNonExistentGroup() {
        dao.removeUserFromGroup("lucas");
        Group g = dao.getUser("lucas").getGroup();
        assertNull(g);
        dao.removeUserFromGroup("lucas");
        Group g1 = dao.getUser("lucas").getGroup();
        assertNull(g1);
    }

    @Test
    public void testAddUserToNonExistentGroup() {
        dao.createUser("test", "1234");
        assertThrows(EmptyResultDataAccessException.class, () -> dao.addUserToGroup("test", "nonExistent"));
    }

    @Test
    public void testAddNonExistentBillToList() {
        User user = dao.getUser("lucas");
        List list = new List();
        list.setName("testlist");
        list.setDescription("testdescription");
        list.setOwner(user);
        list.setCreateTime(1648727482L);
        List listReturned = dao.createListAndReturnList(list);
        assertThrows(UncategorizedSQLException.class, () -> dao.addBillToList(listReturned.getListId(), 123456789L));
    }

    @Test
    public void testAddBillToNonExistentList() {
        User u = dao.getUser("lucas");
        Bill b = new Bill();
        b.setName("test");
        b.setAmount(10.20);
        b.setCreateTime(1648727482L);
        b.setDescription("testdescription");
        b.setPaymentMethod("Cash");
        b.setOwner(u);
        Bill billReturned = dao.createBillAndReturnBill(b);
        int rowsAffected = dao.addBillToList(123456789L, billReturned.getBillId());
        assertEquals(0, rowsAffected);
    }

    @Test
    public void testGetListItemsForNonExistentList() {
        ArrayList<ListItem> listItems = dao.getListItemsForList(123456789L);
        assertEquals(0, listItems.size());
    }

    @Test
    public void testGetUserBillsForNonExistentBill() {
        ArrayList<UserBill> ubs = dao.getUserBillsForBill(123456789L);
        assertEquals(0, ubs.size());
    }

    @Test
    public void testGetUserBillsForNonExistentUser() {
        ArrayList<UserBill> ubs = dao.getUserBillsForUser("nonexistent");
        assertEquals(0, ubs.size());
    }

    @Test
    public void testSetUserBillToPaidForNonExistentBill() {
        int rowsAffected = dao.setUserBillToPaid(123456789L, "lucas");
        assertEquals(0, rowsAffected);
    }

    @Test
    public void testSetUserBillToPaidForUnrelatedUser() {
        User user = dao.getUser("lucas");
        Bill bill = new Bill();
        bill.setName("testbill");
        bill.setDescription("testdescription");
        bill.setAmount(12.12d);
        bill.setPaymentMethod("Cash");
        bill.setOwner(user);
        bill.setCreateTime(1648727482L);
        Bill billReturned = dao.createBillAndReturnBill(bill);

        User user1 = dao.getUser("leopold");
        UserBill userBill = new UserBill();
        userBill.setBill(billReturned);
        userBill.setUser(user1);
        userBill.setPercentage(0.5d);
        userBill.setPaid(false);

        int rowsAffected = dao.setUserBillToPaid(userBill.getUserBillId(), "nonexistent");
        assertEquals(0, rowsAffected);
    }
}
