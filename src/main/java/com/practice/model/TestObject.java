package com.practice.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data

public class TestObject {

    final String myText = "Hello World";
    final BigDecimal id = BigDecimal.valueOf(12345);
    final Instant timeUTC = Instant.now();

}
