package cs5031.groupc.practical3.utils;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import cs5031.groupc.practical3.SqlFileReader;
import cs5031.groupc.practical3.database.DataAccessObject;
import cs5031.groupc.practical3.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
public class InputValidationUtilsTest {

    final static String DELETE_SCRIPT = "src/test/resources/db/delete.sql";
    final static String DEMO_SCRIPT = "src/test/resources/db/demo_data.sql";

    @Autowired
    DataAccessObject dao;

    @Autowired
    JdbcTemplate jdbcTemplate;

    private InputValidationUtils validator;

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
        validator = new InputValidationUtils(dao);

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
    public void testInSameGroupPositive() {
        User u1 = dao.getUser("leopold");
        User u2 = dao.getUser("lukas");
        validator.inSameGroup(u1, u2);
    }

    @Test
    public void testInSameGroupNegative() {
        User u1 = dao.getUser("leopold");
        User u2 = dao.getUser("jane");

        assertThrows(
                ResponseStatusException.class,
                () -> validator.inSameGroup(u1, u2)
        );
    }


    @Test
    public void testUserInGroupPositive() {
        User u1 = dao.getUser("leopold");
        validator.userInGroup(u1, "macintosh");
    }

    @Test
    public void testUserInGroupUserWrong() {
        User u1 = dao.getUser("jane");
        assertThrows(
                ResponseStatusException.class,
                () -> validator.userInGroup(u1, "macintosh")
        );
    }

    @Test
    public void testUserInGroupGroupWrong() {
        User u1 = dao.getUser("leopold");
        assertThrows(
                ResponseStatusException.class,
                () -> validator.userInGroup(u1, "gannochy")
        );
    }


    @Test
    public void testUserInGroupUserNull() {
        //User u1 = dao.getUser("jane");
        assertThrows(
                ResponseStatusException.class,
                () -> validator.userInGroup(null, "macintosh")
        );
    }

    @Test
    public void testUserInGroupGroupNull() {
        User u1 = dao.getUser("leopold");
        assertThrows(
                ResponseStatusException.class,
                () -> validator.userInGroup(u1, null)
        );
    }

    @Test
    public void testUserInGroupShouldHaveGroupPositive() {
        User u1 = dao.getUser("leopold");
        validator.userHasGroup(u1, true);
    }

    @Test
    public void testUserInGroupShouldNotHaveGroupPositive() {
        User u1 = dao.getUser("user");
        validator.userHasGroup(u1, false);
    }

    @Test
    public void testUserInGroupShouldHaveGroupNegative() {
        User u1 = dao.getUser("leopold");
        assertThrows(
                ResponseStatusException.class,
                () -> validator.userHasGroup(u1, false)
        );
    }

    @Test
    public void testUserInGroupShouldNotHaveGroupNegative() {
        User u1 = dao.getUser("user");
        assertThrows(
                ResponseStatusException.class,
                () -> validator.userHasGroup(u1, true)
        );
    }

    @Test
    public void testUserInGroupShouldHaveGroupUserNull() {
        //User u1 = dao.getUser("leopold");
        assertThrows(
                ResponseStatusException.class,
                () -> validator.userHasGroup(null, false)
        );
    }

    @Test
    public void testUserInGroupShouldNotHaveGroupUserNull() {
        //User u1 = dao.getUser("user");
        assertThrows(
                ResponseStatusException.class,
                () -> validator.userHasGroup(null, true)
        );
    }


    @Test
    public void testUserHasGroupPositive() {
        User u1 = dao.getUser("leopold");
        validator.userHasGroup(u1);
    }

    @Test
    public void testUserHasGroupNegative() {
        User u1 = dao.getUser("user");
        assertThrows(
                ResponseStatusException.class,
                () -> validator.userHasGroup(u1)
        );
    }


    @Test
    public void testUserHasGroupUserNull() {
        //User u1 = dao.getUser("user");
        assertThrows(
                ResponseStatusException.class,
                () -> validator.userHasGroup(null, true)
        );
    }


    @Test
    public void userIsNotAdminPositive() {
        User u1 = dao.getUser("lukas");
        validator.userIsNotAdmin(u1);
    }

    @Test
    public void userIsNotAdminNegative() {
        User u1 = dao.getUser("leopold");
        assertThrows(
                ResponseStatusException.class,
                () -> validator.userIsNotAdmin(u1)
        );
    }

    @Test
    public void userIsNotAdminUserNull() {
        //User u1 = dao.getUser("leopold");
        assertThrows(
                ResponseStatusException.class,
                () -> validator.userIsNotAdmin(null)
        );
    }

    @Test
    public void testValueInRangePositive1() {
        validator.valueInRange(1, 0, 2);
    }

    @Test
    public void testValueInRangePositive2() {
        validator.valueInRange(0, 0, 2);
    }

    @Test
    public void testValueInRangePositive3() {
        validator.valueInRange(2, 0, 2);
    }

    @Test
    public void testValueInRangeNegative1() {
        assertThrows(
                ResponseStatusException.class,
                () -> validator.valueInRange(-1, 0, 2)
        );
    }

    @Test
    public void testValueInRangeNegative2() {
        assertThrows(
                ResponseStatusException.class,
                () -> validator.valueInRange(3, 0, 2)
        );
    }

    @Test
    public void testValueInRangeNegative3() {
        assertThrows(
                ResponseStatusException.class,
                () -> validator.valueInRange(-0.0001, 0, 2)
        );
    }

    @Test
    public void testValueInRangeNegative4() {
        assertThrows(
                ResponseStatusException.class,
                () -> validator.valueInRange(2.0001, 0, 2)
        );
    }

    @Test
    public void testUserEnabledPositive() {
        User u1 = dao.getUser("leopold");
        validator.userEnabled(u1);
    }

    @Test
    public void testUserEnabledNegative() {
        dao.changeUserEnabled("leopold", false);
        User u1 = dao.getUser("leopold");

        assertThrows(
                ResponseStatusException.class,
                () -> validator.userEnabled(u1)
        );
    }

    @Test
    public void testUserEnabledUserNull() {
        assertThrows(
                ResponseStatusException.class,
                () -> validator.userEnabled(null)
        );
    }

}