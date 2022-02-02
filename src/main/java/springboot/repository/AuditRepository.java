package springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import springboot.model.Audit;
import java.util.List;

public interface AuditRepository extends JpaRepository<Audit, Long> {

    @Query("select x from Audit x where x.person_id = :id")
    List<Audit> findAllByPersonId(@Param("id") long person_id);

    @Modifying
    @Query("delete from Audit where person_id = :id")
    void deleteById(@Param("id") long person_id);
}