package com.practice.in_memory_store;

import com.practice.model.Symbol;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class InMemoryStore {

    private final List<Symbol> symbols;

    public InMemoryStore() {
        symbols = Stream.of("AMZN","AAPL","FB","GOOG","MSFT","NFLX","TSLA")
                .map(Symbol::new)
                .collect(Collectors.toList());

    }

    public List<Symbol> getAllSymbols() {
        return symbols;
    };
}
