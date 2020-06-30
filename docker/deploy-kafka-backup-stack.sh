echo "Deploying Kafka BACKUP..."
COMPOSE_FILE=/hosthome/jredondo/Projects/flink-cep-spring-integration/docker/docker-kafka-backup-stack.yml
docker-machine ssh manager1 REGISTRY_IP=192.168.99.147 MANAGER_IP=192.168.99.147 docker stack deploy -c $COMPOSE_FILE producer_consumer

