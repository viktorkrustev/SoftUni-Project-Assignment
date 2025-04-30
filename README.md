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

<div class="image-container">
  <p>Home page:</p>
  <img src="https://github.com/user-attachments/assets/c28dbad3-7b2a-4229-ae50-dc8a64c90ef2" alt="Screenshot 2024-08-05 143731"/>
</div>
<div class="image-container">
  <p>Login:</p>
  <img src="https://github.com/user-attachments/assets/46dae9dc-ba37-4ec2-81a3-c552972738f1" alt="Screenshot 2024-08-05 143731"/>
</div>
<div class="image-container">
  <p>Products page:</p>
  <img src="https://github.com/user-attachments/assets/9ff0910f-9225-453f-b32e-e62c4266c91f" alt="image"/>
</div>
<div class="image-container">
  <p>About page:</p>
  <img src="https://github.com/user-attachments/assets/67de89fa-425d-4e40-a793-56e43b5ce914" alt="Screenshot 2024-08-05 144432"/>
</div>
<div class="image-container">
  <p>Product page:</p>
  <img src="https://github.com/user-attachments/assets/83cefee1-4dd3-4695-ad51-a759a66764b1" alt="Screenshot 2024-08-05 144432"/>
</div>
<div class="image-container">
  <p>Review section:</p>
  <img src="https://github.com/user-attachments/assets/7598936f-30bc-496e-b8da-0904779d99db" alt="Screenshot 2024-08-05 144432"/>
</div>
<div class="image-container">
  <p>Profile page:</p>
  <img src="https://github.com/user-attachments/assets/6e351fa4-26c8-4397-88f9-059d1690eb18" alt="Screenshot 2024-08-05 144432"/>
</div>



## Technologies Used

- **Spring Boot**: Core framework for building the application.
- **Spring Data JPA**: Data access layer.
- **Spring Security**: Security and authentication.
- **ModelMapper**: Object mapping.
- **Java Mail**: Email services.
- **Thymeleaf**: Template engine for email templates.
- **H2 Database**: In-memory database for testing.
- **MySQL**: Primary databases for production.

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

