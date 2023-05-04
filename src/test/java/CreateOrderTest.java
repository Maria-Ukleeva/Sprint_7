import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrderTest {

    private  String[] colour;
    public Response response;

    public CreateOrderTest(String[] colour){
        this.colour = colour;
    }

    @Parameterized.Parameters
    public static Object[][] getData(){

        return new Object[][]{
                {new String[]{"BLACK"}},
                {new String[]{"GREY"}},
                {new String[]{"BLACK", "GREY"}},
        };
    }

    @Before
    public void setUp(){
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
    }

    @Test
    public void shouldCreateOrder(){
        Order order = new Order(colour);
        response = given()
                .header("Content-type", "application/json")
                .and()
                .body(order)
                .when()
                .post("/api/v1/orders");

        response.then().statusCode(201).and().assertThat().body("track", notNullValue());

    }

    @After
    public void cleanUp(){
        String track = response.body().asString();
        given()
                .header("Content-type", "application/json")
                .and()
                .body(track)
                .when()
                .put("/api/v1/orders/cancel");
    }
}
