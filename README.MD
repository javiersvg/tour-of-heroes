![Java CI](https://github.com/javiersvg/tour-of-heroes/workflows/Java%20CI/badge.svg)
[![Build Status](https://travis-ci.org/javiersvg/tour-of-heroes.svg?branch=master)](https://travis-ci.org/javiersvg/tour-of-heroes)
[![Build Status](https://sonarcloud.io/api/project_badges/measure?project=com.javiersvg%3Atour-of-heroes&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.javiersvg%3Atour-of-heroes)

# Spring Boot and AngularJS

Spring boot application meant to log in users that generate a token with to 
 Google javascript API library. 
 The front end [application](https://github.com/javiersvg/angular-tour-of-heroes)
 
 ## Development server
 Run `./gradlew bootRun` for a dev server. Navigate to `http://localhost:8080/`.
 
 ## Build   
 Run `./gradlew bootRun` to build the project. The build artifacts will be stored in the `build/` directory.
 
 ## Run with docker
 In an environments with a container with mongo running run:
 `docker run -d -e "SPRING_DATA_MONGODB_HOST=mongo" --link="mongo" -p 8080:8080 --name=tour-of-heroes javiersvg/tour-of-heroes`