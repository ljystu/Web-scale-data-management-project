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

## Local deployment: k8s
Prerequisites:
minikube is installed

Run the following command to start all services
```shell
minikube addons enable ingress
kubectl apply -f .\k8s\.
```