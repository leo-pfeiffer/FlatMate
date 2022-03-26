package cs5031.groupc.practical3.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import cs5031.groupc.practical3.model.Bill;
import org.springframework.jdbc.core.RowMapper;

public class BillMapper implements RowMapper<Bill> {

    @Override
    public Bill mapRow(ResultSet rs, int rowNum) throws SQLException {
        Bill group = new Bill();
        group.setBillId(rs.getLong("bill_id"));
        group.setName(rs.getString("name"));
        group.setDescription(rs.getString("description"));
        group.setAmount(rs.getDouble("amount"));
        group.setPaymentMethod(rs.getString("payment_method"));
        group.setOwner(rs.getString("username"));
        return group;
    }

}
