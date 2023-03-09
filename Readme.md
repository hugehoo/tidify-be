### TODO
- [X] Dockerfile, ssh 자동화 script
- [X] validation 
- [X] RestExceptionHandler
- [X] User 연관관계 매핑
  - [X] Folder
  - [X] Bookmark
  - [ ] 테스트 코드 작성 후 리팩토링
- [X] Pagination + 정렬
- [X] Redis 띄우기
- [X] refresh token 만료시 -> 재발급
- [X] API 문서 - Swagger?
- [ ] Label ~~Ehcache~~ -> Redis 캐싱 
- [ ] Apple login
- [ ] createTime, updateTime 자동 변경 안되네. audity? 추가할것
- [ ] ERD
- [ ] Log
- [ ] 예외처리 고도화
- [ ] 로그아웃 API
  - 로그아웃 후에 다시 로그인 시, 기존 존재하는 계정이니 로직 분기할 것. 


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
  - [ ] Folder
  - [ ] Bookmark