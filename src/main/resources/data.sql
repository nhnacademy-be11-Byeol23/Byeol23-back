-- tags
insert into tags (tag_name)
values ('노벨문학상'),
       ('으스스한'),
       ('유쾌한'),
       ('여름'),
       ('겨울');


-- 회원 테스트 데이터
insert into members(login_id, login_password, member_name, nickname, phone_number, email, birth_date, latest_logined_at,
                    joined_at, `status`, current_point, member_role, joined_from, grade_id)
values ('test', 'test', '테스트 데이터', '테스트트틋', '01012341234', 'test@nhnacademy.com', '2000-03-15', '2025-10-21 12:12:11',
        '2023-11-22 11:23:11', '활성', 0, 'USER', '웹', 2),
       ('admin', 'admin', '관리자', '관리자', '01011111111', 'admin@nhnacademy.com', '2000-03-15', '2025-10-21 12:12:11',
        '2021-09-11 16:22:11', '활성', 999999, 'USER', '웹', 1);


-- contributor
INSERT INTO contributors (contributor_role, contributor_name)
VALUES ('AUTHOR', '한강'),
       ('AUTHOR', '김경식'),
       ('TRANSLATOR', '김난주'),
       ('AUTHOR', '가브리엘 가르시아 마르케스'),
       ('AUTHOR', '다자이 오사무'),
       ('TRANSLATOR', '홍한별');


-- publisher
INSERT INTO publishers(publisher_name)
VALUES ('민음사'),
       ('문학동네'),
       ('펭귄 클래식');


-- book
INSERT INTO books(book_name, toc, `description`, regular_price, sale_price, isbn, publish_date, is_pack, book_status,
                  stock, publisher_id, is_deleted, view_count)
VALUES ('８월에 만나요', '1장, 2장, 3장, 4장, 5장, 6장', '결혼한 지 스물일곱 해가 된 평범한 주부 아나 막달레나 바흐는 ......', 16000, 15990, 1234567890123,
        '2024-11-22', 1, '판매중', 12, 1, 0, 10);

select * from books;
INSERT INTO book_contributor(book_id, contributor_id)
VALUES (2, 4);

INSERT INTO book_tag(book_id, tag_id)
VALUES (1, 1);

INSERT INTO book_image(book_image_url, book_id)
VALUES ('book_image_url_sample', 1);


-- category
INSERT INTO categories(category_name, path_id, path_name)
VALUES ('한국문학', '한국문학 path id', '한국문학 path name');

INSERT INTO categories(category_name, parent_id, path_id, path_name)
VALUES ('현대', 1, '한국문학>현대 path id', '한국문학>현대 path name');


-- coupons
INSERT INTO coupon_policy(coupon_name, criterion_price, discount_rate, discount_limit, is_active)
VALUES ('생일', 50000, 30, 5000, 0);


INSERT INTO coupon_policy(coupon_name, criterion_price, discount_amount, is_active)
VALUES ('welcome', 30000, 3000, 1);

INSERT INTO category_coupon_policy(category_id, coupon_policy_id)
VALUES (1, 1);

INSERT INTO book_coupon_policy(book_id, coupon_policy_id)
VALUES (1, 2);

INSERT INTO coupons(member_id, coupon_policy_id, expired_date, created_date, used_at)
VALUES (1, 1, '2025-11-22', '2025-03-11', '2025-10-29 11:11:11');


-- 배송 정책 테스트 데이터
insert into delivery_policy(free_delivery_condition, delivery_fee, changed_at)
values (30000, 3000, '2025-10-24 10:05:30'),
       (50000, 2000, '2024-10-22 09:12:11');


-- orders
INSERT INTO orders(member_id, order_number, total_book_price, actual_order_price, ordered_at, order_status,
                   delivery_sent_date, delivery_desired_date, receiver, post_code,
                   receiver_address, receiver_address_detail, receiver_phone, receiver_address_extra,
                   delivery_policy_id)
VALUES (1, 251234567890, 37500, 21000, '2025-10-29 11:11:11', '대기', '2025-11-01', '2025-11-03', '최유현',
        30098, '보람로 96', '2005동 201호', '01012345678', '(주소)', 1);

INSERT INTO orders(member_id, order_number, total_book_price, actual_order_price, ordered_at, order_status,
                   delivery_sent_date, delivery_desired_date, receiver, post_code,
                   receiver_address, receiver_address_detail, receiver_phone, receiver_address_extra,
                   delivery_policy_id)
VALUES (1, 123456789012, 50000, 40000, '2025-10-29 11:11:11', '대기', '2025-11-01', '2025-11-03', '노형우',
        12345, '보람로 78', '1234동 890호', '01009876543', '(주우우소)', 1);

INSERT INTO packaging(packaging_name, packaging_image_url, packaging_price)
VALUES ('test packaging', 'test packaging url from minio', 2000);

INSERT INTO order_details(quantity, order_price, book_id, packaging_id, order_id)
VALUES (3, 42000, 2, 1, 3),
       (1, 19000, 2, 1, 4);

INSERT INTO reviews(review_rate, review_content, created_at, revised_at, member_id, order_detail_id)
VALUES (5, '책이 맛있고 사장님이 친절해요.', '2025-10-01 11:11:11', '2025-10-03 11:11:11', 1, 1),
       (3, '재밌다!! 신선하다!!', '2025-11-01 11:11:11', '2025-11-05 11:11:11', 1, 2);

insert into review_image(review_image_url, review_id)
values ('review image url from minio 1', 1),
       ('review image url from minio 1', 2);


-- refunds
insert into refund_policy(refund_policy_name, refund_condition, `comment`, changed_at)
values ('반품 정책 이름 1', '반품 조건 1', '반품 정책 변경 사유 1', '2020-11-11 11:11:11');


insert into refunds(order_id, refund_policy_id, refunded_at, refund_reason, refund_quantity, refund_price, refund_fee)
values (1, 1, '2025-11-11 11:11:11', '반품 사유 1', 3, 48000, 5000);


-- payments
insert into payments(payment_key, order_name, payment_method, total_amount, payment_request_at, payment_approved_at,
                     order_id)
values ('결제 키 1', '주문 이름 1', '토스페이', 35000, '2025-11-01 11:11:11', '2025-11-01 11:20:11', 1);


-- 회원 등급
insert into grades(grade_name, criterion_price, point_rate)
values ('일반', 100000, 1),
       ('로얄', 200000, 2);

-- 주소 테스트 데이터
-- insert into addresses(post_code, address_info, address_detail, address_extra, address_alias, is_default, member_id)
-- values ('12345', '대전광역시 유성구 대학로 99', '정보화본부 1306호', 'N1 건물', '정보화 본부', true, 1);


-- carts
insert into carts(member_id)
values (1);

insert into cart_books(quantity, book_id, cart_id)
values (150, 1, 1);


-- likes
insert into likes(member_id, book_id)
values (1, 1);