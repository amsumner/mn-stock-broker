package com.practice.repository;

import com.practice.model.SymbolEntity;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

@Singleton
@Requires(notEnv = Environment.TEST)
public class TestDataProvider {

    private static final Logger LOG = LoggerFactory.getLogger(TestDataProvider.class);
    private final SymbolsRepository repository;

    public TestDataProvider(SymbolsRepository repository) {
        this.repository = repository;
    }

    @EventListener
    public void init(StartupEvent event) {
        if (repository.findAll().isEmpty()) {
            LOG.info("Adding Test data as empty database was found!!!");
            Stream.of("APPL", "AMZN", "FB", "TSLA")
                    .map(SymbolEntity::new)
                    .forEach(repository::save);
        }
    }
}
