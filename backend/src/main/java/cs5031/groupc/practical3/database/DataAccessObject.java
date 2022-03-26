package cs5031.groupc.practical3.database;

import java.util.ArrayList;
import cs5031.groupc.practical3.model.Bill;
import cs5031.groupc.practical3.model.Group;
import cs5031.groupc.practical3.model.List;
import cs5031.groupc.practical3.model.ListItem;
import cs5031.groupc.practical3.model.User;
import cs5031.groupc.practical3.model.UserBill;
import cs5031.groupc.practical3.vo.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DataAccessObject {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public ArrayList<Group> getAllGroups() {
        String sql = "SELECT * FROM 'group'";
        return (ArrayList<Group>) jdbcTemplate.query(sql, (rs, rowNum) -> new Group(
                rs.getLong("group_id"),
                rs.getString("name")
        ));
    }

    public Group getGroup(Long groupId) {
        String sql = "SELECT * FROM 'group' WHERE group_id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Group(
                rs.getLong("group_id"),
                rs.getString("name")
        ), groupId);
    }

    public Long getGroupIdForUser(String username) {
        String sql = "SELECT group_id FROM 'user' WHERE username = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, username);
    }

    public User getUser(String username) {
        Long groupId = getGroupIdForUser(username);
        Group group;
        if (groupId != null) {
            group = getGroup(groupId);
        } else {
            group = null;
        }
        String sql = "SELECT * FROM 'user' WHERE username = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new User(
                rs.getString("username"),
                rs.getString("password"),
                group,
                UserRole.valueOf(rs.getString("role")),
                rs.getBoolean("enabled")
        ), username);
    }

    public int createUser(String username, String password) {
        // new user is always USER
        String role = UserRole.USER.getRole();
        String sql = "INSERT INTO 'user' (username, password, role, enabled) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql, username, password, role, true);
    }

    public int createGroup(String name) {
        String sql = "INSERT INTO 'group' (name) VALUES (?)";
        return jdbcTemplate.update(sql, name);
    }

    public int addUserToGroup(String username, Long groupId) {
        String sql = "UPDATE 'user' SET 'group_id' = ? WHERE username = ?";
        return jdbcTemplate.update(sql, groupId, username);
    }

    public int removeUserFromGroup(String username) {
        String sql = "UPDATE 'user' SET 'group_id' = NULL WHERE username = ?";
        return jdbcTemplate.update(sql, username);
    }

    public String getUserIdForBill(Long billId) {
        String sql = "SELECT owner FROM 'bill' WHERE bill_id = ?";
        return jdbcTemplate.queryForObject(sql, String.class, billId);
    }

    public Bill getBill(Long billId) {
        String username = getUserIdForBill(billId);
        User owner = getUser(username);

        String sql = "SELECT * FROM 'bill' WHERE bill_id = ?";
        return jdbcTemplate.queryForObject(sql, ((rs, rowNum) -> new Bill(
                rs.getLong("bill_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDouble("amount"),
                rs.getString("payment_method"),
                owner
        )), billId);
    }

    public String getUserIdForList(Long listId) {
        String sql = "SELECT owner FROM 'list' WHERE list_id = ?";
        return jdbcTemplate.queryForObject(sql, String.class, listId);
    }

    public Long getBillIdForList(Long listId) {
        String sql = "SELECT bill_id FROM 'list' WHERE list_id = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, listId);
    }

    public List getList(Long listId) {
        String username = getUserIdForList(listId);
        assert username != null;
        User owner = getUser(username);

        Long billId = getBillIdForList(listId);
        Bill bill;

        if (billId != null) {
            bill = getBill(billId);
        } else {
            bill = null;
        }

        String sql = "SELECT * FROM 'bill' WHERE bill_id = ?";
        return jdbcTemplate.queryForObject(sql, ((rs, rowNum) -> new List(
                rs.getLong("list_id"),
                rs.getString("name"),
                rs.getString("description"),
                owner,
                bill
        )), listId);
    }

    public Bill getBillForList(Long listId) {
        String sql = "SELECT bill_id FROM 'list' WHERE list_id = ?";
        Long billId = jdbcTemplate.queryForObject(sql, Long.class, listId);
        return getBill(billId);
    }

    public ArrayList<ListItem> getListItemsForList(Long listId) {
        List list = getList(listId);
        String sql = "SELECT * FROM 'list_item' WHERE list_id = ?";
        return (ArrayList<ListItem>) jdbcTemplate.query(sql, ((rs, rowNum) -> new ListItem(
                rs.getLong("list_item_id"),
                rs.getString("name"),
                list
        )), listId);
    }

    public String getUsernameForUserBill(Long billId) {
        String sql = "SELECT username FROM 'user_bill' WHERE user_bill_id = ?";
        return jdbcTemplate.queryForObject(sql, String.class, billId);
    }

    public Long getBillIdForUserBill(Long userBillId) {
        String sql = "SELECT bill_id FROM 'user_bill' WHERE user_bill_id = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, userBillId);
    }

    public UserBill getUserBill(Long userBillId) {
        String username = getUsernameForUserBill(userBillId);
        assert username != null;
        User user = getUser(username);

        Long billId = getBillIdForUserBill(userBillId);
        assert billId != null;
        Bill bill = getBill(billId);

        String sql = "SELECT * FROM 'user_bill' WHERE user_bill_id = ?";

        return jdbcTemplate.queryForObject(sql, ((rs, rowNum) -> new UserBill(
                rs.getLong("user_bill_id"),
                user,
                bill,
                rs.getDouble("percentage"),
                rs.getBoolean("paid")
        )), userBillId);
    }

}
