package kr.hhplus.be.server.domain.concert;


import jakarta.persistence.*;
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
public class Concert extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String title;

    @NotNull
    private String performer;

    @Builder
    public Concert(String title, String performer){
        if(!StringUtils.hasText(title)){
            throw new BusinessException(ErrorCode.REQUIRE_FIELD_MISSING);
        }
        if(!StringUtils.hasText(performer)){
            throw new BusinessException(ErrorCode.REQUIRE_FIELD_MISSING);
        }

        this.title = title;
        this.performer = performer;
    }

}
