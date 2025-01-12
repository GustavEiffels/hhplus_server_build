package kr.hhplus.be.server.domain.schedule;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QConcertSchedule is a Querydsl query type for ConcertSchedule
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QConcertSchedule extends EntityPathBase<ConcertSchedule> {

    private static final long serialVersionUID = 1905873027L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QConcertSchedule concertSchedule = new QConcertSchedule("concertSchedule");

    public final kr.hhplus.be.server.common.QBaseEntity _super = new kr.hhplus.be.server.common.QBaseEntity(this);

    public final kr.hhplus.be.server.domain.concert.QConcert concert;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createAt = _super.createAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isReserveAble = createBoolean("isReserveAble");

    public final DateTimePath<java.time.LocalDateTime> reservation_end = createDateTime("reservation_end", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> reservation_start = createDateTime("reservation_start", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> showTime = createDateTime("showTime", java.time.LocalDateTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updateAt = _super.updateAt;

    public QConcertSchedule(String variable) {
        this(ConcertSchedule.class, forVariable(variable), INITS);
    }

    public QConcertSchedule(Path<? extends ConcertSchedule> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QConcertSchedule(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QConcertSchedule(PathMetadata metadata, PathInits inits) {
        this(ConcertSchedule.class, metadata, inits);
    }

    public QConcertSchedule(Class<? extends ConcertSchedule> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.concert = inits.isInitialized("concert") ? new kr.hhplus.be.server.domain.concert.QConcert(forProperty("concert")) : null;
    }

}

