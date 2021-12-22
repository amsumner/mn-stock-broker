package com.practice.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.rxjava3.http.client.Rx3HttpClient;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
public class HelloWorldControllerTest {

    @Inject
    EmbeddedApplication application;

    @Inject
    @Client ("/")
    Rx3HttpClient client;

    @Test
    void returnsJsonObject() {
        final ObjectNode result = client.toBlocking().retrieve("/json", ObjectNode.class);
    }

}
