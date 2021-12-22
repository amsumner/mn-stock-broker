package com.practice.controller;

import com.practice.in_memory_store.InMemoryAccountStore;
import com.practice.model.WatchList;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/account/watchlist")
public class WatchListController {

    static final UUID ACCOUNT_ID = UUID.randomUUID();

    private final InMemoryAccountStore store;

    public WatchListController(InMemoryAccountStore store) {
        this.store = store;
    }

    @Get(produces = MediaType.APPLICATION_JSON)
    public WatchList get() {
        log.info("GetWatchList - {}", Thread.currentThread().getName());
        return store.getWatchList(ACCOUNT_ID);
    }

    @Put(consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public WatchList update(@Body WatchList watchList) {
        return store.updateWatchList(ACCOUNT_ID, watchList);
    }

    @Delete(value = "/{accountId}")
    public void delete(@PathVariable UUID accountId) {
        store.deleteWatchList(accountId);
    }

}
