-- Create the ships table
CREATE TABLE ships (
    id SERIAL PRIMARY KEY,
    MMSI BIGINT NOT NULL,
    ShipName VARCHAR(100) NOT NULL
);

-- Modify ais_positions table to include a foreign key to ships
CREATE TABLE ais_positions (
    id SERIAL PRIMARY KEY,
    ship_id INT NOT NULL,
    latitude FLOAT NOT NULL,
    longitude FLOAT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ship
        FOREIGN KEY (ship_id) 
        REFERENCES ships (id)
);

-- Trigger function and trigger for table size limit remain unchanged
CREATE OR REPLACE FUNCTION limit_table_size()
RETURNS TRIGGER AS $$
BEGIN
    IF (SELECT COUNT(*) FROM ais_positions) > 250000 THEN
        DELETE FROM ais_positions
        WHERE id IN (
            SELECT id FROM ais_positions
            ORDER BY timestamp ASC
            LIMIT 10000
        );
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER check_table_size
AFTER INSERT ON ais_positions
FOR EACH STATEMENT
EXECUTE FUNCTION limit_table_size();
