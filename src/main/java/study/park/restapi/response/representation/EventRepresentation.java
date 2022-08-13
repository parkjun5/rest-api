package study.park.restapi.response.representation;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import study.park.restapi.controller.EventController;
import study.park.restapi.domain.Event;

import java.util.Objects;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

//BeanSerializer
public class EventRepresentation extends RepresentationModel<Event> {
    @JsonUnwrapped
    private Event event;

    public EventRepresentation(Event event) {
        this.event = event;
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EventRepresentation that = (EventRepresentation) o;
        return Objects.equals(event, that.event);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), event);
    }
}
