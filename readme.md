# SpringBoot JPA Redis Cache

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