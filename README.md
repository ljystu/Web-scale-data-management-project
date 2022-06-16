# Web-scale-data-management-project

## Local deployment: docker-compose
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
