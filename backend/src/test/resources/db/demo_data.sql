INSERT INTO 'group' (name)
VALUES ('macintosh'),
       ('gannochy'),
       ('fife park'),
       ('powell hall');

INSERT INTO user (username, password, group_id, role, enabled)
VALUES ('admin', '87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61', NULL, 'ADMIN', true),
       ('user', '87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61', NULL, 'USER', true),
       ('leopold', '87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61', 1, 'ADMIN', true),
       ('lukas', '87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61', 1, 'USER', true),
       ('lucas', '87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61', 1, 'USER', true),
       ('jonathan', '87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61', 1, 'USER', true),
       ('anna', '87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61', NULL, 'USER', true),
       ('jane', '87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61', 2, 'ADMIN', true),
       ('mary', '87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61', 2, 'USER', true),
       ('claire', '87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61', 3, 'ADMIN', true),
       ('john', '87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61', 3, 'USER', true),
       ('chris', '87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61', 3, 'USER', true),
       ('will', '87bedde97f210319eae092f835432f811eaf19a986072bfa8096f3bc5eed4f61', 3, 'USER', true);

INSERT INTO 'bill' (name, description, amount, create_time, payment_method, owner)
VALUES ('spotify', 'monthly music service', 9.99, 1648727500, 'cash', 'leopold'),
       ('netflix', 'monthly streaming service', 9.99, 1648727500, 'paypal', 'lukas'),
       ('shopping', 'grocery run', 17.03, 1648727500, 'cash', 'lukas'),
       ('shopping', 'another grocery run', 27.13, 1648727500, 'cash', 'lucas');

INSERT INTO 'user_bill' (username, bill_id, percentage, paid)
VALUES ('leopold', 1, 0.25, true),
       ('lukas', 1, 0.25, true),
       ('lucas', 1, 0.25, true),
       ('jonathan', 1, 0.25, true),
       ('leopold', 2, 0.33, false),
       ('lukas', 2, 0.33, false),
       ('lucas', 2, 0.34, false),
       ('leopold', 3, 0.5, false),
       ('lukas', 3, 0.5, true),
       ('jonathan', 4, 0.5, false),
       ('lucas', 4, 0.5, false);


INSERT INTO 'list' (name, description, owner, bill_id, create_time)
VALUES ('shopping list', 'my shopping list', 'lukas', 3, 1648727500),
       ('shopping list', 'my shopping list', 'lucas', 4, 1648727500),
       ('party planning', 'drinks and snacks', 'leopold', NULL, 1648727500);

INSERT INTO 'list_item' (name, list_id)
VALUES ('peach', 1),
       ('pears', 1),
       ('plums', 1),
       ('oranges', 1),
       ('tomato', 2),
       ('potato', 2),
       ('celery', 2),
       ('broccoli', 2),
       ('crisps', 3),
       ('beer', 3),
       ('soda', 3);