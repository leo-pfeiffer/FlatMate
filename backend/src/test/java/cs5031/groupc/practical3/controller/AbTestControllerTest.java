package cs5031.groupc.practical3.controller;

import static org.junit.jupiter.api.Assertions.fail;

import cs5031.groupc.practical3.Practical3Application;
import cs5031.groupc.practical3.database.DataAccessObject;
import cs5031.groupc.practical3.SqlFileReader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

@ContextConfiguration
@SpringBootTest
public class AbTestControllerTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    DataAccessObject dao;

    WebTestClient client;
    ConfigurableApplicationContext ctx;

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
        ctx = SpringApplication.run(Practical3Application.class, args);

        client = WebTestClient.bindToServer().baseUrl("http://localhost:8080").build();
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

    @Test
    public void testAbTestAccessible() {
        client.post().uri("/_ab_testing/track?name=experiment&variant=a&event=test")
                .accept(MediaType.ALL)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void testAbTestAccessibleFromUser() {
        client.post().uri("/_ab_testing/track?name=experiment&variant=a&event=test")
                .headers(headers -> headers.setBasicAuth("lukas", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.ALL)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void testAbTestAccessibleFromAdmin() {
        client.post().uri("/_ab_testing/track?name=experiment&variant=a&event=test")
                .headers(headers -> headers.setBasicAuth("leopold", "87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61"))
                .accept(MediaType.ALL)
                .exchange()
                .expectStatus().isOk();
    }
}
