### `GET /api/main?page=1`

메인 페이지

요청

- 파라미터로 페이지(`page`) 번호 전달

---

**응답**

- 성공 시 `200 OK`

```json
"result" : [{
{
  	"date": Datetime,
	"id" : Number,
	"title" : Text,
	"img_url" : Text,
	"category" : Text
} ...
}]

```
---
**에러 메시지**
- 400 : 범위를 벗어난 페이지를 요청했을 때.
 ```json
    {
        "status": 400,
        "message": "Out of range."
    }
 ```
    


### `GET /api/main/{id}`

상세 페이지

요청

- url 에 엔드포인트로 id 값을 넣어 요청.

---

**응답**

- 성공 시 `200 OK`

```json
"result" : {
  	"date": Datetime,
	"id" : Number,
	"title" : Text,
	"img_url" : Text,
	"category" : Text,
	"text" : Text,
	"content" : Text
}
```

**에러 메시지**
- 404 : 잘못된 id 값을 요청했을 때.
 ```json
    {
        "status": 404,
        "message": "page does not exist."
    }
 ```

### `GET /api/tag?category={category}&page=1`

카테고리 별 기사 목록 페이지

요청

- 파라미터로 카테고리 전달
- 파라미터로 페이지(`page`) 번호 전달

---

**응답**

- 성공 시 `200 OK`

```json
"result" : [{
{
  	"date": Datetime,
	"id" : Number,
	"title" : Text,
	"img_url" : Text,
	"category" : Text
} ...
}]

```
**에러 메시지**
- 400 : 범위를 벗어난 페이지를 요청했을 때.
 ```json
    {
        "status": 400,
        "message": "Out of range."
    }
 ```

- 404 : 잘못된 카테고리를 요청했을 때.
 ```json
    {
        "status": 404,
        "message": "category does not exist."
    }
 ```


### `POST /api/like/{id}`

좋아요 기능

요청

- url 에 엔드포인트로 id 값을 넣어 요청.
  
Cookie

	Authorization: "Bearer {Access_token}"
  
---

**응답**

- 성공 시 `200 OK`

```json
	"result" : Text
```
---
**에러 메시지**
- 404 : 잘못된 id 값을 요청했을 때.
 ```json
	{
	    "status": 404,
	    "message": "page does not exist."
	}
 ```
- 401 : 토큰이 존재하지 않음.
```json
	{
	    "status": 401,
	    "message": "Token does not exist"
	}
```
- 401 : 토큰 에러

```json
	{
	    "status": 401,
	    "message": "Authentication token expired"
	}
```
