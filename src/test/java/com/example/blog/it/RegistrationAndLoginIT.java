package com.example.blog.it;

import com.example.blog.service.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegistrationAndLoginIT {

    private static final String TEST_USERNAME = "test_username1";
    private static final String TEST_PASSWORD = "password10";
    private static final String DUMMY_SESSION_ID = "session_id_1";
    private static final String SESSION_COOKIE_NAME = "SESSION";

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void beforeEach() {
        userService.delete(TEST_USERNAME);
    }
    @AfterEach
    public void afterEach() {
        userService.delete(TEST_USERNAME);
    }

    @Test
    public void integrationTest() {
//      ユーザー作成
        var xsrfToken = getCsrfCookie();
        register(xsrfToken);

//      ログイン失敗
//      Cookie にXSRF-TOKEN がない
        loginFailure_NoXSRFTokenInCookie(xsrfToken);

//      ヘッダーにX-XSRF-TOKEN がない
        loginFailure_NoXXSRFTokenInHeader(xsrfToken);

//      Cookie のXSRF-TOKEN とヘッダーのX-XSRF-TOKEN の値が異なる
        loginFailure_DifferentToken(xsrfToken);

//      ユーザー名が存在しない
        loginFailure_GivenUsernameDoesNotExistInDatabase(xsrfToken);

//      パスワードがデータベースに保存されているパスワードと違う
        loginFailure_GivenPasswordIsNotSameAsPasswordInDatabase(xsrfToken);

//      ログイン成功
        loginSuccess(xsrfToken);
    }

    private String getCsrfCookie() {

        // ## Arrange ##

        // ## Act ##
        var responseSpec = webTestClient.get().uri("/csrf-cookie").exchange();

        // ## Assert ##
        var response = responseSpec.returnResult(String.class);
        var xsrfTokenOpt = Optional.ofNullable(response.getResponseCookies().getFirst("XSRF-TOKEN"));

        responseSpec.expectStatus().isNoContent();
        assertThat(xsrfTokenOpt)
                .isPresent()
                .hasValueSatisfying(xsrfTokenCoolie ->
                        assertThat(xsrfTokenCoolie.getValue()).isNotBlank()
                );
        return xsrfTokenOpt.get().getValue();
    }
    private void register(String xsrfToken){
        // ## Arrange ##
        var bodyJson = String.format("""
        {
        "username": "%s",
        "password": "%s"
        }
        """, TEST_USERNAME, TEST_PASSWORD);

        // ## Act ##
        var responseSpec = webTestClient
                .post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie("XSRF-TOKEN", xsrfToken)
                .header("X-XSRF-TOKEN", xsrfToken)
                .bodyValue(bodyJson)
                .exchange();
        // ## Assert ##
        responseSpec.expectStatus().isCreated();

    }

    private void loginSuccess(String xsrfToken){

        // ## Arrange ##
        var bodyJson = String.format("""
        {
        "username": "%s",
        "password": "%s"
        }
        """, TEST_USERNAME, TEST_PASSWORD);

        // ## Act ##
        var responseSpec = webTestClient
                .post().uri("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie("XSRF-TOKEN", xsrfToken)
                .cookie(SESSION_COOKIE_NAME, DUMMY_SESSION_ID)
                .header("X-XSRF-TOKEN", xsrfToken)
                .bodyValue(bodyJson)
                .exchange();
        // ## Assert ##
        responseSpec
                .expectStatus().isOk()
                .expectCookie().value(SESSION_COOKIE_NAME, v -> assertThat(v)
                        .isNotBlank()
                        .isNotEqualTo(DUMMY_SESSION_ID))
        ;

    }

    private void loginFailure_NoXSRFTokenInCookie(String xsrfToken){
        // ## Arrange ##
        var bodyJson = String.format("""
        {
        "username": "%s",
        "password": "%s"
        }
        """, TEST_USERNAME, TEST_PASSWORD);

        // ## Act ##
        var responseSpec = webTestClient
                .post().uri("/login")
                .contentType(MediaType.APPLICATION_JSON)
//                .cookie("XSRF-TOKEN", xsrfToken)
                .cookie(SESSION_COOKIE_NAME, DUMMY_SESSION_ID)
                .header("X-XSRF-TOKEN", xsrfToken)
                .bodyValue(bodyJson)
                .exchange();
        // ## Assert ##
        responseSpec.expectStatus().isForbidden();
    }

    private void loginFailure_NoXXSRFTokenInHeader(String xsrfToken){
        // ## Arrange ##
        var bodyJson = String.format("""
        {
        "username": "%s",
        "password": "%s"
        }
        """, TEST_USERNAME, TEST_PASSWORD);

        // ## Act ##
        var responseSpec = webTestClient
                .post().uri("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie("XSRF-TOKEN", xsrfToken)
                .cookie(SESSION_COOKIE_NAME, DUMMY_SESSION_ID)
//                .header("X-XSRF-TOKEN", xsrfToken)
                .bodyValue(bodyJson)
                .exchange();
        // ## Assert ##
        responseSpec.expectStatus().isForbidden();
    }

    private void loginFailure_DifferentToken(String xsrfToken){
        // ## Arrange ##
        var bodyJson = String.format("""
        {
        "username": "%s",
        "password": "%s"
        }
        """, TEST_USERNAME, TEST_PASSWORD);

        // ## Act ##
        var responseSpec = webTestClient
                .post().uri("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie("XSRF-TOKEN", xsrfToken)
                .cookie(SESSION_COOKIE_NAME, DUMMY_SESSION_ID)
                .header("X-XSRF-TOKEN", xsrfToken + "_invalid") //03-24
                .bodyValue(bodyJson)
                .exchange();
        // ## Assert ##
        responseSpec.expectStatus().isForbidden();
    }

    private void loginFailure_GivenUsernameDoesNotExistInDatabase(String xsrfToken){
        // ## Arrange ##
        var bodyJson = String.format("""
        {
        "username": "%s",
        "password": "%s"
        }
        """, TEST_USERNAME + "_invalid", TEST_PASSWORD); //03-25 "_invalid"

        // ## Act ##
        var responseSpec = webTestClient
                .post().uri("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie("XSRF-TOKEN", xsrfToken)
                .cookie(SESSION_COOKIE_NAME, DUMMY_SESSION_ID)
                .header("X-XSRF-TOKEN", xsrfToken)
                .bodyValue(bodyJson)
                .exchange();
        // ## Assert ##
        responseSpec.expectStatus().isUnauthorized();
    }

    private void loginFailure_GivenPasswordIsNotSameAsPasswordInDatabase(String xsrfToken){
        // ## Arrange ##
        var bodyJson = String.format("""
        {
        "username": "%s",
        "password": "%s"
        }
        """, TEST_USERNAME, TEST_PASSWORD + "_invalid"); //03-026 "_invalid"

        // ## Act ##
        var responseSpec = webTestClient
                .post().uri("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie("XSRF-TOKEN", xsrfToken)
                .cookie(SESSION_COOKIE_NAME, DUMMY_SESSION_ID)
                .header("X-XSRF-TOKEN", xsrfToken)
                .bodyValue(bodyJson)
                .exchange();
        // ## Assert ##
        responseSpec.expectStatus().isUnauthorized();
    }

}
