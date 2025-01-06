import pygame
from pygame.locals import QUIT, MOUSEBUTTONDOWN
import connect_to_kafka
from database import get_ship_by_mmsi

# Configuration
REST_PROXY_URL = "http://localhost:8082"  
TOPIC_NAME = "ais_data_positions"
HEADERS_AVRO = {
    "Accept": "application/vnd.kafka.avro.v2+json"
}
HEADERS = {
    "Content-Type": "application/vnd.kafka.v2+json"
}

# A dictionary to store ship locations by ShipMMSI
ship_locations = {}

# Pygame settings
SCREEN_WIDTH = 1280     
SCREEN_HEIGHT = 720   
MAP_IMAGE = "GUI/world_map.jpg" 

# Function to update ship locations
def update_ship_locations(message):
    ship_data = message.get("value", {})
    ship_mmsi = ship_data.get("ShipMMSI")
    #if get_ship_by_mmsi(ship_mmsi): 
    latitude = ship_data.get("Latitude")
    longitude = ship_data.get("Longitude")

    if ship_mmsi and latitude and longitude:
        ship_locations[ship_mmsi] = {"Latitude": latitude, "Longitude": longitude}

# Function to convert latitude and longitude to screen coordinates
def lat_lon_to_screen(lat, lon):
    x = (lon + 180) * (SCREEN_WIDTH / 360)  
    y = (90 - lat) * (SCREEN_HEIGHT / 180)  
    return int(x), int(y)


def draw_map(screen, map_image, quit_button, label):
    # Scale and draw the map background
    map_image_scaled = pygame.transform.scale(map_image, (SCREEN_WIDTH, SCREEN_HEIGHT))
    screen.blit(map_image_scaled, (0, 0))

    # Draw ship locations as dots
    for coords in ship_locations.values():
        lat = coords["Latitude"]
        lon = coords["Longitude"]
        x, y = lat_lon_to_screen(lat, lon)
        pygame.draw.circle(screen, (255, 0, 0), (x, y), 1)  

    # Draw the label
    try:
        label_font = pygame.font.SysFont(None, 48)
        label_text = label_font.render(label, True, (0, 0, 0))  
        label_bg = pygame.Rect(SCREEN_WIDTH // 2 - label_text.get_width() // 2 - 10, 5,
                               label_text.get_width() + 20, label_text.get_height() + 10)
        pygame.draw.rect(screen, (255, 255, 255), label_bg)  
        screen.blit(label_text, (label_bg.x + 10, label_bg.y + 5))
    except Exception as e:
        print(f"Error drawing label: {e}")

    # Draw the quit button
    try:
        pygame.draw.rect(screen, (200, 0, 0), quit_button)
        font = pygame.font.SysFont(None, 36) 
        text = font.render("Quit", True, (255, 255, 255)) 
        screen.blit(text, (quit_button.x + 10, quit_button.y + 5))
    except Exception as e:
        print(f"Error drawing quit button: {e}")

def get_clicked_ship(mouse_pos):
    for mmsi, coords in ship_locations.items():
        lat = coords["Latitude"]
        lon = coords["Longitude"]
        ship_x, ship_y = lat_lon_to_screen(lat, lon)
        
        if abs(mouse_pos[0] - ship_x) <= 5 and abs(mouse_pos[1] - ship_y) <= 5:
            return mmsi  
    return None

# Main execution
def main():
    try:
        connect_to_kafka.create_consumer_instance()
        connect_to_kafka.subscribe_to_topic()

        pygame.init()
        screen = pygame.display.set_mode((SCREEN_WIDTH, SCREEN_HEIGHT)) 
        pygame.display.set_caption("Ship Locations")
        map_image = pygame.image.load(MAP_IMAGE).convert()

        quit_button = pygame.Rect(SCREEN_WIDTH - 120, 20, 100, 40)
        label = "Track A Boat"

        running = True
        while running:
            for event in pygame.event.get():
                if event.type == QUIT:
                    running = False
                elif event.type == MOUSEBUTTONDOWN:
                    if quit_button.collidepoint(event.pos):
                        running = False
                    else:
                        clicked_ship = get_clicked_ship(event.pos)
                        if clicked_ship:
                            # Call get_ship_by_mmsi function for the clicked ship
                            ship_data = get_ship_by_mmsi(clicked_ship)
                            if ship_data:
                                print(f"Clicked Ship Data: {ship_data}")

            messages = connect_to_kafka.consume_messages()
            for message in messages:
                update_ship_locations(message)

            draw_map(screen, map_image, quit_button, label)
            pygame.display.flip()

            pygame.time.delay(1000) 

    finally:
        connect_to_kafka.delete_consumer_instance()
        pygame.quit()


if __name__ == "__main__":
    main()
