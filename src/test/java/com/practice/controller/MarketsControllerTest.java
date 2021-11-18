package com.practice.controller;

import com.practice.model.Symbol;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
public class MarketsControllerTest {

    @Inject
    EmbeddedApplication application;

    //TODO: Find why i cannot use RxHttpClient
    @Inject
    @Client("/") HttpClient client;

    @Test
    void marketsControllerTest() {
        final List<LinkedHashMap<String, String>> result = client.toBlocking().retrieve("/markets", List.class);
        assertEquals(7, result.size());
        assertThat(result)
                .extracting(entry -> entry.get("value"))
                .containsExactlyInAnyOrder("AMZN","AAPL","FB","GOOG","MSFT","NFLX","TSLA");
    }
}
