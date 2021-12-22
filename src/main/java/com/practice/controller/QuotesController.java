package com.practice.controller;

import com.practice.in_memory_store.InMemoryStore;
import com.practice.model.Quote;
import com.practice.model.error.CustomError;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Optional;

@Controller("/quotes")
@Secured(SecurityRule.IS_ANONYMOUS)
public class QuotesController {

    private final InMemoryStore store;

    public QuotesController(InMemoryStore store) {
        this.store = store;
    }

    @Operation(summary = "Returns a quote for the given symbol")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @ApiResponse(responseCode = "400", description = "Invalid symbol")
    @Tag(name = "Quotes")
    @Get("/{symbol}")
    public HttpResponse getQuote(@PathVariable String symbol) {
       final Optional<Quote> quote = store.fetchQuote(symbol);
       if (quote.isEmpty()) {
           final CustomError notFound = CustomError.builder()
                   .status(HttpStatus.NOT_FOUND.getCode())
                   .error(HttpStatus.NOT_FOUND.name())
                   .message("Quote for symbol not available")
                   .path("/quotes/"+symbol)
                   .build();
           return HttpResponse.notFound(notFound);
       }
        return HttpResponse.ok(quote.get());
    }
}
