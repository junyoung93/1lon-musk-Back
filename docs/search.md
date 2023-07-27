### `GET /api/keyword`

검색창 페이지

---

**응답**

최근 게시물 20개 중에서 중복되는 제목 단어 6개의 키워드를 줌.

- 성공 시 `200 OK` 와

```json
{
	"keyword" :[{
			Text,
			Text,
			Text,
			Text,
			Text,
			Text
		   }] 
}
```
---

### `GET /api/search?q=content&page=1`

검색결과 페이지
검색결과로 제목이나 내용에 해당 content 단어가 있는 기사를 응답.
※ 파라미터에  검색하고자하는 것을 기입

---

**응답**

※  12개 씩 

- 성공 시 `200 OK` 와 게시글 반환

```json
"result" : [{
  	"date": Datetime,
	"id" : Number,
	"title" : Text,
	"img_url" : Text,
	"category" : Text
},...
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
