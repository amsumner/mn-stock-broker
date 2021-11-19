package com.practice.controller;

import com.practice.in_memory_store.InMemoryStore;
import com.practice.model.Quote;
import com.practice.model.Symbol;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
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
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
