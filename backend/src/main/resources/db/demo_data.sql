INSERT INTO user (username, password, group_id, role, enabled)
VALUES ('admin', 'pass', NULL, 'ADMIN', true),
       ('user', 'pass', NULL, 'USER', true),
       ('leopold', 'pass', 1, 'ADMIN', true),
       ('lukas', 'pass', 1, 'USER', true),
       ('lucas', 'pass', 1, 'USER', true),
       ('jonathan', 'pass', 1, 'USER', true),
       ('anna', 'pass', 2, 'ADMIN', true),
       ('jane', 'pass', 2, 'USER', true),
       ('mary', 'pass', 2, 'USER', true),
       ('claire', 'pass', 3, 'ADMIN', true),
       ('john', 'pass', 3, 'USER', true),
       ('chris', 'pass', 3, 'USER', true),
       ('will', 'pass', 3, 'USER', true);

INSERT INTO 'group' (name)
VALUES ('macintosh'),
       ('gannochy'),
       ('fife park'),
       ('powell hall');

INSERT INTO 'bill' (name, description, amount, payment_method, owner)
VALUES ('spotify', 'monthly music service', 9.99, 'cash', 'leopold'),
       ('netflix', 'monthly streaming service', 9.99, 'paypal', 'lukas'),
       ('shopping', 'grocery run', 17.03, 'cash', 'lukas'),
       ('shopping', 'another grocery run', 27.13, 'cash', 'lucas');

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


INSERT INTO 'list' (name, description, owner, bill_id)
VALUES ('shopping list', 'my shopping list', 'lukas', 3),
       ('shopping list', 'my shopping list', 'lucas', 4),
       ('party planning', 'drinks and snacks', 'leopold', NULL);

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