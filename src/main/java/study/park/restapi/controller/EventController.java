package study.park.restapi.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import study.park.restapi.domain.Event;
import study.park.restapi.response.dto.EventDto;
import study.park.restapi.response.representation.ErrorRepresentation;
import study.park.restapi.response.representation.EventDtoRepresentation;
import study.park.restapi.response.representation.EventRepresentation;
import study.park.restapi.response.validate.EventValidator;
import study.park.restapi.repository.EventRepository;
import study.park.restapi.service.EventService;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE+"; charset=utf8")
@RequiredArgsConstructor
public class EventController {

    private final EventRepository eventRepository;

    private final ModelMapper modelMapper;

    private final EventValidator eventValidator;

    private final EventService eventService;

    @GetMapping
    public ResponseEntity queryEvent(Pageable pageable, PagedResourcesAssembler<Event> pagedResourcesAssembler) {
        Page<Event> pageEvent = this.eventRepository.findAll(pageable);
        var entityModels = pagedResourcesAssembler.toModel(pageEvent, EventRepresentation::new);
        entityModels.add(Link.of("/docs/index.html#resources-query-events").withRel("profile"));
        return ResponseEntity.ok(entityModels);
    }

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

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id) {
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Event event = optionalEvent.get();

        EventRepresentation eventRepresentation = new EventRepresentation(event);
        eventRepresentation.add(Link.of("/docs/index.html#resources-index-access").withRel("profile"));
        return ResponseEntity.ok(eventRepresentation);
    }

    @PutMapping("/{id}")
    public ResponseEntity putEvent(@PathVariable Integer id, @RequestBody @Valid EventDto eventDto, Errors errors) {
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (errors.hasErrors()) {
            return getBadRequest(errors);
        }

        eventValidator.validate(eventDto, errors);

        EventDto result = eventService.updateAllById(id, eventDto);
        EventDtoRepresentation eventDtoRepresentation = new EventDtoRepresentation(id ,result);
        eventDtoRepresentation.add(Link.of("/docs/index.html#resources-patch-event").withRel("profile"));
        return ResponseEntity.ok(eventDtoRepresentation);
    }

    @PatchMapping("/{id}")
    public ResponseEntity patchEvent(@PathVariable Integer id, @RequestBody @Valid EventDto eventDto, Errors errors) {
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Event event = eventService.updateById(id, eventDto);
        EventDto result = modelMapper.map(event, EventDto.class);
        EventDtoRepresentation eventDtoRepresentation = new EventDtoRepresentation(id ,result);
        eventDtoRepresentation.add(Link.of("/docs/index.html#resources-patch-event").withRel("profile"));
        return ResponseEntity.ok(eventDtoRepresentation);
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
