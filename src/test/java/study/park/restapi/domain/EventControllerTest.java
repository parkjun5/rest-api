package study.park.restapi.domain;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.ResultActions;
import study.park.restapi.BaseControllerTest;
import study.park.restapi.config.jwt.JwtProperties;
import study.park.restapi.repository.AccountRepository;
import study.park.restapi.repository.EventRepository;
import study.park.restapi.domain.response.dto.EventDto;
import study.park.restapi.service.AccountService;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class EventControllerTest extends BaseControllerTest {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    private static String TOKEN = "";

    @BeforeEach
    void init() {
        String username = "parkjun2@gmail.com";
        String password = "123";
        Account account = creteAccount(username, password);

        TOKEN = JwtProperties.TOKEN_PREFIX + JWT.create()
                .withSubject(account.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
                .withClaim("username", account.getEmail())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));
    }


    @Test
    @DisplayName("?????? ????????? POST /api/events/")
    void createEvent() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("SpringTester")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2022, 8, 11, 18, 0, 0))
                .closeEnrollmentDateTime(LocalDateTime.of(2022, 8, 12, 18, 0, 0))
                .beginEventDateTime(LocalDateTime.of(2022, 8, 15, 18, 0, 0))
                .endEventDateTime(LocalDateTime.of(2022, 8, 30, 18, 0, 0))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("????????? ????????? ?????????")
                .build();

        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(eventDto))
                        .header("Authorization", TOKEN)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=utf8"))
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("free").value(Matchers.not(true)))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()));

        Event event = eventRepository.findByName(eventDto.getName())
                .orElseThrow(() -> new NullPointerException("????????????"));
        assertThat(event.getManager()).isNotNull();
    }

    @Test
    @DisplayName("???????????? ?????? ??? ?????? ????????? POST /api/events/")
    void event_Bad_Request_Unknown_Input() throws Exception {
        Event event = Event.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2022, 8, 11, 18, 0, 0))
                .closeEnrollmentDateTime(LocalDateTime.of(2022, 8, 12, 18, 0, 0))
                .beginEventDateTime(LocalDateTime.of(2022, 8, 15, 18, 0, 0))
                .endEventDateTime(LocalDateTime.of(2022, 8, 30, 18, 0, 0))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("????????? ????????? ?????????")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @DisplayName("??? ?????? ?????? ??? ?????? ????????? POST /api/events/")
    void event_Bad_Request_Empty_Input() throws Exception {
        EventDto nameNullEvent = EventDto.builder()
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2022, 8, 11, 18, 0, 0))
                .closeEnrollmentDateTime(LocalDateTime.of(2022, 8, 12, 18, 0, 0))
                .beginEventDateTime(LocalDateTime.of(2022, 8, 15, 18, 0, 0))
                .endEventDateTime(LocalDateTime.of(2022, 8, 30, 18, 0, 0))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("????????? ????????? ?????????")
                .build();

        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(nameNullEvent))
                        .header("Authorization", TOKEN)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("_links.index").exists());
    }

    @Test
    @DisplayName("????????? ?????? ??? ?????? ????????? POST /api/events/")
    void event_Bad_Request_Wrong_Input() throws Exception {
        EventDto nameNullEvent = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2022, 8, 11, 18, 0, 0))
                .closeEnrollmentDateTime(LocalDateTime.of(2022, 8, 12, 18, 0, 0))
                .beginEventDateTime(LocalDateTime.of(2022, 8, 31, 18, 0, 0))
                .endEventDateTime(LocalDateTime.of(2022, 8, 1, 18, 0, 0))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("????????? ????????? ?????????")
                .build();

        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(nameNullEvent))
                        .header("Authorization", TOKEN)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("_links.index").exists());
    }

    @Test
    @DisplayName("Bad Request ?????? ?????? POST /api/events/")
    void event_Bad_Request_Response() throws Exception {
        EventDto nameNullEvent = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2022, 8, 11, 18, 0, 0))
                .closeEnrollmentDateTime(LocalDateTime.of(2022, 8, 12, 18, 0, 0))
                .beginEventDateTime(LocalDateTime.of(2022, 8, 31, 18, 0, 0))
                .endEventDateTime(LocalDateTime.of(2022, 8, 1, 18, 0, 0))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("????????? ????????? ?????????")
                .build();

        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(nameNullEvent))
                        .header("Authorization", TOKEN)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("_links.index").exists());
    }

    @Test
    @DisplayName("???????????? ??????/ ?????? ?????? ??????")
    void isOffLineIsFree() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2022, 8, 11, 18, 0, 0))
                .closeEnrollmentDateTime(LocalDateTime.of(2022, 8, 12, 18, 0, 0))
                .beginEventDateTime(LocalDateTime.of(2022, 8, 15, 18, 0, 0))
                .endEventDateTime(LocalDateTime.of(2022, 8, 30, 18, 0, 0))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .build();

        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(eventDto))
                        .header("Authorization", TOKEN)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=utf8"))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andExpect(jsonPath("offline").value(true));
    }

    @Test
    @DisplayName("HATEOAS ??????")
    void createEventPlusHateoas() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2022, 8, 11, 18, 0, 0))
                .closeEnrollmentDateTime(LocalDateTime.of(2022, 8, 12, 18, 0, 0))
                .beginEventDateTime(LocalDateTime.of(2022, 8, 15, 18, 0, 0))
                .endEventDateTime(LocalDateTime.of(2022, 8, 30, 18, 0, 0))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("????????? ????????? ?????????")
                .build();

        mockMvc.perform(post("/api/events/hateoas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(eventDto))
                        .header("Authorization", TOKEN)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists());
    }

    @Test
    @DisplayName("REST Docs ??????")
    void createEventWithRestDocs() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2022, 8, 11, 18, 0, 0))
                .closeEnrollmentDateTime(LocalDateTime.of(2022, 8, 12, 18, 0, 0))
                .beginEventDateTime(LocalDateTime.of(2022, 8, 15, 18, 0, 0))
                .endEventDateTime(LocalDateTime.of(2022, 8, 30, 18, 0, 0))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("????????? ????????? ?????????")
                .build();

        mockMvc.perform(post("/api/events/hateoas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(eventDto))
                        .header("Authorization", TOKEN)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andDo(document(
                        "create-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("update-event").description("link to update an existing event"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("it tells accept-type"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("it tells content-type ")
                        ),
                        relaxedRequestFields(
                                fieldWithPath("name").description("name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("begin date time of enrollment"),
                                fieldWithPath("closeEnrollmentDateTime").description("close date time of enrollment"),
                                fieldWithPath("beginEventDateTime").description("begin date time of new event"),
                                fieldWithPath("endEventDateTime").description("end date time of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("basePrice of new event"),
                                fieldWithPath("maxPrice").description("maxPrice of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("it tells location"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("it tells content-type ")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("id").description("identify of new event"),
                                fieldWithPath("name").description("name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("begin date time of enrollment"),
                                fieldWithPath("closeEnrollmentDateTime").description("close date time of enrollment"),
                                fieldWithPath("beginEventDateTime").description("begin date time of new event"),
                                fieldWithPath("endEventDateTime").description("end date time of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("basePrice of new event"),
                                fieldWithPath("maxPrice").description("maxPrice of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment"),
                                fieldWithPath("free").description("it tells this event free or not"),
                                fieldWithPath("offline").description("it tells this event offline meeting or not"),
                                fieldWithPath("eventStatus").description("eventStatus"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-events.href").description("link to query events"),
                                fieldWithPath("_links.update-event.href").description("link to update existing event"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @Test
    @DisplayName("REST Action ??????")
    void createEventSplitAction() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2022, 8, 11, 18, 0, 0))
                .closeEnrollmentDateTime(LocalDateTime.of(2022, 8, 12, 18, 0, 0))
                .beginEventDateTime(LocalDateTime.of(2022, 8, 15, 18, 0, 0))
                .endEventDateTime(LocalDateTime.of(2022, 8, 30, 18, 0, 0))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("????????? ????????? ?????????")
                .build();

        ResultActions resultActions = mockMvc.perform(post("/api/events/hateoas")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(eventDto))
                .header("Authorization", TOKEN));

        resultActions.andDo(print())
                .andExpect(status().isCreated());

        resultActions.andDo(document(
                "create-event",
                links(
                        linkWithRel("self").description("link to self"),
                        linkWithRel("query-events").description("link to query events"),
                        linkWithRel("update-event").description("link to update an existing event"),
                        linkWithRel("profile").description("link to profile")
                ),
                requestHeaders(
                        headerWithName(HttpHeaders.ACCEPT).description("it tells accept-type"),
                        headerWithName(HttpHeaders.CONTENT_TYPE).description("it tells content-type ")
                ),
                relaxedRequestFields(
                        fieldWithPath("name").description("name of new event"),
                        fieldWithPath("description").description("description of new event"),
                        fieldWithPath("beginEnrollmentDateTime").description("begin date time of enrollment"),
                        fieldWithPath("closeEnrollmentDateTime").description("close date time of enrollment"),
                        fieldWithPath("beginEventDateTime").description("begin date time of new event"),
                        fieldWithPath("endEventDateTime").description("end date time of new event"),
                        fieldWithPath("location").description("location of new event"),
                        fieldWithPath("basePrice").description("basePrice of new event"),
                        fieldWithPath("maxPrice").description("maxPrice of new event"),
                        fieldWithPath("limitOfEnrollment").description("limit of enrollment")
                ),
                responseHeaders(
                        headerWithName(HttpHeaders.LOCATION).description("it tells location"),
                        headerWithName(HttpHeaders.CONTENT_TYPE).description("it tells content-type ")
                ),
                relaxedResponseFields(
                        fieldWithPath("id").description("identify of new event"),
                        fieldWithPath("name").description("name of new event"),
                        fieldWithPath("description").description("description of new event"),
                        fieldWithPath("beginEnrollmentDateTime").description("begin date time of enrollment"),
                        fieldWithPath("closeEnrollmentDateTime").description("close date time of enrollment"),
                        fieldWithPath("beginEventDateTime").description("begin date time of new event"),
                        fieldWithPath("endEventDateTime").description("end date time of new event"),
                        fieldWithPath("location").description("location of new event"),
                        fieldWithPath("basePrice").description("basePrice of new event"),
                        fieldWithPath("maxPrice").description("maxPrice of new event"),
                        fieldWithPath("limitOfEnrollment").description("limit of enrollment"),
                        fieldWithPath("free").description("it tells this event free or not"),
                        fieldWithPath("offline").description("it tells this event offline meeting or not"),
                        fieldWithPath("eventStatus").description("eventStatus"),
                        fieldWithPath("_links.self.href").description("link to self"),
                        fieldWithPath("_links.query-events.href").description("link to query events"),
                        fieldWithPath("_links.update-event.href").description("link to update existing event"),
                        fieldWithPath("_links.profile.href").description("link to profile")
                )
        ));
    }

    @Test
    @DisplayName("30?????? ???????????? 10?????? ?????? ????????? ????????? ????????????")
    void queryEvents() throws Exception {
        //given
        IntStream.range(0, 30).forEach(this::generateEvent);
        //when
        this.mockMvc.perform(get("/api/events")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "name,DESC")
                        .header("Authorization", TOKEN)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventRepresentationList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document(
                        "query-events"
                ));

    }

    @Test
    @DisplayName("????????? ???????????? ???????????? ?????? ????????????")
    void getEventNoAuth() throws Exception {
        //given
        Event event = generateEvent(150);
        //when
        this.mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document(
                        "events-get"
                ));
    }

    @Test
    @DisplayName("????????? ???????????? ???????????? ?????? ????????????")
    void getEventWithAuth() throws Exception {
        //given
        Event event = generateEvent(150);

        //when
        this.mockMvc.perform(get("/api/events/{id}", event.getId())
                        .header("Authorization", TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andDo(document(
                        "events-get"
                ));
    }

    @Test
    @DisplayName("????????? ???????????? ?????? ?????? ??????????????? ?????? ??????")
    void getEvent404() throws Exception {
        //given
        //when
        this.mockMvc.perform(get("/api/events/{id}", 999)
                        .header("Authorization", TOKEN))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("????????? ???????????? ????????? ?????? ??????")
    void putEvent() throws Exception {
        //given
        Event event = generateEvent(150);

        EventDto eventDto = modelMapper.map(event, EventDto.class);
        eventDto.setName("PUT UPDATE");
        eventDto.setDescription("PUT is change everyThing");
        eventDto.setLocation("?????????");
        eventDto.setBasePrice(309);
        
        //when
        ResultActions resultActions = this.mockMvc.perform(
                put("/api/events/{id}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(eventDto))
                        .header("Authorization", TOKEN)
        )
        .andDo(print())
        .andExpect(status().isOk());

        resultActions.andExpect(jsonPath("name").value("PUT UPDATE"))
                .andExpect(jsonPath("location").value("?????????"))
                .andExpect(jsonPath("description").value("PUT is change everyThing"))
                .andExpect(jsonPath("basePrice").value(309))
                .andDo(document("events-put"));
    }

    @Test
    @DisplayName("????????? ???????????? ????????? ?????? ??????")
    void putEvent400() throws Exception {
        //given
        Event event = generateEvent(150);

        EventDto eventDto = EventDto.builder().build();
        //when
        ResultActions resultActions = this.mockMvc.perform(
                        put("/api/events/{id}", event.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaTypes.HAL_JSON)
                                .content(objectMapper.writeValueAsString(eventDto))
                                .header("Authorization", TOKEN)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("????????? ???????????? ????????? ?????? ?????? ??????")
    void patchEvent() throws Exception {
        //given
        Event event = generateEvent(150);

        EventDto eventDto = EventDto.builder()
                .name("updated!")
                .location("?????????")
                .build();

        //when
        ResultActions resultActions = this.mockMvc.perform(
                        patch("/api/events/{id}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(eventDto))
                                .header("Authorization", TOKEN)
                )
                .andDo(print())
                .andExpect(status().isOk());

        //then
        resultActions.andExpect(jsonPath("name").value("updated!"))
                .andExpect(jsonPath("location").value("?????????"))
                .andDo(document("events-patch"));

    }

    private Event generateEvent(int index) {
        Event event = Event.builder()
                .name("event_" + index)
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2022, 8, 11, 18, 0, 0))
                .closeEnrollmentDateTime(LocalDateTime.of(2022, 8, 12, 18, 0, 0))
                .beginEventDateTime(LocalDateTime.of(2022, 8, 15, 18, 0, 0))
                .endEventDateTime(LocalDateTime.of(2022, 8, 30, 18, 0, 0))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("????????? ?????????_" + index)
                .free(true)
                .offline(true)
                .build();
        Account account = accountRepository.findByEmail("parkjun2@gmail.com").orElseThrow(() -> new UsernameNotFoundException("?????????..?"));
        event.setManager(account);

        eventRepository.save(event);
        return event;
    }

    private Account creteAccount(String username, String password) {
        Account account = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        return accountService.saveAccount(account);
    }

}