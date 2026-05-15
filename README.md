# E-Commerce API with JWT Authentication

This project is a Spring Boot e-commerce application updated for Laboratory 10 by adding JWT authentication to the existing system. It includes a backend REST API, a simple frontend using HTML/CSS/JavaScript, user registration and login, and protected pages that require a valid JWT token.

## Project Overview

The application supports:

- User registration
- User login with JWT token generation
- Token-based authentication using `Authorization: Bearer <token>`
- Protected routes such as account, cart, and checkout pages
- Product and category endpoints
- Role-based access control using Spring Security

## Technologies Used

### Backend

- Java 25
- Spring Boot 4.0.5
- Spring Security
- Spring Data JPA
- MySQL
- Lombok
- JJWT 0.11.5

### Frontend

- HTML5
- CSS3
- Vanilla JavaScript
- Fetch API
- Local Storage for JWT token storage

## JWT Authentication Flow

The JWT authentication flow implemented in this project works as follows:

1. The user registers using `/api/auth/register`.
2. The user logs in using `/api/auth/login`.
3. If the credentials are valid, the server generates a JWT token.
4. The frontend stores the token in `localStorage` using the key `jwt_token`.
5. Protected requests include the token in the header:

```http
Authorization: Bearer <token>
```

6. The backend validates the token using a custom JWT filter.
7. If the token is valid, access is granted to protected pages and endpoints.
8. If the token is missing or invalid, the user is redirected to the login page.

## JWT Components Implemented

### 1. JWT Utility Service

File:

- [JwtUtil.java](C:/Users/HP/Videos/EcommerceApi/src/main/java/com/ws101/senardelacerna/ecommerceapi/security/JwtUtil.java)

Responsibilities:

- Generate JWT tokens
- Extract username from token
- Extract claims
- Validate token
- Check expiration

### 2. JWT Authentication Filter

File:

- [JwtAuthenticationFilter.java](C:/Users/HP/Videos/EcommerceApi/src/main/java/com/ws101/senardelacerna/ecommerceapi/security/JwtAuthenticationFilter.java)

Responsibilities:

- Read the `Authorization` header
- Check for `Bearer <token>`
- Extract username from JWT
- Validate the token
- Set authenticated user in Spring Security context

### 3. Security Configuration

File:

- [SecurityConfig.java](C:/Users/HP/Videos/EcommerceApi/src/main/java/com/ws101/senardelacerna/ecommerceapi/config/SecurityConfig.java)

Implemented security features:

- Stateless session policy using `SessionCreationPolicy.STATELESS`
- JWT filter registration using `addFilterBefore(...)`
- Public access for login and register endpoints
- Authentication required for protected routes
- Unauthorized requests return `401 Unauthorized`

### 4. Login and Token Response

File:

- [AuthController.java](C:/Users/HP/Videos/EcommerceApi/src/main/java/com/ws101/senardelacerna/ecommerceapi/controller/AuthController.java)

Behavior:

- Authenticates username and password
- Generates JWT on successful login
- Returns token in the response body

## Important Configuration

JWT settings are defined in:

- [application.properties](C:/Users/HP/Videos/EcommerceApi/src/main/resources/application.properties)

```properties
jwt.secret=abcdefghijklmnopqrstuvwxyz123456
jwt.expiration=86400000
```

`jwt.expiration=86400000` means the token is valid for 24 hours.

## API Endpoints

Base URL:

```text
http://localhost:8080/api
```

### Authentication Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/auth/register` | Register a new user | Public |
| POST | `/auth/login` | Login and receive JWT token | Public |
| GET | `/auth/me` | Get current authenticated user | Protected |

### Product Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/products` | Get all products | Public |
| GET | `/products/{id}` | Get product by ID | Public |
| POST | `/products` | Create product | ADMIN |
| PUT | `/products/{id}` | Update product | ADMIN |
| DELETE | `/products/{id}` | Delete product | ADMIN |

### Category Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/categories` | Get all categories | Public |

## Frontend JWT Integration

The frontend was updated to work with JWT authentication.

### Login Page

File:

- [login.html](C:/Users/HP/Videos/EcommerceApi/src/main/resources/static/login.html)

Behavior:

- Sends login request to `/api/auth/login`
- Receives token from backend
- Saves token to `localStorage`
- Redirects user after successful login

### Shared Fetch Logic

File:

- [script.js](C:/Users/HP/Videos/EcommerceApi/src/main/resources/static/js/script.js)

Behavior:

- Reads `jwt_token` from `localStorage`
- Automatically adds `Authorization: Bearer <token>` to requests
- Redirects to login page on `401 Unauthorized`

### Protected Pages

Protected frontend pages:

- `/pages/account.html`
- `/pages/cart.html`
- `/pages/checkout.html`

These pages:

- check whether `jwt_token` exists
- call `/api/auth/me`
- allow access only if the token is valid
- redirect to `/login.html` if the token is missing or invalid

## How to Run the Project

### 1. Start the backend

```powershell
.\gradlew.bat bootRun
```

The application will run at:

```text
http://localhost:8080
```

### 2. Open the frontend in the browser

Open:

```text
http://localhost:8080/register.html
```

or

```text
http://localhost:8080/login.html
```

## Manual Testing Guide

### Register a user

1. Open `http://localhost:8080/register.html`
2. Fill in the registration form
3. Submit the form

Expected result:

- user is successfully registered

### Login and receive JWT

1. Open `http://localhost:8080/login.html`
2. Enter username and password
3. Submit the login form

Expected result:

- login succeeds
- token is stored in browser `localStorage` under `jwt_token`

### Test protected page access

1. Open:

```text
http://localhost:8080/pages/account.html
```

Expected result:

- user can access the page if the token is valid

### Test unauthorized behavior

1. Open browser DevTools
2. Go to Console
3. Run:

```javascript
localStorage.removeItem('jwt_token')
```

4. Refresh a protected page

Expected result:

- user is redirected to `/login.html`

### Test protected API manually

Run in the browser console:

```javascript
fetch('/api/auth/me', {
  headers: {
    Authorization: `Bearer ${localStorage.getItem('jwt_token')}`
  }
}).then(r => r.json()).then(console.log)
```

Expected result:

- current authenticated user information is returned

## Build Verification

The project was verified by compiling the Java source successfully using:

```powershell
.\gradlew.bat compileJava
```

## Project Structure

```text
src/main/java/com/ws101/senardelacerna/ecommerceapi/
├── config/
│   └── SecurityConfig.java
├── controller/
│   └── AuthController.java
├── dto/
│   ├── AuthResponse.java
│   └── LoginRequest.java
├── security/
│   ├── JwtAuthenticationFilter.java
│   └── JwtUtil.java
└── service/
    └── CustomUserDetailsService.java
```

## Laboratory 10 Summary

This project now satisfies the main goal of Laboratory 10: incorporating JWT authentication into an existing Spring Boot project.

Implemented successfully:

- JWT token generation
- JWT token validation
- JWT authentication filter
- Stateless Spring Security configuration
- Token-based login
- Protected routes using Bearer token authentication
- Frontend integration using Fetch API and localStorage

## Author

SenarDelacerna

Course:

- WS101

## License

This project is for educational purposes only.
