package com.practice.repository;

import com.practice.model.persistence.QuoteEntity;
import com.practice.model.persistence.SymbolEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuotesRepository extends CrudRepository<QuoteEntity, Integer> {

    @Override
    List<QuoteEntity> findAll();

    Optional<QuoteEntity> findBySymbol(SymbolEntity symbol);

    // ordering
    List<QuoteDTO> listOrderByVolumeDesc();

    List<QuoteDTO> listOrderByVolumeAsc();
}
