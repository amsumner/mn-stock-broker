package com.practice.controller;

import com.practice.in_memory_store.InMemoryStore;
import com.practice.model.Quote;
import com.practice.model.error.CustomError;
import com.practice.model.persistence.QuoteEntity;
import com.practice.model.persistence.SymbolEntity;
import com.practice.repository.QuoteDTO;
import com.practice.repository.QuotesRepository;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.query.QueryModel;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller("/quotes")
@Secured(SecurityRule.IS_ANONYMOUS)
public class QuotesController {

    private final InMemoryStore store;
    private final QuotesRepository repository;

    public QuotesController(InMemoryStore store, QuotesRepository repository) {
        this.store = store;
        this.repository = repository;
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


    @Get("/jpa")
    public List<QuoteEntity> getAllQuotesViaJPA() {
        return repository.findAll();
    }


    @Operation(summary = "Returns a quote for the given symbol via JPA fetched from the DB")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @ApiResponse(responseCode = "400", description = "Invalid symbol")
    @Tag(name = "Quotes")
    @Get("/{symbol}/jpa")
    public HttpResponse getQuoteViaJPA(@PathVariable String symbol) {
        final Optional<QuoteEntity> quote = repository.findBySymbol(new SymbolEntity(symbol));
        if (quote.isEmpty()) {
            final CustomError notFound = CustomError.builder()
                    .status(HttpStatus.NOT_FOUND.getCode())
                    .error(HttpStatus.NOT_FOUND.name())
                    .message("Quote for symbol not available in DB")
                    .path("/quotes/" + symbol + "/jpa")
                    .build();
            return HttpResponse.notFound(notFound);
        }
        return HttpResponse.ok(quote.get());
    }

    @Get("/jpa/ordered/desc")
    public List<QuoteDTO> orderedDesc() {
        return repository.listOrderByVolumeDesc();
    }

    @Get("/jpa/ordered/asc")
    public List<QuoteDTO> orderedAsc() {
        return repository.listOrderByVolumeAsc();
    }

    @Get("/jpa/{volume}")
    public List<QuoteDTO> volumeFilter (@PathVariable BigDecimal volume) {
        return repository.findByVolumeGreaterThanOrderByVolumeAsc(volume);
    }

    @Get("/jpa/pagination{?page,volume}")
    public List<QuoteDTO> volumeFilterPagination (@QueryValue Optional<Integer> page,
                                                   @QueryValue Optional<BigDecimal> volume) {
        int myPage = page.isEmpty() ? 0 : page.get();
        BigDecimal myVolume = volume.isEmpty() ? BigDecimal.ZERO : volume.get();
        return repository.findByVolumeGreaterThanOrderByVolumeAsc(myVolume, Pageable.from(myPage, 2));
    }

    @Get("/jpa/pagination/{page}")
    public List<QuoteDTO> allWithPagination (@PathVariable int page) {
        return repository.list(Pageable.from(page, 1)).getContent();
    }

}
