
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
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


    //Search for the user and return the user ID
    private String searchForUser() {
        String userId = "";
        String response = get("/users").asString();
        List list = from(response).getList("findAll { it.username.equals(\"Samantha\") }.id");
        if (!list.isEmpty()) {
            userId = list.get(0).toString();
        }
        return userId;
    }

    //Use the user ID fetched to make a search for all the posts written by the user
    private List<String> searchForPosts(){
        String userId = searchForUser();
        List <String> postIds = new ArrayList<String>();
        String response = get("/posts").asString();
        postIds = from(response).getList(String.format("findAll { it.userId.equals(%s) }.id", userId));
        return postIds;
    }

    @Test
    public void FetchCommentsAndValidateEmail(){
        System.out.println("Finally.. " + searchForPosts());

        //For each post, fetch the comments and validate if the emails in the comment section are in the proper format
    }

}
