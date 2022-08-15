package study.park.restapi.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.park.restapi.domain.Event;
import study.park.restapi.repository.EventRepository;
import study.park.restapi.domain.response.dto.EventDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;

    @Override
    public Event updateById(Integer id, EventDto eventDto) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NullPointerException("이벤트가 존재하지 않습니다."));
        event.patchEventByDto(eventDto);
        return event;
    }

    @Override
    public EventDto updateAllById(Integer id, EventDto eventDto) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NullPointerException("이벤트가 존재하지 않습니다."));
        event.putEvent(eventDto);
        return modelMapper.map(event, EventDto.class);
    }
}
