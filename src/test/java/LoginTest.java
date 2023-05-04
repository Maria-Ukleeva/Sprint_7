import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class LoginTest {
    public String login;
    private Response response;

    @Before
    public void setUp(){
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
        login = "mu" + new Random().nextInt(1000);
        Courier courier = new Courier(login, "1234", "Maria");
        given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier");
    }

    @Test
    public void shouldSuccessfullyLoginWithValidCredentials(){
        Credentials credentials = new Credentials(login, "1234");
        response = given()
                .header("Content-type", "application/json")
                .and()
                .body(credentials)
                .when()
                .post("/api/v1/courier/login");
        response.then().statusCode(200).and().assertThat().body("id", notNullValue());
    }

    @Test
    public void shouldRespondWithErrorIfPasswordIsMissing(){
        Credentials credentials = new Credentials(login, null);
        response = given()
                .header("Content-type", "application/json")
                .and()
                .body(credentials)
                .when()
                .post("/api/v1/courier/login");
        response.then().statusCode(400).and().assertThat().body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    public void shouldRespondWithErrorIfLoginIsMissing(){
        Credentials credentials = new Credentials(null,"1234");
        response = given()
                .header("Content-type", "application/json")
                .and()
                .body(credentials)
                .when()
                .post("/api/v1/courier/login");
        response.then().statusCode(400).and().assertThat().body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    public void shouldRespondWithErrorIfCredentialsInvalid(){
        Credentials credentials = new Credentials(login, "4321");
        response = given()
                .header("Content-type", "application/json")
                .and()
                .body(credentials)
                .when()
                .post("/api/v1/courier/login");
        response.then().statusCode(404).and().assertThat().body("message", equalTo("Учетная запись не найдена"));
    }

    @After
    public void cleanUp(){
            String id = response.body().asString().substring(6, response.body().asString().length() - 1);
            User user = new User(id);
            given()
                    .header("Content-type", "application/json")
                    .and()
                    .body(user)
                    .when()
                    .delete("/api/v1/courier/" + id);

    }
}
