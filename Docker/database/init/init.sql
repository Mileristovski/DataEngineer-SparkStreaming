CREATE TABLE ais_positions (
    id SERIAL PRIMARY KEY,
    latitude FLOAT NOT NULL,
    longitude FLOAT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE OR REPLACE FUNCTION limit_table_size()
RETURNS TRIGGER AS $$
BEGIN
    IF (SELECT COUNT(*) FROM ais_positions) > 5000 THEN
        DELETE FROM ais_positions
        WHERE id IN (
            SELECT id FROM ais_positions
            ORDER BY timestamp ASC
            LIMIT 1000
        );
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER check_table_size
AFTER INSERT ON ais_positions
FOR EACH STATEMENT
EXECUTE FUNCTION limit_table_size();
