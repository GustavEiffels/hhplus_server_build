package kr.hhplus.be.server.infrastructure.queue_token;

public interface TokenRedisRepository {

// MAPPING_TABLE :
    void insertMappingTable(String tokenId,Long userId);

    Long findUserIdByTokenId(String tokenId);

// WAITING_AREA
    void insertWaitingArea(String tokenId);

    Long findWaitingTokenByTokenId(String tokenId);


    // isActiveToken
    boolean isActiveToken(String tokenId);

    void insertActive(String tokenId);

    long countActive();

    void deleteActive(String tokenId);
}
