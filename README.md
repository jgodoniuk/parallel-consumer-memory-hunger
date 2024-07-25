# parallel-consumer-memory-hunger
This repo is a supplementary project to the issue reported in confluentinc/parallel-consumer GitHub repository. It is a simulation of a possible memory problem in Parallel Consumer's ShardManager.retryQueue. 

### How to run.

1. To run this simulation a running Kafka server is necessary of course. I used a local Kafka installation, but Docker image or cloud Kafka can be used as well.
2. Run `org.example.ParallelConsumerApp` class. It runs a consumer, observing a topic with name: `kafka-test-topic` on a `localhost:9092`. When an other than local Kafka installation is used, KAFKA_HOST constant in this class needs to be set appropriatelly.
3. There is a `produce.sh` script in `./commands` folder which can be used to produce a number of messages. it is prepared for a local Kafka, so if other Kafka installation is used, a proper changes to the script are necessary.
4. To obtain an interesting result, I run a scenario with multiple messages. For example: produce 10 messages, then next 10 after some interval, and so on. It was enough to send 30 minutes to observe an accrual of entries in ShardManager.retryQueue. 
