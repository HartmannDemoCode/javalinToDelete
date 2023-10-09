package dk.cphbusiness;

import static org.junit.jupiter.api.Assertions.*;
import static io.restassured.RestAssured.given;
import org.junit.jupiter.api.*;

import static org.hamcrest.Matchers.*;

class PersonRessourceTest {

    private final String BASE_URL = "http://localhost:7777";
    @BeforeAll
    static void setUpAll() {
        PersonRessource.runServer(7777);
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Hul igennem")
    void testHulIgennem(){
        // given, when, then
        given()
                .when()
                .get(BASE_URL + "/person")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Get all persons")
    void testGetAll(){
        // given, when, then
        given()
                .when()
                .get(BASE_URL + "/person")
                .then()
                .body("size()", is(3));
    }
    @Test
    @DisplayName("Get all persons check first person")
    void testAllBody(){
        // given, when, then
        given()
                .when()
                .get(BASE_URL + "/person")
                .prettyPeek()
                .then()
                .body("1.firstName", is("Hans"));
    }
    @Test
    @DisplayName("Get all persons 2")
    void testAllBody2(){
        // given, when, then
        given()
                .when()
                .get(BASE_URL + "/person")
                .prettyPeek()
                .then()
                .body();
    }
}