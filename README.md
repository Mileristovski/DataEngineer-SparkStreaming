# Track a Boat

### Arichitecture :

- Kafka
- Zookeper
- Structured Streaming
- Python

### Le projet :
Ce projet consiste à exploiter les données AIS (Système d'Identification Automatique) pour surveiller et analyser les mouvements des navires en temps réel. 
Grâce à la collecte de ces données en continu, nous serons en mesure de visualiser sur une carte interactive le positionnement actuel de chaque bateau, ainsi que leurs trajectoires et destinations prévues. 
Ce système permet également d'identifier le type de bateau (cargo, ferry, yacht, etc.) et d’afficher des images représentatives de chaque navire, afin d’enrichir l'expérience utilisateur. L’objectif est de fournir un outil de suivi maritime puissant et intuitif, accessible tant pour les professionnels que pour les passionnés du domaine maritime.


### Parametres pour les topics Kafka
cleanup.policy
	delete	DYNAMIC_TOPIC_CONFIG
	
delete.retention.ms
	60000	DYNAMIC_TOPIC_CONFIG
	
retention.bytes
	52428800	DYNAMIC_TOPIC_CONFIG
	
retention.ms
	60000	DYNAMIC_TOPIC_CONFIG
	
segment.bytes
	10485760	DYNAMIC_TOPIC_CONFIG
	
segment.ms
	30000	DYNAMIC_TOPIC_CONFIG
	
