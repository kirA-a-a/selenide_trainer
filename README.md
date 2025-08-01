# Spring Boot Employee Application

Это приложение было переписано с Spring MVC на Spring Boot.

## Описание

Приложение представляет собой веб-форму для сбора информации о сотрудниках с валидацией данных.

## Технологии

- Spring Boot 3.2.0
- Java 17
- Maven
- JSP (JavaServer Pages)
- Hibernate Validator
- Lombok

## Структура проекта

```
src/main/java/com/safronov/spring/mvc/
├── Application.java              # Главный класс Spring Boot приложения
├── MyController.java             # Контроллер для обработки HTTP запросов
├── Employee.java                 # Простая модель Employee
├── config/
│   └── WebConfig.java           # Конфигурация для JSP
└── untitled/
    ├── entity/
    │   └── Employee.java        # Модель Employee с валидацией
    └── validation/
        ├── CheckEmail.java      # Кастомная аннотация валидации
        └── CheckEmailValidator.java # Валидатор для email
```

## Запуск приложения

### Требования
- Java 17 или выше
- Maven 3.6 или выше

### Команды для запуска

1. **Сборка проекта:**
   ```bash
   mvn clean compile
   ```

2. **Запуск приложения:**
   ```bash
   mvn spring-boot:run
   ```

3. **Сборка JAR файла:**
   ```bash
   mvn clean package
   ```

4. **Запуск JAR файла:**
   ```bash
   java -jar target/untitled-1.0-SNAPSHOT.jar
   ```

## Доступ к приложению

После запуска приложение будет доступно по адресу: http://localhost:8080

### Доступные страницы:

- **Главная страница:** http://localhost:8080/
- **Форма сотрудника:** http://localhost:8080/askDetails
- **Результат валидации:** http://localhost:8080/showDetails (POST запрос)

## Основные изменения при переходе на Spring Boot

1. **Удалены XML конфигурации:**
   - `web.xml`
   - `applicationContext.xml`

2. **Добавлены Spring Boot зависимости:**
   - `spring-boot-starter-web`
   - `spring-boot-starter-validation`
   - `tomcat-embed-jasper` для JSP

3. **Создан главный класс приложения:**
   - `Application.java` с аннотацией `@SpringBootApplication`

4. **Обновлен контроллер:**
   - Заменены `@RequestMapping` на `@GetMapping` и `@PostMapping`

5. **Добавлена конфигурация:**
   - `application.properties` для настроек Spring Boot
   - `WebConfig.java` для настройки JSP

## Валидация

Приложение включает валидацию полей формы:
- Имя и фамилия: от 2 до 30 символов, только буквы
- Email: корректный формат и должен заканчиваться на "qa.automation.ru"
- Обязательные поля: версия Java, опыт в автотестировании, языки программирования 