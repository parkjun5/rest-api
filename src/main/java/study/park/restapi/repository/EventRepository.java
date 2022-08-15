package study.park.restapi.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import study.park.restapi.domain.Event;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Integer> {

    Optional<Event> findByName(String name);
}
