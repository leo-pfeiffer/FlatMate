package cs5031.groupc.practical3.database;

import java.util.ArrayList;
import cs5031.groupc.practical3.mapper.BillMapper;
import cs5031.groupc.practical3.mapper.GroupMapper;
import cs5031.groupc.practical3.mapper.ListItemMapper;
import cs5031.groupc.practical3.mapper.ListMapper;
import cs5031.groupc.practical3.mapper.UserMapper;
import cs5031.groupc.practical3.model.Bill;
import cs5031.groupc.practical3.model.Group;
import cs5031.groupc.practical3.model.List;
import cs5031.groupc.practical3.model.ListItem;
import cs5031.groupc.practical3.model.User;
import cs5031.groupc.practical3.vo.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DataAccessObject {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public User getUser(String username) {
        String sql = "SELECT * FROM 'user' WHERE username = ?";
        return jdbcTemplate.queryForObject(sql, new UserMapper(), username);
    }

    public int createUser(String username, String password) {
        // new user is always USER
        String role = UserRole.USER.getRole();
        String sql = "INSERT INTO 'user' (username, password, role) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, username, password, role);
    }

    public int createGroup(String name) {
        String sql = "INSERT INTO 'group' (name) VALUES (?)";
        return jdbcTemplate.update(sql, name);
    }

    public Group getGroup(String groupName) {
        String sql = "SELECT * FROM 'group' WHERE name = ?";
        return jdbcTemplate.queryForObject(sql, new GroupMapper(), groupName);
    }

    public int addUserToGroup(String username, String groupName) {
        Long groupId = getGroup(groupName).getGroupId();
        assert groupId != null;
        String sql = "UPDATE 'user' SET 'group_id' = ? WHERE username = ?";
        return jdbcTemplate.update(sql, groupId, username);
    }

    public int removeUserFromGroup(String username) {
        String sql = "UPDATE 'user' SET 'group_id' = NULL WHERE username = ?";
        return jdbcTemplate.update(sql, username);
    }

    public Bill getBill(Long billId) {
        String sql = "SELECT * FROM 'bill' WHERE 'bill_id' = ?";
        return jdbcTemplate.queryForObject(sql, new BillMapper(), billId);
    }

    public List getList(int listId) {
        String sql = "SELECT * FROM 'bill' WHERE 'list_id' = ?";
        List list = jdbcTemplate.queryForObject(sql, new ListMapper(), listId);
        assert list != null;
        ArrayList<ListItem> listItems = getListItemsForList(list.getListId());
        // todo add list items to list
        return list;
    }

    public Bill getBillForList(int listId) {
        String sql = "SELECT 'bill_id' FROM 'list' WHERE 'list_id' = ?";
        Long billId = jdbcTemplate.queryForObject(sql, Long.class, listId);
        return getBill(billId);
    }

    public ArrayList<ListItem> getListItemsForList(Long listId) {
        String sql = "SELECT * FROM 'list_item' WHERE 'list_id' = ?";
        return (ArrayList<ListItem>) jdbcTemplate.query(sql, new ListItemMapper(), listId);
    }

}
