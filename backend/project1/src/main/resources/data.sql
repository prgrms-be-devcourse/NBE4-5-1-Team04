-- 고객 데이터 삽입
INSERT INTO customer (id, username, password, name, email)
VALUES
    (1, 'jjang9', 'jjang1234', '짱구', 'jjang9@example.com'),
    (2, 'cheolsu', 'cheolsu1234', '철수', 'cheolsu@example.com'),
    (3, 'yuli', 'yuli1234', '유리', 'yuli@example.com'),
    (4, 'maeng9', 'maeng1234', '맹구', 'maeng9@example.com');

-- 상품 데이터 삽입
INSERT INTO item (id, name, price, stock_quantity)
VALUES
    (1, '스타벅스', 6800, 10),
    (2, '맥심커피', 1000, 10),
    (3, '카누커피', 1500, 10),
    (4, '컴포즈 커피', 2800, 10),
    (5, '이디야 커피', 3000, 10),
    (6, '빽다방', 2000, 10),
    (7, '커피빈', 4000, 10),
    (8, '투썸플레이스', 5000, 10),
    (9, '엔젤리너스', 3500, 10),
    (10, '더벤티', 4500, 10),
    (11, '탐앤탐스', 4200, 10),
    (12, '폴바셋', 5500, 10),
    (13, '할리스', 4000, 10);

-- 주문 데이터 삽입
INSERT INTO orders (id, customer_id, total_price)
VALUES
    (1, 1, 28400);

-- 주문 항목 데이터 삽입
INSERT INTO order_item (id, order_id, item_id, quantity)
VALUES
    (1, 1, 1, 3),
    (2, 1, 2, 2);
