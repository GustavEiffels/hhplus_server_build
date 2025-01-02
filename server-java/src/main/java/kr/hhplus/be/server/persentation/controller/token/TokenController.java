package kr.hhplus.be.server.persentation.controller.token;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/queue_token/")
public class TokenController {

    @PostMapping("{userId}")
    public ResponseEntity<TokenApiDto.GenerateTokenRes> createQueueToken(@PathVariable("userId") Long userId){
        return new ResponseEntity<>(new TokenApiDto.GenerateTokenRes(12L,"Create QueueToken Success"), HttpStatus.CREATED);
    }
}
