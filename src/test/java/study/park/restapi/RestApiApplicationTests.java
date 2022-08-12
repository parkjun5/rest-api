package study.park.restapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class RestApiApplicationTests {

    @Test
    void contextLoads() {
        String initTest = "hello world";
        assertThat(initTest).isNotEmpty();
    }

}
