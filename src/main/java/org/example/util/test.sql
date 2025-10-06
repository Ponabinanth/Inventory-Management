CREATE DATABASE inventorDB;
USE inventoryDB;

CREATE TABLE products
(
    product_id INT PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    quantity INT NOT NULL CHECK (quantity >= 0),
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0)
);

INSERT INTO product VALUES
(1, 'Laptop', 'Electronics', 50, 100000);
INSERT INTO product VALUES
(2, 'Chair', 'Furniture', 76, 2500);
INSERT INTO product VALUES
(3, 'Pen', 'Stationery', 100, 80);
INSERT INTO product VALUES
(4, 'Phone', 'Electronics', 65, 30000);
INSERT INTO product VALUES
(5, 'Table', 'Furniture', 27, 15000);

select * from products;

SELECT product_name, category FROM products;

SELECT * FROM products WHERE quantity > 10;

SELECT * FROM products WHERE price < 5000;

SELECT * FROM products WHERE category = 'Electronics';

SELECT * FROM products ORDER BY price DESC;

SELECT * FROM products ORDER BY price DESC LIMIT 3;

SELECT SUM(quantity) AS total_quantity FROM products;

SELECT AVG(price) AS average_price FROM products;

SELECT * FROM products ORDER BY price DESC LIMIT 1;

SELECT * FROM products ORDER BY price ASC LIMIT 1;

SELECT category, COUNT(*) AS total_products FROM products GROUP BY category;