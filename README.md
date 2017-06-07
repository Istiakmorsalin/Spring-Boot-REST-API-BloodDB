# Spring-Boot-REST-API-BloodDB

To Up and Running: 

1. Install maven
2. Download dependencies 
3. mvn spring-boot:run


Current APIS:

Users:
1. Create user/Register user:
 Endpoint: http://localhost:8080/api/users(POST) RequestBody: json
2. Update user
  Endpoint: http://localhost:8080/api/users/id(PUT) RequestBody: json (Keep uid in request)
3. Get List of Users
  Endpoint: http://localhost:8080/api/users(GET)
4. Search User by Lat Long and Blood Group
  Endpoint: http://localhost:8080/api/users/search?userLatitude=33.342&userLongitude=23.3&bloodGroup=B Plus
5. Delete User:
  Endpoint: http://localhost:8080/api/users/id
