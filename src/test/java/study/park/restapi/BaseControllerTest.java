package study.park.restapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import study.park.restapi.config.RestDocsConfiguration;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
@Disabled
public class BaseControllerTest {
    @Autowired
    protected MockMvc mockMvc; //가짜 dispatcher servlet 생성

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected ModelMapper modelMapper;

}
