package com.practice.controller;

import com.practice.in_memory_store.InMemoryAccountStore;
import com.practice.model.WatchList;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;

import java.util.UUID;

@Controller("/account/watchlist")
public class WatchListController {

    static final UUID ACCOUNT_ID = UUID.randomUUID();

    private final InMemoryAccountStore store;

    public WatchListController(InMemoryAccountStore store) {
        this.store = store;
    }

    @Get(produces = MediaType.APPLICATION_JSON)
    public WatchList get() {
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
