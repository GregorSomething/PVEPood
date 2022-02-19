


const app = Vue.createApp({
    data() {
        return {
            loggedIn: false,
            user: null,
            screen: 'home',
            // Type: [{id: int, name: String, description: String, price: float, type: String, category: String, count: int, imgUrl: String}]
            products: [
                {id: 0, name: "Retro Burger", description: "Human food", price: 3.99, type: "Burger", category: "Toit", count: 347, imgUrl: 'https://mamma.ee/arendus/wp-content/uploads/2020/11/Mamma-Retroburger-230g.jpg'},
                {id: 1, name: "Kanapitsa Ananassiga", description: "Human food", price: 8.99, type: "Pitsa", category: "Toit", count: 10, imgUrl: 'https://superskypark.ee/wp-content/uploads/2020/01/kana-ananassi-pitsa.jpg'},
                {id: 2, name: "Rafaello", description: "Human food", price: 6.99, type: "Kommid", category: "Toit", count: 1, imgUrl: 'https://balticfresh.com/image/cache/catalog/April%20products/ferrero-rafaello-15-pcs-800x800.jpg'},
                {id: 0, name: "Retro Burger", description: "Human food", price: 3.99, type: "Burger", category: "Toit", count: 347, imgUrl: 'https://mamma.ee/arendus/wp-content/uploads/2020/11/Mamma-Retroburger-230g.jpg'},
                {id: 1, name: "Kanapitsa Ananassiga", description: "Human food", price: 8.99, type: "Pitsa", category: "Toit", count: 10, imgUrl: 'https://superskypark.ee/wp-content/uploads/2020/01/kana-ananassi-pitsa.jpg'},
                {id: 2, name: "Rafaello", description: "Human food", price: 6.99, type: "Kommid", category: "Toit", count: 1, imgUrl: 'https://balticfresh.com/image/cache/catalog/April%20products/ferrero-rafaello-15-pcs-800x800.jpg'},
                {id: 0, name: "Retro Burger", description: "Human food", price: 3.99, type: "Burger", category: "Toit", count: 347, imgUrl: 'https://mamma.ee/arendus/wp-content/uploads/2020/11/Mamma-Retroburger-230g.jpg'},
                {id: 1, name: "Kanapitsa Ananassiga", description: "Human food", price: 8.99, type: "Pitsa", category: "Toit", count: 10, imgUrl: 'https://superskypark.ee/wp-content/uploads/2020/01/kana-ananassi-pitsa.jpg'},
                {id: 2, name: "Rafaello", description: "Human food", price: 6.99, type: "Kommid", category: "Toit", count: 1, imgUrl: 'https://balticfresh.com/image/cache/catalog/April%20products/ferrero-rafaello-15-pcs-800x800.jpg'},
            ],
            // Type: [{cartCount: int, item: {id: int, name: String, description: String, price: float, type: String, category: String, count: int, imgUrl: String}}]
            cart: []
        };
    },
    computed: {

    },
    methods: {
        title() {
            return {
                "home": "Tooted",
                "cart": "Ostukorv",
                "pay":  "Maksma",
                "user": "Konto",
            }[this.screen];
        },

        /*  #########################
            ### UTILITY FUNCTIONS ###
            ######################### */

        UTIL_checkUsername(username) {
            return /^[a-zA-Z0-9_]{5,100}$/.test(username);
        },
        UTIL_checkPassword(password) {
            return /^[a-zA-Z0-9_'!"#%&/()=+\-*]{4,100}$/.test(password);
        },
        UTIL_checkDisplayName(displayName) {
            return /^[a-zA-Z0-9_ ]{5,100}$/.test(displayName);
        },

        /*  ####################
            ### UI FUNCTIONS ###
            #################### */

        UI_onSubmitLogin(event) {
            console.log('username:', event.target.elements.username.value);
            console.log('password:', event.target.elements.password.value);
            console.log('remember-me:', event.target.elements["remember-me"].checked);

            if(!(this.UTIL_checkUsername(event.target.elements.username.value) && this.UTIL_checkPassword(event.target.elements.password.value))) {
                event.preventDefault();
            }
        },

        UI_onSubmitRegister(event) {
            console.log('username:', event.target.elements.username.value);
            console.log('displayName:', event.target.elements.displayName.value);
            console.log('password:', event.target.elements.password.value);

            if(!(this.UTIL_checkUsername(event.target.elements.username.value) && this.UTIL_checkDisplayName(event.target.elements.displayName.value) && this.UTIL_checkPassword(event.target.elements.password.value))) {
                event.preventDefault();
            }
        },

        /*  #####################
            ### API FUNCTIONS ###
            ##################### */

        /**
            @returns [{id: int, name: String, description: String, price: float, type: String, category: String, count: int, imgUrl: String}]
            Retrieves all products from the server.
        */
        async API_allProducts() {
            return fetch("api/shop/all")
                .then(res => res.json());
        },
        /**
            @param name         Products with that name.
            @param type         Products of that type.
            @param category     Products in that category.
            @returns [{id: int, name: String, description: String, price: float, type: String, category: String, count: int, imgUrl: String}]
            Searches for products.
        */
        async API_products(name, type, category) {
            return fetch(`api/shop/list?n=${name}&t=${type}&c=${category}`)
                .then(res => res.json());
        },
        /**
            @param productID    Product which will be added to user's cart.
            @param count        How many of that product will be added to user's cart.
            @returns void
        */
        async API_addToCart(productID, count) {
            fetch(`api/cart/add/${productID}/${count}`, {
                method: "POST",
                headers: {'Content-Type': 'application/json'}, 
                body: "{}"
            }).then(res => console.log(res));
        },
        /**
            @returns {
                price: float,
                items: [{item: {id: int, name: String, description: String, price: float, type: String, category: String, count: int, imgUrl: String}, cartCount: int}]
            }
        */
        async API_getCart() {
            return fetch("api/cart/get")
                .then(res => res.json());
        },
        /**
            @returns {
                username: String, clientPass: null, roles: ["ADMIN"], sRoles: ["ADMIN"], imageUrl: URL, displayName: String, enabled: boolean, password: null, authorities: null, credentialsNonExpired: boolean, accountNonExpired: boolean, accountNonLocked: boolean
            }
         */
        async API_getUser() {
            return fetch("api/user/get")
               .then(res => res.json())
               .catch(err => null);
        },
        /**
            @returns true if logged in, false if not logged in
        */
        async API_isUserLoggedIn() {
            return (await fetch("api/user/get", {redirect: 'manual'})).status == 200;
        },



        findCartProduct(id) {
            return this.cart.find(cartProduct => cartProduct.item && cartProduct.item.id == id);
        },
        decrementCount(id) {
            let cartProduct = this.findCartProduct(id);
            if(cartProduct && cartProduct.cartCount > 0) {
                cartProduct.cartCount--;
                this.API_addToCart(id, cartProduct.cartCount);
                if(cartProduct.cartCount == 0) this.cart.splice(this.cart.indexOf(cartProduct), 1);
            }
        },
        incrementCount(id) {
            let cartProduct = this.findCartProduct(id);
            if(!cartProduct) {
                this.cart.push(cartProduct = {id, item: this.products[id], cartCount: 1});
            } else {
                cartProduct.cartCount++;
            }
            this.API_addToCart(id, cartProduct.cartCount);
        },
        async getCart() {
            this.cart = (await this.API_getCart()).items;
        }
    },
    mounted() {
        this.API_allProducts().then(products => {
            if(products) this.products = products;
        });
        this.API_getUser().then(user => {
            console.log('user/get:', user);
            this.user = user;
        });
        this.API_isUserLoggedIn().then(isLoggedIn => {
            this.loggedIn = isLoggedIn;

            if(this.loggedIn) {
                this.API_getCart().then(cart => {
                    if(cart && cart.items) this.cart = cart.items;
                });
            }
        });
    }
}); 

app.mount("#app");
