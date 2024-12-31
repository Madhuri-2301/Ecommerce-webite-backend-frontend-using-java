# Ecommerce-webite-backend-frontend-using-java
# Simple E-Commerce Website

A beginner-friendly e-commerce website built with Spring Boot, MySQL, and HTML/CSS/JavaScript.

## Project Structure
```
ecommerce-website/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── ecommerce/
│   │   │           ├── EcommerceApplication.java
│   │   │           ├── controller/
│   │   │           │   ├── ProductController.java
│   │   │           │   ├── UserController.java
│   │   │           │   └── CartController.java
│   │   │           ├── model/
│   │   │           │   ├── Product.java
│   │   │           │   ├── User.java
│   │   │           │   └── Cart.java
│   │   │           └── repository/
│   │   │               ├── ProductRepository.java
│   │   │               ├── UserRepository.java
│   │   │               └── CartRepository.java
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── css/
│   │       │   │   └── style.css
│   │       │   └── js/
│   │       │       └── main.js
│   │       └── templates/
│   │           ├── index.html
│   │           ├── products.html
│   │           └── cart.html
├── pom.xml
└── README.md
```

## Features
- Product listing and details
- User registration and login
- Shopping cart functionality
- Basic checkout process
- Responsive design
- RESTful API endpoints

## Prerequisites
- Java 17 or higher
- Maven
- MySQL

## Setup Instructions
1. Clone the repository
2. Create MySQL database named `ecommerce`
3. Update `application.properties` with your database credentials
4. Run `mvn spring-boot:run`
5. Access the application at `http://localhost:8080`

## Technologies Used
- Backend: Spring Boot, Spring Security, JPA
- Frontend: HTML, CSS, JavaScript
- Database: MySQL
- Build Tool: Maven
