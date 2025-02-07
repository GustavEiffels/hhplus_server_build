# REDIS 적용 

## 대기열 추가 
### QueueTokenServiceRedisImpl.class
```java
    @Override
    public String createToken(Long userId) {
        // 1.Create UUID
        String tokenId = UUID.randomUUID().toString();

        // 2. Insert into Redis Hash : Mapping Table
        repository.putMappingTable(tokenId,userId);

        // 3. Insert into ZSET : WAITING AREA ( WAITING QUEUE )
        repository.putWaiting(tokenId);

        return tokenId;
    }
```
> 1. 토큰 발급 : UUID 생성
> 2. **토큰 값, 사용자 Id 를 넣어서 연결 시킨다.** : ( HASH - ADD )
> 3. 대기열 집합에 토큰 추가 ( ZSET - ADD )


## 대기열 활성화
### QueueTokenServiceRedisImpl.class
```java
    public void activate(Long tokenCnt){

        if(tokenCnt != null && tokenCnt > 30){
            maxActiveToken = tokenCnt;
        }

        // 1. 현재 활성화 되어 있는 토큰 수 구하기
        long currentActiveCnt     = repository.countActive();

        // 2. 활성화 가능한 토큰 수 반환
        long activeAbleCnt        = maxActiveToken-currentActiveCnt;


        // 3. 대기열에서 제외
        Set<String> tokenSet = repository.popFromWaiting(activeAbleCnt).stream()
                .map(tuple -> String.valueOf(tuple.getValue()))
                .collect(Collectors.toSet());

        // 4. 제외한 토큰 활성화
        tokenSet.forEach(repository::putActive);
    }
```
> 1. 현재 활성화 되어 있는 토큰 수 구하기 : ( ZSET - CARD) )
> 2. 활성화 가능 토큰 수 구하기 : ( 최대 활성화 가능수 - 현재 활성화 된 토큰 수 )
> 3. 활성화 가능 토큰 수 만큼 대기열에서 토큰을 추출 : ( ZSET - POPMIN )
> 4. 추출한 토큰을 활성화 영역에 추가 : ( ZSET - ADD )


## 만료된 토큰 삭제 
### QueueTokenServiceRedisImpl.class
```java
    @Override
    public void expireToken() {
        // 1. 현재 시간 측정
        Long currentTime = System.currentTimeMillis();

        // 2. 삭제해야할 목록 가져오기
        Set<String> deleteTokenSet = repository.findExpiredFromActive(currentTime).stream()
                                    .map(Object::toString)
                                    .collect(Collectors.toSet());

        // 3. active 토큰 삭제
        repository.deleteByScoreFromActive(currentTime);


        // 4. Mapping Table 에서 토큰을 제거 :: 토큰과 유저간의 관계 제거
        deleteTokenSet.forEach(repository::deleteFromMappingTable);
    }
```
> 1. 만료일 기준이 되는 시간 획득
> 2. 활성화 영역에서 **만료 기준일을 사용**하여 삭제할  토큰 값들 가져오기 : ( ZSET - RANGE_BY_SOCORE )
> 3. 활성화 영역에서 **만료 기준일을 사용**하여 만료된 토큰을 삭제 : ( ZSET - REMOVE_RANGE_BY_SCORE : ZREM_RANGE_BY_SCORE )
> 4. 삭제된 토큰 값을 사용하여 **연결 시킨 토큰 값, 사용자 Id 를 삭제한다.** : ( HASH - REMOVE : REM )

## 토큰 검증 
### QueueTokenServiceRedisImpl.class
```java
    @Override
    public Boolean isValidAndActive(Long userId,String tokenId) {
        // 1. Find UserId By TokenId  ( Redis )
        Long findUserId = repository.findUserIdFromMappingTable(tokenId);

        // 2. NOT FOUND USER BY TOKEN ID
        if( findUserId == null ){
            throw new BusinessException(ErrorCode.NOT_FOUND_QUEUE_TOKEN);
        }

        // 3. NOT EQUAL TOKEN OWNER WITH USER
        if(!findUserId.equals(userId)){
            throw new BusinessException(ErrorCode.NOT_MATCHED_WITH_USER);
        }

        Long   waitingRank           = repository.getRankFromWaiting(tokenId);
        Double activeTokenExpireTime = repository.getScoreFromActive(tokenId);


        if(waitingRank!=null){
            return  false;
        }
        if(activeTokenExpireTime>System.currentTimeMillis()){
            return true;
        }
        else if(activeTokenExpireTime >= System.currentTimeMillis()){
            throw new BusinessException(ErrorCode.EXPIRE_QUEUE_TOKEN);
        }
        else{
            throw new BusinessException(ErrorCode.NOT_FOUND_QUEUE_TOKEN);
        }
    }
```
> 1. 토큰 아이디를 사용하여 해당 토큰이 사용자의 것인지 확인 : ( HASH - GET )
> 2. 토큰 아이디가 NULL 인 경우 : 존재하지 않는 토큰
> 3. 토큰 소유자와 사용자가 일치하지 않을 경우 예외 : 사용자 불일치 문제
> 4. waitingRank - 대기열에 존재하는 지 확인 : ( ZSET - RANK )
> 5. activeTokenExpireTime - 활성화 영역에서 SCORE 를 추출 : ( ZSET - SCORE )
> 6. waitingRank 이 존재하는 경우 - 현재 대기열에 속해 있음 : false 반환
> 7. activeTokenExpireTime 가 현재 시간 보다 큼 - 활성화 영역에 있으며 만료되지 않았음으로 : true 반환
> 8. activeTokenExpireTime 가 현재 시간 보다 작음 - 만료된 토큰
