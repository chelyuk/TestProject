import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Assert;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class MyTest {

    public String getToken;
    RequestSpecification requestSpecification;
    ResponseSpecification responseSpecification;
    private static final String BASE_URI = "https://restful-booker.herokuapp.com";
    private static final String CONTENT_TYPE = "application/json";

    @BeforeClass
    public void beforeClass() {
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.setBaseUri(BASE_URI);
        requestSpecBuilder.setContentType(ContentType.JSON);
        requestSpecBuilder.log(LogDetail.ALL);
        requestSpecification = requestSpecBuilder.build();

        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .log(LogDetail.ALL);
        responseSpecification = responseSpecBuilder.build();
    }

    @Test
    public void authToken() {
        String payload = "{\n" +
                "   \"username\" : \"admin\", \n" +
                "   \"password\" : \"password123\"\n" +
                "}";
        Response response = given()
                .baseUri(BASE_URI)
                .contentType(CONTENT_TYPE)
                .body(payload)
        .when()
                .post("/auth")
        .then()
                .log().all()
                .extract().response();
        getToken = response.jsonPath().getString("token");
        System.out.println("Token: " + getToken);
     }
//  GET request
     @Test
    public void getBookingIds() {
        int bookingID = given()
                .baseUri(BASE_URI)
        .when()
                .get("/booking")
        .then()
                .extract().response().path("bookingid[0]");
        System.out.println("BookingID: " + bookingID);
//      Assert using Hamcrest
        assertThat(bookingID,equalTo(15));
//      Assert using TestNG
        Assert.assertEquals(bookingID, 15);
     }
//  PUT request
     @Test
    public void updateBooking() {
        String payload = "{\n" +
                "   \"firstname\" : \"James\",\n" +
                "   \"lastname\" : \"Brown\",\n" +
                "   \"totalprice\" : 444,\n" +
                "   \"depositpaid\" : true,\n" +
                "   \"bookingdates\" : {\n" +
                "        \"checkin\" : \"2018-01-01\" \n" +
                "        \"checkout\" : \"2019-01-01\" \n" +
                "   },\n" +
                "   \"additionalneeds\" : \"Breakfast\"\n" +
                "}";
        given()
                .baseUri(BASE_URI)
                .contentType(CONTENT_TYPE)
                .body(payload)
                .header("Cookie", "token=" + getToken)
                .log().all()
        .when()
                .put("/booking/1")
        .then()
                .log().all()
                .assertThat()
                .statusCode(200);
     }
//  PATCH request
     @Test
    public void partialUpdateBooking() {
        String payload = "{\n" +
                "   \"firstname\" : \"Abishek\",\n" +
                "   \"lastname\" : \"Chauhan\"\n" +
                "}";
        given()
                .baseUri(BASE_URI)
                .contentType(CONTENT_TYPE)
                .header("Cookie", "token=" + getToken)
                .body(payload)
                .log().all()
        .when()
                .patch("/booking/15")
        .then()
                .log().all()
                .assertThat()
                .statusCode(200);
     }
//     DELETE request
     @Test
    public void deleteBooking() {
        given()
                .baseUri(BASE_URI)
                .contentType(CONTENT_TYPE)
                .header("Cookie", "token=" + getToken)
                .log().all()
        .when()
                .delete("/booking/15")
        .then()
                .log().all()
                .assertThat()
                .statusCode(201);
     }
//     Validate status code
     @Test
    public void validateStatusCode() {
        given()
                .baseUri(BASE_URI)
                .contentType(CONTENT_TYPE)
        .when()
                .get("/booking")
        .then()
                .assertThat()
                .statusCode(200);
     }
//     Assert responsebody
     @Test
    public void createBooking() {
         String payload = "{\n" +
                 "   \"firstname\" : \"Jim\",\n" +
                 "   \"lastname\" : \"Brown\",\n" +
                 "   \"totalprice\" : 111,\n" +
                 "   \"depositpaid\" : true,\n" +
                 "   \"bookingdates\" : {\n" +
                 "        \"checkin\" : \"2018-01-01\" \n" +
                 "        \"checkout\" : \"2019-01-01\" \n" +
                 "   },\n" +
                 "   \"additionalneeds\" : \"Breakfast\"\n" +
                 "}";
         given()
                 .baseUri(BASE_URI)
                 .contentType(CONTENT_TYPE)
                 .body(payload)
         .when()
                 .post("/booking")
         .then()
                 .log().all()
                 .assertThat()
                 .statusCode(200)
                 .body("booking.firstname", equalTo("Jim"),
                         "booking.lastname", equalTo("Brown"),
                         "booking.totalprice", equalTo(111));
     }
//     Extract single field and validate
    @Test
    public void validateSingleFieldUsingHamcrest() {
        int bookingID = given()
                .baseUri(BASE_URI)
                .contentType(CONTENT_TYPE)
        .when()
                .get("/booking")
        .then()
                .extract()
                .response()
                .path("bookingid[0]");
        System.out.println("BookingId: " + bookingID);
//        Assert using Hamcrest
        assertThat(bookingID, equalTo(15));
//        Assert using TestNG
        Assert.assertEquals(bookingID, 15);
    }
//    Send payload as file
    @Test
    public void sendPayloadAsFile() {
        File payload = new File("src/main/resources/SendPayloadAsFile.json");
        Response response = given().spec(requestSpecification)
                .header("Cookie", "token=" + getToken)
                .body(payload)
        .when()
                .patch("/booking/15")
        .then()
                .extract().response();
    }
}
