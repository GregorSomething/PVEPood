


const app = Vue.createApp({
    data() {
        return {
            screen: 'register',
            // Type: [{name: String, description: String, price: float, type: String, category: String, count: int, imageUrl: String}]
            products: [
                {id: 0, name: "Retro Burger", description: "Human food", price: 3.99, type: "Burger", category: "Toit", count: 347, imageUrl: 'https://mamma.ee/arendus/wp-content/uploads/2020/11/Mamma-Retroburger-230g.jpg'},
                {id: 1, name: "Kanapitsa Ananassiga", description: "Human food", price: 8.99, type: "Pitsa", category: "Toit", count: 10, imageUrl: 'https://superskypark.ee/wp-content/uploads/2020/01/kana-ananassi-pitsa.jpg'},
                {id: 2, name: "Rafaello", description: "Human food", price: 6.99, type: "Kommid", category: "Toit", count: 1, imageUrl: 'https://balticfresh.com/image/cache/catalog/April%20products/ferrero-rafaello-15-pcs-800x800.jpg'},
                {id: 0, name: "Retro Burger", description: "Human food", price: 3.99, type: "Burger", category: "Toit", count: 347, imageUrl: 'https://mamma.ee/arendus/wp-content/uploads/2020/11/Mamma-Retroburger-230g.jpg'},
                {id: 1, name: "Kanapitsa Ananassiga", description: "Human food", price: 8.99, type: "Pitsa", category: "Toit", count: 10, imageUrl: 'https://superskypark.ee/wp-content/uploads/2020/01/kana-ananassi-pitsa.jpg'},
                {id: 2, name: "Rafaello", description: "Human food", price: 6.99, type: "Kommid", category: "Toit", count: 1, imageUrl: 'https://balticfresh.com/image/cache/catalog/April%20products/ferrero-rafaello-15-pcs-800x800.jpg'},
                {id: 0, name: "Retro Burger", description: "Human food", price: 3.99, type: "Burger", category: "Toit", count: 347, imageUrl: 'https://mamma.ee/arendus/wp-content/uploads/2020/11/Mamma-Retroburger-230g.jpg'},
                {id: 1, name: "Kanapitsa Ananassiga", description: "Human food", price: 8.99, type: "Pitsa", category: "Toit", count: 10, imageUrl: 'https://superskypark.ee/wp-content/uploads/2020/01/kana-ananassi-pitsa.jpg'},
                {id: 2, name: "Rafaello", description: "Human food", price: 6.99, type: "Kommid", category: "Toit", count: 1, imageUrl: 'https://balticfresh.com/image/cache/catalog/April%20products/ferrero-rafaello-15-pcs-800x800.jpg'},
            ]
        };
    },
    computed: {

    },
    methods: {
        /*  #########################
            ### UTILITY FUNCTIONS ###
            ######################### */

        UTIL_checkUsername(username) {
            return /^[a-zA-Z0-9_]{5,100}$/.test(username);
        },
        UTIL_checkPassword(password) {
            return /^[a-zA-Z0-9_'!"#%&/()=+\-*]{7,100}$/.test(password);
        },
        UTIL_checkDisplayName(displayName) {
            return /^[a-zA-Z0-9_ ]{5,100}$/.test(displayName);
        },

        /*  ####################
            ### UI FUNCTIONS ###
            #################### */

        UI_onSubmitLogin() {
            console.log('username:', event.target.elements.username.value);
            console.log('password:', event.target.elements.password.value);
            console.log('remember-me:', event.target.elements["remember-me"].checked);

            if(!(this.UTIL_checkUsername(event.target.elements.username.value) && this.UTIL_checkPassword(event.target.elements.password.value))) {
                event.preventDefault();
            }
        },

        UI_onSubmitRegister() {
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
            @returns [{name: String, description: String, price: float, type: String, category: String, count: int, imageUrl: String}]
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
            @returns [{name: String, description: String, price: float, type: String, category: String, count: int, imageUrl: String}]
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
                items: [{name: String, description: String, price: float, type: String, category: String, count: int, imageUrl: String}]
            }
        */
        async API_getCart() {
            return fetch("api/cart/get")
                .then(res => res.json());
        },
    }
}); 

app.mount("#app");
