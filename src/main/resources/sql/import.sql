-- Initialize grades
INSERT INTO grades (grade_id, grade_name, criterion_price, point_rate) VALUES (1, '일반', 0, 0.00);

-- Initialize a member for tests
INSERT INTO members (member_id, login_id, login_password, member_name, nickname, phone_number, email, birth_date, latest_logined_at, joined_at, status, current_point, member_role, joined_from, grade_id)
VALUES (1, 'testLogin', 'password', '홍길동', 'testnick', '01012345678', 'test@example.com', '1990-01-01', NULL, NOW(), 'ACTIVE', 0, 'USER', 'LOCAL', 1);

-- Initialize publisher
INSERT INTO publishers (publisher_id, publisher_name) VALUES (1, 'testPub');

-- Initialize a delivery_policy (required by orders)
INSERT INTO delivery_policy (delivery_policy_id, free_delivery_condition, delivery_fee, changed_at) VALUES (1, 0, 2500, NOW());

-- Initialize book (minimal required columns)
INSERT INTO books (book_id, book_name, toc, description, regular_price, sale_price, isbn, publish_date, is_pack, book_status, stock, publisher_id, is_deleted, view_count, updated_at)
VALUES (1, 'Test Book', 'toc', 'desc', 10000, 8000, '1234567890123', '2020-01-01', 0, 'AVAILABLE', 10, 1, 0, 0, NOW());

-- Initialize orders (minimal required columns)
INSERT INTO orders (order_id, order_number, order_password, total_book_price, actual_order_price, ordered_at, order_status, delivery_sent_date, delivery_desired_date, receiver, post_code, receiver_address, receiver_address_detail, receiver_address_extra, receiver_phone, member_id, delivery_policy_id, point_history_id)
VALUES (1, 'ORD-1', NULL, 8000, 8000, NOW(), '대기', NULL, NULL, '홍길동', '00000', '서울', '상세', NULL, '01012345678', 1, 1, NULL);

-- Initialize order_details
INSERT INTO order_details (order_detail_id, quantity, order_price, book_id, packaging_id, order_id)
VALUES (1, 1, 8000, 1, NULL, 1);

-- leave room for additional test data
