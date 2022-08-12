package study.park.restapi.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class EventTest {

    @Test
    void builder() throws Exception {
        //given
        Event event = Event.builder()
                .name("Event Spring Rest Api")
                .description("Rest Api development with Spring")
                .build();

        //then
        assertThat(event).isNotNull();
    }
    
    @Test
    void javaBean() throws Exception {
        //given
        String eventTest = "EventTest";
        String spring_events = "Spring Events";

        //when
        Event event = Event.createEvent();
        event.setName(eventTest);
        event.setDescription(spring_events);

        //then
        assertThat(event).extracting("name")
                .isEqualTo(eventTest);
        assertThat(event).extracting("description")
                .isEqualTo(spring_events);
    }

    @Test
    void isFreeTest() throws Exception {
        //given
        Event event = Event.builder()
                .basePrice(0)
                .maxPrice(0)
                .build();

        //when
        event.update();

        //then
        assertThat(event.isFree()).isTrue();
    }

    @Test
    void isNotFree1() throws Exception {
        //given
        Event event = Event.builder()
                .basePrice(110)
                .maxPrice(0)
                .build();

        //when
        event.update();

        //then
        assertThat(event.isFree()).isFalse();
    }


    @Test
    void isNotFree2() throws Exception {
        //given
        Event event = Event.builder()
                .basePrice(0)
                .maxPrice(200)
                .build();

        //when
        event.update();

        //then
        assertThat(event.isFree()).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void isOffline(String location) throws Exception {
        //given
        Event event = Event.builder()
                .basePrice(0)
                .maxPrice(200)
                .location(location)
                .build();

        //when
        event.update();
        //then
        assertThat(event.isOffline()).isTrue();
    }

    @Test
    void isNotOffline() throws Exception {
        //given
        Event event = Event.builder()
                .basePrice(0)
                .location("신사역 어딘가")
                .build();

        //when
        event.update();

        //then
        assertThat(event.isOffline()).isFalse();
    }
}