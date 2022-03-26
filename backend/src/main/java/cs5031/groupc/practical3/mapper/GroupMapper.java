package cs5031.groupc.practical3.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import cs5031.groupc.practical3.model.Group;
import org.springframework.jdbc.core.RowMapper;

public class GroupMapper implements RowMapper<Group> {

    @Override
    public Group mapRow(ResultSet rs, int rowNum) throws SQLException {
        Group group = new Group();
        group.setGroupId(rs.getLong("group_id"));
        group.setName(rs.getString("name"));
        return group;
    }
}