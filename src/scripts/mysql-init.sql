DROP DATABASE IF EXISTS spring_rest_db;
DROP USER IF EXISTS `restadmin`@`%`;
CREATE DATABASE IF NOT EXISTS spring_rest_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS `restadmin`@`%` IDENTIFIED WITH mysql_native_password BY 'password';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, REFERENCES, INDEX, ALTER, EXECUTE, CREATE VIEW, SHOW VIEW,
CREATE ROUTINE, ALTER ROUTINE, EVENT, TRIGGER ON `spring_rest_db`.* TO `restadmin`@`%`;