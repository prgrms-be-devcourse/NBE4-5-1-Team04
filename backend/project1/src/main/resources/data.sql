-- 기존 데이터 삭제
DELETE FROM order_item;
DELETE FROM order_tbl;
DELETE FROM customer;
DELETE FROM item;

-- 테이블 생성
CREATE TABLE IF NOT EXISTS customer (
                                        id INT PRIMARY KEY AUTO_INCREMENT,
                                        username VARCHAR(255) NOT NULL,
    password VARCHAR(512) NOT NULL, -- 🔹 길이 증가
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
    apiKey VARCHAR(50) UNIQUE  -- 🔹 apiKey 컬럼 추가
    );

CREATE TABLE IF NOT EXISTS item (
                                    id INT PRIMARY KEY AUTO_INCREMENT,
                                    name VARCHAR(255) NOT NULL,
    price INT NOT NULL,
    stock INT NOT NULL DEFAULT 10,
    image_uuid BINARY(16) NULL
    );

CREATE TABLE IF NOT EXISTS order_tbl (
                                         id INT PRIMARY KEY AUTO_INCREMENT,
                                         customer_id INT NOT NULL,
                                         total_price INT NOT NULL,
                                         date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 🔹 `NOT NULL` 추가
                                         delivery_status VARCHAR(20) DEFAULT 'PENDING',
    FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS order_item (
                                          id INT PRIMARY KEY AUTO_INCREMENT,
                                          order_id INT NOT NULL,
                                          item_id INT NOT NULL,
                                          quantity INT NOT NULL,
                                          CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES order_tbl(id) ON DELETE CASCADE, -- 🔹 `CONSTRAINT` 명시
    CONSTRAINT fk_item FOREIGN KEY (item_id) REFERENCES item(id) ON DELETE CASCADE
    );

-- 고객 데이터 삽입
INSERT INTO customer (id, username, password, name, email)
VALUES
    (1, 'jjang9', 'jjang1234', '짱구', 'jjang9@example.com'),
    (2, 'cheolsu', 'cheolsu1234', '철수', 'cheolsu@example.com'),
    (3, 'yuli', 'yuli1234', '유리', 'yuli@example.com'),
    (4, 'maeng9', 'maeng1234', '맹구', 'maeng9@example.com');


-- 상품 데이터 삽입
INSERT INTO item (id, name, price, stock, image_uuid)
VALUES
    (1, '스타벅스커피', 48000, 7, NULL),
    (2, '믹스커피', 1000, 8, NULL),
    (3, '공유커피', 2500, 9, NULL),
    (4, '컴포즈커피', 38000, 8, NULL);

-- 주문 데이터 삽입 (`date` 값 추가)
INSERT INTO order_tbl (id, customer_id, total_price, date, delivery_status)
VALUES
    (1, 1, 146000, NOW(), 'SHIPPED'),
    (2, 2, 78500, NOW(), 'SHIPPED');

-- 주문 항목 데이터 삽입
INSERT INTO order_item (id, order_id, item_id, quantity)
VALUES
    (1, 1, 1, 3),
    (2, 1, 2, 2),
    (3, 2, 3, 5),
    (4, 2, 4, 1);
