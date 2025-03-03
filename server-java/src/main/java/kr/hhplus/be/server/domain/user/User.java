package kr.hhplus.be.server.domain.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.common.BaseEntity;
import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.io.Serializable;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long id;

    @NotNull
    private String name;

    @NotNull
    @Min(value = 0)
    @Max(value = 100_000_000)
    private Long point = 0L; // min :  0  max : 100_000_000

    @Version
    private Integer version; // 낙관적 락 추가

    private User(String name){
        this.name  = name;
    }

    public static User create(String name){
        if(!StringUtils.hasText(name)){
            throw new BusinessException(ErrorCode.REQUIRE_FIELD_MISSING);
        }
        return new User(name);
    }

    public long pointTransaction(long pointAmount){
        if(this.point + pointAmount < 0 ){
            throw new BusinessException(ErrorCode.INSUFFICIENT_BALANCE);
        }
        if(this.point + pointAmount > 100_000_000){
            throw new BusinessException(ErrorCode.MAXIMUM_POINT_EXCEEDED);
        }
        this.point += pointAmount;
        return this.point;
    }
}
