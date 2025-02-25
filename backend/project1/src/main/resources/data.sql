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
INSERT INTO order_tbl (id, customer_id, total_price, date, order_status)
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
