echo "Deploying Zookeeper, Kafka and Producer..."
COMPOSE_FILE=/hosthome/jredondo/Projects/spring-integration-producer-consumer/docker/docker-producer-kafka-stack.yml
docker-machine ssh manager1 REGISTRY_IP=192.168.99.147 docker stack deploy -c $COMPOSE_FILE producer_consumer
sleep 60
echo "Deploying Consumer-..."
COMPOSE_FILE=/hosthome/jredondo/Projects/spring-integration-producer-consumer/docker/docker-only-consumer-stack.yml
docker-machine ssh manager1 REGISTRY_IP=192.168.99.147 docker stack deploy -c $COMPOSE_FILE producer_consumer

