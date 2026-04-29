// ==========================
// TASK 5: FETCH API & ASYNCHRONOUS DATA
// ==========================

// API Configuration
const API_BASE_URL = "http://localhost:8080/api";
const PRODUCTS_ENDPOINT = `${API_BASE_URL}/products`;
const CATEGORIES_ENDPOINT = `${API_BASE_URL}/categories`;
const IMAGE_FALLBACK = "image/placeholder.jpg";

// Global state
let products = [];
let categories = [];
let cart = [];

// ==========================
// API REQUEST ERROR CLASS
// ==========================

class ApiRequestError extends Error {
    constructor(message, status) {
        super(message);
        this.name = "ApiRequestError";
        this.status = status;
    }
}

// ==========================
// GENERIC FETCH FUNCTION WITH ERROR HANDLING
// ==========================

async function fetchAPI(url, options = {}) {
    const defaultOptions = {
        headers: {
            "Content-Type": "application/json",
            "Accept": "application/json"
        }
    };
    
    const mergedOptions = { ...defaultOptions, ...options };
    
    try {
        console.log(`🌐 API Request: ${mergedOptions.method || 'GET'} ${url}`);
        
        const response = await fetch(url, mergedOptions);
        
        if (!response.ok) {
            let errorMessage = `HTTP ${response.status}: ${response.statusText}`;
            
            try {
                const errorData = await response.json();
                if (errorData.message) {
                    errorMessage = errorData.message;
                }
            } catch (e) {}
            
            if (response.status === 404) {
                throw new ApiRequestError("API endpoint not found. Please check if backend is running on port 8080.", 404);
            }
            if (response.status === 500) {
                throw new ApiRequestError("Server error occurred. Please check backend console for details.", 500);
            }
            throw new ApiRequestError(errorMessage, response.status);
        }
        
        if (response.status === 204) {
            return null;
        }
        
        const data = await response.json();
        console.log(`✅ API Success: ${url}`, data);
        return data;
        
    } catch (error) {
        console.error(`❌ API Error: ${url}`, error);
        
        if (error.message.includes('Failed to fetch')) {
            throw new ApiRequestError('Unable to connect to server. Please make sure the backend is running on port 8080.', 0);
        }
        
        throw error;
    }
}

// ==========================
// PRODUCT API METHODS
// ==========================

async function fetchProducts() {
    try {
        console.log(`[fetchProducts] Requesting ${PRODUCTS_ENDPOINT}`);
        const data = await fetchAPI(PRODUCTS_ENDPOINT);
        const productsList = Array.isArray(data) ? data : [];
        console.log(`[fetchProducts] Loaded ${productsList.length} products from database`);
        return productsList;
    } catch (error) {
        console.error("[fetchProducts] Failed to load products:", error);
        throw error;
    }
}

async function fetchProductById(id) {
    try {
        console.log(`[fetchProductById] Requesting ${PRODUCTS_ENDPOINT}/${id}`);
        const product = await fetchAPI(`${PRODUCTS_ENDPOINT}/${id}`);
        console.log(`[fetchProductById] Loaded product: ${product.name}`);
        return product;
    } catch (error) {
        console.error(`[fetchProductById] Failed to load product ${id}:`, error);
        throw error;
    }
}

async function fetchCategories() {
    try {
        console.log(`[fetchCategories] Requesting ${CATEGORIES_ENDPOINT}`);
        const data = await fetchAPI(CATEGORIES_ENDPOINT);
        const categoriesList = Array.isArray(data) ? data : [];
        console.log(`[fetchCategories] Loaded ${categoriesList.length} categories`);
        return categoriesList;
    } catch (error) {
        console.error("[fetchCategories] Failed to load categories:", error);
        return [];
    }
}

// ==========================
// PRODUCT NORMALIZATION
// ==========================

function normalizeProduct(product) {
    // Use the image URL exactly as from database
    let imageUrl = product.imageUrl || IMAGE_FALLBACK;
    
    // Log to see what image URL we're getting
    console.log(`[normalizeProduct] Product: ${product.name}, DB Image URL: ${imageUrl}`);
    
    return {
        id: product.id,
        name: product.name ?? "Unnamed Product",
        description: product.description ?? "No description available.",
        price: Number(product.price ?? 0),
        imageUrl: imageUrl,
        stockQuantity: product.stockQuantity ?? 0,
        category: product.category ?? null
    };
}

function formatPrice(price) {
    return new Intl.NumberFormat("en-PH", {
        style: "currency",
        currency: "PHP"
    }).format(price);
}

function escapeHtml(str) {
    if (!str) return '';
    return str
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

// ==========================
// PRODUCT CARD CREATION
// ==========================

function getStockStatus(quantity) {
    if (!quantity || quantity <= 0) {
        return { class: 'out-of-stock', text: '❌ Out of Stock' };
    } else if (quantity < 10) {
        return { class: 'low-stock', text: `⚠️ Low Stock (${quantity} left)` };
    } else {
        return { class: 'in-stock', text: `✅ In Stock (${quantity} units)` };
    }
}

function createProductCard(product) {
    const stockStatus = getStockStatus(product.stockQuantity);
    
    return `
        <article class="product-card" data-product-id="${product.id}">
            <img src="${product.imageUrl}" alt="${escapeHtml(product.name)}" onerror="this.src='${IMAGE_FALLBACK}'">
            <h3>${escapeHtml(product.name)}</h3>
            <p class="product-price">${formatPrice(product.price)}</p>
            <p class="product-description">${escapeHtml(product.description)}</p>
            <div class="stock-status ${stockStatus.class}">${stockStatus.text}</div>
            <div class="product-actions">
                <button class="add-to-cart-btn" data-id="${product.id}" type="button">Add to Cart</button>
                <a href="/pages/detail.html?id=${product.id}" class="view-btn">View Details</a>
            </div>
        </article>
    `;
}

// ==========================
// RENDERING FUNCTIONS
// ==========================

function renderProductGrid(productList) {
    const productContainer = document.querySelector(".product-container");
    if (!productContainer) {
        console.error("[renderProductGrid] No product container found.");
        return;
    }

    if (!productList || productList.length === 0) {
        productContainer.innerHTML = `
            <div class="empty-state">
                <h2>No products available</h2>
                <p>Please add products to the database.</p>
                <button onclick="location.reload()" class="retry-btn">Refresh</button>
            </div>
        `;
        return;
    }

    productContainer.innerHTML = productList.map(createProductCard).join("");
    console.log(`[renderProductGrid] Rendered ${productList.length} products`);
}

function renderLoadingState() {
    const productContainer = document.querySelector(".product-container");
    if (!productContainer) return;

    productContainer.innerHTML = `
        <div class="loading-spinner">
            <div class="spinner"></div>
            <p>Loading products from database...</p>
        </div>
    `;
}

function renderProductError(error) {
    const productContainer = document.querySelector(".product-container");
    if (!productContainer) return;

    const message = error instanceof ApiRequestError
        ? error.message
        : "Unable to load products right now. Please try again later.";

    productContainer.innerHTML = `
        <div class="error-message">
            <h2>⚠️ Could not load products</h2>
            <p>${escapeHtml(message)}</p>
            <p style="margin-top: 10px;">Make sure:</p>
            <ul style="text-align: left; display: inline-block;">
                <li>Backend is running: <code>./gradlew bootRun</code></li>
                <li>Backend is on port 8080</li>
                <li>Database has products</li>
            </ul>
            <br>
            <button onclick="location.reload()" class="retry-btn">Retry</button>
        </div>
    `;
}

// ==========================
// FEATURED PRODUCTS (LANDING PAGE)
// ==========================

async function renderFeaturedProducts() {
    const productGrid = document.querySelector('.featured .product-grid');
    if (!productGrid) return;

    productGrid.innerHTML = '<div class="loading-spinner"><div class="spinner"></div><p>Loading featured products...</p></div>';

    try {
        const apiProducts = await fetchProducts();
        const featuredProducts = apiProducts.slice(0, 4);
        
        productGrid.innerHTML = '';
        
        featuredProducts.forEach(product => {
            const normalizedProduct = normalizeProduct(product);
            const productDiv = document.createElement('div');
            productDiv.className = 'product';
            productDiv.setAttribute('data-product-id', normalizedProduct.id);
            
            productDiv.innerHTML = `
                <img src="${normalizedProduct.imageUrl}" alt="${escapeHtml(normalizedProduct.name)}" onerror="this.src='${IMAGE_FALLBACK}'">
                <h3>${escapeHtml(normalizedProduct.name)}</h3>
                <p>${formatPrice(normalizedProduct.price)}</p>
                <p class="description-preview">${escapeHtml(normalizedProduct.description.substring(0, 50))}...</p>
                <button class="add-to-cart-btn" data-id="${normalizedProduct.id}">Add to Cart</button>
            `;
            
            productDiv.style.cursor = 'pointer';
            productDiv.addEventListener('click', (e) => {
                if (e.target.tagName !== 'BUTTON') {
                    window.location.href = `/pages/detail.html?id=${normalizedProduct.id}`;
                }
            });
            
            productGrid.appendChild(productDiv);
        });
        
        console.log(`[renderFeaturedProducts] Rendered ${featuredProducts.length} featured products`);
    } catch (error) {
        console.error("[renderFeaturedProducts] Error:", error);
        productGrid.innerHTML = `<div class="error-message">Failed to load featured products: ${error.message}</div>`;
    }
}

async function renderDiscountedProducts() {
    const discountedGrid = document.querySelector('.discounted .product-grid');
    if (!discountedGrid) return;

    discountedGrid.innerHTML = '<div class="loading-spinner"><div class="spinner"></div><p>Loading discounted products...</p></div>';

    try {
        const apiProducts = await fetchProducts();
        
        let discountedProducts = apiProducts.filter(p => p.price > 300).slice(0, 4);
        if (discountedProducts.length < 4) {
            discountedProducts = apiProducts.slice(4, 8);
        }
        if (discountedProducts.length === 0) {
            discountedProducts = apiProducts.slice(0, 4);
        }
        
        discountedGrid.innerHTML = '';
        
        discountedProducts.forEach(product => {
            const normalizedProduct = normalizeProduct(product);
            const originalPrice = (normalizedProduct.price * 1.3).toFixed(2);
            
            const productDiv = document.createElement('div');
            productDiv.className = 'product';
            productDiv.setAttribute('data-product-id', normalizedProduct.id);
            
            productDiv.innerHTML = `
                <img src="${normalizedProduct.imageUrl}" alt="${escapeHtml(normalizedProduct.name)}" onerror="this.src='${IMAGE_FALLBACK}'">
                <h3>${escapeHtml(normalizedProduct.name)}</h3>
                <p><del>${formatPrice(originalPrice)}</del> ${formatPrice(normalizedProduct.price)}</p>
                <p class="description-preview">${escapeHtml(normalizedProduct.description.substring(0, 50))}...</p>
                <button class="add-to-cart-btn" data-id="${normalizedProduct.id}">Add to Cart</button>
            `;
            
            productDiv.style.cursor = 'pointer';
            productDiv.addEventListener('click', (e) => {
                if (e.target.tagName !== 'BUTTON') {
                    window.location.href = `/pages/detail.html?id=${normalizedProduct.id}`;
                }
            });
            
            discountedGrid.appendChild(productDiv);
        });
        
        console.log(`[renderDiscountedProducts] Rendered ${discountedProducts.length} discounted products`);
    } catch (error) {
        console.error("[renderDiscountedProducts] Error:", error);
        discountedGrid.innerHTML = `<div class="error-message">Failed to load discounted products</div>`;
    }
}

// ==========================
// PRODUCT DETAIL PAGE
// ==========================

async function loadProductDetail() {
    const urlParams = new URLSearchParams(window.location.search);
    const productId = urlParams.get('id');

    if (!productId || !window.location.pathname.includes('detail.html')) {
        return;
    }

    const productInfoDiv = document.querySelector('.product-info');
    if (productInfoDiv) {
        productInfoDiv.innerHTML = '<div class="loading-spinner"><div class="spinner"></div><p>Loading product details...</p></div>';
    }

    try {
        const product = await fetchProductById(productId);
        const normalizedProduct = normalizeProduct(product);

        document.title = `${normalizedProduct.name} - Clothing Shop`;

        const productImg = document.querySelector('.product-wrapper img');
        if (productImg) {
            productImg.src = normalizedProduct.imageUrl;
            productImg.alt = normalizedProduct.name;
            productImg.onerror = function() { this.src = IMAGE_FALLBACK; };
        }

        if (productInfoDiv) {
            productInfoDiv.innerHTML = `
                <h1>${escapeHtml(normalizedProduct.name)}</h1>
                <div class="price">${formatPrice(normalizedProduct.price)}</div>
                <div class="stock-status ${getStockStatus(normalizedProduct.stockQuantity).class}">
                    ${getStockStatus(normalizedProduct.stockQuantity).text}
                </div>
                <p class="description">${escapeHtml(normalizedProduct.description)}</p>
                <div class="options">
                    <label>Color</label>
                    <select>
                        <option>Black</option>
                        <option>White</option>
                        <option>Blue</option>
                    </select>

                    <label>Size</label>
                    <select>
                        <option>Small</option>
                        <option>Medium</option>
                        <option>Large</option>
                    </select>

                    <label>Quantity</label>
                    <input type="number" value="1" min="1" max="${normalizedProduct.stockQuantity || 10}" id="detail-quantity">
                </div>
                <button class="btn" id="detail-add-to-cart">Add to Cart</button>
            `;
        }

        const specTable = document.querySelector('.specs table');
        if (specTable) {
            const newRow = document.createElement('tr');
            newRow.innerHTML = `
                <th>Product ID</th>
                <td>${normalizedProduct.id}</td>
            `;
            specTable.appendChild(newRow);
        }

        const addToCartBtn = document.getElementById('detail-add-to-cart');
        if (addToCartBtn) {
            addToCartBtn.addEventListener('click', () => {
                const quantity = parseInt(document.getElementById('detail-quantity')?.value || 1);
                addToCart(normalizedProduct.id, quantity);
            });
        }

    } catch (error) {
        console.error("[loadProductDetail] Error:", error);
        if (productInfoDiv) {
            productInfoDiv.innerHTML = `
                <div class="error-message">
                    <h2>Product not found</h2>
                    <p>${error.message}</p>
                    <a href="/pages/products.html" class="btn">Back to Products</a>
                </div>
            `;
        }
    }
}

// ==========================
// CART STATE MANAGEMENT
// ==========================

function loadCart() {
    const savedCart = localStorage.getItem('cart');
    if (savedCart) {
        cart = JSON.parse(savedCart);
        console.log(`[loadCart] Loaded ${cart.length} items from localStorage`);
        updateCartCount();
        renderCart();
        updateCheckoutSummary();
    }
}

function saveCart() {
    localStorage.setItem('cart', JSON.stringify(cart));
    updateCartCount();
    console.log(`[saveCart] Saved ${cart.length} items to localStorage`);
}

function updateCartCount() {
    const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0);
    const cartCountElements = document.querySelectorAll('#cartCount');
    cartCountElements.forEach(el => {
        if (el) el.textContent = totalItems;
    });
}

function addToCart(productId, quantity = 1) {
    const product = products.find(p => p.id === Number(productId));
    if (!product) {
        console.error(`[addToCart] Product ${productId} not found`);
        return;
    }

    const existingItem = cart.find(item => item.id === product.id);
    if (existingItem) {
        existingItem.quantity += quantity;
    } else {
        cart.push({
            id: product.id,
            name: product.name,
            price: product.price,
            imageUrl: product.imageUrl,
            quantity: quantity
        });
    }

    saveCart();
    renderCart();
    updateCheckoutSummary();
    showNotification(`${product.name} added to cart!`, 'success');
    
    const productCard = document.querySelector(`.product-card[data-product-id="${productId}"], .product[data-product-id="${productId}"]`);
    if (productCard) {
        productCard.classList.add('fade-in');
        setTimeout(() => productCard.classList.remove('fade-in'), 500);
    }
}

function updateQuantity(productId, newQuantity) {
    const item = cart.find(item => item.id === Number(productId));
    
    if (item) {
        newQuantity = parseInt(newQuantity);
        
        if (newQuantity <= 0) {
            cart = cart.filter(item => item.id !== Number(productId));
        } else {
            item.quantity = newQuantity;
        }
        
        saveCart();
        renderCart();
        updateCheckoutSummary();
    }
}

function removeFromCart(productId) {
    cart = cart.filter(item => item.id !== Number(productId));
    saveCart();
    renderCart();
    updateCheckoutSummary();
    showNotification('Item removed from cart', 'info');
}

function renderCart() {
    const cartContainer = document.querySelector('.cart-items');
    const subtotalElement = document.querySelector('.subtotal h2, .subtotal');
    const emptyCartMessage = document.querySelector('.empty-cart');
    const checkoutBtn = document.querySelector('.checkout-btn');

    if (!cartContainer) return;

    if (cart.length === 0) {
        cartContainer.innerHTML = '';
        if (emptyCartMessage) emptyCartMessage.style.display = 'block';
        if (subtotalElement) {
            subtotalElement.innerHTML = '<h2>Subtotal: ₱0</h2>';
        }
        if (checkoutBtn) checkoutBtn.style.opacity = '0.5';
        return;
    }

    if (emptyCartMessage) emptyCartMessage.style.display = 'none';
    if (checkoutBtn) checkoutBtn.style.opacity = '1';

    cartContainer.innerHTML = '';

    cart.forEach(item => {
        const li = document.createElement('li');
        li.classList.add('cart-card');
        li.setAttribute('data-cart-id', item.id);

        li.innerHTML = `
            <img src="${item.imageUrl || IMAGE_FALLBACK}" alt="${escapeHtml(item.name)}" onerror="this.src='${IMAGE_FALLBACK}'">
            <div class="product-info">
                <h3>${escapeHtml(item.name)}</h3>
                <p class="price">${formatPrice(item.price)}</p>
            </div>
            <input type="number" value="${item.quantity}" min="1" data-id="${item.id}" class="cart-quantity">
            <button class="remove-btn" data-id="${item.id}">Remove</button>
        `;

        cartContainer.appendChild(li);
    });

    document.querySelectorAll('.cart-quantity').forEach(input => {
        input.addEventListener('change', (e) => {
            updateQuantity(e.target.dataset.id, e.target.value);
        });
    });

    document.querySelectorAll('.remove-btn').forEach(btn => {
        btn.addEventListener('click', (e) => {
            removeFromCart(e.target.dataset.id);
        });
    });

    const subtotal = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
    if (subtotalElement) {
        subtotalElement.innerHTML = `<h2>Subtotal: ${formatPrice(subtotal)}</h2>`;
    }
}

function updateCheckoutSummary() {
    const subtotal = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
    const shippingFee = subtotal > 0 ? 60 : 0;
    const total = subtotal + shippingFee;
    
    const itemCountSpan = document.getElementById('item-count');
    const subtotalSpan = document.getElementById('subtotal-amount');
    const shippingSpan = document.getElementById('shipping-fee');
    const totalSpan = document.getElementById('total-amount');
    
    if (itemCountSpan) {
        const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0);
        itemCountSpan.textContent = totalItems;
    }
    if (subtotalSpan) subtotalSpan.textContent = formatPrice(subtotal);
    if (shippingSpan) shippingSpan.textContent = formatPrice(shippingFee);
    if (totalSpan) totalSpan.textContent = formatPrice(total);
}

// ==========================
// NOTIFICATION SYSTEM
// ==========================

function showNotification(message, type = 'success') {
    const notification = document.createElement('div');
    notification.textContent = message;
    
    const bgColor = type === 'success' ? '#27ae60' : (type === 'error' ? '#e74c3c' : '#3498db');
    
    notification.style.cssText = `
        position: fixed;
        bottom: 20px;
        right: 20px;
        background: ${bgColor};
        color: white;
        padding: 12px 24px;
        border-radius: 8px;
        z-index: 1000;
        animation: slideIn 0.3s ease-out;
        font-family: Arial, sans-serif;
        box-shadow: 0 5px 15px rgba(0,0,0,0.2);
    `;
    
    if (!document.querySelector('#notification-styles')) {
        const style = document.createElement('style');
        style.id = 'notification-styles';
        style.textContent = `
            @keyframes slideIn {
                from { opacity: 0; transform: translateX(100%); }
                to { opacity: 1; transform: translateX(0); }
            }
            @keyframes slideOut {
                from { opacity: 1; transform: translateX(0); }
                to { opacity: 0; transform: translateX(100%); }
            }
            .fade-in { animation: fadeIn 0.5s ease-in-out; }
            @keyframes fadeIn {
                from { opacity: 0; transform: translateY(20px); }
                to { opacity: 1; transform: translateY(0); }
            }
            .retry-btn {
                background: #e67e22;
                color: white;
                border: none;
                padding: 8px 16px;
                border-radius: 5px;
                cursor: pointer;
                margin-top: 10px;
            }
            .retry-btn:hover { background: #d35400; }
            .stock-status.out-of-stock { color: #e74c3c; }
            .stock-status.low-stock { color: #f39c12; }
            .stock-status.in-stock { color: #27ae60; }
        `;
        document.head.appendChild(style);
    }
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.style.animation = 'slideOut 0.3s ease-out';
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

// ==========================
// CHECKOUT FORM VALIDATION
// ==========================

function setupCheckoutForm() {
    const placeOrderBtn = document.getElementById('place-order-btn');
    if (!placeOrderBtn) return;
    
    placeOrderBtn.addEventListener('click', (e) => {
        e.preventDefault();
        validateAndSubmitForm();
    });
    
    function validateAndSubmitForm() {
        const nameInput = document.querySelector('input[name="name"]');
        const provinceInput = document.querySelector('input[name="province"]');
        const municipalityInput = document.querySelector('input[name="municipality"]');
        const streetInput = document.querySelector('input[name="street"]');
        const zipInput = document.querySelector('input[name="zip"]');
        
        document.querySelectorAll('.error-message').forEach(el => el.remove());
        document.querySelectorAll('.error').forEach(el => el.classList.remove('error'));
        
        let isValid = true;
        
        const validateField = (field, fieldName, pattern = null) => {
            if (!field || !field.value.trim()) {
                isValid = false;
                if (field) {
                    field.classList.add('error');
                    const errorMsg = document.createElement('span');
                    errorMsg.textContent = `${fieldName} is required`;
                    errorMsg.classList.add('error-message');
                    field.parentNode.insertBefore(errorMsg, field.nextSibling);
                }
            } else if (pattern && !pattern.test(field.value.trim())) {
                isValid = false;
                field.classList.add('error');
                const errorMsg = document.createElement('span');
                errorMsg.textContent = `Please enter a valid ${fieldName.toLowerCase()}`;
                errorMsg.classList.add('error-message');
                field.parentNode.insertBefore(errorMsg, field.nextSibling);
            }
        };
        
        validateField(nameInput, 'Full Name');
        validateField(provinceInput, 'Province');
        validateField(municipalityInput, 'Municipality');
        validateField(streetInput, 'Street Address');
        validateField(zipInput, 'Zip Code', /^\d{4,5}$/);
        
        const paymentSelected = document.querySelector('input[name="payment"]:checked');
        if (!paymentSelected) {
            isValid = false;
            const paymentSection = document.querySelector('.payment-method-section');
            if (paymentSection) {
                const errorMsg = document.createElement('span');
                errorMsg.textContent = 'Please select a payment method';
                errorMsg.classList.add('error-message');
                paymentSection.appendChild(errorMsg);
            }
        }
        
        if (isValid) {
            if (cart.length === 0) {
                alert('Your cart is empty! Please add items before checking out.');
                window.location.href = '/pages/products.html';
                return;
            }
            
            const subtotal = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
            
            console.log('Order Successful!', {
                items: cart,
                subtotal: subtotal,
                shipping: 60,
                total: subtotal + 60,
                customer: {
                    name: nameInput.value,
                    address: `${streetInput.value}, ${municipalityInput.value}, ${provinceInput.value}`,
                    zip: zipInput.value,
                    payment: paymentSelected.value
                }
            });
            
            alert('Order placed successfully! Thank you for your purchase.');
            cart = [];
            saveCart();
            window.location.href = '/index.html';
        }
    }
}

// ==========================
// USER ACCOUNT PAGE
// ==========================

function setupUserAccount() {
    const currentUser = {
        name: "Nicole",
        orderHistory: [
            { orderId: "#001", date: "2026-03-01", total: 250, items: ["Stylish Shirt"] },
            { orderId: "#002", date: "2026-02-28", total: 150, items: ["Trendy Pants"] },
            { orderId: "#003", date: "2026-02-25", total: 500, items: ["Casual Jacket"] }
        ]
    };
    
    const headerH1 = document.querySelector('header h1');
    if (headerH1) {
        headerH1.textContent = `Welcome, ${currentUser.name}`;
    }
    
    const orderCards = document.querySelectorAll('.order-card');
    orderCards.forEach((card, index) => {
        const summary = card.querySelector('summary');
        if (summary && currentUser.orderHistory[index]) {
            const newSummary = summary.cloneNode(true);
            summary.parentNode.replaceChild(newSummary, summary);
            
            newSummary.addEventListener('click', () => {
                if (card.querySelector('.order-details')) return;
                
                const order = currentUser.orderHistory[index];
                const detailsDiv = document.createElement('div');
                detailsDiv.classList.add('order-details');
                detailsDiv.style.cssText = 'margin-top: 10px; padding-top: 10px; border-top: 1px solid #ddd;';
                
                const itemsList = order.items.map(item => `<li>${escapeHtml(item)}</li>`).join('');
                
                detailsDiv.innerHTML = `
                    <p><strong>Order Date:</strong> ${order.date}</p>
                    <p><strong>Total:</strong> ${formatPrice(order.total)}</p>
                    <p><strong>Items:</strong></p>
                    <ul>${itemsList}</ul>
                `;
                
                card.appendChild(detailsDiv);
            });
        }
    });
}

// ==========================
// REVIEW SECTION
// ==========================

function setupReviewSection() {
    const ratingSlider = document.getElementById('rating');
    const ratingValue = document.getElementById('rating-value');
    const reviewForm = document.getElementById('review-form');
    const reviewText = document.getElementById('review-text');
    const reviewsContainer = document.getElementById('reviews-container');
    
    const currentUser = { name: "Lyndel Carpio" };
    
    if (ratingSlider && ratingValue) {
        ratingSlider.addEventListener('input', function() {
            ratingValue.textContent = this.value;
        });
    }
    
    if (reviewForm) {
        reviewForm.addEventListener('submit', function(event) {
            event.preventDefault();
            
            if (!reviewText.value.trim()) {
                alert('Please write a review first');
                return;
            }
            
            const rating = ratingSlider ? ratingSlider.value : 0;
            const currentDate = new Date().toLocaleDateString();
            const starRating = '★'.repeat(rating) + '☆'.repeat(5 - rating);
            
            const newReview = document.createElement('div');
            newReview.className = 'review-item';
            newReview.innerHTML = `
                <p><strong>${escapeHtml(currentUser.name)}</strong> <span style="color: #ffc107;">${starRating}</span></p>
                <p>"${escapeHtml(reviewText.value.trim())}"</p>
                <small>Posted on: ${currentDate}</small>
                <hr>
            `;
            
            if (reviewsContainer) {
                reviewsContainer.appendChild(newReview);
            }
            
            reviewText.value = '';
            if (ratingSlider) ratingSlider.value = 0;
            if (ratingValue) ratingValue.textContent = '0';
            
            alert('Thank you for your review!');
        });
    }
}

// ==========================
// SIGNUP FORM VALIDATION
// ==========================

function setupSignupForm() {
    const signupForm = document.getElementById('signup-form');
    if (!signupForm) return;
    
    signupForm.addEventListener('submit', (e) => {
        e.preventDefault();
        
        const fullname = document.getElementById('fullname');
        const email = document.getElementById('email');
        const password = document.getElementById('password');
        const confirm = document.getElementById('confirm');
        
        let isValid = true;
        
        document.querySelectorAll('.error-message').forEach(el => el.remove());
        document.querySelectorAll('.error').forEach(el => el.classList.remove('error'));
        
        const showFieldError = (field, message) => {
            const errorMsg = document.createElement('span');
            errorMsg.textContent = message;
            errorMsg.classList.add('error-message');
            field.parentNode.insertBefore(errorMsg, field.nextSibling);
        };
        
        if (!fullname.value.trim()) {
            isValid = false;
            fullname.classList.add('error');
            showFieldError(fullname, 'Full name is required');
        }
        
        if (!email.value.trim()) {
            isValid = false;
            email.classList.add('error');
            showFieldError(email, 'Email is required');
        } else if (!/^\S+@\S+\.\S+$/.test(email.value)) {
            isValid = false;
            email.classList.add('error');
            showFieldError(email, 'Please enter a valid email address');
        }
        
        if (!password.value.trim()) {
            isValid = false;
            password.classList.add('error');
            showFieldError(password, 'Password is required');
        } else if (password.value.length < 6) {
            isValid = false;
            password.classList.add('error');
            showFieldError(password, 'Password must be at least 6 characters');
        }
        
        if (!confirm.value.trim()) {
            isValid = false;
            confirm.classList.add('error');
            showFieldError(confirm, 'Please confirm your password');
        } else if (password.value !== confirm.value) {
            isValid = false;
            confirm.classList.add('error');
            showFieldError(confirm, 'Passwords do not match');
        }
        
        if (isValid) {
            alert('Account created successfully!');
            window.location.href = '/pages/account.html';
        }
    });
}

// ==========================
// FILTER FUNCTIONALITY
// ==========================

function setupFilters() {
    const lowToHighInput = document.querySelector('input[value="low-to-high"]');
    const highToLowInput = document.querySelector('input[value="high-to-low"]');
    
    const filterProducts = () => {
        let filteredProducts = [...products];
        
        if (lowToHighInput && lowToHighInput.checked) {
            filteredProducts.sort((a, b) => a.price - b.price);
        } else if (highToLowInput && highToLowInput.checked) {
            filteredProducts.sort((a, b) => b.price - a.price);
        }
        
        renderProductGrid(filteredProducts);
    };
    
    if (lowToHighInput) lowToHighInput.addEventListener('change', filterProducts);
    if (highToLowInput) highToLowInput.addEventListener('change', filterProducts);
}

// ==========================
// EVENT DELEGATION
// ==========================

function setupEventDelegation() {
    document.body.addEventListener('click', (event) => {
        const addButton = event.target.closest('.add-to-cart-btn');
        if (addButton) {
            const productId = addButton.getAttribute('data-id');
            if (productId) {
                addToCart(productId);
            }
        }
    });
}

// ==========================
// MAIN INITIALIZATION
// ==========================

async function initializeProductsPage() {
    const productContainer = document.querySelector('.product-container');
    if (!productContainer) {
        console.log("[initializeProductsPage] Not on products page");
        return;
    }
    
    console.log("🚀 Initializing Products Page with Fetch API...");
    console.log(`📡 API Endpoint: ${PRODUCTS_ENDPOINT}`);
    
    renderLoadingState();
    
    try {
        const apiProducts = await fetchProducts();
        products = apiProducts.map(normalizeProduct);
        console.log(`[initializeProductsPage] Normalized ${products.length} products`);
        
        // Log all image URLs to debug
        products.forEach(p => {
            console.log(`Product: ${p.name}, Image: ${p.imageUrl}`);
        });
        
        renderProductGrid(products);
        setupFilters();
    } catch (error) {
        console.error("[initializeProductsPage] Error:", error);
        renderProductError(error);
    }
}

async function initializeLandingPage() {
    if (document.querySelector('.featured .product-grid')) {
        console.log("🚀 Initializing Landing Page with Fetch API...");
        await renderFeaturedProducts();
        await renderDiscountedProducts();
    }
}

// ==========================
// DOM CONTENT LOADED EVENT
// ==========================

document.addEventListener('DOMContentLoaded', async () => {
    console.log("========================================");
    console.log("🛍️ E-Commerce Frontend - Task 5 Complete");
    console.log("📡 Using Fetch API with async/await");
    console.log(`📍 Backend API: ${API_BASE_URL}`);
    console.log("========================================");
    
    loadCart();
    setupEventDelegation();
    await initializeProductsPage();
    await initializeLandingPage();
    setupCheckoutForm();
    setupUserAccount();
    loadProductDetail();
    setupSignupForm();
    setupReviewSection();
    updateCheckoutSummary();
    
    if (document.querySelector('.cart-items')) {
        renderCart();
    }
    
    console.log("✅ Application initialized successfully!");
});