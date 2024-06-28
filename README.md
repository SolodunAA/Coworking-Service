# Coworking-Service
how to run: main class app/start/CoworkingApp.java

when app started: to log in as admin: login: admin password: admin

docker run:
docker run -d --name coworking_service -p 5433:5432 -e POSTGRES_USER=coworking_user -e POSTGRES_PASSWORD=1111 -e POSTGRES_DB=coworkingDB postgres:latest