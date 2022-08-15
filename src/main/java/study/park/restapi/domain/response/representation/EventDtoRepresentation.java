package study.park.restapi.domain.response.representation;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import study.park.restapi.controller.EventController;
import study.park.restapi.domain.response.dto.EventDto;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class EventDtoRepresentation extends RepresentationModel<EventDto> {

    @JsonUnwrapped
    private EventDto eventDto;

    public EventDtoRepresentation(Integer id,EventDto eventDto, Link... links) {
        this.eventDto = eventDto;
        add(linkTo(EventController.class).slash(id).withSelfRel());
    }
}
