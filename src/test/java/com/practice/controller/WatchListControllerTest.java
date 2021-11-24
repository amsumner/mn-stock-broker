package com.practice.controller;

import com.practice.in_memory_store.InMemoryAccountStore;
import com.practice.in_memory_store.InMemoryStore;
import com.practice.model.Quote;
import com.practice.model.Symbol;
import com.practice.model.WatchList;
import com.practice.model.error.CustomError;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@MicronautTest
public class WatchListControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(WatchListControllerTest.class);
    private static final UUID ACCOUNT_ID = WatchListController.ACCOUNT_ID;

    @Inject
    EmbeddedApplication application;

    //TODO: Find why i cannot use RxHttpClient
    @Inject
    @Client("/account") HttpClient client;

    @Inject
    InMemoryAccountStore store;

    public BigDecimal randomValue() {
        return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1,100));
    }

    @Test
    void returnsEmptyWatchListForAccount() {
    final WatchList result = client.toBlocking()
            .retrieve(HttpRequest.GET("/watchlist"), Argument.of(WatchList.class));
    assertTrue(result.getSymbols().isEmpty());
    assertTrue(store.getWatchList(ACCOUNT_ID).getSymbols().isEmpty());
    }

    @Test
    void returnWatchListForAccount() {
        final List<Symbol> symbols = Stream.of("AMZN", "NFLX", "AAPL")
                .map(Symbol::new)
                .collect(Collectors.toList());
        WatchList watchList = new WatchList(symbols);
        store.updateWatchList(ACCOUNT_ID, watchList);

        final WatchList result = client.toBlocking()
                .retrieve(HttpRequest.GET("/watchlist"), WatchList.class);
        assertEquals(3 , result.getSymbols().size());
        assertEquals(3, store.getWatchList(ACCOUNT_ID).getSymbols().size());
    }

    @Test
    void canUpdateWatchListForAccount() {
        final List<Symbol> symbols = Stream.of("AMZN", "NFLX", "AAPL")
                .map(Symbol::new)
                .collect(Collectors.toList());
        WatchList watchList = new WatchList(symbols);

        final HttpResponse<WatchList> result = client.toBlocking()
                .exchange(HttpRequest.PUT("/watchlist", watchList), WatchList.class);
        assertEquals(HttpResponse.ok().status(), result.getStatus());
        assertEquals(3 , result.body().getSymbols().size());
        assertEquals(watchList, store.getWatchList(ACCOUNT_ID));
    }

    @Test void canDelteWatchlistForAccount() {
        final List<Symbol> symbols = Stream.of("AMZN", "NFLX", "AAPL")
                .map(Symbol::new)
                .collect(Collectors.toList());
        WatchList watchList = new WatchList(symbols);

        store.updateWatchList(ACCOUNT_ID ,watchList);

        assertEquals(3, store.getWatchList(ACCOUNT_ID).getSymbols().size());

        final HttpResponse response = client.toBlocking()
                .exchange(HttpRequest.DELETE("/watchlist/" + ACCOUNT_ID));

        assertTrue(store.getWatchList(ACCOUNT_ID).getSymbols().isEmpty());



    }


}
