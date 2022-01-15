package com.practice.model.persistence;

import io.micronaut.core.annotation.Introspected;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity(name = "quote")
@Table(name = "quote", schema = "mn")
@Data
@Introspected
public class QuoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(targetEntity = SymbolEntity.class)
    @JoinColumn(name = "symbol", referencedColumnName = "value")
    private SymbolEntity symbol;

    private BigDecimal bid;

    private BigDecimal ask;
    @Column(name = "last_price")
    private BigDecimal lastPrice;

    private BigDecimal volume;

}
