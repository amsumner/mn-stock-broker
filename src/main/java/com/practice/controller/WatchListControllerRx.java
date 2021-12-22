package com.practice.controller;

import com.practice.in_memory_store.InMemoryAccountStore;
import com.practice.model.WatchList;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import jakarta.inject.Named;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Slf4j
@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/account/watchlist-rx")
public class WatchListControllerRx {

    static final UUID ACCOUNT_ID = UUID.randomUUID();

    private final InMemoryAccountStore store;

    private final Scheduler scheduler;

    public WatchListControllerRx(@Named(TaskExecutors.IO) ExecutorService executorService,
                                 InMemoryAccountStore store) {
        this.store = store;
        this.scheduler = Schedulers.from(executorService);
    }

    @Get(produces = MediaType.APPLICATION_JSON)
    @ExecuteOn(TaskExecutors.IO)
    public WatchList get() {
        log.info("GetWatchList - {}", Thread.currentThread().getName());
        return store.getWatchList(ACCOUNT_ID);
    }

    @Get(value = "/single", produces = MediaType.APPLICATION_JSON)
    public @NonNull Flowable<WatchList> getReactive() {
        log.info("getAsSingle - {}", Thread.currentThread().getName());
        return Single.fromCallable(() -> store.getWatchList(ACCOUNT_ID)).toFlowable().subscribeOn(scheduler);
    }

    @Put(consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ExecuteOn(TaskExecutors.IO)
    public WatchList update(@Body WatchList watchList) {
        return store.updateWatchList(ACCOUNT_ID, watchList);
    }

    @Delete(value = "/{accountId}")
    @ExecuteOn(TaskExecutors.IO)
    public void delete(@PathVariable UUID accountId) {
        store.deleteWatchList(accountId);
    }

}
