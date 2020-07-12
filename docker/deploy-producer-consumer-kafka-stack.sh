echo "Deploying Zookeeper, Kafka and Producer..."

COMPOSE_FILE=docker/docker-producer-kafka-stack.yml
eval $(docker-machine env manager1)

REGISTRY_IP=192.168.99.147 docker stack deploy -c $COMPOSE_FILE producer_consumer
sleep 60
echo "Deploying Consumer-..."
COMPOSE_FILE=docker/docker-only-consumer-stack.yml
REGISTRY_IP=192.168.99.147 docker stack deploy -c $COMPOSE_FILE producer_consumer

docker-machine env --unset

