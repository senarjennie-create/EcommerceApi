const API_BASE_URL = "http://localhost:8080/api/v1";
const PRODUCTS_ENDPOINT = `${API_BASE_URL}/products`;
const IMAGE_FALLBACK = "/images/cloth.jpg";

let products = [];
let cart = [];

class ApiRequestError extends Error {
    constructor(message, status) {
        super(message);
        this.name = "ApiRequestError";
        this.status = status;
    }
}

async function fetchProducts() {
    try {
        console.log(`[fetchProducts] Requesting ${PRODUCTS_ENDPOINT}`);
        const response = await fetch(PRODUCTS_ENDPOINT, {
            method: "GET",
            headers: {
                "Accept": "application/json"
            }
        });

        if (!response.ok) {
            if (response.status === 404) {
                throw new ApiRequestError("Products endpoint was not found.", 404);
            }

            if (response.status === 500) {
                throw new ApiRequestError("Server error while loading products.", 500);
            }

            throw new ApiRequestError(
                `Unexpected response while loading products: ${response.status} ${response.statusText}`,
                response.status
            );
        }

        const data = await response.json();
        console.log(`[fetchProducts] Loaded ${Array.isArray(data) ? data.length : 0} products`);
        return Array.isArray(data) ? data : [];
    } catch (error) {
        if (error instanceof ApiRequestError) {
            if (error.status === 404) {
                console.error("[fetchProducts] 404 error: check ProductController mapping and frontend API path.", error);
            } else if (error.status === 500) {
                console.error("[fetchProducts] 500 error: backend failed while loading products.", error);
            } else {
                console.error("[fetchProducts] API request failed.", error);
            }
        } else {
            console.error("[fetchProducts] Network or parsing error while loading products.", error);
        }

        throw error;
    }
}

function normalizeProduct(product) {
    return {
        id: product.id,
        name: product.name ?? "Unnamed Product",
        description: product.description ?? "No description available.",
        price: Number(product.price ?? 0),
        imageUrl: product.imageUrl || IMAGE_FALLBACK
    };
}

function formatPrice(price) {
    return new Intl.NumberFormat("en-PH", {
        style: "currency",
        currency: "PHP"
    }).format(price);
}

function createProductCard(product) {
    return `
        <article class="product-card" data-product-id="${product.id}">
            <img src="${product.imageUrl}" alt="${product.name}" onerror="this.src='${IMAGE_FALLBACK}'">
            <h3>${product.name}</h3>
            <p class="product-price">${formatPrice(product.price)}</p>
            <p class="product-description">${product.description}</p>
            <div class="product-actions">
                <button class="add-to-cart-btn" data-id="${product.id}" type="button">Add to Cart</button>
                <a href="/pages/detail.html?id=${product.id}" class="view-btn">View Details</a>
            </div>
        </article>
    `;
}

function renderProductGrid(productList) {
    const main = document.querySelector("main");
    if (!main) {
        console.error("[renderProductGrid] No <main> element found.");
        return;
    }

    const heading = main.querySelector("h1")?.outerHTML ?? "<h1>Product List</h1>";

    if (productList.length === 0) {
        main.innerHTML = `
            ${heading}
            <section class="product-container empty-state-panel">
                <div class="empty-state">
                    <h2>No products available</h2>
                    <p>The API returned an empty list. Add products in the backend and refresh this page.</p>
                </div>
            </section>
        `;
        return;
    }

    main.innerHTML = `
        ${heading}
        <section class="product-container">
            ${productList.map(createProductCard).join("")}
        </section>
    `;
}

function renderLoadingState() {
    const main = document.querySelector("main");
    if (!main) {
        return;
    }

    const heading = main.querySelector("h1")?.outerHTML ?? "<h1>Product List</h1>";
    main.innerHTML = `
        ${heading}
        <section class="product-container">
            <div class="loading-spinner">
                <div class="spinner"></div>
                <p>Loading products...</p>
            </div>
        </section>
    `;
}

function renderProductError(error) {
    const main = document.querySelector("main");
    if (!main) {
        return;
    }

    const heading = main.querySelector("h1")?.outerHTML ?? "<h1>Product List</h1>";
    const message = error instanceof ApiRequestError
        ? error.message
        : "Unable to load products right now. Please try again later.";

    main.innerHTML = `
        ${heading}
        <section class="product-container">
            <div class="error-message">
                <h2>Could not load products</h2>
                <p>${message}</p>
            </div>
        </section>
    `;
}

function loadCart() {
    const savedCart = localStorage.getItem("cart");
    if (savedCart) {
        cart = JSON.parse(savedCart);
    }
}

function saveCart() {
    localStorage.setItem("cart", JSON.stringify(cart));
}

function addToCart(productId) {
    const product = products.find((item) => item.id === Number(productId));
    if (!product) {
        console.error(`[cart] Product ${productId} was not found in the current product list.`);
        return;
    }

    const existingItem = cart.find((item) => item.id === product.id);
    if (existingItem) {
        existingItem.quantity += 1;
    } else {
        cart.push({
            id: product.id,
            name: product.name,
            price: product.price,
            imageUrl: product.imageUrl,
            quantity: 1
        });
    }

    saveCart();
    console.log(`[cart] Added product ${product.id} to cart.`);
}

function setupEventDelegation() {
    document.body.addEventListener("click", (event) => {
        const button = event.target.closest(".add-to-cart-btn");
        if (!button) {
            return;
        }

        addToCart(button.dataset.id);
    });
}

async function initializeProductsPage() {
    if (!document.querySelector("main")) {
        return;
    }

    renderLoadingState();

    try {
        const apiProducts = await fetchProducts();
        products = apiProducts.map(normalizeProduct);
        renderProductGrid(products);
    } catch (error) {
        renderProductError(error);
    }
}

document.addEventListener("DOMContentLoaded", async () => {
    loadCart();
    setupEventDelegation();
    await initializeProductsPage();
});
