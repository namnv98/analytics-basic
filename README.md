# analytics-basic

This is a Spring Boot + Ignite implementation of a clustered cache with REST interface, run from local or from Kubernetes 
Domain is Instrument - Trade - Price - Position, aimed to demonstrate basic analytics functionalities and performance concerns.

## Run:
mvnw spring-boot:run

## Build Docker image:
mvnw spring-boot:build-image

## Deploy to Kubernetes:
Modify image reference at ./kubernetes/analytics-pod.yaml to your newly built image from either local or a registry. 

kubectl apply -f ./kubernetes/ignite-ip-finder.yaml

kubectl apply -f ./kubernetes/analytics-pod.yaml

kubectl apply -f ./kubernetes/analytics-service.yaml


## Access Ignite cluster via JDBC:
Find NodePort mapping from Kubernetes service, the port mapped from 10800:

kubectl get service

Access via URL: jdbc:ignite:thin://[KUBERNETES_CLUSTER_IP]:[kUBERNETES_NODE_PORT]/

## Access REST end-point:
Find NodePort mapping from Kubernetes service, the port mapped from 8080:

kubectl get service

http://[KUBERNETES_CLUSTER_IP]:[KUBERNETES_NODE_PORT]/hello

http://[KUBERNETES_CLUSTER_IP]:[KUBERNETES_NODE_PORT]/loadCache

http://[KUBERNETES_CLUSTER_IP]:[KUBERNETES_NODE_PORT]/cleanCache

http://[KUBERNETES_CLUSTER_IP]:[KUBERNETES_NODE_PORT]/countTradeByBook

http://[KUBERNETES_CLUSTER_IP]:[KUBERNETES_NODE_PORT]/findTradesByBook

http://[KUBERNETES_CLUSTER_IP]:[KUBERNETES_NODE_PORT]/findTradesById