package cs5031.groupc.practical3.database;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import cs5031.groupc.practical3.model.Bill;
import cs5031.groupc.practical3.model.Group;
import cs5031.groupc.practical3.model.List;
import cs5031.groupc.practical3.model.ListItem;
import cs5031.groupc.practical3.model.User;
import cs5031.groupc.practical3.model.UserBill;
import cs5031.groupc.practical3.vo.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class DataAccessObject {

    /**
     * The JDBC Template.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * The constructor.
     *
     * @param jdbcTemplate The JDBC template.
     */
    @Autowired
    public DataAccessObject(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // GROUPS ====================

    /**
     * Given a group ID, return the group with that ID.
     *
     * @param groupId The id of the group to retrieve.
     * @return A Group object.
     */
    public Group getGroup(final Long groupId) {
        if (groupId == null || groupId == 0L) {
            return null;
        }
        String sql = "SELECT * FROM 'group' WHERE group_id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Group(
                rs.getLong("group_id"),
                rs.getString("name")
        ), groupId);
    }

    /**
     * Given a group name, return the group object.
     *
     * @param name The name of the group to retrieve.
     * @return A Group object.
     */
    public Group getGroup(final String name) {
        if (name == null) {
            return null;
        }
        String sql = "SELECT * FROM 'group' WHERE name = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Group(
                rs.getLong("group_id"),
                rs.getString("name")
        ), name);
    }

    /**
     * Create a new group with the given name.
     *
     * @param name The name of the group to be created.
     * @return The number of rows affected by the update.
     */
    public int createGroup(final String name) {
        String sql = "INSERT INTO 'group' (name) VALUES (?)";
        return jdbcTemplate.update(sql, name);
    }

    /**
     * This function returns an ArrayList of Group objects.
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
     * Get a user by username.
     *
     * @param username The username of the user to retrieve.
     * @return A User object.
     */
    public User getUser(final String username) {
        if (username == null) {
            return null;
        }
        String sql = "SELECT * FROM 'user' WHERE username = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new User(
                rs.getString("username"),
                rs.getString("password"),
                getGroup(rs.getLong("group_id")),
                UserRole.valueOf(rs.getString("role")),
                rs.getBoolean("enabled")
        ), username);
    }

    /**
     * Create a new user with the given username and password.
     *
     * @param username the username of the user
     * @param password The password of the user.
     * @return The number of rows affected.
     */
    public int createUser(final String username, final String password) {
        // new user is always USER
        String role = UserRole.USER.getRole();
        String sql = "INSERT INTO 'user' (username, password, role, enabled) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql, username, password, role, true);
    }

    /**
     * Get all users from the database.
     *
     * @return An ArrayList of User objects.
     */
    public ArrayList<User> getAllUsers() {
        String sql = "SELECT * FROM 'user'";
        return (ArrayList<User>) jdbcTemplate.query(sql, (rs, rowNum) -> new User(
                rs.getString("username"),
                rs.getString("password"),
                getGroup(rs.getLong("group_id")),
                UserRole.valueOf(rs.getString("role")),
                rs.getBoolean("enabled")
        ));
    }

    /**
     * Add a user to a group.
     *
     * @param username The username of the user to add to the group.
     * @param groupId  The id of the group to add the user to.
     * @return The number of rows affected by the update.
     */
    public int addUserToGroup(final String username, final Long groupId) {
        String sql = "UPDATE 'user' SET 'group_id' = ? WHERE username = ?";
        return jdbcTemplate.update(sql, groupId, username);
    }


    /**
     * Add a user to a group by group name.
     *
     * @param username  The username of the user to add to the group.
     * @param groupName The name of the group to add the user to.
     * @return The number of rows affected by the update.
     */
    public int addUserToGroup(final String username, final String groupName) {
        Group group = getGroup(groupName);
        String sql = "UPDATE 'user' SET 'group_id' = ? WHERE username = ?";
        return jdbcTemplate.update(sql, group.getGroupId(), username);
    }

    /**
     * Update the username of a user with the given old username to the new username.
     *
     * @param oldUsername The username of the user whose username you want to change.
     * @param newUsername The new username to be set.
     * @return The number of rows that were updated.
     */
    public int changeUserName(final String oldUsername, final String newUsername) {
        String sql = "UPDATE 'user' SET 'username' = ? WHERE username = ?";
        return jdbcTemplate.update(sql, newUsername, oldUsername);
    }

    /**
     * Update the enabled field of the user with the given username to the given value.
     *
     * @param username The username of the user to be updated.
     * @param enabled  The boolean value that will be set to the user's enabled field.
     * @return The number of rows that were updated.
     */
    public int changeUserEnabled(final String username, final boolean enabled) {
        String sql = "UPDATE 'user' SET 'enabled' = ? WHERE username = ?";
        return jdbcTemplate.update(sql, enabled, username);
    }

    /**
     * Remove a user from a group.
     *
     * @param username The username of the user to be removed from the group.
     * @return The number of rows affected by the update.
     */
    public int removeUserFromGroup(final String username) {
        String sql = "UPDATE 'user' SET 'group_id' = NULL WHERE username = ?";
        return jdbcTemplate.update(sql, username);
    }

    /**
     * Given a username, set the role of the user to ADMIN.
     *
     * @param username The username of the user to be updated.
     * @return The number of rows that were updated.
     */
    public int setRoleToAdmin(final String username) {
        String sql = "UPDATE 'user' SET 'role' = ? WHERE username = ?";
        return jdbcTemplate.update(sql, UserRole.ADMIN.getRole(), username);
    }

    /**
     * Given a username, set the role of the user to USER.
     *
     * @param username The username of the user to update.
     * @return The number of rows that were updated.
     */
    public int setRoleToUser(final String username) {
        String sql = "UPDATE 'user' SET 'role' = ? WHERE username = ?";
        return jdbcTemplate.update(sql, UserRole.USER.getRole(), username);
    }

    // BILL ====================

    /**
     * Given a bill id, return the bill with the given id.
     *
     * @param billId The id of the bill to get.
     * @return A Bill object.
     */
    public Bill getBill(final Long billId) {
        if (billId == null || billId == 0L) {
            return null;
        }
        String sql = "SELECT * FROM 'bill' WHERE bill_id = ?";
        return jdbcTemplate.queryForObject(sql, ((rs, rowNum) -> new Bill(
                rs.getLong("bill_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDouble("amount"),
                rs.getString("payment_method"),
                getUser(rs.getString("owner")),
                rs.getLong("create_time")
        )), billId);
    }

    /**
     * Create a new bill.
     *
     * @param bill The bill object that we want to insert into the database.
     * @return The number of rows affected by the update.
     */
    public int createBill(final Bill bill) {
        assert bill != null;
        String sql = "INSERT INTO 'bill' (name, description, amount, payment_method, owner, create_time) VALUES (?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                bill.getName(),
                bill.getDescription(),
                bill.getAmount(),
                bill.getPaymentMethod(),
                bill.getOwner().getUsername(),
                bill.getCreateTime());
    }

    /**
     * Creates a Bill and returns the bill.
     *
     * @param bill The (incomplete) bill object.
     * @return The complete bill object.
     */
    public Bill createBillAndReturnBill(final Bill bill) {
        assert bill != null;
        String sql = "INSERT INTO 'bill' (name, description, amount, payment_method, owner, create_time) VALUES (?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, bill.getName());
            ps.setString(2, bill.getDescription());
            ps.setDouble(3, bill.getAmount());
            ps.setString(4, bill.getPaymentMethod());
            ps.setString(5, bill.getOwner().getUsername());
            ps.setLong(6, bill.getCreateTime());
            return ps;
        }, keyHolder);

        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return this.getBill(id);
    }

    /**
     * Given a group ID, return all the bills for that group.
     *
     * @param groupId The id of the group that the bill is associated with.
     * @return An ArrayList of Bills.
     */
    public ArrayList<Bill> getBillsForGroup(final Long groupId) {
        assert groupId != null;
        String sql = "SELECT * FROM 'bill' INNER JOIN user u ON bill.owner = u.username WHERE u.group_id = ?";
        return (ArrayList<Bill>) jdbcTemplate.query(sql, (rs, rowNum) -> new Bill(
                rs.getLong("bill_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDouble("amount"),
                rs.getString("payment_method"),
                getUser(rs.getString("owner")),
                rs.getLong("create_time")
        ), groupId);
    }

    /**
     * Given a username, return all the bills that the user is a participant of.
     *
     * @param username The username of the user who owns the bill.
     * @return An ArrayList of Bill objects.
     */
    public ArrayList<Bill> getBillsForUser(final String username) {
        assert username != null;
        String sql = "SELECT b.bill_id as bill_id, name, description, amount, payment_method, owner, create_time "
                + "FROM user_bill "
                + "INNER JOIN bill b ON user_bill.bill_id = b.bill_id "
                + "WHERE user_bill.username = ?";
        return (ArrayList<Bill>) jdbcTemplate.query(sql, (rs, rowNum) -> new Bill(
                rs.getLong("bill_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDouble("amount"),
                rs.getString("payment_method"),
                getUser(rs.getString("owner")),
                rs.getLong("create_time")
        ), username);
    }


    // LIST ====================

    /**
     * Given a listId, return the list with that id.
     *
     * @param listId The id of the list to get.
     * @return A List object.
     */
    public List getList(final Long listId) {
        String sql = "SELECT * FROM 'list' WHERE list_id = ?";
        return jdbcTemplate.queryForObject(sql, ((rs, rowNum) -> new List(
                rs.getLong("list_id"),
                rs.getString("name"),
                rs.getString("description"),
                getUser(rs.getString("owner")),
                getBill(rs.getLong("bill_id")),
                rs.getLong("create_time")
        )), listId);
    }

    /**
     * Create a new list in the database.
     *
     * @param list The list object that we want to insert into the database.
     * @return The number of rows affected by the update.
     */
    public int createList(final List list) {
        assert list != null;
        String sql = "INSERT INTO 'list' (name, description, owner, bill_id, create_time) VALUES (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                list.getName(),
                list.getDescription(),
                list.getOwner().getUsername(),
                list.getBill() == null ? null : list.getBill().getBillId(),
                list.getCreateTime());
    }

    /**
     * Creates a list and returns it after creation.
     *
     * @param list The (incomplete) Listobject to be added to the database.
     * @return Returns the complete list object.
     */
    public List createListAndReturnList(final List list) {
        assert list != null;
        String sql = "INSERT INTO 'list' (name, description, owner, bill_id, create_time) VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, list.getName());
            ps.setString(2, list.getDescription());
            ps.setString(3, list.getOwner().getUsername());
            if (list.getBill() == null) {
                ps.setNull(4, 0);
            } else {
                ps.setLong(4, list.getBill().getBillId());
            }
            ps.setLong(5, list.getCreateTime());
            return ps;
        }, keyHolder);

        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return this.getList(id);
    }

    /**
     * Get all the lists for a group.
     *
     * @param groupId The id of the group that the user is a member of.
     * @return A List object.
     */
    public ArrayList<List> getListsForGroup(final Long groupId) {
        assert groupId != null;
        String sql = "SELECT list_id, name, description, owner, bill_id, create_time "
                + "FROM list "
                + "INNER JOIN user ON list.owner = user.username "
                + "WHERE user.group_id = ?";
        return (ArrayList<List>) jdbcTemplate.query(sql, (rs, rowNum) -> new List(
                rs.getLong("list_id"),
                rs.getString("name"),
                rs.getString("description"),
                getUser(rs.getString("owner")),
                getBill(rs.getLong("bill_id")),
                rs.getLong("create_time")
        ), groupId);
    }

    /**
     * Adds a bill to a list.
     *
     * @param listId The id of the ist to which a bill shall be added.
     * @param billId The id of the bill that shall be added.
     * @return The number of rows affected by the update.
     */
    public int addBillToList(final Long listId, final Long billId) {
        String sql = "UPDATE 'list' SET bill_id = ? WHERE list_id = ?";
        return jdbcTemplate.update(sql, billId, listId);
    }

    // LIST ITEM =============================================================

    /**
     * Get a list item by its id.
     *
     * @param listItemId The id of the list item to be retrieved.
     * @return A ListItem object.
     */
    public ListItem getListItem(final Long listItemId) {
        String sql = "SELECT * FROM 'list_item' WHERE list_item_id = ?";
        return jdbcTemplate.queryForObject(sql, ((rs, rowNum) -> new ListItem(
                rs.getLong("list_item_id"),
                rs.getString("name"),
                getList(rs.getLong("list_id"))
        )), listItemId);
    }

    /**
     * Get all the list items for a given list.
     *
     * @param listId The id of the list we want to get the list items for.
     * @return An ArrayList of ListItems.
     */
    public ArrayList<ListItem> getListItemsForList(final Long listId) {
        String sql = "SELECT * FROM 'list_item' WHERE list_id = ?";
        return (ArrayList<ListItem>) jdbcTemplate.query(sql, ((rs, rowNum) -> new ListItem(
                rs.getLong("list_item_id"),
                rs.getString("name"),
                getList(listId)
        )), listId);
    }

    /**
     * Create a new list item.
     *
     * @param listItem The ListItem object to be inserted.
     * @return The number of rows affected by the update.
     */
    public int createListItem(final ListItem listItem) {
        String sql = "INSERT INTO 'list_item' (name, list_id) VALUES (?, ?)";
        return jdbcTemplate.update(sql,
                listItem.getName(),
                listItem.getList().getListId());
    }

    // USER BILL =============================================================

    /**
     * Create a new user bill.
     *
     * @param userBill The UserBill object to be inserted.
     * @return The number of rows affected by the update.
     */
    public int createUserBill(final UserBill userBill) {
        assert userBill != null;
        String sql = "INSERT INTO 'user_bill' (username, bill_id, percentage, paid) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                userBill.getUser().getUsername(),
                userBill.getBill().getBillId(),
                userBill.getPercentage(),
                userBill.isPaid());
    }

    /**
     * Given a userBillId, return the UserBill object with that id.
     *
     * @param userBillId The id of the userBill to get.
     * @return A UserBill object.
     */
    public UserBill getUserBill(final Long userBillId) {
        if (userBillId == null || userBillId == 0L) {
            return null;
        }
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
     * Given a bill id, return all the user bills for that bill.
     *
     * @param billId The id of the bill to get the user bills for.
     * @return An ArrayList of UserBill objects.
     */
    public ArrayList<UserBill> getUserBillsForBill(final Long billId) {
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
     * Given a username, return all user bills for that username. For testing.
     *
     * @param username The username of the user to get the user bills for.
     * @return An ArrayList of UserBill objects.
     */
    public ArrayList<UserBill> getUserBillsForUser(final String username) {
        String sql = "SELECT * FROM 'user_bill' WHERE username = ?";
        return (ArrayList<UserBill>) jdbcTemplate.query(sql, ((rs, rowNum) -> new UserBill(
                rs.getLong("user_bill_id"),
                getUser(rs.getString("username")),
                getBill(rs.getLong("bill_id")),
                rs.getDouble("percentage"),
                rs.getBoolean("paid")
        )), username);
    }


    /**
     * Update the paid field of the user_bill table to true if the billId and username match.
     *
     * @param billId   The id of the bill to be paid.
     * @param username The username of the user who is paying the bill.
     * @return The number of rows updated.
     */
    public int setUserBillToPaid(final Long billId, final String username) {
        String sql = "UPDATE 'user_bill' SET paid = ? WHERE user_bill_id = ? AND username = ?";
        return jdbcTemplate.update(sql, true, billId, username);
    }

    /**
     * Inserts a new row into the experiment_tracker table.
     *
     * @param name    The name of the experiment.
     * @param variant The variant of the experiment.
     * @param event   The event that occurred.
     * @return The number of rows affected by the insert.
     */
    public int insertExperiment(final String name, final String variant, final String event) {
        String sql = "INSERT INTO 'experiment_tracker' (experiment, variant, event) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, name, variant, event);
    }

    /**
     * Get all experiments.
     *
     * @return ArrayList of experiments as hash maps
     */
    public ArrayList<HashMap<String, String>> getAllExperiments() {
        String sql = "SELECT * FROM 'experiment_tracker'";
        return (ArrayList<HashMap<String, String>>) jdbcTemplate.query(sql, ((rs, rowNum) -> {
            HashMap<String, String> hm = new HashMap<>();
            hm.put("experiment", rs.getString("experiment"));
            hm.put("variant", rs.getString("variant"));
            hm.put("event", rs.getString("event"));
            return hm;
        }));
    }
}
