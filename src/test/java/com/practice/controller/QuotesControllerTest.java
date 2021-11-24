package com.practice.controller;

import com.practice.in_memory_store.InMemoryStore;
import com.practice.model.Quote;
import com.practice.model.Symbol;
import com.practice.model.error.CustomError;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@MicronautTest
public class QuotesControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(QuotesControllerTest.class);

    @Inject
    EmbeddedApplication application;

    //TODO: Find why i cannot use RxHttpClient
    @Inject
    @Client("/") HttpClient client;

    @Inject
    InMemoryStore store;

    public BigDecimal randomValue() {
        return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1,100));
    }

    @Test
    void QuotesControllerTestPerSymbol() {

        final Quote apple = initRandomQuote("APPL");
        store.update(apple);

        final Quote amazon = initRandomQuote("AMZN");
        store.update(amazon);

        final Quote appleResult = client.toBlocking().retrieve("/quotes/APPL", Quote.class);
        LOG.info("Result {}", appleResult);
        assertThat(apple).isEqualToComparingFieldByField(appleResult);

        final Quote amazonResult = client.toBlocking().retrieve("/quotes/AMZN", Quote.class);
        LOG.info("Result {}", amazonResult);
        assertThat(amazon).isEqualToComparingFieldByField(amazonResult);
    }

    @Test
    void returnNotFoundOnUnsupportedSymbol() {
        try {
            client.toBlocking().retrieve(HttpRequest.GET("/quotes/UNSUPPORTED"),
                    Argument.of(Quote.class),
                    Argument.of(CustomError.class)
            );
        } catch (HttpClientResponseException error) {
            assertEquals(HttpStatus.NOT_FOUND, error.getStatus());
            LOG.info("Body {}", error.getResponse().getBody(CustomError.class));
            final Optional<CustomError> customError=  error.getResponse().getBody(CustomError.class);
            assertTrue(customError.isPresent());
            assertEquals(404, customError.get().getStatus());
            assertEquals("NOT_FOUND", customError.get().getError());
            assertEquals("Quote for symbol not available", customError.get().getMessage());
            assertEquals("/quotes/UNSUPPORTED", customError.get().getPath());
        }
    }

    private Quote initRandomQuote(String symbolValue) {
        return Quote.builder()
                .ask(randomValue())
                .bid(randomValue())
                .lastPrice(randomValue())
                .symbol(new Symbol(symbolValue))
                .volume(randomValue())
                .build();
    }
}
