package study.park.restapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class RestApiApplicationTests {

    @Test
    void contextLoads() {
        String initTest = "hello world";
        assertThat(initTest).isNotEmpty();
    }

}
