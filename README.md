# Web-scale-data-management-project

## Local deployment: docker-compose
Run the following command to run all the services
```bash
docker-compose up
```

## For development
Build images for eureka-service, order-service, payment-service and stock-service
```bash
mvn clean package
docker-compose build
```
