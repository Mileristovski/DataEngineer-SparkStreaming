import asyncio
import websockets
from kafka import KafkaProducer
import json

# Kafka configuration
KAFKA_SERVER = 'localhost:9092'
KAFKA_TOPIC = 'ais_data'

# Initialize Kafka producer
producer = KafkaProducer(bootstrap_servers=KAFKA_SERVER)

# AIS Stream API details
AIS_STREAM_URL = 'wss://stream.aisstream.io/v0/stream'
API_KEY = 'e66dea5edc96977c45bd4ddba1e1b34acf718a04'
BOUNDING_BOXES = [[[25.835302, -80.207729], [25.602700, -79.879297]]]

# Subscription message
subscription_message = json.dumps({
    "APIKey": API_KEY,
    "BoundingBoxes": BOUNDING_BOXES
})

async def connect_to_ais_stream():
    async with websockets.connect(AIS_STREAM_URL) as websocket:
        # Send subscription message
        await websocket.send(subscription_message)
        # Receive and process messages
        async for message in websocket:
            decoded_message = json.loads(message.decode("utf-8"))
            
            # Extract key and merge "Message" content with the rest
            key = decoded_message["MessageType"]
            message_data = decoded_message.pop("Message")
            message_data.update(decoded_message)
            message_data.pop("MessageType")
            # Serialize the modified data back to JSON
            message_json = json.dumps(message_data)
            
            # Send the processed message to Kafka
            producer.send(KAFKA_TOPIC, value=message_json.encode("utf-8"), key=key.encode("utf-8"))

# Run the WebSocket client
asyncio.get_event_loop().run_until_complete(connect_to_ais_stream())
