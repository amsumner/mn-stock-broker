package com.practice.controller;

import com.practice.in_memory_store.InMemoryStore;
import com.practice.model.Symbol;
import com.practice.model.persistence.SymbolEntity;
import com.practice.repository.SymbolsRepository;
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

import java.util.List;

@Controller("/markets")
@Secured(SecurityRule.IS_ANONYMOUS)
public class MarketsController {

    private final InMemoryStore inMemoryStore;
    private final SymbolsRepository repository;

    public MarketsController(InMemoryStore inMemoryStore, SymbolsRepository repository) {
        this.inMemoryStore = inMemoryStore;
        this.repository = repository;
    }

    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @Operation(summary = "Return All available markets")
    @Tag(name = "markets")
    @Get("/")
    public List<Symbol> all() {
        return inMemoryStore.getAllSymbols();
    }

    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @Operation(summary = "Return All available markets from database via JPA")
    @Tag(name = "markets")
    @Get("/jpa")
    public List<SymbolEntity> allSymbolsViaJpa() {
        return repository.findAll();
    }


}
