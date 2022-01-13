create table symbols
(
    VALUE VARCHAR PRIMARY KEY
);

create table quote
(
    id SERIAL PRIMARY KEY,
    bid NUMERIC,
    ask NUMERIC,
    last_price NUMERIC,
    volume NUMERIC,
    symbol VARCHAR,
    FOREIGN KEY (symbol) REFERENCES symbols(value),
    CONSTRAINT last_price_is_positive CHECK (last_price > 0),
    CONSTRAINT volume_is_positive_or_zero CHECK (last_price > 0)
);