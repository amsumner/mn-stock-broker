package com.practice.controller;

import com.practice.model.TestObject;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Controller
public class JsonController {

    @Get("/json")
    public TestObject json () {
        return new TestObject();
    }
}
