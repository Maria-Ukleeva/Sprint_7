import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class OrderListTest {
    String[][] colours = new String[][] {{"BLACK"}, {"GREY"}, {"BLACK", "GREY"}};

    @Before
    public void setUp(){
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
        int randomColour = new Random().nextInt(colours.length);
        Order order = new Order(colours[randomColour]);
        given()
                .header("Content-type", "application/json")
                .and()
                .body(order)
                .when()
                .post("/api/v1/orders");
    }

    @Test
    public void shouldReturnOrderListWithoutCourierId(){

        Response response = given()
                .header("Content-type", "application/json")
                .when()
                .get("/api/v1/orders");
        response.then().statusCode(200).and().assertThat().body("orders", notNullValue());

    }

    @Test
    public void shouldReturnErrorWhenCourierIdIsInvalid(){
        Response response = given()
                .header("Content-type", "application/json")
                .when()
                .get("/api/v1/orders?courierId=1000000");
        response.then().statusCode(404).and().assertThat().body("message", equalTo("Курьер с идентификатором 1000000 не найден"));
    }


}
