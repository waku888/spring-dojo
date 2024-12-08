package com.example.blog;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Base64;

public class SomeTrialTest {

    @Test
    public void base64() {
        var uuid = "a20ebddb-02bd-4465-8981-feb2c19d9289";
        var encodedBytes = Base64.getEncoder().encode(uuid.getBytes());
        var encodedString = new String(encodedBytes);
        System.out.println(encodedString);
// =>YTIwZWJkZGItMDJiZC00NDY1LTg5ODEtZmViMmMxOWQ5Mjg5
//   YTIwZWJkZGItMDJiZC00NDY1LTg5ODEtZmViMmMxOWQ5Mjg5
    }


    @Test
    public void bcrypt() {
        var encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode("password00"));
        System.out.println(encoder.encode("password00"));
        System.out.println(encoder.encode("password00"));
// 同じパスワードでも出力値が異なることに注意<= ソルトの付加
    }
}
