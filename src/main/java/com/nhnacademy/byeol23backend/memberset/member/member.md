


## MemberService
### updateMember()
> FindMemberById()로 가져온 회원은 영속 상태가 된다.  
> 그리고 updateMemberInfo()로 해당 회원의 필드값을 변경하게 되면 JPA에서 변경을 감지하고 UPDATE SQL을 만든다.  
> updateMember()는 `@Transactional` 어노테이션이 붙어 있기 때문에 updateMember()가 끝날 때, 생성한 UPDATE SQL을 flush & commi 하여 실제 DB에 반영된다.

### reactivate()
> 휴면 상태인 회원을 활성 상태로 변경한다.










### issue
MemberService
- 등급 테이블에서 "일반"에 해당하는 등급 id를 가져오는데 이때 등급 테이블에 값이 없으면 에러가 난다.
