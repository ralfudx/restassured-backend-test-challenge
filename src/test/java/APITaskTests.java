
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Set;

import static io.restassured.RestAssured.*;
import static io.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.*;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class APITaskTests {

    @BeforeClass
    public void setup(){
        baseURI = "https://jsonplaceholder.typicode.com";
        ResponseSpecBuilder builder = new ResponseSpecBuilder();
        builder.expectStatusCode(200);
        builder.expectContentType(ContentType.JSON);
        responseSpecification = builder.build();
    }


    private String searchForUserID(String username) {
        String userId = "";
        String response = get("/users").asString();
        List list = from(response).getList("findAll { it.username.equals(username) }.id");
        if (!list.isEmpty()) {
            System.out.println(list.get(0));
            System.out.printf("Cover/%s/all%n", list.get(0));
            userId = list.get(0).toString();
        }
        return userId;

        //Search for the user
        //Use the details fetched to make a search for the posts written by the user
        //For each post, fetch the comments and validate if the emails in the comment section are in the proper format
    }

    @Test
    public void validateEmail(){
        System.out.println("Finally.. " + searchForUserID("Samantha"));
    }

    //@Test
    public void getAllCountries() {
                given().
                        get("https://jsonplaceholder.typicode.com/users").
                        then().
                        statusCode(200).
                        contentType(ContentType.JSON).
                        body("username", equalTo("Samantha"), "id");
                //body("username", hasItems("Antonette", "Samantha", "Leopoldo_Corkery"));
    }
}
