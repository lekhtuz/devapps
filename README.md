# devapps
This is whatever I feel like developing for my own needs. Currently:
- /echo log incoming request
- /oauth very simple Oauth2 service provider

# Commands without Docker
```
cd $HOME/Documents/w_devapps/devapps
mvn clean package
java -jar target/devapps-0.0.1-SNAPSHOT.jar
curl -v http://localhost:8080/echo
```
# Commands to dockerize existing jar
```
docker build --tag=devapps:latest .
docker run -p 8080:8080 devapps:latest
```
# Commands with Docker
```
mvn clean spring-boot:build-image
```