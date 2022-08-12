package study.park.restapi.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import study.park.restapi.domain.Event;
import study.park.restapi.response.dto.EventDto;
import study.park.restapi.response.resource.ErrorRepresentation;
import study.park.restapi.response.resource.EventRepresentation;
import study.park.restapi.response.validate.EventValidator;
import study.park.restapi.repository.EventRepository;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE+"; charset=utf8")
@RequiredArgsConstructor
public class EventController {

    private final EventRepository eventRepository;

    private final ModelMapper modelMapper;

    private final EventValidator eventValidator;

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
        if (errors.hasErrors()) {
            return getBadRequest(errors);
        }

        eventValidator.validate(eventDto, errors);

        if (errors.hasErrors()) {
            return getBadRequest(errors);
        }

        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        Event savedEvent = eventRepository.save(event);
        URI createdUri = linkTo(EventController.class).slash(savedEvent.getId()).toUri();
        return ResponseEntity.created(createdUri).body(event);

    }

    @PostMapping(value = "/hateoas")
    public ResponseEntity createEventWithHateoas(@RequestBody @Valid EventDto eventDto, Errors errors) {
        if (errors.hasErrors()) {
            return getBadRequest(errors);
        }

        eventValidator.validate(eventDto, errors);

        if (errors.hasErrors()) {
            return getBadRequest(errors);
        }

        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        Event savedEvent = eventRepository.save(event);
        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(savedEvent.getId());
        URI createdUri = selfLinkBuilder.toUri();

        EventRepresentation eventRepresentation = new EventRepresentation(event);
        eventRepresentation.add(linkTo(EventController.class).withRel("query-events"));
        eventRepresentation.add(selfLinkBuilder.withRel("update-event"));
        eventRepresentation.add(Link.of("/docs/index.html#resources-events-create").withRel("profile"));

        return ResponseEntity.created(createdUri).body(eventRepresentation);
    }

    private static ResponseEntity<ErrorRepresentation> getBadRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorRepresentation(errors));
    }

}
