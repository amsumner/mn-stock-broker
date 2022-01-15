package com.practice.repository;

import com.practice.model.persistence.QuoteEntity;
import com.practice.model.persistence.SymbolEntity;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

@Singleton
@Requires(notEnv = Environment.TEST)
public class TestDataProvider {

    private static final Logger LOG = LoggerFactory.getLogger(TestDataProvider.class);
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    private final SymbolsRepository symbolRepository;
    private final QuotesRepository quotesRepository;

    public TestDataProvider(SymbolsRepository repository, QuotesRepository quotesRepository) {
        this.symbolRepository = repository;
        this.quotesRepository = quotesRepository;
    }

    @EventListener
    public void init(StartupEvent event) {
        if (symbolRepository.findAll().isEmpty()) {
            LOG.info("Adding Test data as empty database was found!!!");
            Stream.of("APPL", "AMZN", "FB", "TSLA")
                    .map(SymbolEntity::new)
                    .forEach(symbolRepository::save);
        }
        if (quotesRepository.findAll().isEmpty()) {
            LOG.info("Adding test quote data as empty database was found!!!");
            symbolRepository.findAll().forEach(symbol -> {
                var quote = new QuoteEntity();
                quote.setSymbol(symbol);
                quote.setAsk(randomValue());
                quote.setBid(randomValue());
                quote.setLastPrice(randomValue());
                quote.setVolume(randomValue());
                quotesRepository.save(quote);
            });
        }
    }

    private BigDecimal randomValue() {
        return BigDecimal.valueOf(RANDOM.nextDouble(1,100));
    }
}
