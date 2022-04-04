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

    // ======================== /ServerRunning ===============================================

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

    // ======================== /getCurrentUser ===============================================

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

    // ======================== /getUserExists ===============================================

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


    // ======================== /createUser ===============================================

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

    // ======================== /getGroup ===============================================

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


    // ======================== /createGroup ===============================================

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


    // ======================== /getGroupUsers ===============================================

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


    // ======================== /addToGroup ===============================================

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


    // ======================== /removeFromGroup ===============================================

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

    // ======================== /removeCurrentUserFromGroup ===============================================

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

    // ======================== /changeGroupAdmin ===============================================

    // ======================== getAllBills ===============================================

    @Test
    public void testGetAllBillsPositiveAdmin() {
        client.get().uri("/api/group/getAllBills")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.bills[0].name").isEqualTo("spotify")
                .jsonPath("$.bills[0].billId").isEqualTo(1)
                .jsonPath("$.bills[0].owner.password").isEqualTo(null)

                .jsonPath("$.bills[1].name").isEqualTo("netflix")
                .jsonPath("$.bills[1].billId").isEqualTo(2)
                .jsonPath("$.bills[1].owner.password").isEqualTo(null)

                .jsonPath("$.bills[2].name").isEqualTo("shopping")
                .jsonPath("$.bills[2].billId").isEqualTo(3)
                .jsonPath("$.bills[2].owner.password").isEqualTo(null)

                .jsonPath("$.bills[3].name").isEqualTo("shopping")
                .jsonPath("$.bills[3].billId").isEqualTo(4)
                .jsonPath("$.bills[3].owner.password").isEqualTo(null);


    }

    @Test
    public void testGetAllBillsNegativeAdmin() {
        client.get().uri("/api/group/getAllBills")
                .headers(headers -> headers.setBasicAuth("admin", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void testGetAllBillsPositiveUser() {
        client.get().uri("/api/group/getAllBills")
                .headers(headers -> headers.setBasicAuth("lukas", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.bills[0].name").isEqualTo("spotify")
                .jsonPath("$.bills[0].billId").isEqualTo(1)
                .jsonPath("$.bills[0].owner.password").isEqualTo(null)

                .jsonPath("$.bills[1].name").isEqualTo("netflix")
                .jsonPath("$.bills[1].billId").isEqualTo(2)
                .jsonPath("$.bills[1].owner.password").isEqualTo(null)

                .jsonPath("$.bills[2].name").isEqualTo("shopping")
                .jsonPath("$.bills[2].billId").isEqualTo(3)
                .jsonPath("$.bills[2].owner.password").isEqualTo(null)

                .jsonPath("$.bills[3].name").isEqualTo("shopping")
                .jsonPath("$.bills[3].billId").isEqualTo(4)
                .jsonPath("$.bills[3].owner.password").isEqualTo(null);


    }

    @Test
    public void testGetAllBillsNegativeUser() {
        client.get().uri("/api/group/getAllBills")
                .headers(headers -> headers.setBasicAuth("user", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void testGetAllBillsPleb() {
        client.get().uri("/api/group/getAllBills")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    // ======================== /getAllBills ===============================================


    // ======================== getAllUserBills ===============================================

    @Test
    public void testGetAllUserBillsPositiveAdmin() {
        client.get().uri("/api/group/getAllUserBills")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[0].userBillId").isEqualTo(1)
                .jsonPath("$.[0].user.username").isEqualTo("leopold")
                .jsonPath("$.[0].user.password").isEqualTo(null)
                .jsonPath("$.[0].bill.billId").isEqualTo(1)
                .jsonPath("$.[0].percentage").isEqualTo(0.25)
                .jsonPath("$.[0].paid").isEqualTo(true)

                .jsonPath("$.[1].userBillId").isEqualTo(2)
                .jsonPath("$.[1].user.username").isEqualTo("lukas")
                .jsonPath("$.[1].user.password").isEqualTo(null)
                .jsonPath("$.[1].bill.billId").isEqualTo(1)
                .jsonPath("$.[1].percentage").isEqualTo(0.25)
                .jsonPath("$.[1].paid").isEqualTo(true)

                .jsonPath("$.[2].userBillId").isEqualTo(3)
                .jsonPath("$.[2].user.username").isEqualTo("lucas")
                .jsonPath("$.[2].user.password").isEqualTo(null)
                .jsonPath("$.[2].bill.billId").isEqualTo(1)
                .jsonPath("$.[2].percentage").isEqualTo(0.25)
                .jsonPath("$.[2].paid").isEqualTo(true)

                .jsonPath("$.[3].userBillId").isEqualTo(4)
                .jsonPath("$.[3].user.username").isEqualTo("jonathan")
                .jsonPath("$.[3].user.password").isEqualTo(null)
                .jsonPath("$.[3].bill.billId").isEqualTo(1)
                .jsonPath("$.[3].percentage").isEqualTo(0.25)
                .jsonPath("$.[3].paid").isEqualTo(true)

                .jsonPath("$.[4].userBillId").isEqualTo(5)
                .jsonPath("$.[4].user.username").isEqualTo("leopold")
                .jsonPath("$.[4].user.password").isEqualTo(null)
                .jsonPath("$.[4].bill.billId").isEqualTo(2)
                .jsonPath("$.[4].percentage").isEqualTo(0.33)
                .jsonPath("$.[4].paid").isEqualTo(false)

                .jsonPath("$.[5].userBillId").isEqualTo(6)
                .jsonPath("$.[5].user.username").isEqualTo("lukas")
                .jsonPath("$.[5].user.password").isEqualTo(null)
                .jsonPath("$.[5].bill.billId").isEqualTo(2)
                .jsonPath("$.[5].percentage").isEqualTo(0.33)
                .jsonPath("$.[5].paid").isEqualTo(false)

                .jsonPath("$.[6].userBillId").isEqualTo(7)
                .jsonPath("$.[6].user.username").isEqualTo("lucas")
                .jsonPath("$.[6].user.password").isEqualTo(null)
                .jsonPath("$.[6].bill.billId").isEqualTo(2)
                .jsonPath("$.[6].percentage").isEqualTo(0.34)
                .jsonPath("$.[6].paid").isEqualTo(false)

                .jsonPath("$.[7].userBillId").isEqualTo(8)
                .jsonPath("$.[7].user.username").isEqualTo("leopold")
                .jsonPath("$.[7].user.password").isEqualTo(null)
                .jsonPath("$.[7].bill.billId").isEqualTo(3)
                .jsonPath("$.[7].percentage").isEqualTo(0.5)
                .jsonPath("$.[7].paid").isEqualTo(false)

                .jsonPath("$.[8].userBillId").isEqualTo(9)
                .jsonPath("$.[8].user.username").isEqualTo("lukas")
                .jsonPath("$.[8].user.password").isEqualTo(null)
                .jsonPath("$.[8].bill.billId").isEqualTo(3)
                .jsonPath("$.[8].percentage").isEqualTo(0.5)
                .jsonPath("$.[8].paid").isEqualTo(true)

                .jsonPath("$.[9].userBillId").isEqualTo(10)
                .jsonPath("$.[9].user.username").isEqualTo("jonathan")
                .jsonPath("$.[9].user.password").isEqualTo(null)
                .jsonPath("$.[9].bill.billId").isEqualTo(4)
                .jsonPath("$.[9].percentage").isEqualTo(0.5)
                .jsonPath("$.[9].paid").isEqualTo(false)

                .jsonPath("$.[10].userBillId").isEqualTo(11)
                .jsonPath("$.[10].user.username").isEqualTo("lucas")
                .jsonPath("$.[10].user.password").isEqualTo(null)
                .jsonPath("$.[10].bill.billId").isEqualTo(4)
                .jsonPath("$.[10].percentage").isEqualTo(0.5)
                .jsonPath("$.[10].paid").isEqualTo(false);

    }

    @Test
    public void testGetAllUserBillsNegativeAdmin() {
        client.get().uri("/api/group/getAllUserBills")
                .headers(headers -> headers.setBasicAuth("admin", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void testGetAllUserBillsPositiveUser() {
        client.get().uri("/api/group/getAllUserBills")
                .headers(headers -> headers.setBasicAuth("lukas", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()

                .jsonPath("$.[0].userBillId").isEqualTo(1)
                .jsonPath("$.[0].user.username").isEqualTo("leopold")
                .jsonPath("$.[0].user.password").isEqualTo(null)
                .jsonPath("$.[0].bill.billId").isEqualTo(1)
                .jsonPath("$.[0].percentage").isEqualTo(0.25)
                .jsonPath("$.[0].paid").isEqualTo(true)

                .jsonPath("$.[1].userBillId").isEqualTo(2)
                .jsonPath("$.[1].user.username").isEqualTo("lukas")
                .jsonPath("$.[1].user.password").isEqualTo(null)
                .jsonPath("$.[1].bill.billId").isEqualTo(1)
                .jsonPath("$.[1].percentage").isEqualTo(0.25)
                .jsonPath("$.[1].paid").isEqualTo(true)

                .jsonPath("$.[2].userBillId").isEqualTo(3)
                .jsonPath("$.[2].user.username").isEqualTo("lucas")
                .jsonPath("$.[2].user.password").isEqualTo(null)
                .jsonPath("$.[2].bill.billId").isEqualTo(1)
                .jsonPath("$.[2].percentage").isEqualTo(0.25)
                .jsonPath("$.[2].paid").isEqualTo(true)

                .jsonPath("$.[3].userBillId").isEqualTo(4)
                .jsonPath("$.[3].user.username").isEqualTo("jonathan")
                .jsonPath("$.[3].user.password").isEqualTo(null)
                .jsonPath("$.[3].bill.billId").isEqualTo(1)
                .jsonPath("$.[3].percentage").isEqualTo(0.25)
                .jsonPath("$.[3].paid").isEqualTo(true)

                .jsonPath("$.[4].userBillId").isEqualTo(5)
                .jsonPath("$.[4].user.username").isEqualTo("leopold")
                .jsonPath("$.[4].user.password").isEqualTo(null)
                .jsonPath("$.[4].bill.billId").isEqualTo(2)
                .jsonPath("$.[4].percentage").isEqualTo(0.33)
                .jsonPath("$.[4].paid").isEqualTo(false)

                .jsonPath("$.[5].userBillId").isEqualTo(6)
                .jsonPath("$.[5].user.username").isEqualTo("lukas")
                .jsonPath("$.[5].user.password").isEqualTo(null)
                .jsonPath("$.[5].bill.billId").isEqualTo(2)
                .jsonPath("$.[5].percentage").isEqualTo(0.33)
                .jsonPath("$.[5].paid").isEqualTo(false)

                .jsonPath("$.[6].userBillId").isEqualTo(7)
                .jsonPath("$.[6].user.username").isEqualTo("lucas")
                .jsonPath("$.[6].user.password").isEqualTo(null)
                .jsonPath("$.[6].bill.billId").isEqualTo(2)
                .jsonPath("$.[6].percentage").isEqualTo(0.34)
                .jsonPath("$.[6].paid").isEqualTo(false)

                .jsonPath("$.[7].userBillId").isEqualTo(8)
                .jsonPath("$.[7].user.username").isEqualTo("leopold")
                .jsonPath("$.[7].user.password").isEqualTo(null)
                .jsonPath("$.[7].bill.billId").isEqualTo(3)
                .jsonPath("$.[7].percentage").isEqualTo(0.5)
                .jsonPath("$.[7].paid").isEqualTo(false)

                .jsonPath("$.[8].userBillId").isEqualTo(9)
                .jsonPath("$.[8].user.username").isEqualTo("lukas")
                .jsonPath("$.[8].user.password").isEqualTo(null)
                .jsonPath("$.[8].bill.billId").isEqualTo(3)
                .jsonPath("$.[8].percentage").isEqualTo(0.5)
                .jsonPath("$.[8].paid").isEqualTo(true)

                .jsonPath("$.[9].userBillId").isEqualTo(10)
                .jsonPath("$.[9].user.username").isEqualTo("jonathan")
                .jsonPath("$.[9].user.password").isEqualTo(null)
                .jsonPath("$.[9].bill.billId").isEqualTo(4)
                .jsonPath("$.[9].percentage").isEqualTo(0.5)
                .jsonPath("$.[9].paid").isEqualTo(false)

                .jsonPath("$.[10].userBillId").isEqualTo(11)
                .jsonPath("$.[10].user.username").isEqualTo("lucas")
                .jsonPath("$.[10].user.password").isEqualTo(null)
                .jsonPath("$.[10].bill.billId").isEqualTo(4)
                .jsonPath("$.[10].percentage").isEqualTo(0.5)
                .jsonPath("$.[10].paid").isEqualTo(false);

    }

    @Test
    public void testGetAllUserBillsNegativeUser() {
        client.get().uri("/api/group/getAllUserBills")
                .headers(headers -> headers.setBasicAuth("user", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void testGetAllUserBillsPleb() {
        client.get().uri("/api/group/getAllUserBills")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    // ======================== /getAllUserBills ===============================================

    // ======================== getAllLists ===============================================

    @Test
    public void testGetAllListsPositiveAdmin() {
        client.get().uri("/api/group/getAllLists")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.lists[0].name").isEqualTo("shopping list")
                .jsonPath("$.lists[0].listId").isEqualTo(1)
                .jsonPath("$.lists[0].owner.password").isEqualTo(null)
                .jsonPath("$.lists[0].bill.billId").isEqualTo(3)
                .jsonPath("$.lists[0].bill.owner.password").isEqualTo(null)

                .jsonPath("$.lists[1].name").isEqualTo("shopping list")
                .jsonPath("$.lists[1].listId").isEqualTo(2)
                .jsonPath("$.lists[1].owner.password").isEqualTo(null)
                .jsonPath("$.lists[1].bill.billId").isEqualTo(4)
                .jsonPath("$.lists[1].bill.owner.password").isEqualTo(null)

                .jsonPath("$.lists[2].name").isEqualTo("party planning")
                .jsonPath("$.lists[2].listId").isEqualTo(3)
                .jsonPath("$.lists[2].owner.password").isEqualTo(null)
                .jsonPath("$.lists[2].bill").isEqualTo(null);


    }

    @Test
    public void testGetAllListsNegativeAdmin() {
        client.get().uri("/api/group/getAllLists")
                .headers(headers -> headers.setBasicAuth("admin", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void testGetAllListsPositiveUser() {
        client.get().uri("/api/group/getAllLists")
                .headers(headers -> headers.setBasicAuth("lukas", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.lists[0].name").isEqualTo("shopping list")
                .jsonPath("$.lists[0].listId").isEqualTo(1)
                .jsonPath("$.lists[0].owner.password").isEqualTo(null)
                .jsonPath("$.lists[0].bill.billId").isEqualTo(3)
                .jsonPath("$.lists[0].bill.owner.password").isEqualTo(null)

                .jsonPath("$.lists[1].name").isEqualTo("shopping list")
                .jsonPath("$.lists[1].listId").isEqualTo(2)
                .jsonPath("$.lists[1].owner.password").isEqualTo(null)
                .jsonPath("$.lists[1].bill.billId").isEqualTo(4)
                .jsonPath("$.lists[1].bill.owner.password").isEqualTo(null)

                .jsonPath("$.lists[2].name").isEqualTo("party planning")
                .jsonPath("$.lists[2].listId").isEqualTo(3)
                .jsonPath("$.lists[2].owner.password").isEqualTo(null)
                .jsonPath("$.lists[2].bill").isEqualTo(null);



    }

    @Test
    public void testGetAllListsNegativeUser() {
        client.get().uri("/api/group/getAllLists")
                .headers(headers -> headers.setBasicAuth("user", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void testGetAllListsPleb() {
        client.get().uri("/api/group/getAllLists")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    // ======================== /getAllLists ===============================================


    // ======================== getAllListItems ===============================================

    @Test
    public void testGetAllListItemsPositiveAdmin() {
        client.get().uri("/api/group/getAllListItems")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[0].listItemId").isEqualTo(1)
                .jsonPath("$.[0].name").isEqualTo("peach")
                .jsonPath("$.[0].list.name").isEqualTo("shopping list")
                .jsonPath("$.[0].list.listId").isEqualTo(1)
                .jsonPath("$.[0].list.owner.password").isEqualTo(null)
                .jsonPath("$.[0].list.bill.billId").isEqualTo(3)
                .jsonPath("$.[0].list.bill.owner.password").isEqualTo(null)

                .jsonPath("$.[1].listItemId").isEqualTo(2)
                .jsonPath("$.[1].name").isEqualTo("pears")
                .jsonPath("$.[1].list.name").isEqualTo("shopping list")
                .jsonPath("$.[1].list.listId").isEqualTo(1)
                .jsonPath("$.[1].list.owner.password").isEqualTo(null)
                .jsonPath("$.[1].list.bill.billId").isEqualTo(3)
                .jsonPath("$.[1].list.bill.owner.password").isEqualTo(null)

                .jsonPath("$.[2].listItemId").isEqualTo(3)
                .jsonPath("$.[2].name").isEqualTo("plums")
                .jsonPath("$.[2].list.name").isEqualTo("shopping list")
                .jsonPath("$.[2].list.listId").isEqualTo(1)
                .jsonPath("$.[2].list.owner.password").isEqualTo(null)
                .jsonPath("$.[2].list.bill.billId").isEqualTo(3)
                .jsonPath("$.[2].list.bill.owner.password").isEqualTo(null)

                .jsonPath("$.[3].listItemId").isEqualTo(4)
                .jsonPath("$.[3].name").isEqualTo("oranges")
                .jsonPath("$.[3].list.name").isEqualTo("shopping list")
                .jsonPath("$.[3].list.listId").isEqualTo(1)
                .jsonPath("$.[3].list.owner.password").isEqualTo(null)
                .jsonPath("$.[3].list.bill.billId").isEqualTo(3)
                .jsonPath("$.[3].list.bill.owner.password").isEqualTo(null)

                .jsonPath("$.[4].listItemId").isEqualTo(5)
                .jsonPath("$.[4].name").isEqualTo("tomato")
                .jsonPath("$.[4].list.name").isEqualTo("shopping list")
                .jsonPath("$.[4].list.listId").isEqualTo(2)
                .jsonPath("$.[4].list.owner.password").isEqualTo(null)
                .jsonPath("$.[4].list.bill.billId").isEqualTo(4)
                .jsonPath("$.[4].list.bill.owner.password").isEqualTo(null)

                .jsonPath("$.[5].listItemId").isEqualTo(6)
                .jsonPath("$.[5].name").isEqualTo("potato")
                .jsonPath("$.[5].list.name").isEqualTo("shopping list")
                .jsonPath("$.[5].list.listId").isEqualTo(2)
                .jsonPath("$.[5].list.owner.password").isEqualTo(null)
                .jsonPath("$.[5].list.bill.billId").isEqualTo(4)
                .jsonPath("$.[5].list.bill.owner.password").isEqualTo(null)

                .jsonPath("$.[6].listItemId").isEqualTo(7)
                .jsonPath("$.[6].name").isEqualTo("celery")
                .jsonPath("$.[6].list.name").isEqualTo("shopping list")
                .jsonPath("$.[6].list.listId").isEqualTo(2)
                .jsonPath("$.[6].list.owner.password").isEqualTo(null)
                .jsonPath("$.[6].list.bill.billId").isEqualTo(4)
                .jsonPath("$.[6].list.bill.owner.password").isEqualTo(null)

                .jsonPath("$.[7].listItemId").isEqualTo(8)
                .jsonPath("$.[7].name").isEqualTo("broccoli")
                .jsonPath("$.[7].list.name").isEqualTo("shopping list")
                .jsonPath("$.[7].list.listId").isEqualTo(2)
                .jsonPath("$.[7].list.owner.password").isEqualTo(null)
                .jsonPath("$.[7].list.bill.billId").isEqualTo(4)
                .jsonPath("$.[7].list.bill.owner.password").isEqualTo(null)

                .jsonPath("$.[8].listItemId").isEqualTo(9)
                .jsonPath("$.[8].name").isEqualTo("crisps")
                .jsonPath("$.[8].list.name").isEqualTo("party planning")
                .jsonPath("$.[8].list.listId").isEqualTo(3)
                .jsonPath("$.[8].list.owner.password").isEqualTo(null)
                .jsonPath("$.[8].list.bill").isEqualTo(null)

                .jsonPath("$.[9].listItemId").isEqualTo(10)
                .jsonPath("$.[9].name").isEqualTo("beer")
                .jsonPath("$.[9].list.name").isEqualTo("party planning")
                .jsonPath("$.[9].list.listId").isEqualTo(3)
                .jsonPath("$.[9].list.owner.password").isEqualTo(null)
                .jsonPath("$.[9].list.bill").isEqualTo(null)

                .jsonPath("$.[10].listItemId").isEqualTo(11)
                .jsonPath("$.[10].name").isEqualTo("soda")
                .jsonPath("$.[10].list.name").isEqualTo("party planning")
                .jsonPath("$.[10].list.listId").isEqualTo(3)
                .jsonPath("$.[10].list.owner.password").isEqualTo(null)
                .jsonPath("$.[10].list.bill").isEqualTo(null);

    }

    @Test
    public void testGetAllListItemsNegativeAdmin() {
        client.get().uri("/api/group/getAllListItems")
                .headers(headers -> headers.setBasicAuth("admin", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void testGetAllListItemsPositiveUser() {
        client.get().uri("/api/group/getAllListItems")
                .headers(headers -> headers.setBasicAuth("lukas", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[0].listItemId").isEqualTo(1)
                .jsonPath("$.[0].name").isEqualTo("peach")
                .jsonPath("$.[0].list.name").isEqualTo("shopping list")
                .jsonPath("$.[0].list.listId").isEqualTo(1)
                .jsonPath("$.[0].list.owner.password").isEqualTo(null)
                .jsonPath("$.[0].list.bill.billId").isEqualTo(3)
                .jsonPath("$.[0].list.bill.owner.password").isEqualTo(null)

                .jsonPath("$.[1].listItemId").isEqualTo(2)
                .jsonPath("$.[1].name").isEqualTo("pears")
                .jsonPath("$.[1].list.name").isEqualTo("shopping list")
                .jsonPath("$.[1].list.listId").isEqualTo(1)
                .jsonPath("$.[1].list.owner.password").isEqualTo(null)
                .jsonPath("$.[1].list.bill.billId").isEqualTo(3)
                .jsonPath("$.[1].list.bill.owner.password").isEqualTo(null)

                .jsonPath("$.[2].listItemId").isEqualTo(3)
                .jsonPath("$.[2].name").isEqualTo("plums")
                .jsonPath("$.[2].list.name").isEqualTo("shopping list")
                .jsonPath("$.[2].list.listId").isEqualTo(1)
                .jsonPath("$.[2].list.owner.password").isEqualTo(null)
                .jsonPath("$.[2].list.bill.billId").isEqualTo(3)
                .jsonPath("$.[2].list.bill.owner.password").isEqualTo(null)

                .jsonPath("$.[3].listItemId").isEqualTo(4)
                .jsonPath("$.[3].name").isEqualTo("oranges")
                .jsonPath("$.[3].list.name").isEqualTo("shopping list")
                .jsonPath("$.[3].list.listId").isEqualTo(1)
                .jsonPath("$.[3].list.owner.password").isEqualTo(null)
                .jsonPath("$.[3].list.bill.billId").isEqualTo(3)
                .jsonPath("$.[3].list.bill.owner.password").isEqualTo(null)

                .jsonPath("$.[4].listItemId").isEqualTo(5)
                .jsonPath("$.[4].name").isEqualTo("tomato")
                .jsonPath("$.[4].list.name").isEqualTo("shopping list")
                .jsonPath("$.[4].list.listId").isEqualTo(2)
                .jsonPath("$.[4].list.owner.password").isEqualTo(null)
                .jsonPath("$.[4].list.bill.billId").isEqualTo(4)
                .jsonPath("$.[4].list.bill.owner.password").isEqualTo(null)

                .jsonPath("$.[5].listItemId").isEqualTo(6)
                .jsonPath("$.[5].name").isEqualTo("potato")
                .jsonPath("$.[5].list.name").isEqualTo("shopping list")
                .jsonPath("$.[5].list.listId").isEqualTo(2)
                .jsonPath("$.[5].list.owner.password").isEqualTo(null)
                .jsonPath("$.[5].list.bill.billId").isEqualTo(4)
                .jsonPath("$.[5].list.bill.owner.password").isEqualTo(null)

                .jsonPath("$.[6].listItemId").isEqualTo(7)
                .jsonPath("$.[6].name").isEqualTo("celery")
                .jsonPath("$.[6].list.name").isEqualTo("shopping list")
                .jsonPath("$.[6].list.listId").isEqualTo(2)
                .jsonPath("$.[6].list.owner.password").isEqualTo(null)
                .jsonPath("$.[6].list.bill.billId").isEqualTo(4)
                .jsonPath("$.[6].list.bill.owner.password").isEqualTo(null)

                .jsonPath("$.[7].listItemId").isEqualTo(8)
                .jsonPath("$.[7].name").isEqualTo("broccoli")
                .jsonPath("$.[7].list.name").isEqualTo("shopping list")
                .jsonPath("$.[7].list.listId").isEqualTo(2)
                .jsonPath("$.[7].list.owner.password").isEqualTo(null)
                .jsonPath("$.[7].list.bill.billId").isEqualTo(4)
                .jsonPath("$.[7].list.bill.owner.password").isEqualTo(null)

                .jsonPath("$.[8].listItemId").isEqualTo(9)
                .jsonPath("$.[8].name").isEqualTo("crisps")
                .jsonPath("$.[8].list.name").isEqualTo("party planning")
                .jsonPath("$.[8].list.listId").isEqualTo(3)
                .jsonPath("$.[8].list.owner.password").isEqualTo(null)
                .jsonPath("$.[8].list.bill").isEqualTo(null)

                .jsonPath("$.[9].listItemId").isEqualTo(10)
                .jsonPath("$.[9].name").isEqualTo("beer")
                .jsonPath("$.[9].list.name").isEqualTo("party planning")
                .jsonPath("$.[9].list.listId").isEqualTo(3)
                .jsonPath("$.[9].list.owner.password").isEqualTo(null)
                .jsonPath("$.[9].list.bill").isEqualTo(null)

                .jsonPath("$.[10].listItemId").isEqualTo(11)
                .jsonPath("$.[10].name").isEqualTo("soda")
                .jsonPath("$.[10].list.name").isEqualTo("party planning")
                .jsonPath("$.[10].list.listId").isEqualTo(3)
                .jsonPath("$.[10].list.owner.password").isEqualTo(null)
                .jsonPath("$.[10].list.bill").isEqualTo(null);

    }

    @Test
    public void testGetAllListItemsNegativeUser() {
        client.get().uri("/api/group/getAllListItems")
                .headers(headers -> headers.setBasicAuth("user", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void testGetAllListItemsPleb() {
        client.get().uri("/api/group/getAllListItems")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    // ======================== /getAllListItems ===============================================

    // ======================== getBill ===============================================

    @Test
    public void testGetBillPositiveAdmin() {
        client.get().uri("/api/group/getBill?id=1")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("spotify")
                .jsonPath("$.description").isEqualTo("monthly music service")
                .jsonPath("$.billId").isEqualTo(1)
                .jsonPath("$.owner.password").isEqualTo(null)
                .jsonPath("$.amount").isEqualTo(9.99)
                .jsonPath("$.paymentMethod").isEqualTo("cash")
                .jsonPath("$.createTime").isEqualTo(1648727500);
    }

    @Test
    public void testGetBillNegativeAdmin() {
        client.get().uri("/api/group/getBill?id=1")
                .headers(headers -> headers.setBasicAuth("admin", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void testGetBillPositiveUser() {
        client.get().uri("/api/group/getBill?id=1")
                .headers(headers -> headers.setBasicAuth("lukas", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("spotify")
                .jsonPath("$.description").isEqualTo("monthly music service")
                .jsonPath("$.billId").isEqualTo(1)
                .jsonPath("$.owner.password").isEqualTo(null)
                .jsonPath("$.amount").isEqualTo(9.99)
                .jsonPath("$.paymentMethod").isEqualTo("cash")
                .jsonPath("$.createTime").isEqualTo(1648727500);
    }

    @Test
    public void testGetBillNegativeUser() {
        client.get().uri("/api/group/getBill?id=1")
                .headers(headers -> headers.setBasicAuth("user", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void testGetBillPleb() {
        client.get().uri("/api/group/getBill?id=1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    // ======================== /getBill ===============================================

    // ======================== getList ===============================================

    @Test
    public void testGetListPositiveAdmin() {
        client.get().uri("/api/group/getList?id=1")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("shopping list")
                .jsonPath("$.description").isEqualTo("my shopping list")
                .jsonPath("$.listId").isEqualTo(1)
                .jsonPath("$.owner.password").isEqualTo(null)
                .jsonPath("$.bill.billId").isEqualTo(3)
                .jsonPath("$.bill.owner.password").isEqualTo(null)
                .jsonPath("$.createTime").isEqualTo(1648727500);
    }

    @Test
    public void testGetListNegativeAdmin() {
        client.get().uri("/api/group/getList?id=1")
                .headers(headers -> headers.setBasicAuth("admin", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void testGetListPositiveUser() {
        client.get().uri("/api/group/getList?id=1")
                .headers(headers -> headers.setBasicAuth("lukas", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("shopping list")
                .jsonPath("$.description").isEqualTo("my shopping list")
                .jsonPath("$.listId").isEqualTo(1)
                .jsonPath("$.owner.password").isEqualTo(null)
                .jsonPath("$.bill.billId").isEqualTo(3)
                .jsonPath("$.bill.owner.password").isEqualTo(null)
                .jsonPath("$.createTime").isEqualTo(1648727500);
    }

    @Test
    public void testGetListNegativeUser() {
        client.get().uri("/api/group/getList?id=1")
                .headers(headers -> headers.setBasicAuth("user", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void testGetListPleb() {
        client.get().uri("/api/group/getList?id=1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    // ======================== /getList ===============================================

    // ======================== createBill ===============================================

    @Test
    public void testCreateBillPositiveAdmin() {

        Bill testBill = new Bill();
        testBill.setName("TestBillForTesting");
        testBill.setDescription("TestDes");
        testBill.setAmount(77.77);
        testBill.setPaymentMethod("cash");



        client.post().uri("/api/bill/create")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .body(BodyInserters.fromValue(testBill))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("TestBillForTesting")
                .jsonPath("$.description").isEqualTo("TestDes")
                .jsonPath("$.billId").isEqualTo(5)
                .jsonPath("$.owner.password").isEqualTo(null)
                .jsonPath("$.owner.username").isEqualTo("leopold")
                .jsonPath("$.amount").isEqualTo(77.77)
                .jsonPath("$.paymentMethod").isEqualTo("cash");
    }

    @Test
    public void testCreateBillNegativeAdmin() {
        client.post().uri("/api/bill/create")
                .headers(headers -> headers.setBasicAuth("admin", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void testCreateBillPositiveUser() {
        Bill testBill = new Bill();
        testBill.setName("TestBillForTesting");
        testBill.setDescription("TestDes");
        testBill.setAmount(77.77);
        testBill.setPaymentMethod("cash");



        client.post().uri("/api/bill/create")
                .headers(headers -> headers.setBasicAuth("lukas", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .body(BodyInserters.fromValue(testBill))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("TestBillForTesting")
                .jsonPath("$.description").isEqualTo("TestDes")
                .jsonPath("$.billId").isEqualTo(5)
                .jsonPath("$.owner.password").isEqualTo(null)
                .jsonPath("$.owner.username").isEqualTo("lukas")
                .jsonPath("$.amount").isEqualTo(77.77)
                .jsonPath("$.paymentMethod").isEqualTo("cash");
    }

    @Test
    public void testCreateBillNegativeUser() {
        client.post().uri("/api/bill/create")
                .headers(headers -> headers.setBasicAuth("user", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void testCreateBillPleb() {
        client.post().uri("/api/bill/create")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    // ======================== /createBill ===============================================


    // ======================== payBill ===============================================

    @Test
    public void testPayBillPositiveAdmin() {


        client.post().uri("/api/bill/pay?billId=2")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        client.get().uri("/api/group/getAllUserBills")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[4].userBillId").isEqualTo(5)
                .jsonPath("$.[4].user.username").isEqualTo("leopold")
                .jsonPath("$.[4].user.password").isEqualTo(null)
                .jsonPath("$.[4].bill.billId").isEqualTo(2)
                .jsonPath("$.[4].percentage").isEqualTo(0.33)
                .jsonPath("$.[4].paid").isEqualTo(true);
    }

    @Test
    public void testPayBillNegativeAdmin() {
        client.post().uri("/api/bill/pay?billId=2")
                .headers(headers -> headers.setBasicAuth("admin", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void testPayBillPositiveUser() {

        client.post().uri("/api/bill/pay?billId=2")
                .headers(headers -> headers.setBasicAuth("lukas", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        client.get().uri("/api/group/getAllUserBills")
                .headers(headers -> headers.setBasicAuth("lukas", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[5].userBillId").isEqualTo(6)
                .jsonPath("$.[5].user.username").isEqualTo("lukas")
                .jsonPath("$.[5].user.password").isEqualTo(null)
                .jsonPath("$.[5].bill.billId").isEqualTo(2)
                .jsonPath("$.[5].percentage").isEqualTo(0.33)
                .jsonPath("$.[5].paid").isEqualTo(true);
    }

    @Test
    public void testPayBillNegativeUser() {
        client.post().uri("/api/bill/pay?billId=2")
                .headers(headers -> headers.setBasicAuth("user", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void testPayBillPleb() {
        client.post().uri("/api/bill/pay?billId=2")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    // ======================== /payBill ===============================================


    // ======================== createList ===============================================

    @Test
    public void testCreateListPositiveAdmin() {

        List testList = new List();
        testList.setName("TestListForTesting");
        testList.setDescription("TestDes");

        client.post().uri("/api/list/create")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .body(BodyInserters.fromValue(testList))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("TestListForTesting")
                .jsonPath("$.description").isEqualTo("TestDes")
                .jsonPath("$.listId").isEqualTo(4)
                .jsonPath("$.owner.password").isEqualTo(null)
                .jsonPath("$.owner.username").isEqualTo("leopold");
    }

    @Test
    public void testCreateListNegativeAdmin() {
        client.post().uri("/api/list/create")
                .headers(headers -> headers.setBasicAuth("admin", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void testCreateListPositiveUser() {

        List testList = new List();
        testList.setName("TestListForTesting");
        testList.setDescription("TestDes");

        client.post().uri("/api/list/create")
                .headers(headers -> headers.setBasicAuth("lukas", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .body(BodyInserters.fromValue(testList))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("TestListForTesting")
                .jsonPath("$.description").isEqualTo("TestDes")
                .jsonPath("$.listId").isEqualTo(4)
                .jsonPath("$.owner.password").isEqualTo(null)
                .jsonPath("$.owner.username").isEqualTo("lukas");

    }

    @Test
    public void testCreateListNegativeUser() {
        client.post().uri("/api/list/create")
                .headers(headers -> headers.setBasicAuth("user", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void testCreateListPleb() {
        client.post().uri("/api/list/create")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    // ======================== /createList ===============================================


    // ======================== createListItem ===============================================

    @Test
    public void testCreateListItemPositiveAdmin() {

        ListItem testListItem = new ListItem();
        testListItem.setName("apple");
        testListItem.setList(dao.getList(1l));

        client.post().uri("/api/list/createItem")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .body(BodyInserters.fromValue(testListItem))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        client.get().uri("/api/group/getAllListItems")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[4].listItemId").isEqualTo(12)
                .jsonPath("$.[4].name").isEqualTo("apple")
                .jsonPath("$.[4].list.name").isEqualTo("shopping list")
                .jsonPath("$.[4].list.listId").isEqualTo(1)
                .jsonPath("$.[4].list.owner.password").isEqualTo(null)
                .jsonPath("$.[4].list.bill.billId").isEqualTo(3);

    }

    @Test
    public void testCreateListItemNegativeAdmin() {
        client.post().uri("/api/list/createItem")
                .headers(headers -> headers.setBasicAuth("admin", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void testCreateListItemPositiveUser() {


        ListItem testListItem = new ListItem();
        testListItem.setName("apple");
        testListItem.setList(dao.getList(1l));

        client.post().uri("/api/list/createItem")
                .headers(headers -> headers.setBasicAuth("lukas", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .body(BodyInserters.fromValue(testListItem))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        client.get().uri("/api/group/getAllListItems")
                .headers(headers -> headers.setBasicAuth("lukas", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[4].listItemId").isEqualTo(12)
                .jsonPath("$.[4].name").isEqualTo("apple")
                .jsonPath("$.[4].list.name").isEqualTo("shopping list")
                .jsonPath("$.[4].list.listId").isEqualTo(1)
                .jsonPath("$.[4].list.owner.password").isEqualTo(null)
                .jsonPath("$.[4].list.bill.billId").isEqualTo(3);

    }

    @Test
    public void testCreateListItemNegativeUser() {
        client.post().uri("/api/list/createItem")
                .headers(headers -> headers.setBasicAuth("user", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void testCreateListItemPleb() {
        client.post().uri("/api/list/createItem")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    // ======================== /createListItem ===============================================


    // ======================== createUserBill ===============================================

    @Test
    public void testCreateUserBillPositiveAdmin() {

        Bill testBill = new Bill();
        testBill.setName("TestBillForTesting");
        testBill.setDescription("TestDes");
        testBill.setAmount(77.77);
        testBill.setPaymentMethod("cash");



        client.post().uri("/api/bill/create")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .body(BodyInserters.fromValue(testBill))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("TestBillForTesting")
                .jsonPath("$.description").isEqualTo("TestDes")
                .jsonPath("$.billId").isEqualTo(5)
                .jsonPath("$.owner.password").isEqualTo(null)
                .jsonPath("$.owner.username").isEqualTo("leopold")
                .jsonPath("$.amount").isEqualTo(77.77)
                .jsonPath("$.paymentMethod").isEqualTo("cash");


        client.post().uri("/api/bill/createUserBill?billId=5&username=lukas&percentage=0.5")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        client.get().uri("/api/group/getAllUserBills")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[11].userBillId").isEqualTo(12)
                .jsonPath("$.[11].user.username").isEqualTo("lukas")
                .jsonPath("$.[11].user.password").isEqualTo(null)
                .jsonPath("$.[11].bill.billId").isEqualTo(5)
                .jsonPath("$.[11].percentage").isEqualTo(0.5)
                .jsonPath("$.[11].paid").isEqualTo(false);

    }

    @Test
    public void testCreateUserBillNegativeAdmin() {
        client.post().uri("/api/bill/createUserBill?billId=5&username=lukas&percentage=0.5")
                .headers(headers -> headers.setBasicAuth("admin", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void testCreateUserBillPositiveUser() {

        Bill testBill = new Bill();
        testBill.setName("TestBillForTesting");
        testBill.setDescription("TestDes");
        testBill.setAmount(77.77);
        testBill.setPaymentMethod("cash");

        client.post().uri("/api/bill/create")
                .headers(headers -> headers.setBasicAuth("lukas", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .body(BodyInserters.fromValue(testBill))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("TestBillForTesting")
                .jsonPath("$.description").isEqualTo("TestDes")
                .jsonPath("$.billId").isEqualTo(5)
                .jsonPath("$.owner.password").isEqualTo(null)
                .jsonPath("$.owner.username").isEqualTo("lukas")
                .jsonPath("$.amount").isEqualTo(77.77)
                .jsonPath("$.paymentMethod").isEqualTo("cash");

        client.post().uri("/api/bill/createUserBill?billId=5&username=lukas&percentage=0.5")
                .headers(headers -> headers.setBasicAuth("lukas", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        client.get().uri("/api/group/getAllUserBills")
                .headers(headers -> headers.setBasicAuth("lukas", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[11].userBillId").isEqualTo(12)
                .jsonPath("$.[11].user.username").isEqualTo("lukas")
                .jsonPath("$.[11].user.password").isEqualTo(null)
                .jsonPath("$.[11].bill.billId").isEqualTo(5)
                .jsonPath("$.[11].percentage").isEqualTo(0.5)
                .jsonPath("$.[11].paid").isEqualTo(false);


    }

    @Test
    public void testCreateUserBillNegativeUser() {
        client.post().uri("/api/bill/createUserBill?billId=5&username=lukas&percentage=0.5")
                .headers(headers -> headers.setBasicAuth("user", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void testCreateUserBillPleb() {
        client.post().uri("/api/bill/createUserBill?billId=5&username=lukas&percentage=0.5")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    // ======================== /createUserBill ===============================================
}
