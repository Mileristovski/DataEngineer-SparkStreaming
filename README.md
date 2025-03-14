# Track a Boat  

## 📌 Présentation  

**Track a Boat** est un projet de surveillance et d’analyse en temps réel des mouvements des navires en exploitant les données AIS (*Automatic Identification System*). L’objectif est d’offrir un outil puissant et intuitif pour suivre la position des bateaux, analyser leurs trajectoires et prévoir leurs destinations.  

Ce système est conçu pour fonctionner en **mode distribué**, ce qui permet un traitement **haute performance** du flux de données (~300 messages/seconde). Il est particulièrement utile pour :  

- **Les professionnels du maritime** : suivi des navires, gestion des risques climatiques, optimisation des chaînes logistiques.  
- **Les passionnés** : exploration et visualisation interactive des mouvements des bateaux.  

## 🏗️ Architecture  

### 📌 Technologies utilisées  

Le projet repose sur une **architecture distribuée** et s’appuie sur les technologies suivantes :  

- **Scala & Apache Spark (Structured Streaming)** : traitement des flux de données en temps réel.  
- **Apache Kafka & Zookeeper** : gestion des files d’attente et distribution des messages.  
- **Docker** : conteneurisation pour un déploiement simplifié.  
- **Python (Pygame & PowerBI)** : visualisation interactive et analyses avancées.  
- **Hadoop** : gestion du stockage et optimisation du traitement des données.  

### ⚙️ Paramètres des Topics Kafka  

Les topics Kafka sont configurés pour optimiser la gestion du flux de données :  

| Paramètre              | Valeur     | Description |
|------------------------|-----------|------------|
| **cleanup.policy**     | `delete`  | Suppression automatique des messages expirés |
| **delete.retention.ms**| `60000`   | Durée avant suppression des messages (60s) |
| **retention.bytes**    | `52428800`| Taille maximale d’un topic (50MB) |
| **retention.ms**       | `60000`   | Durée de conservation des messages (60s) |
| **segment.bytes**      | `10485760`| Taille maximale d’un segment (10MB) |
| **segment.ms**         | `30000`   | Temps avant rotation d’un segment (30s) |

## 🌍 Fonctionnalités  

- **Visualisation en temps réel** 📍 : carte interactive affichant la position et les trajectoires des bateaux.  
- **Heatmap dynamique** 🔥 : représentation graphique de la densité des navires sur la carte.  
- **Filtrage par type de navire** ⛵ : différenciation des cargos, ferries, yachts, etc.  
- **Analyses avancées** 📊 : via PowerBI pour l’optimisation des opérations et la gestion des risques.  
- **Déploiement distribué** 🚀 : possibilité de diviser la carte en 4/6/8 morceaux pour mieux gérer la charge de données.  

## 🚀 Déploiement avec Docker Compose  

Chaque dossier contient un fichier `.yml` permettant de **démarrer uniquement les services nécessaires**.  

### 📂 **Structure des services**  

```
dev/
│── Kafka/                                 # Service Kafka & Zookeeper
│── Kafka_WebSocketsConnection/            # Connexion Kafka ↔ WebSockets
│── Kafka_WebSocketsConnection_Streaming/  # Streaming en temps réel via WebSockets
│── StructuredStreaming/                   # Traitement des données avec Spark Streaming
│── WebSocketsConnection/                  # Service WebSockets indépendant
```

### 🛠️ **Lancer un service spécifique**  

1. **Démarrer Kafka uniquement**  
   ```bash
   cd dev/Kafka
   docker-compose -f kafka.yml up -d
   ```

2. **Lancer Kafka + WebSockets**  
   ```bash
   cd dev/Kafka_WebSocketsConnection
   docker-compose -f kafka_websockets.yml up -d
   ```

3. **Activer l'ensemble des services pour un déploiement complet**  
   ```bash
   cd dev
   docker-compose -f full_deployment.yml up -d
   ```

💡 **Astuce** : Vérifiez le contenu des fichiers `.yml` pour voir quels services sont démarrés avant de les exécuter !  
