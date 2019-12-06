
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


    //helper function to search for user
    private String searchForUser() {
        String userId = "";
        String response = given().
                get("/users").then().extract().asString();
        List usernameId = from(response).getList("findAll { it.username.equals(\"Samantha\") }.id");
        if (!usernameId.isEmpty()) {
            userId = usernameId.get(0).toString();
        }
        return userId;
    }

    //helper function to search for posts
    private List<String> searchForPosts(){
        String userId = searchForUser();
        List <String> postIds;
        String response = given().
                get("/posts").then().extract().asString();
        postIds = from(response).getList(String.format("findAll { it.userId.equals(%s) }.id", userId));
        return postIds;
    }

    //Search for the user and return the user ID
    @Test
    public void searchUserAndValidateUserID(){
        String userId = searchForUser();
        if (userId == "" || userId == null) {
            Assert.assertFalse(userId == "");
            Assert.assertFalse(userId == null);
        }
    }


    //Use the user ID fetched to make a search for all the posts written by the user
    @Test
    public void searchAllPostsByUserAndValidatePostIDs(){
        List <String> postIds = searchForPosts();
        if (postIds == null){
            Assert.assertFalse(postIds == null);
        }
        if(postIds.isEmpty()){
            //This ensures that the test does NOT fail for a user without any post - simply print a message
            System.out.println("User has no posts yet!.... PostIds: " + postIds);
            Assert.assertTrue(postIds.isEmpty());
        }
    }


    //Fetch the comments for each post by the user and validate if the emails in the comment section are in the proper format
    @Test
    public void FetchCommentsAndValidateEmail(){
        String regex = "/^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$/";
        String number = "";
        List userPostIds = searchForPosts();

        //check to see that the user has posts before iterating (in case user has no posts)
        if(!userPostIds.isEmpty()) {
            for (int i = 0; i < userPostIds.size(); i++) {
                number = userPostIds.get(i).toString();
                String response = given().
                        get("/comments?postId=" + number).then().extract().asString();

                //check for emails with invalid format
                List inValidEmails = from(response).getList(String.format("findAll { !it.email.matches(%s)}.email", regex));
                if (!inValidEmails.isEmpty()) {
                    System.out.printf("The InValid email(s) for post Id: %s is/are %s%n", number, inValidEmails.toString());
                    Assert.assertTrue(inValidEmails.isEmpty());
                }
            }
        }
    }

}
