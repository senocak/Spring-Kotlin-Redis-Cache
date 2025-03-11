# SpringBoot JPA Redis Cache

## Requirements
- Java 17
- PostgreSQL
- Redis

## Configuration
### Environment Variables
#### PostgreSQL
- SERVER_IP (default: localhost)
- POSTGRESQL_PORT (default: 5432)
- POSTGRESQL_DB (default: caching)
- POSTGRESQL_SCHEMA (default: public)
- POSTGRESQL_USER (default: postgres)
- POSTGRESQL_PASSWORD (default: senocak)

#### Redis
- REDIS_HOST (default: localhost)
- REDIS_PORT (default: 6379)
- REDIS_PASSWORD (default: senocak)
- REDIS_TIMEOUT (default: 300)

## API

### Create New Category
```http request
POST http://localhost:8081/api/v1/categories
Content-Type: application/json

{
"name": "{{$random.alphabetic(10)}}"
}
```

### Get All Categories
```http request
GET http://localhost:8081/api/v1/categories
?next=0
&max={{$random.integer(100)}}
```

### Get Category
```http request
GET http://localhost:8081/api/v1/categories/hdjxRaFNZE
```

### Update Category
```http request
PATCH http://localhost:8081/api/v1/categories/hdjxRaFNZE
Content-Type: application/json

{
"name": "{{$random.alphabetic(10)}}"
}
```

### Delete Category
```http request
DELETE http://localhost:8081/api/v1/categories/hdjxRaFNZE
```
