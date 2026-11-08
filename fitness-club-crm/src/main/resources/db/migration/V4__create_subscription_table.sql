CREATE TABLE subscription (
id BIGSERIAL PRIMARY KEY,
client_id BIGINT NOT NULL,
type VARCHAR NOT NULL,
trainer_id BIGINT,
base_price NUMERIC(12,2),
final_price NUMERIC(12,2),
unlimited BOOLEAN NOT NULL,
total_visits INTEGER,
remaining_visits INTEGER,
start_date DATE,
end_date DATE,
status VARCHAR NOT NULL
);