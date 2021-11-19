package com.practice.controller;

import com.practice.in_memory_store.InMemoryStore;
import com.practice.model.Quote;
import com.practice.model.Symbol;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;

import java.util.Optional;

@Controller("/quotes")
public class QuotesController {

    private final InMemoryStore store;

    public QuotesController(InMemoryStore store) {
        this.store = store;
    }

    @Get("/{symbol}")
    public HttpResponse getQuote(@PathVariable String symbol) {
       Optional<Quote> quote = store.fetchQuote(symbol);
        return HttpResponse.ok(quote.get());
    }
}
