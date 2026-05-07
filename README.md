# 🛍️ E-Commerce Clothing Shop API

A full-stack e-commerce application with Spring Boot backend and responsive vanilla JavaScript frontend.

## 📋 Table of Contents
- [Technologies Used](#technologies-used)
- [Security Architecture](#security-architecture)
- [Validation Rules](#validation-rules)
- [Database Schema](#database-schema)
- [API Endpoints](#api-endpoints)
- [Screenshots](#screenshots)
- [Setup Instructions](#setup-instructions)
- [Testing Results](#testing-results)
- [Error Handling](#error-handling)

## 🚀 Technologies Used

### Backend
| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 25 | Programming Language |
| Spring Boot | 4.0.5 | Framework |
| Spring Security | 4.0.5 | Authentication & Authorization |
| Spring Data JPA | - | Database Access |
| H2 Database | - | Development Database |
| MySQL | 8.0 | Production Database |
| Lombok | - | Boilerplate Reduction |

### Frontend
| Technology | Purpose |
|------------|---------|
| HTML5 | Structure |
| CSS3 | Styling & Responsive Design |
| JavaScript (Vanilla) | Interactivity |
| Fetch API | Backend Communication |

## 🔐 Security Architecture

### Session-Based Authentication

This application uses **Session-Based Authentication** (HTTP sessions + cookies) instead of JWT.

#### How Session-Based Auth Works:
┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│ Client │ │ Server │ │ Database │
│ (Browser) │ │ (Backend) │ │ (MySQL) │
└──────┬──────┘ └──────┬──────┘ └──────┬──────┘
│ │ │
│ 1. POST /login │ │
│ (username/pass) │ │
│──────────────────>│ │
│ │ 2. Verify creds │
│ │──────────────────>│
│ │<──────────────────│
│ 3. JSESSIONID │ │
│ cookie set │ │
│<──────────────────│ │
│ │ │
│ 4. GET /products │ │
│ (with cookie) │ │
│──────────────────>│ 5. Validate │
│ │ session & role │
│ 6. Return data │ │
│<──────────────────│ │

text

#### Security Features:

| Feature | Implementation |
|---------|----------------|
| Password Storage | BCryptPasswordEncoder (hashing + salting) |
| Session Management | One session per user, 30-minute timeout |
| Cookie Security | HTTP-only, secure flag in production |
| CSRF Protection | Enabled for state-changing requests |
| Session Fixation Protection | New session created after login |

#### Role-Based Access Control (RBAC):

| Role | Permissions |
|------|-------------|
| **ADMIN** | Full CRUD operations on all products, categories, users |
| **USER** | View products, manage cart, place orders |
| **SELLER** | Manage own products |

#### Authentication Flow:

1. **Login:** User submits credentials to `/api/auth/login`
2. **Verification:** Server validates using BCrypt password encoder
3. **Session Creation:** New HTTP session created with JSESSIONID cookie
4. **Authorization:** User roles loaded from database
5. **Request Processing:** Session validated on each subsequent request
6. **Logout:** Session invalidated and cookie cleared

## ✅ Validation Rules

### Product Validation (CreateProductDto)

| Field | Constraint | Error Message |
|-------|------------|----------------|
| `name` | `@NotBlank`, `@Size(min=2, max=100)` | "Product name is required", "Name must be between 2-100 characters" |
| `price` | `@NotNull`, `@Positive` | "Price is required", "Price must be greater than 0" |
| `stockQuantity` | `@NotNull`, `@Positive` | "Stock quantity is required", "Stock must be greater than or equal to 0" |
| `description` | `@Size(max=500)` | "Description cannot exceed 500 characters" |

### User Registration Validation (RegisterUserDto)

| Field | Constraint | Error Message |
|-------|------------|----------------|
| `username` | `@NotBlank`, `@Size(min=3, max=50)` | "Username is required", "Username must be between 3-50 characters" |
| `password` | `@NotBlank`, `@Size(min=6, max=100)` | "Password is required", "Password must be at least 6 characters" |
| `email` | `@NotBlank`, `@Email` | "Email is required", "Please provide a valid email address" |

### Validation Error Response Example

```json
{
    "timestamp": "2026-05-07T10:00:00",
    "status": 400,
    "error": "Validation Failed",
    "message": "Validation failed",
    "errors": {
        "price": "Price must be greater than 0"
    },
    "path": "/api/products"
}
Global Exception Handling
The application uses @ControllerAdvice to handle validation errors globally:

Exception	HTTP Status	Description
MethodArgumentNotValidException	400 Bad Request	Validation failed
EntityNotFoundException	404 Not Found	Resource not found
DataIntegrityViolationException	400 Bad Request	Duplicate entry
AccessDeniedException	403 Forbidden	Insufficient permissions
🗄️ Database Schema
Tables Structure
sql
-- Categories Table
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200)
);

-- Products Table
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INT DEFAULT 0,
    image_url VARCHAR(500),
    category_id BIGINT,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
);

-- Users Table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE
);

-- Orders Table
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_date TIMESTAMP NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    customer_email VARCHAR(100) NOT NULL,
    shipping_address VARCHAR(500),
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(50)
);

-- Order Items Table
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);
Entity Relationships Diagram
text
┌─────────────────┐       ┌─────────────────┐
│   categories    │       │    products     │
├─────────────────┤       ├─────────────────┤
│ id (PK)         │◄──────│ id (PK)         │
│ name            │       │ name            │
│ description     │       │ description     │
└─────────────────┘       │ price           │
                          │ stock_quantity  │
                          │ image_url       │
                          │ category_id (FK)│
                          └─────────────────┘
                                   │
                                   │
                                   ▼
                          ┌─────────────────┐
                          │   order_items   │
                          ├─────────────────┤
                          │ id (PK)         │
                          │ order_id (FK)   │
                          │ product_id (FK) │
                          │ quantity        │
                          │ price           │
                          └─────────────────┘
                                   │
                                   │
                                   ▼
                          ┌─────────────────┐
                          │     orders      │
                          ├─────────────────┤
                          │ id (PK)         │
                          │ order_date      │
                          │ customer_name   │
                          │ customer_email  │
                          │ total_amount    │
                          └─────────────────┘
📡 API Endpoints
Base URL: http://localhost:8080/api
Authentication Endpoints
Method	Endpoint	Description	Auth Required
POST	/auth/register	Register new user	Public
POST	/auth/login	Login and get session cookie	Public
GET	/auth/me	Get current user info	Authenticated
POST	/auth/logout	Logout and invalidate session	Authenticated
Product Endpoints
Method	Endpoint	Description	Auth Required
GET	/products	Get all products	Public
GET	/products/{id}	Get product by ID	Public
GET	/products/category/{name}	Filter by category	Public
GET	/products/search/price?min=&max=	Filter by price range	Public
POST	/products	Create new product	ADMIN only
PUT	/products/{id}	Update product	ADMIN only
DELETE	/products/{id}	Delete product	ADMIN only
Category Endpoints
Method	Endpoint	Description	Auth Required
GET	/categories	Get all categories	Public
GET	/categories/{id}	Get category by ID	Public
POST	/categories	Create category	ADMIN only
PUT	/categories/{id}	Update category	ADMIN only
DELETE	/categories/{id}	Delete category	ADMIN only
HTTP Status Codes
Status	Description
200 OK	Request successful
201 Created	Resource created successfully
204 No Content	Deletion successful
400 Bad Request	Validation failed or invalid input
401 Unauthorized	Not authenticated / session expired
403 Forbidden	Authenticated but insufficient permissions
404 Not Found	Resource does not exist
500 Internal Server Error	Server-side error
Example API Calls
bash
# Register a new user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"password123","email":"john@example.com","role":"USER"}'

# Login (get JSESSIONID cookie)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"password123"}' \
  -c cookies.txt

# Get all products (public)
curl http://localhost:8080/api/products

# Create product (ADMIN only - requires cookie)
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"New Product","price":99.99,"stockQuantity":10}' \
  -b cookies.txt

# Get products by category
curl http://localhost:8080/api/products/category/Dresses

# Get products by price range
curl http://localhost:8080/api/products/search/price?min=100&max=500

# Logout
curl -X POST http://localhost:8080/api/auth/logout -b cookies.txt
📱 Screenshots
Desktop View
https://screenshots/desktop-view.png

*Figure 1: Landing page on desktop (4-column product grid)*

Mobile View
https://screenshots/mobile-view.png

*Figure 2: Landing page on mobile (1-column product grid, stacked navigation)*

Products Page
https://screenshots/products-page.png

Figure 3: Products listing page showing all items with Add to Cart buttons

Add to Cart Functionality
https://screenshots/add-to-cart.png

Figure 4: Product added to cart notification

Shopping Cart
https://screenshots/cart-page.png

Figure 5: Cart page showing items with quantity controls and subtotal

Remove from Cart
https://screenshots/remove-cart.png

Figure 6: Empty cart state after removing items

Checkout Page
https://screenshots/checkout-page.png

Figure 7: Checkout form with validation and order summary

Account Page
https://screenshots/account-page.png

Figure 8: User account page with order history

Browser Console (Successful Fetch)
https://screenshots/console-success.png

Figure 9: Browser console showing successful API calls with no CORS errors

🛠️ Setup Instructions
Prerequisites
Java 17 or higher

Gradle (included as wrapper)

VS Code (for frontend) or any browser

MySQL (optional, H2 works out of the box)

Backend Setup
bash
# 1. Clone the repository
git clone <your-repo-url>
cd EcommerceApi

# 2. Build the project (skip tests)
./gradlew clean build -x test

# 3. Run the Spring Boot application
./gradlew bootRun
The backend will start at: http://localhost:8080

Frontend Setup
Option 1: VS Code Live Server (Recommended)

Open project in VS Code

Install Live Server extension

Right-click on index.html or pages/products.html

Select "Open with Live Server"

Option 2: Direct Browser

Navigate to src/main/resources/static/

Double-click index.html

✅ Testing Results
Flow Test Results
Test Case	Expected Result	Actual Result	Status
Backend starts	Port 8080 listening	✅ Working	PASS
Frontend loads	Products displayed	✅ 8 products loaded	PASS
Products load from DB	Data from database	✅ From MySQL/H2	PASS
User registration	New user created	✅ 201 Created	PASS
User login	JSESSIONID cookie set	✅ Cookie received	PASS
Session persistence	Survives requests	✅ Working	PASS
Add to Cart	Item added to cart	✅ Notification shown	PASS
Update quantity	Subtotal updates	✅ Real-time update	PASS
Remove from cart	Item disappears	✅ Cart updates	PASS
Data persistence	Survives restart	✅ Still there	PASS
Logout	Session invalidated	✅ 200 OK	PASS
Protected route	Redirects to login	✅ 401 Unauthorized	PASS
Responsive Test Results
Device	Screen Width	Grid Columns	Navigation	Status
iPhone SE	375px	1 column	Stacked	✅ PASS
iPhone 12	390px	1 column	Stacked	✅ PASS
iPad Mini	768px	2 columns	Horizontal	✅ PASS
iPad Pro	1024px	3 columns	Horizontal	✅ PASS
Desktop	1920px	4 columns	Horizontal	✅ PASS
🐛 Error Handling
Backend Error Responses
404 - Product Not Found

json
{
    "timestamp": "2026-05-07T10:00:00",
    "status": 404,
    "error": "Not Found",
    "message": "Product not found with id: 999",
    "path": "/api/products/999"
}
400 - Validation Failed

json
{
    "timestamp": "2026-05-07T10:00:00",
    "status": 400,
    "error": "Validation Failed",
    "message": "Validation failed",
    "errors": {
        "price": "Price must be greater than 0"
    },
    "path": "/api/products"
}
401 - Unauthorized

json
{
    "timestamp": "2026-05-07T10:00:00",
    "status": 401,
    "error": "Unauthorized",
    "message": "Authentication required",
    "path": "/api/orders"
}
403 - Forbidden

json
{
    "timestamp": "2026-05-07T10:00:00",
    "status": 403,
    "error": "Forbidden",
    "message": "Access denied",
    "path": "/api/products"
}
Frontend Error Handling
javascript
async function fetchAPI(url, options = {}) {
    try {
        const response = await fetch(url, mergedOptions);
        
        // Handle 401 Unauthorized - Redirect to login
        if (response.status === 401) {
            sessionStorage.setItem('redirectAfterLogin', window.location.pathname);
            window.location.href = '/login.html';
        }
        
        // Handle 403 Forbidden - Access denied message
        if (response.status === 403) {
            alert("Access Denied: You don't have permission.");
        }
        
        return await response.json();
    } catch (error) {
        console.error("API Error:", error);
        showError(error.message);
    }
}
📁 Project Structure
text
EcommerceApi/
├── src/main/java/com/ws101/senardelacerna/ecommerceapi/
│   ├── config/
│   │   ├── SecurityConfig.java        # Spring Security Configuration
│   │   └── WebConfig.java             # CORS Configuration
│   ├── controller/
│   │   ├── AuthController.java        # Authentication endpoints
│   │   ├── ProductController.java     # Product REST endpoints
│   │   └── CategoryController.java    # Category REST endpoints
│   ├── dto/
│   │   ├── CreateProductDto.java      # Product creation DTO
│   │   ├── RegisterUserDto.java       # User registration DTO
│   │   ├── LoginRequest.java          # Login request DTO
│   │   └── AuthResponse.java          # Authentication response DTO
│   ├── entity/
│   │   ├── User.java                  # User JPA Entity (implements UserDetails)
│   │   ├── Role.java                  # User Role Enum
│   │   ├── Product.java               # Product JPA Entity
│   │   └── Category.java              # Category JPA Entity
│   ├── repository/
│   │   ├── UserRepository.java        # User CRUD
│   │   ├── ProductRepository.java     # Product CRUD
│   │   └── CategoryRepository.java    # Category CRUD
│   ├── service/
│   │   ├── CustomUserDetailsService.java  # UserDetailsService implementation
│   │   ├── ProductService.java        # Product Business Logic
│   │   └── CategoryService.java       # Category Business Logic
│   └── exception/
│       └── GlobalExceptionHandler.java   # Global exception handling
├── src/main/resources/
│   ├── application.properties         # Spring Config
│   └── static/                        # Frontend Files
│       ├── index.html
│       ├── login.html
│       ├── register.html
│       ├── pages/
│       │   ├── products.html
│       │   ├── cart.html
│       │   ├── checkout.html
│       │   ├── account.html
│       │   └── detail.html
│       ├── css/
│       ├── js/
│       │   └── script.js
│       └── image/
├── screenshots/                       # Documentation Images
└── build.gradle                       # Gradle Configuration
👨‍💻 Author
Senar de Lacerna

Course: WS101 - Web Development

Project: E-Commerce Clothing Shop API

📅 Date
May 7, 2026

🎯 Features Implemented
Backend Features
✅ RESTful API endpoints

✅ Spring Security with Session-Based Authentication

✅ BCrypt password encoding

✅ Role-based access control (USER, ADMIN, SELLER)

✅ JPA Entity relationships (One-to-Many, Many-to-One)

✅ Database persistence with H2/MySQL

✅ Global exception handling with @ControllerAdvice

✅ Bean Validation with custom error messages

✅ CORS configuration for frontend access

✅ DTO pattern for data transfer

Frontend Features
✅ Fetch API with async/await

✅ 401/403 error handling with redirect

✅ Session-based authentication with cookies

✅ Protected routes (checkout page)

✅ Responsive design (mobile, tablet, desktop)

✅ Dynamic product rendering

✅ Shopping cart with localStorage

✅ Checkout form validation

✅ User account page with order history

✅ Product reviews section

✅ Price filtering

📝 License
This project is for educational purposes only.