INSERT INTO category(name) VALUES ('Eurogames');
INSERT INTO category(name) VALUES ('Ameritrash');
INSERT INTO category(name) VALUES ('Familiar');

INSERT INTO author(name, nationality) VALUES ('Alan R. Moon', 'US');
INSERT INTO author(name, nationality) VALUES ('Vital Lacerda', 'PT');
INSERT INTO author(name, nationality) VALUES ('Simone Luciani', 'IT');
INSERT INTO author(name, nationality) VALUES ('Perepau Llistosella', 'ES');
INSERT INTO author(name, nationality) VALUES ('Michael Kiesling', 'DE');
INSERT INTO author(name, nationality) VALUES ('Phil Walker-Harding', 'US');

INSERT INTO game(title, age, category_id, author_id) VALUES ('On Mars', '14', 1, 2);
INSERT INTO game(title, age, category_id, author_id) VALUES ('Aventureros al tren', '8', 3, 1);
INSERT INTO game(title, age, category_id, author_id) VALUES ('1920: Wall Street', '12', 1, 4);
INSERT INTO game(title, age, category_id, author_id) VALUES ('Barrage', '14', 1, 3);
INSERT INTO game(title, age, category_id, author_id) VALUES ('Los viajes de Marco Polo', '12', 1, 3);
INSERT INTO game(title, age, category_id, author_id) VALUES ('Azul', '8', 3, 5);

INSERT INTO client(name) VALUES ('Juan Rodriguez');
INSERT INTO client(name) VALUES ('Claudia Martinez');
INSERT INTO client(name) VALUES ('Pedro Sanchez');
INSERT INTO client(name) VALUES ('Laura Gomez');
INSERT INTO client(name) VALUES ('Carlos Lopez');
INSERT INTO client(name) VALUES ('Ana Ramirez');

INSERT INTO loan(game_id, client_id, loan_date, return_date) VALUES (4, 3, '2026-09-10', '2026-09-24');
INSERT INTO loan(game_id, client_id, loan_date, return_date) VALUES (6, 6, '2026-07-15', '2026-07-22');
INSERT INTO loan(game_id, client_id, loan_date, return_date) VALUES (1, 1, '2026-08-30', '2026-09-10');
INSERT INTO loan(game_id, client_id, loan_date, return_date) VALUES (2, 5, '2026-08-11', '2026-08-24');
INSERT INTO loan(game_id, client_id, loan_date, return_date) VALUES (3, 4, '2026-08-01', '2026-08-10');
INSERT INTO loan(game_id, client_id, loan_date, return_date) VALUES (5, 2, '2026-09-07', '2026-09-10');
