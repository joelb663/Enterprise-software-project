DROP PROCEDURE if exists spCreatePeople;

DELIMITER //
CREATE PROCEDURE spCreatePeople()
BEGIN

    DECLARE counter INT DEFAULT 0;
    DECLARE first VARCHAR(100);
    DECLARE last VARCHAR(100);
    DECLARE date DATE;
    DECLARE personID int;

    myLoop : LOOP

        select firstname into first from firstnames order by rand() limit 1;
        select lastname into last from lastnames order by rand() limit 1;
        SELECT FROM_UNIXTIME(RAND() * (1577836800 - 0) + 0) into date;

        insert into people (id, first_name, last_name, dob, last_modified) values (0, first, last, date, CURRENT_TIMESTAMP);
        select id into personID from people where first_name = first and last_name = last and dob = date;
        insert into audits (id, change_msg, changed_by, person_id, when_occurred) values (0, 'added', 1, personID, CURRENT_TIMESTAMP);

        SET counter = counter + 1;
        IF counter < 10000 THEN
            ITERATE myLoop;
        end if;
        LEAVE myLoop;

    END LOOP myLoop;

END //
DELIMITER ;

call spCreatePeople;