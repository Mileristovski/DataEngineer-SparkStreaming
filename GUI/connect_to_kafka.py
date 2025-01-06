import requests
import sys

REST_PROXY_URL = "http://localhost:8082"  # Replace with your Kafka REST Proxy URL
TOPIC_NAME = "ais_data_positions"  # Replace with your topic name
HEADERS_AVRO = {
    "Accept": "application/vnd.kafka.avro.v2+json"
}
HEADERS = {
    "Content-Type": "application/vnd.kafka.v2+json"
}

# Function to consume messages from the Kafka topic
def consume_messages():
    url = f"{REST_PROXY_URL}/consumers/my_consumer_group/instances/my_instance/records"
    try:
        response = requests.get(url, headers=HEADERS_AVRO, timeout=50)
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        print(f"Error fetching messages: {e}", file=sys.stderr)
        return []

# Function to create a consumer instance
def create_consumer_instance():
    url = f"{REST_PROXY_URL}/consumers/my_consumer_group"
    payload = {
        "name": "my_instance",
        "format": "avro",
        "auto.offset.reset": "earliest",
        "auto.commit.enable": "false"
    }
    try:
        response = requests.post(url, headers=HEADERS, json=payload, timeout=10)
        response.raise_for_status()
        print("Consumer instance created.")
    except requests.exceptions.RequestException as e:
        print(f"Error creating consumer instance: {e}", file=sys.stderr)

# Function to subscribe to the topic
def subscribe_to_topic():
    url = f"{REST_PROXY_URL}/consumers/my_consumer_group/instances/my_instance/subscription"
    payload = {"topics": [TOPIC_NAME]}
    try:
        response = requests.post(url, headers=HEADERS, json=payload, timeout=10)
        response.raise_for_status()
        print(f"Subscribed to topic: {TOPIC_NAME}")
    except requests.exceptions.RequestException as e:
        print(f"Error subscribing to topic: {e}", file=sys.stderr)

# Function to delete the consumer instance
def delete_consumer_instance():
    url = f"{REST_PROXY_URL}/consumers/my_consumer_group/instances/my_instance"
    try:
        response = requests.delete(url, headers=HEADERS, timeout=10)
        response.raise_for_status()
        print("Consumer instance deleted.")
    except requests.exceptions.RequestException as e:
        print(f"Error deleting consumer instance: {e}", file=sys.stderr)