import asyncio
import websockets
from kafka import KafkaProducer
import json
import os
import logging
from dotenv import load_dotenv

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
        [int(os.getenv("BOUNDING_BOXES_1")), int(os.getenv("BOUNDING_BOXES_2"))], 
        [int(os.getenv("BOUNDING_BOXES_3")), int(os.getenv("BOUNDING_BOXES_4"))], 
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
