package com.example.blog.it;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegistrationAndLoginIT {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void integrationTest() {
//      ユーザー作成
        var xsrfToken = getRoot();
        register(xsrfToken);

//      ログイン失敗
//      Cookie にXSRF-TOKEN がない
//      ヘッダーにX-XSRF-TOKEN がない
//      Cookie のXSRF-TOKEN とヘッダーのX-XSRF-TOKEN の値が異なる
//      ユーザー名が存在しない
//      パスワードがデータベースに保存されているパスワードと違う
//      ログイン成功
//      ユーザー名がデータベースに存在する
//      パスワードがデータベースに保存されているパスワードと違う
//      Cookie のXSRF-TOKEN とヘッダーのX-XSRF-TOKEN の値が一致する
//      → 200 OK が返る
//      → レスポンスにSet-Cookie: JSESSIONID が返ってくる

    }

    private String getRoot() {

        // ## Arrange ##

        // ## Act ##
        var responseSpec = webTestClient.get().uri("/").exchange();

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
        var bodyJson = """
        {
        "username": "user2",
        "password": "password2"
        }
        """;
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
}
