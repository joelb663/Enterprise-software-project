package springboot.services;

import PasswordHash.HashUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import springboot.model.Audit;
import springboot.model.Person;
import springboot.model.Session;
import springboot.repository.AuditRepository;
import springboot.repository.PersonRepository;
import springboot.repository.SessionRepository;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
public class PersonControllerHibernate {
    private static final Logger LOGGER = LogManager.getLogger();
    private final PersonRepository personRepository;
    private final SessionRepository sessionRepository;
    private final AuditRepository auditRepository;
    private Long userID;

    public PersonControllerHibernate(PersonRepository personRepository, SessionRepository sessionRepository, AuditRepository auditRepository) {
        this.personRepository = personRepository;
        this.sessionRepository = sessionRepository;
        this.auditRepository = auditRepository;
    }

    @GetMapping("/people/:{id}")
    public ResponseEntity<Person> fetchPersonById(@PathVariable long id, @RequestHeader Map<String, String> headers) {
        if(checkSessionToken(headers).equals(false)) {
            LOGGER.info("Error: session token is invalid or missing");
            return new ResponseEntity<>(null, HttpStatus.valueOf(401));
        }

        Optional<Person> person = personRepository.findById(id);
        if(person.isEmpty()) {
            LOGGER.info("Error: id is not in the database table");
            return new ResponseEntity<>(null, HttpStatus.valueOf(404));
        }
        return new ResponseEntity<>(person.get(), HttpStatus.valueOf(200));
    }

    @PostMapping("/people")
    @Transactional(rollbackFor = { SQLException.class })
    public ResponseEntity<List<String>> insertPerson(@RequestBody Person person, @RequestHeader Map<String, String> headers) {
        if(checkSessionToken(headers).equals(false)) {
            LOGGER.info("Error: session token is invalid or missing");
            return new ResponseEntity<>(null, HttpStatus.valueOf(401));
        }
        List<String> errors = checkUserInputs(person);
        if(!errors.isEmpty())
            return new ResponseEntity<>(errors, HttpStatus.valueOf(400));

        LOGGER.info(person);
        person.setLastModified(getDate());
        person = personRepository.save(person);
        LOGGER.info(person);

        insertAudit("added", person.getId(), "", "");

        return new ResponseEntity<>(null, HttpStatus.valueOf(200));
    }

    @PutMapping("/people/:{id}")
    @Transactional(rollbackFor = { SQLException.class })
    public ResponseEntity<List<String>> updatePerson(@PathVariable long id, @RequestBody Person newPerson, @RequestHeader Map<String, String> headers) {
        if(checkSessionToken(headers).equals(false)) {
            LOGGER.info("Error: session token is invalid or missing");
            return new ResponseEntity<>(null, HttpStatus.valueOf(401));
        }

        if(!personRepository.existsById(id)) {
            LOGGER.info("Error: id is not in the database table");
            return new ResponseEntity<>(null, HttpStatus.valueOf(404));
        }

        List<String> errors = checkUserInputs(newPerson);
        if(!errors.isEmpty())
            return new ResponseEntity<>(errors, HttpStatus.valueOf(400));

        Optional<Person> person = personRepository.findById(id);
        Person oldPerson =  person.get();

        if (!oldPerson.getFirstName().equals(newPerson.getFirstName()))
            insertAudit("firstName", id, oldPerson.getFirstName(), newPerson.getFirstName());
        if (!oldPerson.getLastName().equals(newPerson.getLastName()))
            insertAudit("lastName", id, oldPerson.getLastName(), newPerson.getLastName());
        if (!oldPerson.getDateOfBirth().equals(newPerson.getDateOfBirth()))
            insertAudit("dateOfBirth", id, String.valueOf(oldPerson.getDateOfBirth()), String.valueOf(newPerson.getDateOfBirth()));

        LOGGER.info(newPerson);
        newPerson.setId(id);
        newPerson.setLastModified(getDate());
        newPerson = personRepository.save(newPerson);
        LOGGER.info(newPerson);

        return new ResponseEntity<>(null, HttpStatus.valueOf(200));
    }

    @DeleteMapping("/people/:{id}")
    @Transactional(rollbackFor = { SQLException.class })
    public ResponseEntity<String> deletePerson(@PathVariable long id, @RequestHeader Map<String, String> headers){
        if(checkSessionToken(headers).equals(false)) {
            LOGGER.info("Error: session token is invalid or missing");
            return new ResponseEntity<>("", HttpStatus.valueOf(401));
        }

        if(personRepository.existsById(id)){
            auditRepository.deleteById(id);
            personRepository.deleteById(id);
            return new ResponseEntity<>("", HttpStatus.valueOf(200));
        }
        LOGGER.info("Error: id is not in the database table");
        return new ResponseEntity<>("", HttpStatus.valueOf(404));
    }

    @GetMapping("/people/:{id}/audittrail")
    public ResponseEntity<List<Audit>> fetchAuditTrail(@PathVariable long id, @RequestHeader Map<String, String> headers) {
        if(checkSessionToken(headers).equals(false)) {
            LOGGER.info("Error: session token is invalid or missing");
            return new ResponseEntity<>(null, HttpStatus.valueOf(401));
        }

        if(auditRepository.findAllByPersonId(id).isEmpty()) {
            LOGGER.info("Error: id is not in the database table");
            return new ResponseEntity<>(null, HttpStatus.valueOf(404));
        }

        List<Audit> audits = auditRepository.findAllByPersonId(id);
        return new ResponseEntity<>(audits, HttpStatus.valueOf(200));
    }

    @GetMapping("/people")
    public ResponseEntity<FetchResults> fetchPeople(@RequestHeader Map<String, String> headers, @RequestParam int pageNum, @RequestParam(required = false) String lastName) {
        if(checkSessionToken(headers).equals(false)) {
            LOGGER.info("Error: session token is invalid or missing");
            return new ResponseEntity<>(null, HttpStatus.valueOf(401));
        }

        Pageable page = PageRequest.of(pageNum, 10);

        FetchResults results;
        if (lastName == null){
            Page<Person> peoplePage = personRepository.findAll(page);
            results = new FetchResults(peoplePage.getPageable().getPageNumber(), "", peoplePage.getTotalElements(), peoplePage.getTotalPages(), peoplePage.getContent());
        } else{
            Page<Person> peoplePage = personRepository.findAllByLastName(lastName + "%", page);
            results = new FetchResults(peoplePage.getPageable().getPageNumber(), lastName, peoplePage.getTotalElements(), peoplePage.getTotalPages(), peoplePage.getContent());
        }
        return new ResponseEntity<>(results, HttpStatus.valueOf(200));
    }

    public Boolean checkSessionToken(Map<String, String> headers){
        if(!headers.containsKey("authorization")) {
            return false;
        } else {
            String HashedToken = HashUtils.getCryptoHash(headers.get("authorization"), "SHA-256");
            Optional<Session> session = sessionRepository.findById(HashedToken);
            session.ifPresent(value -> userID = value.getId());
            return session.isPresent();
        }
    }

    public List<String> checkUserInputs(Person person){
        List<String> errors = new ArrayList<>();

        if (person.getFirstName().length() == 0 | person.getFirstName().length() > 100)
            errors.add("ERROR: First name must be between 1 and 100 characters");
        if (person.getLastName().length() == 0 | person.getLastName().length() > 100)
            errors.add("ERROR: Last name must be between 1 and 100 characters");
        if(!person.getDateOfBirth().isBefore(LocalDate.now()))
            errors.add("ERROR: Date given was in the future");

        return errors;
    }

    public String getDate(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    public void insertAudit(String fieldName, Long personID, String oldField, String newField){
        Audit newAudit = new Audit();
        if(fieldName.equals("added"))
            newAudit.setChange_msg("added");
        else
            newAudit.setChange_msg(fieldName + " changed from " + oldField + " to " + newField);

        newAudit.setChanged_by(userID);
        newAudit.setPerson_id(personID);
        newAudit.setWhen_occurred(getDate());
        auditRepository.save(newAudit);
    }
}