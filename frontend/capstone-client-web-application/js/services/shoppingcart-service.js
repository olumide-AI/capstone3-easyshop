class ShoppingCartService {
  cart = { items: [], total: 0 };

  addToCart(productId) {
    const url = `${config.baseUrl}/cart/products/${productId}`;
    const headers = userService.getHeaders();
    axios.post(url, {}, { headers })
      .then(() => this.loadCart())
      .catch(() => {
        templateBuilder.append("error", { error: "Add product to cart failed." }, "errors");
      });
  }

  // loads the cart from back-end and re-renders
  loadCart() {
    const url = `${config.baseUrl}/cart`;
    axios.get(url)
      .then(response => {
        this.setCart(response.data);
        this.updateCartDisplay();
      })
      .catch(() => {
        templateBuilder.append("error", { error: "Load cart failed." }, "errors");
      });
  }

  setCart(data) {
    this.cart.total = data.total;
    this.cart.items = Object.values(data.items);
  }

  // clears the cart on both front-end and back-end
  clearCart() {
    const url = `${config.baseUrl}/cart`;
    const headers = userService.getHeaders();
    axios.delete(url, { headers })
      .then(() => {
        this.cart = { items: [], total: 0 };
        this.updateCartDisplay();
        this.loadCartPage();
      })
      .catch(() => {
        templateBuilder.append("error", { error: "Empty cart failed." }, "errors");
      });
  }

   checkout() {
    const url = `${config.baseUrl}/checkout`;
    const headers = userService.getHeaders();
    axios.post(url, {}, { headers })
      .then(response => {
-       const order = response.data;
-       window.location.href = `/order-confirmation.html?orderId=${order.orderId}`;
+       const order = response.data;
+       // save the full order for the next page
+       sessionStorage.setItem('lastOrder', JSON.stringify(order));
+       // redirect with the ID in the query string
+       window.location.href = `/order-confirmation.html?orderId=${order.orderId}`;
      })
      .catch(() => {
        templateBuilder.append("error", { error: "Checkout failed." }, "errors");
      });
  }


  // updates the little cart badge in your nav
  updateCartDisplay() {
    const cartControl = document.getElementById("cart-items");
    if (cartControl) {
      cartControl.innerText = this.cart.items.length;
    }
  }

  // builds the full cart page, with Clear + Checkout buttons + item list
  loadCartPage() {
    const main = document.getElementById("main");
    main.innerHTML = "";

    // header area
    const header = document.createElement("div");
    header.classList.add("cart-header");

    const title = document.createElement("h1");
    title.innerText = "Your Cart";
    header.appendChild(title);

    // Clear button
    const clearBtn = document.createElement("button");
    clearBtn.classList.add("btn", "btn-danger");
    clearBtn.innerText = "Clear";
    clearBtn.addEventListener("click", () => this.clearCart());
    header.appendChild(clearBtn);

    // Checkout button
    const checkoutBtn = document.createElement("button");
    checkoutBtn.classList.add("btn", "btn-success", "ml-2");
    checkoutBtn.innerText = "Checkout";
    checkoutBtn.disabled = this.cart.items.length === 0;
    checkoutBtn.addEventListener("click", () => this.checkout());
    header.appendChild(checkoutBtn);

    // Total display
    const totalDisplay = document.createElement("span");
    totalDisplay.classList.add("cart-total");
    totalDisplay.innerText = ` Total: $${this.cart.total.toFixed(2)}`;
    header.appendChild(totalDisplay);

    main.appendChild(header);

    // item list
    this.cart.items.forEach(item => this.buildItem(item, main));
  }

  buildItem(item, parent) {
    const container = document.createElement("div");
    container.classList.add("cart-item");

    // product name
    const name = document.createElement("h4");
    name.innerText = item.product.name;
    container.appendChild(name);

    // image + price
    const img = document.createElement("img");
    img.src = `/images/products/${item.product.imageUrl}`;
    img.classList.add("product-thumb");
    container.appendChild(img);

    const price = document.createElement("p");
    price.innerText = `Price: $${item.product.price}`;
    container.appendChild(price);

    // quantity
    const qty = document.createElement("p");
    qty.innerText = `Quantity: ${item.quantity}`;
    container.appendChild(qty);

    parent.appendChild(container);
  }
}

// wire it up once DOM is ready
document.addEventListener("DOMContentLoaded", () => {
  window.cartService = new ShoppingCartService();
  if (userService.isLoggedIn()) {
    cartService.loadCart();
    cartService.loadCartPage();
  } else {
    // redirect to login or show a message…
    templateBuilder.append("error", { error: "Please log in to see your cart." }, "errors");
  }
});
