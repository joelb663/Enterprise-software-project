package gateway;

import java.util.ArrayList;
import model.Person;

public class Results {
    private int currentPage;
    private String searchBy;
    private long totalElements;
    private int totalPages;
    private ArrayList<Person> persons;

    public Results(int currentPage, String searchBy, long totalElements, int totalPages, ArrayList<Person> persons) {
        this.currentPage = currentPage;
        this.searchBy = searchBy;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.persons = persons;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public String getSearchBy() {
        return searchBy;
    }

    public void setSearchBy(String searchBy) {
        this.searchBy = searchBy;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public ArrayList<Person> getPersons() {
        return persons;
    }

    public void setPersons(ArrayList<Person> persons) {
        this.persons = persons;
    }

    @Override
    public String toString() {
        return "Results{" +
                "currentPage=" + currentPage +
                ", searchBy='" + searchBy + '\'' +
                ", totalElements=" + totalElements +
                ", totalPages=" + totalPages +
                ", persons=" + persons +
                '}';
    }
}
