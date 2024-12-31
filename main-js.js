// Global state
let currentUser = null;
let cart = [];

// Check authentication status on page load
document.addEventListener('DOMContentLoaded', () => {
    checkAuth();
    loadFeaturedProducts();
    updateCartCount();
});

// Authentication functions
function checkAuth() {
    const token = localStorage.getItem('token');
    if (token) {
        fetch('/api/users/profile', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        })
        .then(response => response.json())
        .then(user => {
            currentUser = user;
            updateNavigation();
        })
        .catch(() => {
            localStorage.removeItem('token');
            updateNavigation();
        });
    }
}

function updateNavigation() {
    const loginButton = document.getElementById('loginButton');
    if (currentUser) {
        loginButton.innerHTML = `
            <a href="/profile">${currentUser.username}</a>
            <button onclick="logout()">Logout</button>
        `;
    } else {
        loginButton.innerHTML = '<a href="/login">Login</a>';
    }
}

function logout() {
    localStorage.removeItem('token');
    currentUser = null;
    updateNavigation();
    window.location.href = '/';
}

// Product functions
function loadFeaturedProducts() {
    fetch('/api/products')
        .then(response => response.json())
        .then(products => {
            const container = document.getElementById('featuredProducts');
            container.innerHTML = products.map(product => createProductCard(product)).join('');
        })
        .catch(error => console.error('Error loading products:', error));
}

function createProductCard(product) {
    return `
        <div class="product-card">
            <img src="${product.imageUrl}" alt="${product.name}">
            <h3>${product.name}</h3>
            <p>${product.description}</p>
            <p class="price">$${product.price}</p>
            <button class="button" onclick="addToCart(${product.id})">Add to Cart</button>
        </div>
    `;
}

// Cart functions
function addToCart(productId) {
    if (!currentUser) {
        window.location.href = '/login';
        return;
    }

    fetch(`/api/cart/add/${productId}`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(() => {
        updateCartCount();
        showNotification('Product added to cart');
    })
    .catch(error => console.error('Error adding to cart:', error));
}

function updateCartCount() {
    if (currentUser) {
        fetch('/api/cart', {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        })
        .then(response => response.json())
        .then(cartItems => {
            const count = cartItems.reduce((total, item) => total + item.quantity, 0);
            document.querySelector('.cart-count').textContent = count;
        });
    }
}

function showNotification(message) {
    const notification = document.createElement('div');
    notification.className = 'notification';
    notification.textContent = message;
    document.body.appendChild(notification);
    setTimeout(() => notification.remove(), 3000);
}

// Search function
function searchProducts(query) {
    fetch(`/api/products/search?q=${encodeURIComponent(query)}`)
        .then(response => response.json())
        .then(products => {
            const container = document.getElementById('productsGrid');
            container.innerHTML = products.map(product => createProductCard(product)).join('');
        })
        .catch(error => console.error('Error searching products:', error));
}
