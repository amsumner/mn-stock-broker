package com.practice.repository;

import com.practice.model.persistence.QuoteEntity;
import com.practice.model.persistence.SymbolEntity;
import io.micronaut.data.annotation.Repository;

import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Slice;
import io.micronaut.data.repository.CrudRepository;

import java.math.BigDecimal;
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

    //filter
    List<QuoteDTO> findByVolumeGreaterThanOrderByVolumeAsc(BigDecimal volume);

    //pagination
    List<QuoteDTO> findByVolumeGreaterThanOrderByVolumeAsc(BigDecimal volume, Pageable pageable);

    Slice<QuoteDTO> list(io.micronaut.data.model.Pageable pageable);
}
