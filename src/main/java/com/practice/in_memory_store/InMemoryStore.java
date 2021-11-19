package com.practice.in_memory_store;

import com.practice.model.Quote;
import com.practice.model.Symbol;
import jakarta.inject.Singleton;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class InMemoryStore {

    private final List<Symbol> symbols;
    private final ThreadLocalRandom current = ThreadLocalRandom.current();
    private Map<String, Quote> cachedQuotes = new HashMap<>();

    public InMemoryStore() {
        symbols = Stream.of("AMZN","AAPL","FB","GOOG","MSFT","NFLX","TSLA")
                .map(Symbol::new)
                .collect(Collectors.toList());
        symbols.forEach(symbol -> {
            cachedQuotes.put(symbol.getValue(), randomQuote(symbol));
        });
    }

    private Quote randomQuote(final Symbol symbol) {
      return   Quote.builder()
                .ask(randomValue())
                .bid(randomValue())
                .lastPrice(randomValue())
                .symbol(symbol)
                .volume(randomValue())
                .build();
    }

    public List<Symbol> getAllSymbols() {
        return symbols;
    };

    public Optional<Quote> fetchQuote(final String symbol) {
        return Optional.ofNullable(cachedQuotes.get(symbol));
    }

    public BigDecimal randomValue() {
        return BigDecimal.valueOf(current.nextDouble(1,100));
    }

    public void update(Quote quote) {
    cachedQuotes.put(quote.getSymbol().getValue(), quote);
    }
}
