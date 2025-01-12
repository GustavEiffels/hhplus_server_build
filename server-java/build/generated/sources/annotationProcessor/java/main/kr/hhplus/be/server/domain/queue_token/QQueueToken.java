package kr.hhplus.be.server.domain.queue_token;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QQueueToken is a Querydsl query type for QueueToken
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QQueueToken extends EntityPathBase<QueueToken> {

    private static final long serialVersionUID = 951020008L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QQueueToken queueToken = new QQueueToken("queueToken");

    public final kr.hhplus.be.server.common.QBaseEntity _super = new kr.hhplus.be.server.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createAt = _super.createAt;

    public final DateTimePath<java.time.LocalDateTime> expireAt = createDateTime("expireAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<QueueTokenStatus> status = createEnum("status", QueueTokenStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updateAt = _super.updateAt;

    public final kr.hhplus.be.server.domain.user.QUser user;

    public QQueueToken(String variable) {
        this(QueueToken.class, forVariable(variable), INITS);
    }

    public QQueueToken(Path<? extends QueueToken> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QQueueToken(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QQueueToken(PathMetadata metadata, PathInits inits) {
        this(QueueToken.class, metadata, inits);
    }

    public QQueueToken(Class<? extends QueueToken> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new kr.hhplus.be.server.domain.user.QUser(forProperty("user")) : null;
    }

}

