package study.park.restapi.response.representation;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.validation.Errors;
import study.park.restapi.controller.IndexController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class ErrorRepresentation extends EntityModel<Errors> {
    @JsonUnwrapped
    private Errors errors;

    public ErrorRepresentation(Errors errors, Link... links) {
        this.errors = errors;
        add(linkTo(IndexController.class).withRel("index"));
    }
}
