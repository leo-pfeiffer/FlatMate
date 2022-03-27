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

    // GROUPS ====================

    /**
     * Given a group ID, return the group with that ID
     *
     * @param groupId The id of the group to retrieve.
     * @return A Group object.
     */
    public Group getGroup(Long groupId) {
        if (groupId == null) return null;
        String sql = "SELECT * FROM 'group' WHERE group_id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Group(
                rs.getLong("group_id"),
                rs.getString("name")
        ), groupId);
    }

    /**
     * Create a new group with the given name
     *
     * @param name The name of the group to be created.
     * @return The number of rows affected by the update.
     */
    public int createGroup(String name) {
        String sql = "INSERT INTO 'group' (name) VALUES (?)";
        return jdbcTemplate.update(sql, name);
    }

    /**
     * This function returns an ArrayList of Group objects
     *
     * @return An ArrayList of Group objects.
     */
    public ArrayList<Group> getAllGroups() {
        String sql = "SELECT * FROM 'group'";
        return (ArrayList<Group>) jdbcTemplate.query(sql, (rs, rowNum) -> new Group(
                rs.getLong("group_id"),
                rs.getString("name")
        ));
    }

    // USERS ====================
    /**
     * Get a user by username
     *
     * @param username The username of the user to retrieve.
     * @return A User object.
     */
    public User getUser(String username) {
        if (username == null) return null;
        String sql = "SELECT * FROM 'user' WHERE username = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new User(
                rs.getString("username"),
                rs.getString("password"),
                getGroupForUser(rs.getString("username")),
                UserRole.valueOf(rs.getString("role")),
                rs.getBoolean("enabled")
        ), username);
    }

    /**
     * Create a new user with the given username and password
     *
     * @param username the username of the user
     * @param password The password of the user.
     * @return The number of rows affected.
     */
    public int createUser(String username, String password) {
        // new user is always USER
        String role = UserRole.USER.getRole();
        String sql = "INSERT INTO 'user' (username, password, role, enabled) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql, username, password, role, true);
    }

    /**
     * Get all users from the database
     *
     * @return An ArrayList of User objects.
     */
    public ArrayList<User> getAllUsers() {
        String sql = "SELECT * FROM 'user'";
        return (ArrayList<User>) jdbcTemplate.query(sql, (rs, rowNum) -> new User(
                rs.getString("username"),
                rs.getString("password"),
                getGroupForUser(rs.getString("username")),
                UserRole.valueOf(rs.getString("role")),
                rs.getBoolean("enabled")
        ));
    }

    /**
     * Get the group for a user
     *
     * @param username The username of the user to get the group for.
     * @return A Group object.
     */
    public Group getGroupForUser(String username) {
        if (username == null) return null;
        User user = getUser(username);
        return user == null ? null : user.getGroup();
    }

    /**
     * Add a user to a group
     *
     * @param username The username of the user to add to the group.
     * @param groupId The id of the group to add the user to.
     * @return The number of rows affected by the update.
     */
    public int addUserToGroup(String username, Long groupId) {
        String sql = "UPDATE 'user' SET 'group_id' = ? WHERE username = ?";
        return jdbcTemplate.update(sql, groupId, username);
    }

    /**
     * Remove a user from a group
     *
     * @param username The username of the user to be removed from the group.
     * @return The number of rows affected by the update.
     */
    public int removeUserFromGroup(String username) {
        String sql = "UPDATE 'user' SET 'group_id' = NULL WHERE username = ?";
        return jdbcTemplate.update(sql, username);
    }

    /**
     * Given a username, set the role of the user to ADMIN
     *
     * @param username The username of the user to be updated.
     * @return The number of rows that were updated.
     */
    public int setRoleToAdmin(String username) {
        String sql = "UPDATE 'user' SET 'role' = ? WHERE username = ?";
        return jdbcTemplate.update(sql, UserRole.ADMIN.getRole(), username);
    }

    /**
     * Given a username, set the role of the user to USER
     *
     * @param username The username of the user to update.
     * @return The number of rows that were updated.
     */
    public int setRoleToUser(String username) {
        String sql = "UPDATE 'user' SET 'role' = ? WHERE username = ?";
        return jdbcTemplate.update(sql, UserRole.USER.getRole(), username);
    }

    // BILL ====================
    /**
     * Given a bill id, return the bill with the given id
     *
     * @param billId The id of the bill to get.
     * @return A Bill object.
     */
    public Bill getBill(Long billId) {
        if (billId == null) return null;
        String sql = "SELECT * FROM 'bill' WHERE bill_id = ?";
        return jdbcTemplate.queryForObject(sql, ((rs, rowNum) -> new Bill(
                rs.getLong("bill_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDouble("amount"),
                rs.getString("payment_method"),
                getUser(rs.getString("owner"))
        )), billId);
    }

    /**
     * Create a new bill
     *
     * @param bill The bill object that we want to insert into the database.
     * @return The number of rows affected by the update.
     */
    public int createBill(Bill bill) {
        assert bill != null;
        String sql = "INSERT INTO 'bill' (name, description, amount, payment_method, owner) VALUES (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                bill.getName(),
                bill.getDescription(),
                bill.getAmount(),
                bill.getPaymentMethod(),
                bill.getOwner().getUsername());
    }

    public ArrayList<Bill> getAllBillsForGroup(Long groupId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // all bills where the user is listed as a participant
    public ArrayList<Bill> getAllBillsForUser(String username) {
        throw new UnsupportedOperationException("Not implemented yet");
    }


    // LIST ====================
    /**
     * Given a listId, return the list with that id
     *
     * @param listId The id of the list to get.
     * @return A List object.
     */
    public List getList(Long listId) {
        String sql = "SELECT * FROM 'bill' WHERE bill_id = ?";
        return jdbcTemplate.queryForObject(sql, ((rs, rowNum) -> new List(
                rs.getLong("list_id"),
                rs.getString("name"),
                rs.getString("description"),
                getUser(rs.getString("owner")),
                getBill(rs.getLong("bill_id"))
        )), listId);
    }

    /**
     * Create a new list in the database
     *
     * @param list The list object that we want to insert into the database.
     * @return The number of rows affected by the update.
     */
    public int createList(List list) {
        assert list != null;
        String sql = "INSERT INTO 'list' (name, description, owner, bill_id) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                list.getName(),
                list.getDescription(),
                list.getOwner().getUsername(),
                list.getBill().getBillId());
    }

    public ArrayList<List> getAllListsForGroup(Long groupId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // LIST ITEM =============================================================

    /**
     * Get a list item by its id
     *
     * @param listItemId The id of the list item to be retrieved.
     * @return A ListItem object.
     */
    public ListItem getListItem(Long listItemId) {
        String sql = "SELECT * FROM 'list_item' WHERE list_item_id = ?";
        return jdbcTemplate.queryForObject(sql, ((rs, rowNum) -> new ListItem(
                rs.getLong("list_item_id"),
                rs.getString("name"),
                getList(rs.getLong("list_id"))
        )), listItemId);
    }

    /**
     * Get all the list items for a given list
     *
     * @param listId The id of the list we want to get the list items for.
     * @return An ArrayList of ListItems.
     */
    public ArrayList<ListItem> getListItemsForList(Long listId) {
        String sql = "SELECT * FROM 'list_item' WHERE list_id = ?";
        return (ArrayList<ListItem>) jdbcTemplate.query(sql, ((rs, rowNum) -> new ListItem(
                rs.getLong("list_item_id"),
                rs.getString("name"),
                getList(listId)
        )), listId);
    }

    /**
     * Create a new list item
     *
     * @param listItem The ListItem object to be inserted.
     * @return The number of rows affected by the update.
     */
    public int createListItem(ListItem listItem) {
        String sql = "INSERT INTO 'list_item' (name, list_id) VALUES (?, ?)";
        return jdbcTemplate.update(sql,
                listItem.getName(),
                listItem.getList().getListId());
    }

    // USER BILL =============================================================

    /**
     * Create a new user bill
     *
     * @param userBill The UserBill object to be inserted.
     * @return The number of rows affected by the update.
     */
    public int createUserBill(UserBill userBill) {
        assert userBill != null;
        String sql = "INSERT INTO 'user_bill' (username, bill_id, percentage, paid) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                userBill.getUser().getUsername(),
                userBill.getBill().getBillId(),
                userBill.getPercentage(),
                userBill.isPaid());
    }

    /**
     * Given a userBillId, return the UserBill object with that id
     *
     * @param userBillId The id of the userBill to get.
     * @return A UserBill object.
     */
    public UserBill getUserBill(Long userBillId) {
        if (userBillId == null) return null;
        String sql = "SELECT * FROM 'user_bill' WHERE user_bill_id = ?";
        return jdbcTemplate.queryForObject(sql, ((rs, rowNum) -> new UserBill(
                rs.getLong("user_bill_id"),
                getUser(rs.getString("username")),
                getBill(rs.getLong("bill_id")),
                rs.getDouble("percentage"),
                rs.getBoolean("paid")
        )), userBillId);
    }

    /**
     * Given a bill id, return all the user bills for that bill
     *
     * @param billId The id of the bill to get the user bills for.
     * @return An ArrayList of UserBill objects.
     */
    public ArrayList<UserBill> getUserBillsForBill(Long billId) {
        String sql = "SELECT * FROM 'user_bill' WHERE bill_id = ?";
        return (ArrayList<UserBill>) jdbcTemplate.query(sql, ((rs, rowNum) -> new UserBill(
                rs.getLong("user_bill_id"),
                getUser(rs.getString("username")),
                getBill(rs.getLong("bill_id")),
                rs.getDouble("percentage"),
                rs.getBoolean("paid")
        )), billId);
    }

    /**
     * Update the paid field of the user_bill table to true if the billId and username match
     *
     * @param billId The id of the bill to be paid.
     * @param username The username of the user who is paying the bill.
     * @return The number of rows updated.
     */
    public int setUserBillToPaid(Long billId, String username) {
        String sql = "UPDATE 'user_bill' SET paid = ? WHERE user_bill_id = ? AND username = ?";
        return jdbcTemplate.update(sql, true, billId, username);
    }

}
