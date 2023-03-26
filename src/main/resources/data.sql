INSERT INTO ROLES (name) VALUES('ROLE_ADMIN');
INSERT INTO ROLES (name) VALUES('ROLE_STANDARD');


INSERT INTO USERS (user_type, email, last_password_reset_date, name, password, surname, telephone_number, role_id)
VALUES ('STANDARD','aleksandrab024@hotmail.com', '2023-04-01T04:49:27Z', 'Aleksandra', 'password', 'Balazevic', '0600538922', '2'),
       ('STANDARD','vladadevic@gmail.com', '2023-04-01T04:49:27Z', 'Vlada', 'password2', 'Devic', '0613191670', '2'),
       ('ADMIN','veljkovex@gmail.com', '2023-04-01T04:49:27Z', 'Veljko', 'password3', 'Bubnjevic', '0612638823', '1');


-- password: 123456 -> $2a$10$iZZWXRF1TFvTNwq9pobYRO8SmuQ9ALEDGtEy4iXNG.bihvOwEJttu
