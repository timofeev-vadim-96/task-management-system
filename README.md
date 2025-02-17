### Система управления задачами (Task Management System)

---

`Стек технологий:`
- Spring Data JPA
- Spring Security
- Spring Web
- Spring Validation
- Spring AOP
- Postgres
- Liquibase
- Swagger + Springdoc-OpenApi
- docker-compose


`Локальный запуск / Quick start`
- задать переменную окружения SECRET_JWT_KEY
- задать переменную окружения POSTGRES_ADMIN_LOGIN
- задать переменную окружения POSTGRES_ADMIN_PASSWORD
- из директории ./docker выполнить команду:
```bash
docker-compose up -d --build
```

`Documentation`
* [OpenAPI description](http://localhost:8080/api/v1/api-docs)
* [Swagger UI](http://localhost:8080/api/v1/swagger-ui)

`Описание проекта`
- реализована ролевая система пользователей: ADMIN, USER
- аутентификация пользователей производится с помощью JWT-токена по email и паролю
- реализована возможность обновления токена по **refresh-токену**
- эндпоинты REST API защищены с помощью цепочки фильтров Spring Security
- пользователя с ролью ADMIN может зарегистрировать только существующий и авторизованный администратор
- Логика ограничения доступа пользователей с ролью USER к заданиям других пользователей и их комментариям   
реализована кастомно, с использованием аспектно-ориентированного подхода (Spring AOP)
- API позволяет получать задачи конкретного автора или исполнителя, а также все комментарии к ним
- на эндпоинте получения перечня заданий обеспечена **пагинация** и **фильтрация**
- все входные данные валидируются, ошибки корректно обрабатываются в соответствующие HHTP-статусы
- сервис описан и задокументирован с помощью Open API и Swagger
- dev-среда поднимается с помощью **docker-compose**

`Сущности`
- Каждая **задача** содержит: 
  - id
  - заголовок
  - описание
  - статус ("в ожидании", "в процессе", "завершено") 
  - приоритет ("высокий", "средний", "низкий") 
  - список комментариев
  - автора задачи 
  - исполнителя задачи
- Каждый **комментарий** содерижт:
  - id
  - текст
  - автора
  - задачу  

`Логика функционала и доступа по ролям`
- **Администратор** может управлять всеми задачами: 
  - создавать новые 
  - редактировать существующие 
  - просматривать 
  - удалять
  - менять статус и приоритет
  - назначать исполнителей задачи
  - оставлять комментарии
- **Пользователи** могут управлять своими задачами, если указаны как исполнитель: 
  - просматривать свои задачи
  - менять статус
  - оставлять комментарии

`Тестирование`
* все контроллеры, сервисы, конвертеры и кастомный репозиторий покрыты тестами
* тесты безопасности вынесены в отдельные классы
* над всеми эндпоинтами сервиса произведено ручное тестирование с помощью Postman
