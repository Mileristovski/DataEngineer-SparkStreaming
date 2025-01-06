import psycopg2
from psycopg2.extras import RealDictCursor
from dotenv import load_dotenv
import os


load_dotenv()

def get_ship_by_mmsi(mmsi):
    connection = None
    try:
        host = os.environ.get("HOST")
        port = os.environ.get("PORT")
        database = os.environ.get("DATABASE")
        user = os.environ.get("USER")
        password = os.environ.get("PASSWORD")

        connection = psycopg2.connect(
            host=host,
            port=port,
            database=database,
            user=user,
            password=password
        )

        
        cursor = connection.cursor(cursor_factory=RealDictCursor)
        query = "SELECT * FROM ships WHERE \"MMSI\" = %s;"

        cursor.execute(query, (int(mmsi),))

        ship = cursor.fetchone()
        print(ship)
        return ship

    except psycopg2.Error as e:
        print(f"Database error: {e}")
        return None

    finally:
        if connection:
            connection.close()


if __name__ == "__main__":
    mmsi = 240389000

    ship = get_ship_by_mmsi(mmsi)
    if ship:
        print(f"Ship found: {ship}")
    else:
        print(f"No ship found with MMSI {mmsi}.")
