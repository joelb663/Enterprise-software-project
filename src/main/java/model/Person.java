package model;

import java.time.LocalDate;

public class Person {
    private Integer id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Integer age;
    private String lastModified;

    public Person(Integer id, String firstName, String lastName, LocalDate dateOfBirth, Integer age, String lastModified) {
        if (!isValidFirstName(firstName))
            throw new IllegalArgumentException("Invalid first name.");
        if (!isValidLastName(lastName))
            throw new IllegalArgumentException("Invalid last name.");
        if (!isValidDateOfBirth(dateOfBirth))
            throw new IllegalArgumentException("Invalid date of birth.");

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.age = age;
        this.lastModified = lastModified;
    }

    //validators
    public static boolean isValidFirstName(String firstName){
        if (firstName == null)
            return false;
        if (firstName.length() < 1 | firstName.length() > 100)
            return false;
        return true;
    }

    public static boolean isValidLastName(String lastName){
        if (lastName == null)
            return false;
        if (lastName.length() < 1 | lastName.length() > 100)
            return false;
        return true;
    }

    public static boolean isValidDateOfBirth(LocalDate dateOfBirth){
        if (dateOfBirth == null)
            return false;
        if (!dateOfBirth.isBefore(LocalDate.now())){
            return false;
        }
        return true;
    }

    //accessors
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (!isValidFirstName(firstName))
            throw new IllegalArgumentException("Invalid first name.");
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (!isValidLastName(lastName))
            throw new IllegalArgumentException("Invalid last name.");
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        if (!isValidDateOfBirth(dateOfBirth))
            throw new IllegalArgumentException("Invalid date of birth.");
        this.dateOfBirth = dateOfBirth;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}