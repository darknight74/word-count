# Word Count App

This app is designed to count the number of words in a text file and return the most and least commons.

## Description

The app uses Spring Boot, MongoDB and RabbitMQ to achieve the goal.

Prerequisite to run the app are MongoDB and RabbitMQ servers running. RabbitMQ management plugin is also required because is used by application to check that the current queue is empty. To enable that plugin `rabbitmq-plugins enable rabbitmq_management`

MongoDB is used to store the read lines and the counted words. RabbitMQ is used as a mean to dispatch jobs for counting words to different processes. The counting processes rely on mapReduce MongoDB feature tos split the lines and count/update elements in MongoDB.

The application therefore works in a basic master/slave fashion.

## Usage

To launch the application in master mode use

`-jar target/word-counter-0.0.1-SNAPSHOT.jar 
--mongo.host=<mongodb_host> --mongo.port=<mongodb_port> --source=<path_to_file_to_count> --rabbit.admin-host=<rabbit_admin_host> --rabbit.admin-port=<rabbit_admin_port> --rabbit.addresses=<rabbit_cluster_addresses> --rabbit.queue=<rabbit_queue_to_use>`

* `rabbit.addresses` is used to specify the list of rabbit servers in the cluster in the `form host1:port1,host2:port2...`
* is possible to specify a specific rabbitMQ exchange to be used using `--rabbit.exchange` (it defaults to null thus using the default rabbit exchange)

To launch the application in slave mode simply omit the source flag

Once the count ends, the master node prints out the result on the command line

It's possible at any time to query the current result using the provided endpoint `/count` on any server: all servers start with random port but is possible to bound them to a specific port using the switch `--server.port`

## Notes
Included tests need running MongoDB and RabbitMQ server to pass: a docker-compose file is included to simply startup manually those services for testing purpose. A better solution is to integrate docker-compose in the maven file using specific plugins.

I originally thought to use a queue implemented in MongoDB using a specific collection reading jobs using the findOneAndUpdate method but this was a slow solution due to locks in MongoDB.

Because RabbitMQ uses just one thread per queue, there's a bottleneck in RabbitMQ. Possibly this could be solved using one of the following solutions:
* sharding the exchange (https://github.com/rabbitmq/rabbitmq-sharding): in this scenario it's important to specify both the switch for the exchange and the queue. 
Unfortunately I wasn't able to fully test this solution because the listener was connecting correctly to the assigned queue but was not reading out of it meaning that I was missing some value to correctly set the reading mode.
* launch Rabbit in cluster mode

Possibly a better solution is to use Apache Kafka or even AWS SQS but I was not able to explore those solutions due to time constraints.  







