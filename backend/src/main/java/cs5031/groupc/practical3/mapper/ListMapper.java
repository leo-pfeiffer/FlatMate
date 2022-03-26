package cs5031.groupc.practical3.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import cs5031.groupc.practical3.model.List;
import org.springframework.jdbc.core.RowMapper;

public class ListMapper implements RowMapper<List> {

    @Override
    public List mapRow(ResultSet rs, int rowNum) throws SQLException {
        List list = new List();
        list.setListId(rs.getLong("list_id"));
        list.setName(rs.getString("name"));
        list.setDescription(rs.getString("description"));
        list.setOwner(rs.getString("owner"));
        list.setBill(rs.getLong("bill_id"));
        return list;
    }

}
