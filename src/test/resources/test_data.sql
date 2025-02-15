INSERT INTO users (email, password, role)
VALUES ('testAdmin@gmail.com', 'password', 'ROLE_ADMIN'),
       ('testUser@gmail.com', 'password', 'ROLE_USER'),
       ('anotherUser@gmail.com', 'password', 'ROLE_USER'),
       ('user4@example.com', 'password', 'ROLE_USER'),
       ('user5@example.com', 'password', 'ROLE_USER'),
       ('user6@example.com', 'password', 'ROLE_USER'),
       ('user7@example.com', 'password', 'ROLE_USER'),
       ('user8@example.com', 'password', 'ROLE_USER'),
       ('user9@example.com', 'password', 'ROLE_USER'),
       ('user10@example.com', 'password', 'ROLE_USER');


INSERT INTO tasks (title, description, priority, status, author_id, implementor_id)
VALUES ('Task 1', 'Description 1', 'HIGH', 'IN_STAY', 1, 2),
       ('Task 2', 'Description 2', 'MIDDLE', 'IN_PROCESS', 1, 3),
       ('Task 3', 'Description 3', 'LOW', 'COMPLETED', 1, 4),
       ('Task 4', 'Description 4', 'HIGH', 'IN_STAY', 1, 5),
       ('Task 5', 'Description 5', 'MIDDLE', 'IN_PROCESS', 1, 6),
       ('Task 6', 'Description 6', 'LOW', 'COMPLETED', 1, 7),
       ('Task 7', 'Description 7', 'HIGH', 'IN_STAY', 1, 8),
       ('Task 8', 'Description 8', 'MIDDLE', 'IN_PROCESS', 1, 9),
       ('Task 9', 'Description 9', 'LOW', 'COMPLETED', 1, 10),
       ('Task 10', 'Description 10', 'HIGH', 'IN_STAY', 1, 2);


INSERT INTO comments (text, author_id, task_id)
VALUES ('Comment 1', 2, 1),
       ('Comment 2', 3, 2),
       ('Comment 3', 4, 3),
       ('Comment 4', 5, 4),
       ('Comment 5', 6, 5),
       ('Comment 6', 7, 6),
       ('Comment 7', 8, 7),
       ('Comment 8', 9, 8),
       ('Comment 9', 10, 9),
       ('Comment 10', 2, 10);