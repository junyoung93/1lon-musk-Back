
# API 문서

## 엔드포인트


### `POST /api/signup`
회원가입 입니다.

**요청**
```json
  {
    "email": Text,
    "password": Text,
    "nickname": Text,
    "marketing": boolean
  }
```
---
**응답**

- 성공 시 `200 OK` 와 함께 엑세스 토큰 발급

```json
Cookie ( AccessToken :  "Bearer {AccessToken}",  RefreshToken : "Bearer {RefreshToken}")

body{
  	"status" : 200	
}

```
---
**에러 메시지**

- 400 : 이메일이나 비밀번호의 유효성 통과가 안되었을때 반환.
    
    ```json
    {
        "status": 400,
        "message": "E-mail and password have different formats."
    }
    ```
    
- 400 : 닉네임 공백의 유효성 통과가 안되었을때 반환.
    
    ```json
    {
        "status": 400,
        "message": "Nullable=false"
    }
    ```
    
- 400 : 엑세스 토큰 발급 오류

  ```json
   {
    	"status": 400,
    	"message": "Unable to issue access tokens"
   }
  ```
---


### POST /api/signin

로그인 요청

- 성공 시 `200 OK`

```json
{
  "email": Text,
  "password": Text
}

```

---

**응답**

- 성공 시 `200 OK` 와 엑세스 토큰

```json
Cookie ( AccessToken :  "Bearer {AccessToken}",  RefreshToken : "Bearer {RefreshToken}")

body{
  	"status" : 200	
}

```

**에러 메시지**

- 400 : 이메일이나 비밀번호의 틀려서 통과가 안되었을때 반환.

```json
{
    "status": 400,
    "message": "Wrong email or password format"
}
```

- 400  : 사용자를 찾을 수 없을 때 반환.

```json
{
    "status": 400,
    "message": "User not found"
}
```
---

### `GET /api/pwd/forgot`

비밀번호 찾기 페이지


**요청**

```json
{
	"email" : Text
}
```

**응답**

- 성공 시 `200 OK`

해당 Email에 background task로 보내야 한다.(backend)

*background task : 따로 처리하는 응답.

**에러 메시지**
- 400 : 사용자를 찾을 수 없을 때 반환.

```json
{
    "status": 400,
    "message": "User not found"
}
```
---
### `GET /api/pwd/newPassword?token={secretemail}`

새 비밀번호 입력 페이지


**요청**

```json
{
	"secretemail" : Text,
	"password" : Text
}
```

**응답**

- 성공 시 `200 OK`

**에러 메시지**

- 400 : 이메일이나 비밀번호의 틀려서 통과가 안되었을 때 반환.

```json
{
    "status": 400,
    "message": "Wrong email or password format"
}
```
---

### GET  /api/user/token
이 엔드포인트는 로그인한 사용자의 토큰을 확인합니다.

**요청**

Cookie 

	Authorization: "Bearer {access_token}"


**응답**

- 성공 시 `200 OK`

```json
    {
  	"nickname" : Text,
	"email" : Text
    }

```


**에러 메시지**

- 400 : 사용자를 찾을 수 없을 때 반환.

```json
{
    "status": 400,
    "message": "User not found"
}
```
- 401 : 토큰 에러

```json
{
    "status": 401,
    "message": "Authentication token expired"
}
```


---

### GET  /refreshToken
이 엔드포인트는 만료된 토큰을 갱신합니다.

**요청**

Cookie

	Authorization: "Bearer {refresh_token}"


**응답**

- 성공 시 `200 OK`

```json
    Cookie
    {
  	AccessToken : Bearer {AccessToken},
    }

```


**에러 메시지**

- 400 : 사용자를 찾을 수 없을 때 반환.

```json
{
    "status": 400,
    "message": "User not found"
}
```
- 401 : 토큰 에러

```json
{
    "status": 401,
    "message": "Authentication token expired"
}
```




