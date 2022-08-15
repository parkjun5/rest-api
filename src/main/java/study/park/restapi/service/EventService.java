package study.park.restapi.service;

import study.park.restapi.domain.Event;
import study.park.restapi.domain.response.dto.EventDto;

public interface EventService {
    Event updateById(Integer id, EventDto eventDto);

    EventDto updateAllById(Integer id, EventDto eventDto);
}
