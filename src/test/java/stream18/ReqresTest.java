package stream18;

import models.LombokCreateUserData;
import models.LombokSingleUserData;
import models.PojoCreateUserDataBody;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static specs.ReqresSpec.reqresRequestSpec;
import static specs.ReqresSpec.reqresResponseSpec;
import static user_data.FakerTestDate.firstName;
import static user_data.FakerTestDate.jobFaker;


public class ReqresTest {
    @Test
    void checkSingleUserId() {
        LombokSingleUserData response = given(reqresRequestSpec)
                .when()
                .get("users/2")
                .then()
                .spec(reqresResponseSpec)
                .statusCode(200)
                .extract().as(LombokSingleUserData.class);

        assertEquals(2, response.getUser().getId());
        assertEquals("janet.weaver@reqres.in", response.getUser().getEmail());
    }

    @Test
    void checkSingleUserNotFound() {

        given(reqresRequestSpec)
                .when()
                .get("/users/23")
                .then()
                .statusCode(404);
    }

    @Test
    void checkListResourceScheme() {

        given(reqresRequestSpec)
                .when()
                .get("/unknown")
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/list_resource_schema"));
    }

    @Test
    void checkCreate() {

        PojoCreateUserDataBody body = new PojoCreateUserDataBody();
        body.setName(firstName);
        body.setJob(jobFaker);

        LombokCreateUserData response = given(reqresRequestSpec)
                .body(body)
                .when()
                .post("/users")
                .then()
                .spec(reqresResponseSpec)
                .statusCode(201)
                .extract().as(LombokCreateUserData.class);

        assertThat(response.getName()).isEqualTo(firstName);
        assertThat(response.getJob()).isEqualTo(jobFaker);

    }

    @Test
    void checkDelete() {

        given(reqresRequestSpec)
                .when()
                .delete("/users/2")
                .then()
                .statusCode(204);

    }

    @Test
    void checkListColor() {
        // @formatter:off
        given(reqresRequestSpec)
                .when()
                .get("/unknown")
                .then()
                .spec(reqresResponseSpec)
                .statusCode(200)
                .body("data.color[3]",
                        equalTo("#7BC4C4"))
                .and()
                .body("data.findAll{it.name =~/./}.name.flatten()",
                        hasItem("aqua sky"))
                .and()
                .body("data.pantone_value.flatten()",
                        hasItems("15-4020", "17-2031", "19-1664", "14-4811", "17-1456", "15-5217"));
// @formatter:on
    }
}