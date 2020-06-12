# Simple Producer/CEP/Consumer implementation using Spring Integration.

This is a step forward on the [Simple Producer/Consumer implementation using Spring Integration](https://github.com/jredondo/spring-integration-producer-consumer).

Basically, a [Complex Event Processor](https://en.wikipedia.org/wiki/Complex_event_processing) process (or simple CEP) has been inserted between the originals producer and consumer processes.  
Producer and consumer code remain untouched, so it keeps being a clean implementation using [Spring Integration](https://docs.spring.io/spring-integration/reference/html/overview.html). 
For the scope of this repository's experiments, only Kafka integration is used. 
The main purpose is to offer and simple implementation suitable as the starting point for testing the "statefulness" of stream processing using Flink and Kafka.  

The Flink CEP implementation is under cep/ directory.  

#### Docker Swarm:

As with the [Simple Producer/Consumer implementation using Spring Integration](https://github.com/jredondo/spring-integration-producer-consumer) some rudimentary scripts and Dockerfiles for deploying with Docker Swarm are provided in the following directories: 

```
$ ls -l docker 
total 48
... deploy-app-stack.sh
... deploy-cep-stack.sh
... deploy-flink-stack.sh
... deploy-kafka-stack.sh
... docker-app-stack.yml
... docker-cep-stack.yml
... docker-flink-stack.yml
... docker-kafka-stack.yml
... start-kafka-consumer.sh
... start-kafka-producer.sh
... start-swarm.sh
... stop-swarm.sh
$ ls -l producer/docker/
total 12
... Dockerfile
... launch-app.sh
... setup_image.sh
$ ls -l consumer/docker/
total 12
... Dockerfile
... launch-app.sh
... setup_image.sh
$
```

For them to be useful, first you will need to create a Swarm of Docker nodes. 
Have a look at [Docker Swarm Overview](https://docs.docker.com/engine/swarm/) to get started.

After that you will need to deploy a [private registry as a service](https://docs.docker.com/registry/deploying/#run-the-registry-as-a-service) 
in your swarm. Notice that all that follow supposes this registry runs over HTTPS. For that purpose [this](https://github.com/docker/distribution/issues/948) might be of help.

#### Contact:

In case the information provided here is not enough to be useful and you think it could, or if you have any comment or suggestion, 
feel free to get in contact.
``` 
Email: Jorge Redondo Flames <jorge.redondo -- gmail.com> 
LinkedIn: https://www.linkedin.com/in/jorge-redondo-flames-45589418a/
``` 

