package cs5031.groupc.practical3.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import cs5031.groupc.practical3.model.UserBill;
import org.springframework.jdbc.core.RowMapper;

public class UserBillMapper implements RowMapper<UserBill> {

    @Override
    public UserBill mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserBill userBill = new UserBill();
        userBill.setUserBillId(rs.getLong("user_bill_id"));
        userBill.setUser(rs.getString("user"));
        userBill.setBill(rs.getLong("bill"));
        userBill.setPercentage(rs.getDouble("percentage"));
        userBill.setPaid(rs.getBoolean("paid"));
        return userBill;
    }

}
