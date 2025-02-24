package kr.hhplus.be.server.senario;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Random;

@SpringBootTest
@ActiveProfiles("local")
public class DummyGenerator {

    @Autowired
    UserJpaRepository userJpaRepository;

    @Autowired
    UserService userService;

    @Test
    void createUser(){
        // delete all user
        deleteAllUser();

        // create 1000 user
        for(int i = 0; i<1000;i++){
            User user = userJpaRepository.save(User.create("test"+i));
            userJpaRepository.save(userService.chargePoints(user.getId(),10000L));
        }



    }

    void deleteAllUser(){
        userJpaRepository.deleteAll();
    }

}
