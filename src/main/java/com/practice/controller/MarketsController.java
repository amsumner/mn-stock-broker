package com.practice.controller;

import com.practice.in_memory_store.InMemoryStore;
import com.practice.model.Symbol;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

import java.util.List;

@Controller("/markets")
public class MarketsController {

    private final InMemoryStore inMemoryStore;

    public MarketsController(InMemoryStore inMemoryStore) {
        this.inMemoryStore = inMemoryStore;
    }

    @Get("/")
    public List<Symbol> all() {
        return inMemoryStore.getAllSymbols();
    }
}
