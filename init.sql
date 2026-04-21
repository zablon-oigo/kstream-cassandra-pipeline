-- Create keyspace

CREATE KEYSPACE sales
WITH replication = {
    'class': 'SimpleStrategy',
    'replication_factor': 1
}
AND durable_writes = true;

-- Switch to sales keyspace
USE sales;

-- Stores every transaction event from Kafka.
CREATE TABLE sales_raw (
    order_id text PRIMARY KEY,
    category text,
    city text,
    country text,
    customer_id text,
    email text,
    first_name text,
    last_name text,
    latitude double,
    longitude double,
    price double,
    product_id text,
    product_name text,
    quantity int,
    timestamp bigint
);

-- Customer Profile Table
CREATE TABLE customer_profile (
    customer_id text PRIMARY KEY,
    city text,
    country text,
    email text,
    first_name text,
    last_name text
);

-- Stores aggregated analytics per country.
CREATE TABLE customer_counts_by_country (
    id uuid PRIMARY KEY,
    country text,
    customer_count bigint
);

-- Stores location events for tracking/analytics.
CREATE TABLE geo (
    id text PRIMARY KEY,
    latitude double,
    longitude double,
    received_at timestamp DEFAULT now()
);

-- Top Product per Window (1-min analytics)

CREATE TABLE top_product_per_window (
    window_start bigint,
    product_name text,
    quantity bigint,
    PRIMARY KEY (window_start, product_name)
);

-- Fetch all customer counts by country
SELECT * FROM customer_counts_by_country;

-- Filter by specific countries
SELECT * FROM customer_counts_by_country 
WHERE country IN ('Kenya', 'Madagascar', 'Cameroon', 'United States of America');