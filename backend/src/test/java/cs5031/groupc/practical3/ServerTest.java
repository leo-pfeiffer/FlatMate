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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;


import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;


@ContextConfiguration
@SpringBootTest
class ServerTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    DataAccessObject dao;

    WebTestClient client;
    ConfigurableApplicationContext ctx;

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

        String[] args = new String[0];
        ctx = SpringApplication.run(Server.class, args);

        client = WebTestClient.
                //bindToController(new Server(dao))
                        bindToServer().baseUrl("http://localhost:8080")
                .build();


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
        ctx.close();
    }

// ======================== ServerRunning ===============================================

    @Test
    public void testServerRunningAdmin() {

        client.get().uri("/")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.TEXT_PLAIN)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("The server is running.");
    }

    @Test
    public void testServerRunningUser() {

        client.get().uri("/")
                .headers(headers -> headers.setBasicAuth("lukas", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.TEXT_PLAIN)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("The server is running.");
    }

    @Test
    public void testServerRunningPleb() {
        client.get().uri("/")
                .accept(MediaType.TEXT_PLAIN)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("The server is running.");
    }

    // ======================== getCurrentUser ===============================================

    @Test
    public void testGetCurrentUserAdmin() {
        client.get().uri("/api/user")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.ALL)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.username").isEqualTo("leopold")
                .jsonPath("$.password").isEqualTo(null)
                .jsonPath("$.group.groupId").isEqualTo(1)
                .jsonPath("$.group.name").isEqualTo("macintosh")
                .jsonPath("$.role").isEqualTo("ADMIN")
                .jsonPath("$.enabled").isEqualTo(true);


    }

    @Test
    public void testGetCurrentUserUser() {
        client.get().uri("/api/user")
                .headers(headers -> headers.setBasicAuth("lukas", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.ALL)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.username").isEqualTo("lukas")
                .jsonPath("$.password").isEqualTo(null)
                .jsonPath("$.group.groupId").isEqualTo(1)
                .jsonPath("$.group.name").isEqualTo("macintosh")
                .jsonPath("$.role").isEqualTo("USER")
                .jsonPath("$.enabled").isEqualTo(true);
    }

    @Test
    public void testGetCurrentUserPleb() {
        client.get().uri("/api/user")
                .accept(MediaType.ALL)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    // ======================== getUserExists ===============================================


    @Test
    public void testGetUserExistsPositiveAdmin() {
        client.get().uri("/api/user/exists?username=leopold")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(boolean.class)
                .isEqualTo(true);

    }

    @Test
    public void testGetUserExistsNegativeAdmin() {
        client.get().uri("/api/user/exists?username=falseUser123")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(boolean.class)
                .isEqualTo(false);


    }

    @Test
    public void testGetUserExistsPositiveUser() {
        client.get().uri("/api/user/exists?username=leopold")
                .headers(headers -> headers.setBasicAuth("lukas", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(boolean.class)
                .isEqualTo(true);

    }

    @Test
    public void testGetUserExistsNegativeUser() {
        client.get().uri("/api/user/exists?username=falseUser123")
                .headers(headers -> headers.setBasicAuth("lukas", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(boolean.class)
                .isEqualTo(false);


    }



    @Test
    public void testGetUserExistsPositivePleb() {
        client.get().uri("/api/user/exists?username=leopold")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized(); // should be 401 unauthorized

    }


    @Test
    public void testGetUserExistsNegativePleb() {
        client.get().uri("/api/user/exists?username=falseUser123")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized(); // should be 401 unauthorized

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
