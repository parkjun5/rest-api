package study.park.restapi.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import study.park.restapi.domain.Event;

public interface EventRepository extends JpaRepository<Event, Integer> {
}
