package com.practice.controller;

import com.practice.auth.jwt.JWTWatchListClient;
import com.practice.in_memory_store.InMemoryAccountStore;
import com.practice.model.Symbol;
import com.practice.model.WatchList;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.reactivex.rxjava3.core.Single;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@MicronautTest
public class WatchListControllerTestRx {

    private static final Logger LOG = LoggerFactory.getLogger(WatchListControllerTestRx.class);
    private static final UUID ACCOUNT_ID = WatchListControllerRx.ACCOUNT_ID;

    @Inject
    EmbeddedApplication application;

    @Inject
    @Client("/")
    JWTWatchListClient client;

    @Inject
    InMemoryAccountStore store;

    public BigDecimal randomValue() {
        return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1, 100));
    }

    @Test
    void returnsEmptyWatchListForAccount() {

        final Single<WatchList> result = client.retrieveWatchList(getAuthorizationHeader()).singleOrError();
        assertTrue(result.blockingGet().getSymbols().isEmpty());
        assertTrue(store.getWatchList(ACCOUNT_ID).getSymbols().isEmpty());
    }

    @Test
    void returnWatchListForAccount() {
        final List<Symbol> symbols = Stream.of("AMZN", "NFLX", "AAPL")
                .map(Symbol::new)
                .collect(Collectors.toList());
        WatchList watchList = new WatchList(symbols);
        store.updateWatchList(ACCOUNT_ID, watchList);

        var result = client.retrieveWatchList(getAuthorizationHeader()).singleOrError().blockingGet();
        assertEquals(3, result.getSymbols().size());
        assertEquals(3, store.getWatchList(ACCOUNT_ID).getSymbols().size());
    }

    @Test
    void returnWatchListForAccountAsSingle() {
        final List<Symbol> symbols = Stream.of("AMZN", "NFLX", "AAPL")
                .map(Symbol::new)
                .collect(Collectors.toList());
        WatchList watchList = new WatchList(symbols);
        store.updateWatchList(ACCOUNT_ID, watchList);

        final WatchList result = client.retrieveWatchListAsSingle(getAuthorizationHeader()).blockingGet();
        assertEquals(3, result.getSymbols().size());
        assertEquals(3, store.getWatchList(ACCOUNT_ID).getSymbols().size());
    }

    @Test
    void canUpdateWatchListForAccount() {
        final List<Symbol> symbols = Stream.of("AMZN", "NFLX", "AAPL")
                .map(Symbol::new)
                .collect(Collectors.toList());
        WatchList watchList = new WatchList(symbols);

        final HttpResponse<WatchList> result = client.updateWatchList(getAuthorizationHeader(), watchList);
        assertEquals(HttpResponse.ok().status(), result.getStatus());
        assertEquals(3, result.body().getSymbols().size());
        assertEquals(watchList, store.getWatchList(ACCOUNT_ID));
    }

    @Test
    void canDelteWatchlistForAccount() {
        final List<Symbol> symbols = Stream.of("AMZN", "NFLX", "AAPL")
                .map(Symbol::new)
                .collect(Collectors.toList());
        WatchList watchList = new WatchList(symbols);

        store.updateWatchList(ACCOUNT_ID, watchList);

        assertEquals(3, store.getWatchList(ACCOUNT_ID).getSymbols().size());

        final HttpResponse response = client.deleteWatchList(getAuthorizationHeader(), WatchListControllerTestRx.ACCOUNT_ID);

        assertTrue(store.getWatchList(ACCOUNT_ID).getSymbols().isEmpty());

    }

    private String getAuthorizationHeader() {
        return "Bearer " + givenMyUserLoggedIn().getAccessToken();
    }

    private BearerAccessRefreshToken givenMyUserLoggedIn() {
        return client.login(new UsernamePasswordCredentials("my-user", "secret"));
    }

}
