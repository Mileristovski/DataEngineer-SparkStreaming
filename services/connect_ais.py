import asyncio
import websockets
from kafka import KafkaProducer
import json
import csv
import os

# Kafka configuration
KAFKA_SERVER = 'localhost:9092'
KAFKA_TOPIC = 'ais_data'

# Initialize Kafka producer
producer = KafkaProducer(bootstrap_servers=KAFKA_SERVER)

# AIS Stream API details
AIS_STREAM_URL = 'wss://stream.aisstream.io/v0/stream'
API_KEY = 'e66dea5edc96977c45bd4ddba1e1b34acf718a04'
BOUNDING_BOXES = [[[-90, -180], [90, 180]]]

# Subscription message
subscription_message = json.dumps({
    "APIKey": API_KEY,
    "BoundingBoxes": BOUNDING_BOXES
})
data_folder = os.path.join("data")
os.makedirs(data_folder, exist_ok=True)


async def connect_to_ais_stream():
    async with websockets.connect(AIS_STREAM_URL) as websocket:
        # Send subscription message
        await websocket.send(subscription_message)
        # Receive and process messages
        async for message in websocket:
            decoded_message = json.loads(message.decode("utf-8"))
            
            key = decoded_message["MessageType"]
            message_json = json.dumps(decoded_message)
            
            # Send the processed message to Kafka
            producer.send(KAFKA_TOPIC, value=message_json.encode("utf-8"), key=key.encode("utf-8"))

                        # Write the message to a CSV file
            # csv_filename = os.path.join(data_folder, f"{key}.csv")
            # csv_file_exists = os.path.exists(csv_filename)
            
            # with open(csv_filename, mode='a', newline='', encoding='utf-8') as csv_file:
            #     csv_writer = csv.DictWriter(csv_file, fieldnames=decoded_message.keys())
                
            #     # Write the header only if the file is new
            #     if not csv_file_exists:
            #         csv_writer.writeheader()
                
            #     csv_writer.writerow(decoded_message)

# Run the WebSocket client
asyncio.get_event_loop().run_until_complete(connect_to_ais_stream())
