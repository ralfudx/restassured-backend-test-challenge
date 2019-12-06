
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.Assert;

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
    public String searchForUser() {
        String userId = "";
        String response = given().
                get("/users").then().extract().asString();
        List list = from(response).getList("findAll { it.username.equals(\"Samantha\") }.id");
        if (!list.isEmpty()) {
            userId = list.get(0).toString();
        }
        return userId;
    }

    //Use the user ID fetched to make a search for all the posts written by the user
    private List<String> searchForPosts(){
        //String userId = searchForUser();
        List <String> postIds = new ArrayList<String>();
        String response = given().
                get("/posts").then().extract().asString();
        //postIds = from(response).getList(String.format("findAll { it.userId.equals(%s) }.id", userId));
        return postIds;
    }

    //Fetch the comments for each post by the user and validate if the emails in the comment section are in the proper format
    @Test
    public void FetchCommentsAndValidateEmail(){
        String regex = "/^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$/";
        String number = "";
        List userPostIds = searchForPosts();

        for (int i = 0; i < userPostIds.size(); i++){
                number = userPostIds.get(i).toString();
                String response = given().
                        get("/comments?postId="+ number).then().extract().asString();
                //check for emails with invalid format
                List inValidEmails = from(response).getList(String.format("findAll { !it.email.matches(%s)}.email", regex));
                if(!inValidEmails.isEmpty()){
                    System.out.printf("The InValid email(s) for post Id: %s is/are %s%n", number, inValidEmails.toString());
                    Assert.assertTrue(!inValidEmails.isEmpty());
                }
        }
    }

}
