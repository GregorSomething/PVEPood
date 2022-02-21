# PVEPood

Simple online e-commerence system made for [ASI karikas 2022](https://asikarikas.ee), using SpringBoot, MariaDB and Vue.js.

Demo available at [Karl-Mihkel Ott's personal website](https://pood.sadblog.xyz/)

## Features

- Sorting by name or price
- Filtering by category (food, supplies, toys etc)
- Search by name
- Modern & easy to use GUI

Admin (username: admin):
- Create new products
- Update existing products
- Delete existing product

User:
- Add & remove products to/from cart
- Cloud synced cart
- Session tokens stored in browser cookies
- Paying for cart
  - Checks if enough supply
  - Subtracts supply count
  - Empties cart
- Account overview tab
- Profile pictures

Products:
- Name
- Description
- Category
- Thumbnail
- Real-time supply count

## Requirements

- Java 11 or newer
- MariaDB or MySQL
- Database account username: user1, password: password
- Empty database called 'test' (`CREATE DATABASE test;`)

## Usage

Run the JAR

For admin:
1. Open https://localhost:8080/register in browser
2. Create an account with username 'admin'
3. Add products

For anonymous browsing:
1. Open https://localhost:8080 in browser
2. Browse products

For users:
1. Open https://localhost:8080/register in browser
2. Create an account with random name
3. Browse, add to cart & buy products

## Contributors

Main contributors are [Gregor Suurvarik](https://github.com/GregorSomething) and [Rainis Randmaa](https://github.com/Matrx007), 
representing Tallinna Tehnikagümnaasium.
