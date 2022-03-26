package cs5031.groupc.practical3.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import cs5031.groupc.practical3.model.ListItem;
import org.springframework.jdbc.core.RowMapper;

public class ListItemMapper implements RowMapper<ListItem> {

    @Override
    public ListItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        ListItem listItem = new ListItem();
        listItem.setListItemId(rs.getLong("list_item_id"));
        listItem.setName(rs.getString("name"));
        listItem.setListId(rs.getLong("list_id"));
        return listItem;
    }

}
