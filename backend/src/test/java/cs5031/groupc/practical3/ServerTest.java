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
import cs5031.groupc.practical3.model.Bill;
import cs5031.groupc.practical3.model.DataProtection;
import cs5031.groupc.practical3.model.Group;
import cs5031.groupc.practical3.model.List;
import cs5031.groupc.practical3.model.ListItem;
import cs5031.groupc.practical3.model.User;
import cs5031.groupc.practical3.model.UserBill;


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



    // ======================== createUser ===============================================

    @Test
    public void testCreateUserPositiveAdmin() {

        User testUser = new User();
        testUser.setUsername("userForTest");
        testUser.setPassword("pass");


        client.post().uri("/api/user/create")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(testUser))
                .exchange()
                .expectStatus().isOk();



        client.get().uri("/api/user/exists?username=userForTest")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(boolean.class)
                .isEqualTo(true);

    }

    @Test
    public void testCreateUserNegativeAdmin() {

        User testUser = new User();
        testUser.setUsername("leopold");
        testUser.setPassword("pass");


        client.post().uri("/api/user/create")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(testUser))
                .exchange()
                .expectStatus().isBadRequest(); // should be bad request


    }

    @Test
    public void testCreateUserPositiveUser() {

        User testUser = new User();
        testUser.setUsername("userForTest");
        testUser.setPassword("pass");


        client.post().uri("/api/user/create")
                .headers(headers -> headers.setBasicAuth("lukas", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(testUser))
                .exchange()
                .expectStatus().isOk();


        // already tested, therefore admin as default
        client.get().uri("/api/user/exists?username=userForTest")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(boolean.class)
                .isEqualTo(true);

    }

    @Test
    public void testCreateUserNegativeUser() {

        User testUser = new User();
        testUser.setUsername("leopold");
        testUser.setPassword("pass");


        client.post().uri("/api/user/create")
                .headers(headers -> headers.setBasicAuth("lukas", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(testUser))
                .exchange()
                .expectStatus().isBadRequest(); // should be bad request


    }

    @Test
    public void testCreateUserPositivePleb() {

        User testUser = new User();
        testUser.setUsername("userForTest");
        testUser.setPassword("pass");


        client.post().uri("/api/user/create")
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(testUser))
                .exchange()
                .expectStatus().isOk();


        // already tested, therefore admin as default
        client.get().uri("/api/user/exists?username=userForTest")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(boolean.class)
                .isEqualTo(true);

    }

    @Test
    public void testCreateUserNegativepleb() {

        User testUser = new User();
        testUser.setUsername("leopold");
        testUser.setPassword("pass");


        client.post().uri("/api/user/create")
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(testUser))
                .exchange()
                .expectStatus().isBadRequest(); // should be bad request


    }



    // ======================== getGroup ===============================================


    @Test
    public void testGetGroupPositiveAdmin() {
        client.post().uri("/api/group?groupname=macintosh")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.groupId").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("macintosh");

    }

    @Test
    public void testGetGroupNegativeAdmin() {
        client.post().uri("/api/group?groupname=InvalidGroupName")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    public void testGetGroupPositiveUser() {
        client.post().uri("/api/group?groupname=macintosh")
                .headers(headers -> headers.setBasicAuth("lukas", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.groupId").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("macintosh");

    }

    @Test
    public void testGetGroupNegativeUser() {
        client.post().uri("/api/group?groupname=InvalidGroupName")
                .headers(headers -> headers.setBasicAuth("lukas", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    public void testGetGroupPleb() {
        client.post().uri("/api/group?groupname=InvalidGroupName")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }


    // ======================== createGroup ===============================================


    @Test
    public void testCreateGroupPositiveAdmin() {
        client.post().uri("/api/group/create?groupname=TestGroupName")
                .headers(headers -> headers.setBasicAuth("admin", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        client.get().uri("/api/user")
                .headers(headers -> headers.setBasicAuth("admin", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.ALL)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.username").isEqualTo("admin")
                .jsonPath("$.password").isEqualTo(null)
                .jsonPath("$.group.groupId").isEqualTo(5)
                .jsonPath("$.group.name").isEqualTo("TestGroupName")
                .jsonPath("$.role").isEqualTo("ADMIN")
                .jsonPath("$.enabled").isEqualTo(true);


    }

    @Test
    public void testCreateGroupNegativeAdmin() {
        client.post().uri("/api/group/create?groupname=macintosh")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();

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
    public void testCreateGroupPositiveUser() {
        client.post().uri("/api/group/create?groupname=TestGroupName")
                .headers(headers -> headers.setBasicAuth("user", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        client.get().uri("/api/user")
                .headers(headers -> headers.setBasicAuth("user", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.ALL)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.username").isEqualTo("user")
                .jsonPath("$.password").isEqualTo(null)
                .jsonPath("$.group.groupId").isEqualTo(5)
                .jsonPath("$.group.name").isEqualTo("TestGroupName")
                .jsonPath("$.role").isEqualTo("ADMIN")
                .jsonPath("$.enabled").isEqualTo(true);


    }

    @Test
    public void testCreateGroupNegativeUser() {
        client.post().uri("/api/group/create?groupname=macintosh")
                .headers(headers -> headers.setBasicAuth("lukas", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();

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
    public void testCreateGroupPleb() {
        client.post().uri("/api/group/create?groupname=macintosh")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }



    // ======================== getGroupUsers ===============================================


    @Test
    public void testGetGroupUsersPositiveAdmin() {
        client.get().uri("/api/group/getUsers")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.users[0]").isEqualTo("leopold")
                .jsonPath("$.users[1]").isEqualTo("lukas")
                .jsonPath("$.users[2]").isEqualTo("lucas")
                .jsonPath("$.users[3]").isEqualTo("jonathan");

    }

    @Test
    public void testGetGroupUsersNegativeAdmin() {
        client.get().uri("/api/group/getUsers")
                .headers(headers -> headers.setBasicAuth("admin", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();


    }

    @Test
    public void testGetGroupUsersPositiveUser() {
        client.get().uri("/api/group/getUsers")
                .headers(headers -> headers.setBasicAuth("lukas", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.users[0]").isEqualTo("leopold")
                .jsonPath("$.users[1]").isEqualTo("lukas")
                .jsonPath("$.users[2]").isEqualTo("lucas")
                .jsonPath("$.users[3]").isEqualTo("jonathan");

    }

    @Test
    public void testGetGroupUsersNegativeUser() {
        client.get().uri("/api/group/getUsers")
                .headers(headers -> headers.setBasicAuth("user", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();


    }


    @Test
    public void testGetGroupUsersPleb() {
        client.get().uri("/api/group/getUsers")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }


    // ======================== addToGroup ===============================================



    @Test
    public void testAddToGroupPositiveAdmin() {
        client.post().uri("/api/group/add?username=user")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        client.get().uri("/api/group/getUsers")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.users[0]").isEqualTo("user")
                .jsonPath("$.users[1]").isEqualTo("leopold")
                .jsonPath("$.users[2]").isEqualTo("lukas")
                .jsonPath("$.users[3]").isEqualTo("lucas")
                .jsonPath("$.users[4]").isEqualTo("jonathan");

    }

    @Test
    public void testAddToGroupNegativeAdmin() {
        client.post().uri("/api/group/add?username=FakeUser")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();

        client.get().uri("/api/group/getUsers")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.users[0]").isEqualTo("leopold")
                .jsonPath("$.users[1]").isEqualTo("lukas")
                .jsonPath("$.users[2]").isEqualTo("lucas")
                .jsonPath("$.users[3]").isEqualTo("jonathan");

    }

    @Test
    public void testAddToGroupNoGroupAdmin() {
        client.post().uri("/api/group/add?username=FakeUser")
                .headers(headers -> headers.setBasicAuth("admin", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();


    }

    @Test
    public void testAddToGroupUserChangedGroupAdmin() {
        client.post().uri("/api/group/add?username=anna")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();



    }

    @Test
    public void testAddToGroupUser() {
        client.post().uri("/api/group/add?username=user")
                .headers(headers -> headers.setBasicAuth("lukas", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden();

    }


    @Test
    public void testAddToGroupPleb() {
        client.get().uri("/api/group/add?username=anna")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }


    // ======================== removeFromGroup ===============================================


    @Test
    public void testRemoveFromGroupPositiveAdmin() {
        client.post().uri("/api/group/remove?username=jonathan")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        client.get().uri("/api/group/getUsers")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.users[0]").isEqualTo("leopold")
                .jsonPath("$.users[1]").isEqualTo("lukas")
                .jsonPath("$.users[2]").isEqualTo("lucas");

    }

    @Test
    public void testRemoveFromGroupNegativeAdmin() {
        client.post().uri("/api/group/remove?username=FakeUser")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();

        client.get().uri("/api/group/getUsers")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.users[0]").isEqualTo("leopold")
                .jsonPath("$.users[1]").isEqualTo("lukas")
                .jsonPath("$.users[2]").isEqualTo("lucas")
                .jsonPath("$.users[3]").isEqualTo("jonathan");

    }

    @Test
    public void testRemoveFromGroupNoGroupAdmin() {
        client.post().uri("/api/group/remove?username=FakeUser")
                .headers(headers -> headers.setBasicAuth("admin", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();


    }


    @Test
    public void testRemoveFromGroupUser() {
        client.post().uri("/api/group/remove?username=user")
                .headers(headers -> headers.setBasicAuth("lukas", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden();

    }


    @Test
    public void testRemoveFromGroupPleb() {
        client.get().uri("/api/group/remove?username=anna")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }


    // ======================== removeCurrentUserFromGroup ===============================================

    @Test
    public void testRemoveCurrentUserFromGroupPositiveAdmin() {
        client.post().uri("/api/group/removeCurrent")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    public void testRemoveCurrentUserFromGroupNoGroupAdmin() {
        client.post().uri("/api/group/removeCurrent")
                .headers(headers -> headers.setBasicAuth("admin", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }


    @Test
    public void testRemoveCurrentUserFromGroupUser() {
        client.post().uri("/api/group/removeCurrent")
                .headers(headers -> headers.setBasicAuth("lukas", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        client.get().uri("/api/group/getUsers")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.users[0]").isEqualTo("leopold")
                .jsonPath("$.users[1]").isEqualTo("lucas")
                .jsonPath("$.users[2]").isEqualTo("jonathan");

    }


    @Test
    public void testRemoveCurrentUserFromGroupPleb() {
        client.get().uri("/api/group/removeCurrent")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    // ======================== changeGroupAdmin ===============================================


    @Test
    public void testChangeGroupAdminPositiveAdmin() {
        client.post().uri("/api/group/changeAdmin?username=lukas")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

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
                .jsonPath("$.role").isEqualTo("ADMIN")
                .jsonPath("$.enabled").isEqualTo(true);

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
                .jsonPath("$.role").isEqualTo("USER")
                .jsonPath("$.enabled").isEqualTo(true);



    }

    @Test
    public void testChangeGroupAdminNegativeAdmin() {
        client.post().uri("/api/group/changeAdmin?username=user")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }



    @Test
    public void testChangeGroupAdminNoGroupAdmin() {
        client.post().uri("/api/group/changeAdmin?username=user")
                .headers(headers -> headers.setBasicAuth("admin", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();


    }


    @Test
    public void testChangeGroupAdminUser() {
        client.post().uri("/api/group/changeAdmin?username=user")
                .headers(headers -> headers.setBasicAuth("lukas", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden();
    }


    @Test
    public void testChangeGroupAdminPleb() {
        client.get().uri("/api/group/changeAdmin?username=user")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
