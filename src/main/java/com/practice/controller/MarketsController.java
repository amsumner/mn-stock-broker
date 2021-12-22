package com.practice.controller;

import com.practice.in_memory_store.InMemoryStore;
import com.practice.model.Symbol;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Controller("/markets")
@Secured(SecurityRule.IS_ANONYMOUS)
public class MarketsController {

    private final InMemoryStore inMemoryStore;

    public MarketsController(InMemoryStore inMemoryStore) {
        this.inMemoryStore = inMemoryStore;
    }

    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @Operation(summary = "Return All available markets")
    @Tag(name = "markets")
    @Get("/")
    public List<Symbol> all() {
        return inMemoryStore.getAllSymbols();
    }
}
