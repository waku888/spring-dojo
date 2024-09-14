package com.example.blog;

import org.junit.jupiter.api.Test;

import java.util.Base64;

public class SpringSessionBase64Test {

    @Test
    public void test() {
        var uuid = "a20ebddb-02bd-4465-8981-feb2c19d9289";
        var encodedBytes = Base64.getEncoder().encode(uuid.getBytes());
        var encodedString = new String(encodedBytes);
        System.out.println(encodedString);
// =>YTIwZWJkZGItMDJiZC00NDY1LTg5ODEtZmViMmMxOWQ5Mjg5
//   YTIwZWJkZGItMDJiZC00NDY1LTg5ODEtZmViMmMxOWQ5Mjg5
    }
}
