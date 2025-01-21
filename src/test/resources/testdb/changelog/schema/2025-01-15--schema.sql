--liquibase formatted sql

--changeset timofeev_vadim:2025-01-15--schema

create table if not exists users
(
    id       bigserial primary key,
    email    varchar(255) not null unique,
    password varchar(255) not null,
    role     varchar(255) not null
        constraint users_role_check
    check (role IN ('ROLE_ADMIN', 'ROLE_USER'))
);

create table if not exists tasks
(
    id             bigserial primary key,
    author_id      bigint references users (id),
    implementor_id bigint references users (id),
    description    varchar(255) not null,
    priority       varchar(255)
        constraint tasks_priority_check check (priority IN ('ВЫСОКИЙ', 'СРЕДНИЙ', 'НИЗКИЙ')),
    status         varchar(255)
        constraint tasks_status_check check (status IN ('В_ОЖИДАНИИ', 'В_ПРОЦЕССЕ', 'ЗАВЕРШЕНО')),
    title          varchar(255)
);


create table if not exists comments
(
    id        bigserial primary key,
    author_id bigint references users (id),
    task_id   bigint references tasks (id),
    text      varchar(255) not null
);