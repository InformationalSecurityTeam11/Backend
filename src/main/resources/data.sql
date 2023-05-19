INSERT INTO ROLES (name) VALUES('ROLE_ADMIN');
INSERT INTO ROLES (name) VALUES('ROLE_STANDARD');


INSERT INTO USERS (user_type, email, last_password_reset_date, name, password, surname, telephone_number, role_id,is_enabled)
VALUES ('STANDARD','aleksandrab024@hotmail.com', '2023-05-01T04:49:27Z', 'Aleksandra', '$2a$10$iZZWXRF1TFvTNwq9pobYRO8SmuQ9ALEDGtEy4iXNG.bihvOwEJttu', 'Balazevic', '0600538922', '2', 'true'),
       ('STANDARD','vladadevic@gmail.com', '2023-05-01T04:49:27Z', 'Vlada', '$2a$10$iZZWXRF1TFvTNwq9pobYRO8SmuQ9ALEDGtEy4iXNG.bihvOwEJttu', 'Devic', '0613191670', '2', 'true'),
       ('ADMIN','veljkovex@gmail.com', '2023-05-01T04:49:27Z', 'Veljko', '$2a$10$iZZWXRF1TFvTNwq9pobYRO8SmuQ9ALEDGtEy4iXNG.bihvOwEJttu', 'Bubnjevic', '0612638823', '1', 'true'),
       ('ADMIN','markomarkovic@gmail.com', '2023-05-01T04:49:27Z', 'Marko', '$2a$10$iZZWXRF1TFvTNwq9pobYRO8SmuQ9ALEDGtEy4iXNG.bihvOwEJttu', 'Markovic>', '0612638823', '1', 'true'),
       ('STANDARD','vlada.devic.2001@gmail.com', '2023-05-07T04:49:27Z', 'Vlada', '$2a$10$iZZWXRF1TFvTNwq9pobYRO8SmuQ9ALEDGtEy4iXNG.bihvOwEJttu', 'Devic', '0613191670', '2', 'true');



-- Ako ne radi login na preloaded korisnike probaj da promenis last_password_reset_date na noviji datum
-- password: 123456 -> $2a$10$iZZWXRF1TFvTNwq9pobYRO8SmuQ9ALEDGtEy4iXNG.bihvOwEJttu
