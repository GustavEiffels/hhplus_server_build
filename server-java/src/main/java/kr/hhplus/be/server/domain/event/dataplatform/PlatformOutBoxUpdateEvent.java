package kr.hhplus.be.server.domain.event.dataplatform;


import kr.hhplus.be.server.domain.outbox.OutBox;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlatformOutBoxUpdateEvent {
    private OutBox outBox;
}
