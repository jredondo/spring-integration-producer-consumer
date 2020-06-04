# Simple Producer/Consumer implementation using Spring Integration.

This is a still an incipient implementation of the Producer/Consumer pattern using (Spring Integration)[https://docs.spring.io/spring-integration/reference/html/overview.html]. 
Its _main_ purpose is to show the decoupling of producer's and consumer's logic from the integration mechanisms provided out of the box by Spring Integration.
Two integration mechanism are provided so far: TCP and Kafka.

#### Docker Swarm:

Some rudimentary scripts and Dockerfiles for deploying with Docker Swarm are provided in the following directories: 

```
$ ls -l docker/
total 32
... deploy-app-stack.sh   
... deploy-kafka-stack.sh
... docker-app-stack.yml
... docker-kafka-stack.yml
... start-kafka-consumer.sh
... start-kafka-producer.sh
... start-swarm.sh
... stop-swarm.sh
$
$ ls -l producer/docker
... Dockerfile
... launch-app.sh
... setup_image.sh
$
$ ls -l consumer/docker
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

#### License:

You are free to do whatever you want with whatever you find here. 
