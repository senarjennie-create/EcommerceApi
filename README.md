# 🛍️ E-Commerce Clothing Shop API

A full-stack e-commerce application with Spring Boot backend and responsive vanilla JavaScript frontend.

## 📋 Table of Contents
- [Technologies Used](#technologies-used)
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

## 🗄️ Database Schema

### Tables Structure

```sql
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
Database Tables (Populated Data)
https://screenshots/database-table.png

Figure 1: Products table with sample data (8 products)

https://screenshots/database-schema.png

Figure 2: Database schema showing all tables

📡 API Endpoints
Base URL: http://localhost:8080/api
Products Endpoints
Method	Endpoint	Description	Request Body	Response
GET	/products	Get all products	-	200 OK + Product array
GET	/products/{id}	Get product by ID	-	200 OK + Product object
POST	/products	Create new product	Product JSON	201 CREATED + Product
PUT	/products/{id}	Update product	Product JSON	200 OK + Updated product
DELETE	/products/{id}	Delete product	-	204 NO CONTENT
GET	/products/category/{name}	Filter by category	-	200 OK + Filtered products
GET	/products/search/price?min=&max=	Filter by price range	-	200 OK + Filtered products
Categories Endpoints
Method	Endpoint	Description	Request Body	Response
GET	/categories	Get all categories	-	200 OK + Category array
GET	/categories/{id}	Get category by ID	-	200 OK + Category object
POST	/categories	Create category	Category JSON	201 CREATED + Category
PUT	/categories/{id}	Update category	Category JSON	200 OK + Updated category
DELETE	/categories/{id}	Delete category	-	204 NO CONTENT
Example API Calls
bash
# Get all products
curl http://localhost:8080/api/products

# Create a new product
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Elegant Dress",
    "description": "Perfect for special occasions",
    "price": 350.00,
    "stockQuantity": 25,
    "imageUrl": "image/dress.jpg",
    "category": { "id": 1 }
  }'

# Get products by category
curl http://localhost:8080/api/products/category/Dresses

# Get products by price range
curl http://localhost:8080/api/products/search/price?min=100&max=500
📱 Screenshots
Desktop View
https://screenshots/desktop-view.png

*Figure 3: Landing page on desktop (4-column product grid)*

Mobile View
https://screenshots/mobile-view.png

*Figure 4: Landing page on mobile (1-column product grid, stacked navigation)*

Products Page
https://screenshots/products-page.png

Figure 5: Products listing page showing all items with Add to Cart buttons

Add to Cart Functionality
https://screenshots/add-to-cart.png

Figure 6: Product added to cart notification

Shopping Cart
https://screenshots/cart-page.png

Figure 7: Cart page showing items with quantity controls and subtotal

Remove from Cart
https://screenshots/remove-cart.png

Figure 8: Empty cart state after removing items

Checkout Page
https://screenshots/checkout-page.png

Figure 9: Checkout form with validation and order summary

Account Page
https://screenshots/account-page.png

Figure 10: User account page with order history

Browser Console (Successful Fetch)
https://screenshots/console-success.png

Figure 11: Browser console showing successful API calls with no CORS errors

🛠️ Setup Instructions
Prerequisites
Java 17 or higher

Gradle (included as wrapper)

VS Code (for frontend) or any browser

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

Access H2 Console (Development)
text
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:ecommerce_db
Username: sa
Password: (leave empty)
✅ Testing Results
Flow Test Results
Test Case	Expected Result	Actual Result	Status
Backend starts	Port 8080 listening	✅ Working	PASS
Frontend loads	Products displayed	✅ 8 products loaded	PASS
Products load from DB	Data from database	✅ From MySQL/H2	PASS
Add to Cart	Item added to cart	✅ Notification shown	PASS
Update quantity	Subtotal updates	✅ Real-time update	PASS
Remove from cart	Item disappears	✅ Cart updates	PASS
Data persistence	Survives restart	✅ Still there	PASS
Responsive Test Results
Device	Screen Width	Grid Columns	Navigation	Status
iPhone SE	375px	1 column	Stacked	✅ PASS
iPhone 12	390px	1 column	Stacked	✅ PASS
iPad Mini	768px	2 columns	Horizontal	✅ PASS
iPad Pro	1024px	3 columns	Horizontal	✅ PASS
Desktop	1920px	4 columns	Horizontal	✅ PASS
API Test Results
Endpoint	Method	Status	Response Time
/api/products	GET	✅ 200 OK	< 100ms
/api/products/1	GET	✅ 200 OK	< 50ms
/api/products	POST	✅ 201 Created	< 150ms
/api/products/1	PUT	✅ 200 OK	< 100ms
/api/products/1	DELETE	✅ 204 No Content	< 50ms
/api/categories	GET	✅ 200 OK	< 50ms
🐛 Error Handling
Backend Error Responses
404 - Product Not Found

json
{
    "timestamp": "2026-04-30T10:00:00",
    "status": 404,
    "error": "Not Found",
    "message": "Product not found with id: 999",
    "path": "/api/products/999"
}
400 - Duplicate Category

json
{
    "timestamp": "2026-04-30T10:00:00",
    "status": 400,
    "error": "Data Integrity Violation",
    "message": "A record with this value already exists",
    "path": "/api/categories"
}
Frontend Error Handling
javascript
async function fetchProducts() {
    try {
        const response = await fetch(PRODUCTS_ENDPOINT);
        if (!response.ok) {
            throw new Error(`HTTP ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error("[fetchProducts] Failed:", error);
        showError(error.message);
        return [];
    }
}
📁 Project Structure
text
EcommerceApi/
├── src/main/java/com/ws101/senardelacerna/ecommerceapi/
│   ├── config/
│   │   └── WebConfig.java              # CORS Configuration
│   ├── controller/
│   │   ├── ProductController.java      # Product REST endpoints
│   │   └── CategoryController.java     # Category REST endpoints
│   ├── dto/
│   │   └── ProductDTO.java             # Data Transfer Object
│   ├── entity/
│   │   ├── Product.java                # Product JPA Entity
│   │   ├── Category.java               # Category JPA Entity
│   │   ├── Order.java                  # Order JPA Entity
│   │   └── OrderItem.java              # OrderItem JPA Entity
│   ├── repository/
│   │   ├── ProductRepository.java      # Product CRUD
│   │   ├── CategoryRepository.java     # Category CRUD
│   │   ├── OrderRepository.java        # Order CRUD
│   │   └── OrderItemRepository.java    # OrderItem CRUD
│   └── service/
│       ├── ProductService.java         # Product Business Logic
│       └── CategoryService.java        # Category Business Logic
├── src/main/resources/
│   ├── application.properties          # Spring Config
│   └── static/                         # Frontend Files
│       ├── index.html
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
├── screenshots/                        # Documentation Images
└── build.gradle                        # Gradle Configuration
👨‍💻 Author
Senar de Lacerna

Course: WS101 - Web Development

Project: E-Commerce Clothing Shop API

📅 Date
April 30, 2026

🎯 Features Implemented
Backend Features
✅ RESTful API endpoints

✅ JPA Entity relationships (One-to-Many, Many-to-One)

✅ Database persistence with H2/MySQL

✅ Global exception handling with @ControllerAdvice

✅ CORS configuration for frontend access

✅ DTO pattern for data transfer

Frontend Features
✅ Fetch API with async/await

✅ Error handling with try/catch

✅ Responsive design (mobile, tablet, desktop)

✅ Dynamic product rendering

✅ Shopping cart with localStorage

✅ Checkout form validation

✅ User account page with order history

✅ Product reviews section

✅ Price filtering

📝 License
This project is for educational purposes only.