


const app = Vue.createApp({
    data() {
        return {
            loggedIn: false,
            user: null,
            screen: 'home',
            // Type: [{id: int, name: String, description: String, price: float, type: String, category: String, count: int, imgUrl: String}]
            products: [],
            // Type: [{cartCount: int, item: {id: int, name: String, description: String, price: float, type: String, category: String, count: int, imgUrl: String}}]
            cart: [],
            category: '-',
            search: '',
            categories: ['-'],
            newProduct: {
                id: 0,
                name: '',
                description: '',
                price: 1.00,
                count: 1,
                category: '',
                type: '',
                imgUrl: ''
            },
            sorting: 'A - Z',
            reg_username: '',
            reg_password: '',
            reg_displayName: '',
            reg_imageUrl: '',
            reg_feedback: '',
            log_feedback: '',
            new_feedback: ''
        };
    },
    computed: {
        sortedProducts() {
            let toBeSorted = [...this.products];

            switch(this.sorting) {
                case 'A - Z':
                    toBeSorted.sort((a, b) => a.name.localeCompare(b.name));
                    break;
                case 'Z - A':
                    toBeSorted.sort((a, b) => b.name.localeCompare(a.name));
                    break;
                case '€ - €€€':
                    toBeSorted.sort((a, b) => a.price - b.price);
                    break;
                case '€€€ - €':
                    toBeSorted.sort((a, b) => b.price - a.price);
                    break;
            }

            return toBeSorted;
        }
    },
    methods: {
        title() {
            if(this.screen == 'new') {
                return this.newProduct.id ? "Muuda" : "Lisa";
            }
            return {
                "home": "Tooted",
                "cart": "Ostukorv",
                "pay":  "Maksma",
                "user": "Konto",
                "new": "Lisa",
                "update": "Muuda",
            }[this.screen];
        },

        /*  #########################
            ### UTILITY FUNCTIONS ###
            ######################### */

        UTIL_checkUsername(username) {
            return /^[a-zA-Z0-9_äöõüÄÖÕÜ]{5,100}$/.test(username);
        },
        UTIL_checkPassword(password) {
            return /^[a-zA-Z0-9_äöõüÄÖÕÜ'!"#%&/()=+\-*]{7,100}$/.test(password);
        },
        UTIL_checkDisplayName(displayName) {
            return /^[a-zA-Z0-9_ äöõüÄÖÕÜ]{5,100}$/.test(displayName);
        },
        UTIL_checkProductCategory(category) {
            return /^[a-zäöõü]+$/.test(category);
        },
        UTIL_checkProductName(name) {
            return /^[a-zäöõüÄÖÕÜ ]+$/.test(name);
        },
        UTIL_checkProductCount(str) {
            return /^[0-9]+$/.test(str);
        },
        UTIL_checkProductPrice(str) {
            return /^[0-9]+(\.[0-9]+)?$/.test(str) && parseFloat(str) > 0;
        },

        /*  ####################
            ### UI FUNCTIONS ###
            #################### */

        UI_onSubmitLogin(event) {
            event.preventDefault();

            if(!(this.UTIL_checkUsername(event.target.elements.username.value) && this.UTIL_checkPassword(event.target.elements.password.value))) {
                return;
            }

            fetch('/login', {
                method: "POST",
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body: `username=${encodeURIComponent(event.target.elements.username.value)}&password=${encodeURIComponent(event.target.elements.password.value)}&remember-me=${encodeURIComponent(event.target.elements["remember-me"].value)}`
            }).then((res) => {
                if(res.status == 200) window.location.replace("/");
            });
        },

        UI_onSubmitRegister(event) {
            event.preventDefault();
            console.log('username:', event.target.elements.username.value);
            console.log('displayName:', event.target.elements.displayName.value);
            console.log('password:', event.target.elements.password.value);

            if(!(this.UTIL_checkUsername(event.target.elements.username.value) && this.UTIL_checkDisplayName(event.target.elements.displayName.value) && this.UTIL_checkPassword(event.target.elements.password.value))) {
                return;
            }

            fetch('/api/user/register', {
                method: "POST",
                redirect: 'manual',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body: `username=${encodeURIComponent(event.target.elements.username.value)}&password=${encodeURIComponent(event.target.elements.password.value)}&displayName=${encodeURIComponent(event.target.elements.displayName.value)}&imageUrl=${encodeURIComponent(event.target.elements.imageUrl.value)}`
            }).then((res) => {
                if(res.status == 200) window.location.replace("/login");
                else this.reg_feedback = 'Kasutajatunnus juba võetud';
            });
        },

        /**
            Must be ran after search string or category has been changed.
            Updates products list using filters.
        */
        UI_applyFilter() {
            this.API_products(this.search, this.category == '-' ? undefined : this.category)
                .then(products => this.products = products);
        },

        /**
            Calculates total of cart contents.
        */
        UI_total() {
            return this.cart.reduce((val, cartItem) => val += cartItem.cartCount * cartItem.item.price, 0).toFixed(2);
        },

        /**
            Pays & updates local copy of products.
        */
        UI_pay() {
            this.API_pay().then(async res => {
                if(res.status == 200) {
                    this.getAllProducts();
                    this.getCart();
                }
            });
        },
        /**
            Create new product from this.newProduct.
        */
        UI_newProduct() {
            this.API_newProduct(this.newProduct).then(res => {
                if(res.status == 200) {
                    this.UI_resetNewProduct();
                    this.screen = 'home';
                    this.getAllProducts();
                } else {
                    this.new_feedback = 'Toote lisamine ebaõnnestus';
                }
            });
        },

        UI_updateProduct() {
            console.log('newProduct:', this.newProduct);
            this.API_updateProduct(this.newProduct).then(res => {
                if(res.status == 200) {
                    this.newProduct = {
                        id: 0,
                        name: '',
                        description: '',
                        price: 1.00,
                        count: 1,
                        category: '',
                        type: '',
                        imgUrl: ''
                    };
                    this.screen = 'home';
                    this.getAllProducts();
                } else {
                    this.new_feedback = 'Failed to update product';
                }
            });
        },

        UI_isAdmin() {
            return this.user && this.user.roles.includes('ADMIN');
        },

        UI_editProduct(product) {
            this.screen = 'new';
            this.newProduct = product;
        },

        UI_deleteProduct(id) {
            this.API_deleteProduct(id)
                .then(res => {
                    if(res.status == 200) this.getAllProducts();
                });
        },
        UI_resetNewProduct() {
            this.newProduct = {
                name: '',
                description: '',
                price: 1.00,
                count: 1,
                category: '',
                type: '',
                imgUrl: ''
            };
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
        async API_products(name, category) {
            let str = `api/shop/list?n=${name}&c=${category}`;
            if(!category) str = `api/shop/list?n=${name}`;
            console.log('GET', str);
            return fetch(str)
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
        /**
            @returns [string]
            Returns all categories in use, which can be used for filtering
        */
        async API_getAllCategories() {
            return await fetch("/api/shop/categories")
                .then(res => res.json());
        },
        async API_pay() {
            return fetch("/api/cart/pay", {
                method: "POST"
            });
        },
        async API_newProduct(product) {
            return fetch("/api/shop/add", {
                method: "POST",
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(product)
            });
        },
        async API_updateProduct(product) {
            return fetch("/api/shop/update", {
                method: "POST",
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(product)
            });
        },
        async API_deleteProduct(id) {
            return fetch("/api/shop/delete?id="+id, {
                method: "DELETE"
            });
        },



        findCartProduct(id) {
            return this.cart.find(cartProduct => cartProduct.item && cartProduct.item.id == id);
        },
        findProduct(id) {
            return this.products.find(product => product.id == id);
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
                this.cart.push(cartProduct = {id, item: this.findProduct(id), cartCount: 1});
            } else {
                cartProduct.cartCount++;
            }
            this.API_addToCart(id, cartProduct.cartCount);
        },
        async getAllProducts() {
            this.API_allProducts().then(products => {
                if(products) this.products = products;
            });
        },
        async getCart() {
            this.cart = (await this.API_getCart()).items;
        }
    },
    mounted() {
        this.getAllProducts();
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
        this.API_getAllCategories().then(categories => this.categories = ['-', ...categories]);
    }
}); 

app.mount("#app");
