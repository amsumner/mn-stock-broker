package com.practice.controller;

import com.practice.in_memory_store.InMemoryAccountStore;
import com.practice.in_memory_store.InMemoryStore;
import com.practice.model.Quote;
import com.practice.model.Symbol;
import com.practice.model.WatchList;
import com.practice.model.error.CustomError;
import io.micronaut.core.type.Argument;
import io.micronaut.http.*;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.rxjava3.http.client.Rx3HttpClient;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;
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

import static io.micronaut.http.HttpRequest.GET;
import static io.micronaut.http.HttpRequest.PUT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@MicronautTest
public class WatchListControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(WatchListControllerTest.class);
    private static final UUID ACCOUNT_ID = WatchListController.ACCOUNT_ID;
    public static final String ACCOUNT_WATCHLIST = "/account/watchlist";

    @Inject
    EmbeddedApplication application;

    //TODO: Find why i cannot use RxHttpClient
    @Inject
    @Client("/")
    Rx3HttpClient client;

    @Inject
    InMemoryAccountStore store;

    public BigDecimal randomValue() {
        return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1, 100));
    }

    private BearerAccessRefreshToken getBearerAccessRefreshToken() {
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
                "my-user", "secret");

        var login = HttpRequest.POST("/login", credentials);
        var response = client.toBlocking().exchange(
                login, BearerAccessRefreshToken.class);

        assertEquals(HttpResponse.ok().status(), response.getStatus());
        BearerAccessRefreshToken token = response.body();
        assertNotNull(token);
        assertEquals("my-user", token.getUsername());
        return token;
    }

    @Test
    void unauthorizedAccessIsForbidden() {
        try {
            client.toBlocking().retrieve(ACCOUNT_WATCHLIST);
            fail("Should fail if no exception is thrown");
        } catch (HttpClientResponseException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
        }
    }


    @Test
    void returnsEmptyWatchListForAccount() {

        BearerAccessRefreshToken token = getBearerAccessRefreshToken();

        var request = GET(ACCOUNT_WATCHLIST)
                .accept(MediaType.APPLICATION_JSON)
                .bearerAuth(token.getAccessToken());

        final WatchList result = client.toBlocking().retrieve(request, WatchList.class);
        assertTrue(result.getSymbols().isEmpty());
        assertTrue(store.getWatchList(ACCOUNT_ID).getSymbols().isEmpty());
    }



    @Test
    void returnWatchListForAccount() {

        BearerAccessRefreshToken token = getBearerAccessRefreshToken();

        final List<Symbol> symbols = Stream.of("AMZN", "NFLX", "AAPL")
                .map(Symbol::new)
                .collect(Collectors.toList());
        WatchList watchList = new WatchList(symbols);
        store.updateWatchList(ACCOUNT_ID, watchList);

        var request = GET(ACCOUNT_WATCHLIST)
                .accept(MediaType.APPLICATION_JSON)
                .bearerAuth(token.getAccessToken());

        final WatchList result = client.toBlocking().retrieve(request, WatchList.class);
        assertEquals(3, result.getSymbols().size());
        assertEquals(3, store.getWatchList(ACCOUNT_ID).getSymbols().size());
    }

    @Test
    void canUpdateWatchListForAccount() {

        BearerAccessRefreshToken token = getBearerAccessRefreshToken();

        final List<Symbol> symbols = Stream.of("AMZN", "NFLX", "AAPL")
                .map(Symbol::new)
                .collect(Collectors.toList());
        WatchList watchList = new WatchList(symbols);

        var request = PUT(ACCOUNT_WATCHLIST,watchList)
                .accept(MediaType.APPLICATION_JSON)
                .bearerAuth(token.getAccessToken());


        final HttpResponse<WatchList> result = client.toBlocking()
                .exchange(request);
        assertEquals(HttpResponse.ok().status(), result.getStatus());
        assertEquals(watchList, store.getWatchList(ACCOUNT_ID));
    }

    @Test
    void canDelteWatchlistForAccount() {

        BearerAccessRefreshToken token = getBearerAccessRefreshToken();

        final List<Symbol> symbols = Stream.of("AMZN", "NFLX", "AAPL")
                .map(Symbol::new)
                .collect(Collectors.toList());
        WatchList watchList = new WatchList(symbols);

        store.updateWatchList(ACCOUNT_ID, watchList);

        assertEquals(3, store.getWatchList(ACCOUNT_ID).getSymbols().size());

        var request = HttpRequest.DELETE("/account/watchlist/" + ACCOUNT_ID)
                .accept(MediaType.APPLICATION_JSON)
                .bearerAuth(token.getAccessToken());

        final HttpResponse response = client.toBlocking()
                .exchange(request);

        assertTrue(store.getWatchList(ACCOUNT_ID).getSymbols().isEmpty());


    }


}
