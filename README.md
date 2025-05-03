# Library Management System


## Dockerization


### Prerequisites


- Docker installed
- Docker Compose installed


### Running the Application with Docker


#### 1. Build the project JAR:


```bash
mvn clean package
```


#### 2. Build and start containers:


```bash
docker-compose up --build
```


#### 3. The backend server will be available at:


http://localhost:8080


Use Postman or curl to interact with the API endpoints.


To stop the containers:


```bash
docker-compose down
```


#### IMPORTANT
- "docker-compose up --build" will build the Docker image and start the containers
- it will create a new database if it does not exist
- it will INSERT THE FIRST ADMIN USER at the first run
- email: admin@library.com
- password: test123
- You can register new users and delete or update the admin user later
- if you want to start with a clean database, you can remove the existing database volume by running:


```bash
docker-compose down -v
```


- You can start with login with the default admin user:


```bash
curl --location 'localhost:8080/api/auth/login?email=cahit%40hotmail.com&password=default_password' \
--header 'Content-Type: application/json' \
--data-raw '{
"email": "admin@library.com",
"password": "test123"
}'
```




#### Notes


The docker-compose.yml starts two services:


db: PostgreSQL database configured with username libraryuser, password librarypass, and database librarydb.


app: Spring Boot application container connected to the db service.
Database connection properties are set via environment variables injected into the app container:


spring.datasource.url=jdbc:postgresql://db:5432/librarydb
spring.datasource.username=username
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update


Exposed ports:
8080 for the Spring Boot app
5432 for the database (optional, for local DB access)
