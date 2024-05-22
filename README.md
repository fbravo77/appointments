
# Appointments app

The platform offers an efficient way for patients to schedule appointments with specialists, such as nutritionists and psychologists. Patients can view specialists' calendars, find available time slots, and provide notes about their issues and preferred appointment locations.

For specialists, the platform maintains a comprehensive history of each patient's appointments and notes, facilitating better-informed consultations. All appointment information is centralized, making it easy to access and manage.

Administrators have control over user management, specialist calendars, and appointment locations. They can also modify the location of specialists for their appointments. Additionally, the platform sends automated reminders and confirmation notifications to patients, ensuring they are kept informed about their appointments.


## Requirements

For building and running the application you need:

- [JDK 1.17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- [Maven 3.9.6](https://maven.apache.org/download.cgi)

## The stack of this repository consist of:
- Java
- Maven
- Spring
- Spring Security
- Spring Scheduler
- Spring Data JPA
- Spring Web
- Spring Mail
- Spring Boot
- WAAPI (In validation)
- Google API Client
- Google API Services Calendar

##  Suggested tools to work:
 - VS Code
- Intellij Idea
- Postman
- Google Chrome
- Git

## Database
You need a postgres database and create a new schema with the name "wellness", then you can connect to that database changing the credentials in the application.properties of the project
Spring JPA will create all the tables, constraints and everything you need in the database

Then you can run the dlls under the file startup_ddls.sql in your database 

Test using postman

To check detail documentation please check: [Technical documentation](https://docs.google.com/document/d/1pmlDajWRoGHrb9dicK_BJGJzZ7TmyjfCQfXCTsnivYE/edit)

