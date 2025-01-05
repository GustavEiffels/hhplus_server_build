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
    private int point; // min :  0  max : 100_000_000


    @Builder
    public User(String name){
        if(!StringUtils.hasText(name)){
            throw new BusinessException(ErrorCode.Entity,"[사용자-이름]은 필수 값 입니다.");
        }
        this.name  = name;
    }

    public int pointTransaction(int pointAmount){
        if(this.point + pointAmount < 0 || this.point + pointAmount > 100_000_000){
            throw new BusinessException(ErrorCode.Entity,"point 는 최소 0, 최대 100,000,000 까지 가질 수 있습니다.");
        }
        this.point += pointAmount;
        return this.point;
    }
}
