package springboot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import springboot.model.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {

    @Query("select x from Person x where x.lastName like :lastname")
    Page<Person> findAllByLastName(@Param("lastname") String lastname, Pageable pageable);
}