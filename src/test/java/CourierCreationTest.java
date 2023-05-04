import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CourierCreationTest {

    public String login;


    @Before
    public void setUp(){
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
        login = "mu" + new Random().nextInt(1000);
    }

   @Test
    public void shouldCreateNewUniqueCourierSuccessfully() {
       Courier courier = new Courier(login, "1234", "Maria");
       Response response = given()
               .header("Content-type", "application/json")
               .and()
               .body(courier)
               .when()
               .post("/api/v1/courier");

           response.then().statusCode(201).and().assertThat().body("ok", equalTo(true));

   }



    @Test
    public void shouldNotCreateNotUniqueUser(){
        Courier courier = new Courier(login, "1234", "Maria");
        given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier");
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier");

            response.then().statusCode(409).and().assertThat().body("message", equalTo("Этот логин уже используется"));
    }

    @Test
    public void shouldRespondWithErrorIfPasswordIsMissing(){
        Courier courier = new Courier(login, null, "Maria");
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier");

        response.then().statusCode(400).and().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    public void shouldRespondWithErrorIfLoginIsMissing(){
        Courier courier = new Courier(null, "1234", "Maria");
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier");

        response.then().statusCode(400).and().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"));

    }

    @After
    public void cleanUp(){
            Credentials credentials = new Credentials(login, "1234");
            Response response2 = given()
                    .header("Content-type", "application/json")
                    .and()
                    .body(credentials)
                    .when()
                    .post("/api/v1/courier/login");

            String id = response2.body().asString().substring(6, response2.body().asString().length() - 1);
            User user = new User(id);
            given()
                    .header("Content-type", "application/json")
                    .and()
                    .body(user)
                    .when()
                    .delete("/api/v1/courier/" + id);


    }
}
