package study.park.restapi.domain;

import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.util.StringUtils;
import study.park.restapi.domain.response.dto.EventDto;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Builder
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Setter
@Entity
public class Event extends RepresentationModel<Event> {

    @Id @GeneratedValue
    @Column(name = "event_id")
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location; // (optional) 없으면 온라인
    private int basePrice; // (optional)
    private int maxPrice; // (optional)
    private int limitOfEnrollment;
    private boolean offline;
    private boolean free;

    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus = EventStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account manager;

    public static Event createEvent() {
        return new Event();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Event event = (Event) o;
        return id != null && Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public void update() {
        this.free = this.basePrice == 0 && this.maxPrice == 0;
        this.offline = !StringUtils.hasText(location);

    }

    public void putEvent(EventDto eventDto) {
            this.name = eventDto.getName();
            this.description = eventDto.getDescription();
            this.beginEnrollmentDateTime = eventDto.getBeginEnrollmentDateTime();
            this.closeEnrollmentDateTime = eventDto.getCloseEnrollmentDateTime();
            this.beginEventDateTime = eventDto.getBeginEventDateTime();
            this.endEventDateTime = eventDto.getEndEventDateTime();
            this.basePrice = eventDto.getBasePrice();
            this.maxPrice = eventDto.getMaxPrice();
            this.location = eventDto.getLocation();
            this.limitOfEnrollment = eventDto.getLimitOfEnrollment();
    }

    public void patchEventByDto(EventDto eventDto) {
        String name = eventDto.getName();
        String description = eventDto.getDescription();
        LocalDateTime beginEnrollmentDateTime = eventDto.getBeginEnrollmentDateTime();
        LocalDateTime closeEnrollmentDateTime = eventDto.getCloseEnrollmentDateTime();
        LocalDateTime beginEventDateTime = eventDto.getBeginEventDateTime();
        LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
        int maxPrice = eventDto.getMaxPrice();
        String location = eventDto.getLocation();
        int limitOfEnrollment = eventDto.getLimitOfEnrollment();
        int basePrice = eventDto.getBasePrice();

        if (StringUtils.hasText(name) && !Objects.equals(name, this.name)) {
            this.name = name;
        }
        if (StringUtils.hasText(description) && !Objects.equals(description, this.description)) {
            this.description = description;
        }
        if (beginEnrollmentDateTime != null && this.beginEnrollmentDateTime != beginEnrollmentDateTime) {
            this.beginEnrollmentDateTime = beginEnrollmentDateTime;
        }
        if (closeEnrollmentDateTime != null && this.closeEnrollmentDateTime != closeEnrollmentDateTime) {
            this.closeEnrollmentDateTime = closeEnrollmentDateTime;
        }
        if (beginEventDateTime != null && this.beginEventDateTime != beginEventDateTime) {
            this.beginEventDateTime = beginEventDateTime;
        }
        if (endEventDateTime != null && this.endEventDateTime != endEventDateTime) {
            this.endEventDateTime = endEventDateTime;
        }
        if (basePrice != 0 && this.basePrice != basePrice) {
            this.basePrice = basePrice;
        }
        if (maxPrice != 0 && this.maxPrice != maxPrice) {
            this.maxPrice = maxPrice;
        }
        if (StringUtils.hasText(location) && !Objects.equals(this.location, location)) {
            this.location = location;
        }
        if (limitOfEnrollment != 0 && this.limitOfEnrollment != limitOfEnrollment) {
            this.limitOfEnrollment = limitOfEnrollment;
        }
    }

}
