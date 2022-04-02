package cs5031.groupc.practical3;

import static org.junit.jupiter.api.Assertions.fail;

import cs5031.groupc.practical3.database.DataAccessObject;
import cs5031.groupc.practical3.testutils.SqlFileReader;
import cs5031.groupc.practical3.vo.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.context.ContextConfiguration;


@ContextConfiguration
@SpringBootTest
class ServerTest {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	DataAccessObject dao;

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
		client = WebTestClient.bindToController(new Server(dao)).build();
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
	@WithMockUser(username = "admin", authorities = {"ADMIN"})
	public void testServerRunningAdmin() {
		client.get().uri("/")
				.accept(MediaType.TEXT_PLAIN)
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class)
				.isEqualTo("The server is running.");
	}

	@Test
	@WithMockUser(username = "user", authorities = {"USER"})
	public void testServerRunningUser() {
		client.get().uri("/")
				.accept(MediaType.TEXT_PLAIN)
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class)
				.isEqualTo("The server is running.");
	}

	@Test
	@WithMockUser(username = "pleb", authorities = {}, roles={})
	public void testServerRunningPleb() {
		client.get().uri("/")
				.accept(MediaType.TEXT_PLAIN)
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class)
				.isEqualTo("The server is running.");
	}

	@Test
	@WithMockUser(username = "admin", authorities = {"ADMIN"})
	public void testUserExistsPositiveAdmin() {
		client.get().uri("/api/user/exists?username=leopold")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody(boolean.class)
				.isEqualTo(true);
	}

	@Test
	@WithMockUser(username = "user", authorities = {"USER"})
	public void testUserExistsPositiveUser() {
		client.get().uri("/api/user/exists?username=leopold")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody(boolean.class)
				.isEqualTo(true);
	}

	@Test
	@WithAnonymousUser//@WithMockUser(username = "pleb", authorities = {}, roles={})
	public void testUserExistsPositivePleb() {
		client.get().uri("/api/user/exists?username=leopold")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody(boolean.class)
				.isEqualTo(true);
	}


	/*

	//TODO: Test api description access.
	@Test
	@WithMockUser(username = "admin", authorities = {"ADMIN"})
	public void testGetApiDescriptionAdmin() {
		client.get().uri("/api")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.info.title").isEqualTo("OpenAPI definition")
				.jsonPath("$.info.version").isEqualTo("v0");

	}
*/




	/*
	.accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(body))
			.exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.gameID").isEqualTo(1)
                .jsonPath("$.answer").isEqualTo(5);
                */

}
