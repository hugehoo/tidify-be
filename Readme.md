### TODO

- [X] Dockerfile, ssh 자동화 script
- [X] validation
- [X] RestExceptionHandler
- [X] User 연관관계 매핑
    - [X] Folder
    - [X] Bookmark
- [X] Pagination + 정렬
- [X] Redis 띄우기
    - [X] 저장 형식 수정 : {RT : UserPk}
- [ ] Redis Cluster 생성
    - [ ] Master Slave
- [X] 카카오 로그인 로직 변경 -> kakao sdk 에서 access token 을 그대로 넘겨준다.
- [X] refresh token 만료시 -> 재발급
- [X] API 문서 - Swagger
- [X] Redis Cache - Label 캐싱
- [X] Apple login
- [ ] createTime, updateTime 자동 변경 audity 추가
- [ ] ERD
- [ ] Log
- [ ] 예외처리 고도화
- [ ] WebSecurityConfigurerAdapter deprecated
- [ ] 로그아웃 API
    - 로그아웃 후에 다시 로그인 시, 기존 존재하는 계정이니 로직 분기할 것.
- redis 가 고장나서 조회 못한것과, redis에 키가 없어서 조회못하는걸 구분해야한다. 해야하나?
- refreshToken -> accessToken 재발급할 땐 refreshToken 자체만으로 발급
  - refreshToken 재발급땐 redis 조회.-> 특정 uri 로 직접 요청. refreshToken 의 만료시간이 얼마 안남았을 때, 
  - ios 쪽에서 직접 refresh-reissue URL 호출 -> 이러면 refreshToken 탈취돼도 우리 URL 모르기에 재발급 못받는다.
  - refresh-reissue URL 호출되면, redis에서 refreshToken 조회 -> 존재하면 재발급, 


### label Controller

- [X] 조회

### folder

- [X] 조회
- [X] 생성
- [X] 수정
- [X] 삭제

### bookmark

- [X] 조회
- [X] 생성
- [X] 수정
- [X] 삭제

### Test Code

- [ ] 인수테스트 시나리오 작성
    - [X] Folder
    - [X] Bookmark
    - [ ] Oauth2

<hr>

### 인수 테스트 시나리오

### Folder 도메인 시나리오

| Given            | When        | Then                          |
|------------------|-------------|-------------------------------|
| 폴더가 저장되어 있을 때    | 폴더 조회 시     | 이름과 라벨의 값은 반드시 존재한다.          |
| 폴더의 이름과 라벨을 지정하여 | 폴더를 저장하면    | 폴더는 정상적으로 저장된다.               |
| 저장된 폴더의          | 이름을 수정하면    | 폴더의 이름이 수정된다.                 |
| 폴더 생성 시          | 이름이 누락되면    | 예외를 던진다.                      |
| 폴더 생성 시          | 라벨이 누락되면    | 예외를 던진다.                      |
| 폴더에 북마크가 존재할 때   | 해당 폴더를 삭제하면 | 북마크는 폴더가 지정되지 않은 상태로 업데이트 된다. |

### Bookmark 도메인 시나리오

| Given          | When                   | Then                   |
|----------------|------------------------|------------------------|
| 북마크 저장되어 있을 때  | 북마크 조회 시               | 이름과 URL 값은 반드시 존재한다.   |
| 북마크가 저장되어 있을 때 | 특정 검색어와 일치하는 북마크가 존재하면 | 해당 북마크가 조회된다.          |
| 북마크를 저장할 때     | 이름과 URL 이 모두 지정되면      | 해당 값으로 북마크가 저장된다.      |
| 북마크를 저장할 때     | URL 은 지정 했지만 이름이 누락되면  | 이름은 URL과 동일한 값으로 저장된다. |
| 북마크를 저장할 때 ☑️  | URL 이 누락되면             | 예외를 던진다.               |
| 북마크를 수정할 때     | 이름을 수정하면               | 북마크의 이름이 수정된다.         |
| 북마크를 수정할 때     | 이름을 빈값으로 수정하면          | 이름은 URL과 같은 값으로 수정된다.  |
| 북마크를 수정할 때     | 폴더를 수정하면               | 북마크는 수정된 폴더에서 조회된다.    |
|                | 북마크를 삭제하면              | 해당 북마크는 조회되지 않는다.      |


