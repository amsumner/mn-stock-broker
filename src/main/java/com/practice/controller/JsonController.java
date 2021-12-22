package com.practice.controller;

import com.practice.model.TestObject;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

@Controller
@Secured(SecurityRule.IS_ANONYMOUS)
public class JsonController {

    @Get("/json")
    public TestObject json () {
        return new TestObject();
    }
}
