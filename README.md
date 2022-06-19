# Web-scale-data-management-project - Group 6


## Deployment instruction
Prerequisites:
minikube is installed

Run the following command to start all services
```shell
minikube addons enable ingress
kubectl apply -f .\k8s\.
```
To run the stress test. Set the `urls.json` as follows
```json
{
  "ORDER_URL" : "http://127.0.0.1:30181",
  "PAYMENT_URL" : "http://127.0.0.1:30183",
  "STOCK_URL" : "http://127.0.0.1:30182"
}
```

## (Optional) Local deployment via docker-compose
Run the following command to run all the services
```bash
docker-compose up
```

### (Optional) build wdm service images locally before running the containers 
Build images for eureka-service, order-service, payment-service and stock-service
```bash
mvn clean package
docker-compose up -f docker-compose-build-services.yml
```



# WDM Project development blog
<!-- The link to the github repository for all of our code is mentioned below:
[Github link](https://github.com/ljystu/Web-scale-data-management-project) -->
This blog contains the process of our development, the issues and solutions we encountered along the way.
## Kick-off of the development
> [name=Jingyu Li & Lang Feng]
>
> [time= May 19, 2022 ]
>
From today we decided to use SpringCloud and Postgresql as our tech stacks. But we are still looking for a solution for the transaction consistency.

## Implementation of microservices
> [name=Jingyu Li]
> [time= May 24, 2022 ]

By today, our basic development of the microservices has finished. But issues regarding distributed development have not been considered yet. The potential solution is a managed framework called [Seata](https://seata.io/en-us/index.html).
## First try of deploying seata
> [name=Jingyu Li]
> [time= May 29, 2022 ]

As far as we know it, Seata is not really well documented. It is pretty hard to deploy. We've encountered a lot of problems deploying version 1.4.2. In this project we used Seata 1.3.0


*change postgresql driver in seata/lib
*change java version below 13(we used 1.8.0.275)

### seata-server
*name in file.conf and registry needs to be the same
```
file.conf
service {
  #transaction service group mapping
  vgroupMapping.eurekaserver-seata-service-group = "default"
}

registry.conf
```
### seata-client
```
application.yml
seata:
  tx-service-group: eurekaserver-seata-service-group
  service:
    vgroupMapping:
      eurekaserver-seata-service-group: default
    grouplist:
      default: 127.0.0.1:8091
  enabled: true
  data-source-proxy-mode: AT
  ```
P.S. don't use underline_ in configuration files.

Put @Globaltransactional on the global transaction methods and @Transactional on the branch transactions. The seata should automatically rollback the transactions but in our implementation, we found that we need to manually rollback the transaction when exceptions are thrown by any branch transations.

Seata uses xid to manage the transactions, all of the branch transactions of a global transaction have the same xid. If any of them throws an exception, the global transaction and its branch transactions will be rollbacked(with same xid). In the implementation, we encountered several bugs when we are trying to get the xid of branch transations. It is not clear whether we need to manually bind the xid to all of branch transations, but we still did so.

If the data-source-proxy-mode is configured to AT, we should create an undo_log table in every database.

## Seata implementation issues and solutions
> [name=Jingyu Li]
> [time= Jun 3, 2022 ]
>
Using seata 1.4.2 guarantees consistency in our system now. Turns out the @EnableDatasourceAutoProxy should be added to the 3 application classes.

? select for update in the sql commands to ensure consistent reading.


### Current Version

| Stack | Version |  
| -------- | -------- | 
| Seata    | 1.3.0   |
| SpringCloud.Alibaba    | 2021.1  |
| Feign    |  3.1.2 |
| Euraka   |  3.1.2 |

We had fulfilled the basic mission of the project, the parameters of the Locust are tested in the below.

| Number of Users | Spawn Rate |  Average Latency| Failure Rate
| -------- | -------- | -------- | -------- | 
| 1000    | 100 | 16s-25s  | 0%|

After large number of experiments, we found that the seata framework is not very stable, the average latency is around 16 to 25 seconds. We assume this situation is not only restricted by the AT transaction mode, but also related to the hardware we are using. The scalability of our system is the what we will focus in the future.

## Partition of Tables
> [name=Lang Feng]
>
> [time= June 6, 2022 ]
>
During the large amount of tests we find that after several stress tests, it become really slow for check out service. The thing is that it gets slower when we test again and again. We found out that there are about 40000+ data in the table, this may be one reason why the latency seems to be so high.

After the discussion, we think maybe partition of the tables will be a useful solution. We partition the table into four sub-tables. In the implenmenting of partition, we partition based on the primary keyid and use hash table method for partitioning.

Successfully we find the latency is significantly reduced.

## Primary key
> [name=Jingyu Li]
> [time= June 8, 2022 ]
>
We did a lot of tests to find out the cause of the huge latency in our checkout method. We found that the simple select method caused 4-5 seconds of delay and even longer when the databse got more data. We confirmed that the latency was not caused by seata framework. It was mainly caused by the database. During the early stage of our development, we used random UUID as our primary key in all of the tables. Although UUID is a convinient choice for the primary keys in our distributed databases for its universally uniquity, Storing UUID values (16-bytes) takes more storage than integers (4-bytes) or even big integers(8-bytes). Furthermore, using UUID values may cause performance issues due to their size and not being ordered.
We did some research on the choices of the primary key of distributed databases and decided to use snowflake algorithm to generate 12-digit unique long numbers as our primary key in every table.
It turns out the performance got significantly better, the latency plungged to 2-3 seconds and the consistency remains intact.

## Docker container build
> [name=Xueyuan Chen]
> [time= June 15, 2022 ]
>
We containerized all the services including the postgresql database, seata, redis, eureka and our 3 wdm services (order, payment and stock). Then local deployment was setup via docker compose. As seata requires specifc tables in the database to work properly, we used bash scripts + sql quries for initialization. All services work well in containers. This local deployed system also passed consistency test and pressure test.

Some note during deployment:
- Official seata images does not seem to be compatible with M1 chip on MacOS due to its arm64 arch. We used a cutomized seata image instead.
- On windows, mounting and executing the database init bash file works directly. But in MacOS before `docker-compose up`, the init_db.sh has to be given execution permission.
- All databases are created in one postgres service for now.

## Database replication
> [name=Daniel van den Akker]
> [time= June 18, 2022 ]
>
In order to create a robust and fault-tolerant system, one feature we chose to include was data-replication. The first iteration of the feature utilized PostgreSQL’s built-in streaming replication feature. That build only created a synchronous replica of the existing database and did not take into consideration server fail-over, connection pooling, load balancing. Instead, we chose to go with an in-house solution in the form of Pgpool-II, which serves as a server proxy for the Postgresql instances.

## Kubernetes deployment and testing
> [name=Xueyuan Chen]
> [time= June 18, 2022 ]
>
- The databases are splitted into different services, which reduced the latency.
- All services and databases need to be deployed as StatefulSets to avoid service host locating issue
- Simple replication by configure replicas does not work for eureka. Some implementation seems needed.

## final versions of tech stacks:
| Stack | Version |  
| -------- | -------- | 
| Seata    | 1.4.2   |
| SpringCloud.Alibaba    | 2021.1  |
| Feign-client    |  3.1.2 |
| Euraka   |  3.1.2 |
| Postgresql | 14.3 |
| Pgpool | 4.3.2 |
| Redis    |  latest |