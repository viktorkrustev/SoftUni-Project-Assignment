# Online Shop Application

## Overview

The **Online Shop Application** is a comprehensive e-commerce platform built with Spring Boot. It offers a full range of functionalities to manage products, orders, users, and reviews, along with integrated email services and scheduled tasks. The application is designed to provide a seamless shopping experience for users and efficient management tools for administrators.

## Key Features

- **User Management**: Register, update, and delete users with secure password encoding and role-based access control.
- **Product Management**: CRUD operations for products, with functionalities to filter, sort, and search products.
- **Order Processing**: Create orders, view orders by date or user, and manage order statuses.
- **Cart Management**: Add products to the cart and manage product stock quantities.
- **Reviews**: Add, view, and delete reviews for products.
- **Email Notifications**: Automated email services for order confirmations and daily reports.
- **Scheduled Tasks**: Regular tasks scheduled for operational efficiency (e.g., daily order reports).
- **Security**: Authentication and authorization using Spring Security.
- **Profile Management**: Update user profiles and upload profile pictures.

## Screenshots

Here are some screenshots:

1. 



## Technologies Used

- **Spring Boot**: Core framework for building the application.
- **Spring Data JPA**: Data access layer.
- **Spring Security**: Security and authentication.
- **ModelMapper**: Object mapping.
- **Java Mail**: Email services.
- **Thymeleaf**: Template engine for email templates.
- **H2 Database**: In-memory database for testing.
- **MySQL/PostgreSQL**: Primary databases for production.

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven 3.6.3 or higher
- MySQL or PostgreSQL database

## To Run the Application:

- Ensure you have Java and Maven installed.
- Clone the repository and navigate to the project directory.
- Use `mvn spring-boot:run` to start the application.
- Access the application via [http://localhost:8080](http://localhost:8080).

## Database Configuration:

- Ensure to configure the database settings in `application.properties` for proper persistence.

