# StarBank Recommendation Service

Сервис рекомендаций банковских продуктов для банка «Стар».

## 🛠 Стек технологий
* Java 17, Spring Boot 4
* H2 (READ_ONLY, база данных транзакций)
* PostgreSQL (динамические правила)
* Liquibase, JPA/Hibernate, JdbcTemplate
* Caffeine (кеширование)
* Telegram Bot API (pengrad)

## 📚 Документация
* [Архитектура и Требования (Wiki)](https://github.com/AndrushaTi42/StarBank/wiki)
* [Инструкция по развертыванию (Wiki)](https://github.com/AndrushaTi42/StarBank/wiki/Deployment)
* [Swagger UI](http://localhost:8080/swagger-ui/index.html) (доступен локально)

## 🚀 Быстрый запуск
1. Установите PostgreSQL и создайте БД `rules_db`.
2. Положите файл `transaction.mv.db` в папку `db/`.
3. Пропишите переменные среды (см. Wiki).
4. Выполните `./mvnw clean package`
5. Запустите `java -jar target/bank-0.0.1-SNAPSHOT.jar`