# Notes & Wishboard Hub

Автор: Кручкевич Б. В. | Група ІП-23-1 | ІФНТУНГ | 2026

---

## Структура

```
notes_&_wishboard_hub/
├── shared-lib/              # Спільні сутності (User, Note, Wish)
├── spring-boot-api/         # REST API, порт 9001
├── dropwizard-analytics/    # Wishes сервіс, порт 9002
├── servlet-legacy/          # WAR для Tomcat, порт 9000
├── frontend/
│   ├── index.html           # Dashboard
│   ├── login.html           # Сторінка входу
│   ├── profile.html         # Технічний профіль
│   ├── css/style.css
│   └── js/
│       ├── api.js           # Всі fetch-запити
│       ├── notes.js         # Логіка нотаток
│       ├── wishes.js        # Логіка вишлисту
│       ├── login.js         # Логіка входу
│       ├── greeting.js      # Читання document.cookie
│       └── toast.js         # Повідомлення
└── init.sql
```

## Порти

| Сервіс            | Порт                             |
|-------------------|----------------------------------|
| Spring Boot API   | 9001                             |
| Dropwizard        | 9002                             |
| Dropwizard Admin  | 9003                             |
| Tomcat (Servlet)  | 9000                             |
| Frontend          | 3000 (live server або будь-який) |

## Запуск

**1. База даних**
```bash
mysql -u root -p < init.sql
```

**2. Збірка**
```bash
mvn clean install
```

**3. Spring Boot (9001)**
```bash
mvn spring-boot:run -pl spring-boot-api
```

**4. Dropwizard (9002)**
```bash
cd dropwizard-analytics
java -jar target/dropwizard-analytics-1.0.0.jar server src/main/resources/analytics.yml
```

**5. Servlet на Tomcat (9000)**
```bash
cd servlet-legacy
mvn clean package
cp target/servlet-legacy.war ~/apache-tomcat-10.1.16/webapps/
~/apache-tomcat-10.1.16/bin/startup.sh
```

**6. Фронтенд**
```bash
# Live Server у VS Code
cd "/mnt/d/Dокументи/Університет/Курс 3/Семестр 2/Java Spring/notes_&_wishboard_hub/frontend/"
code .
```

## API

**Spring Boot (9001)**
- `GET  /api/notes?userId=1`
- `POST /api/notes`  — `{ title, content, userId }`
- `DELETE /api/notes/{id}`
- `GET  /api/users`
- `POST /api/users`  — `{ username, email, password }`

**Dropwizard (9002)**
- `GET  /api/wishes?userId=1`
- `GET  /api/wishes/stats`
- `POST /api/wishes`  — `{ name, description, userId }`
- `PUT  /api/wishes/{id}/achieve`
- `DELETE /api/wishes/{id}`

**Servlet (9000)**
- `GET  /servlet-legacy/session`
- `POST /servlet-legacy/session/login`  — form params: `username`, `password`
- `POST /servlet-legacy/session/logout`
- `GET  /servlet-legacy/profile`  — захищено `CookieFilter`
