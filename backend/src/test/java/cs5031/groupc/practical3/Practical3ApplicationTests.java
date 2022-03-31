package cs5031.groupc.practical3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;

import cs5031.groupc.practical3.database.DataAccessObject;
import cs5031.groupc.practical3.database.SQLiteDataSource;
import cs5031.groupc.practical3.model.Bill;
import cs5031.groupc.practical3.model.Group;
import cs5031.groupc.practical3.model.List;
import cs5031.groupc.practical3.model.ListItem;
import cs5031.groupc.practical3.model.User;
import cs5031.groupc.practical3.model.UserBill;
import cs5031.groupc.practical3.testutils.SqlFileReader;
import cs5031.groupc.practical3.vo.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.jdbc.core.JdbcTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.context.annotation.Configuration;

@SpringBootTest(classes = Server.class)
//@ComponentScan({"cs5031.groupc.practical3.*"})
class Practical3ApplicationTests {



	@Autowired
	DataAccessObject dao;

	@Autowired
	JdbcTemplate jdbcTemplate;

	WebTestClient client;
	String userrole = "";
	final static String adminrole = UserRole.ADMIN.getRole();

	final static String DELETE_SCRIPT = "src/test/resources/db/delete.sql";
	final static String DEMO_SCRIPT = "src/test/resources/db/demo_data.sql";


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
		client = WebTestClient.bindToController(new Server()).build();
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
	@WithMockUser(username = "admin", authorities = {"admin"})
	public void getMessageWithMockUserCustomAuthorities() {
		client.get().uri("/")
				.accept(MediaType.TEXT_PLAIN)
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class)
				.isEqualTo("The server is running.");
	}

}
