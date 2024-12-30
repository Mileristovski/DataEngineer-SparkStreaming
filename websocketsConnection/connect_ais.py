import asyncio
import websockets
from kafka import KafkaProducer, KafkaAdminClient
from kafka.errors import KafkaError
import json
import os
import logging
from dotenv import load_dotenv
from time import sleep

# Load environment variables from .env
load_dotenv()

logging.basicConfig(
    level=logging.INFO,  # Set logging level
    format='%(asctime)s - %(levelname)s - %(message)s'
)

KAFKA_SERVER = os.getenv('KAFKA_SERVER')
KAFKA_TOPIC = os.getenv('KAFKA_TOPIC')

logging.info(f"Kafka server: {KAFKA_SERVER}")
logging.info(f"Kafka topic: {KAFKA_TOPIC}")

def topic_exists(topic_name):
    """
    Check if a Kafka topic exists.
    """
    try:
        admin_client = KafkaAdminClient(bootstrap_servers=KAFKA_SERVER)
        topics = admin_client.list_topics()
        admin_client.close()
        return topic_name in topics
    except KafkaError as e:
        logging.error(f"Failed to connect to Kafka or fetch topics: {e}")
        return False

def wait_for_topic(topic_name, timeout=300, interval=5):
    """
    Wait for a Kafka topic to exist within a timeout period.
    """
    logging.info(f"Waiting for Kafka topic '{topic_name}' to be ready...")
    elapsed_time = 0
    while elapsed_time < timeout:
        if topic_exists(topic_name):
            logging.info(f"Kafka topic '{topic_name}' is ready.")
            return True
        sleep(interval)
        elapsed_time += interval
    logging.error(f"Timeout reached while waiting for Kafka topic '{topic_name}' to be ready.")
    return False

# Check if the topic exists, if not wait for it
if not wait_for_topic(KAFKA_TOPIC):
    raise RuntimeError(f"Kafka topic '{KAFKA_TOPIC}' is not available. Exiting.")

# Initialize Kafka producer
try:
    producer = KafkaProducer(bootstrap_servers=KAFKA_SERVER)
    logging.info("Successfully connected to Kafka.")
except Exception as e:
    logging.error(f"Failed to connect to Kafka: {e}")
    raise

AIS_STREAM_URL = os.getenv('AIS_STREAM_URL')
API_KEY = os.getenv('API_KEY')

logging.info(f"AIS Stream URL: {AIS_STREAM_URL}")
logging.info(f"API Key: {API_KEY}")

BOUNDING_BOXES = [
    [
        [int(os.getenv("LAT_BOTTOM_LEFT")), int(os.getenv("LON_BOTTOM_LEFT"))],
        [int(os.getenv("LAT_TOP_RIGHT")), int(os.getenv("LON_TOP_RIGHT"))],
    ]
]

logging.info(f"Bounding boxes: {BOUNDING_BOXES}")

subscription_message = json.dumps({
    "APIKey": API_KEY,
    "BoundingBoxes": BOUNDING_BOXES,
    "FilterMessageTypes": ["PositionReport", "ShipStaticData"]
})
logging.info("Subscription message prepared.")

async def connect_to_ais_stream():
    try:
        logging.info(f"Connecting to AIS stream at {AIS_STREAM_URL}...")
        async with websockets.connect(AIS_STREAM_URL) as websocket:
            
            await websocket.send(subscription_message)
            
            async for message in websocket:
                try:
                    decoded_message = json.loads(message.decode("utf-8"))
                    
                    key = decoded_message["MessageType"]
                    message_json = json.dumps(decoded_message)
                    
                    producer.send(KAFKA_TOPIC, value=message_json.encode("utf-8"), key=key.encode("utf-8"))
                except Exception as e:
                    logging.error(f"Failed to process AIS message: {e}")
    except Exception as e:
        logging.error(f"Failed to connect to AIS stream: {e}")
        raise

try:
    asyncio.run(connect_to_ais_stream())
except Exception as e:
    logging.error(f"Script terminated with error: {e}")
