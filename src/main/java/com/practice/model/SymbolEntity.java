package com.practice.model;

import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Introspected
@Table(name = "symbols", schema = "mn")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SymbolEntity {

    @Id
    private String value;

}
