package study.park.restapi.events;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

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
    
}