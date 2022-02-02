package springboot.model;

import javax.persistence.*;

@Entity
@Table(name = "audits")
public class Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "change_msg", nullable = false, length = 1000)
    private String change_msg;

    @Column(name = "changed_by", nullable = false)
    private Long changed_by;

    @Column(name = "person_id", nullable = false)
    private Long person_id;

    @Column(name = "when_occurred", nullable = false)
    private String when_occurred;

    public Audit() {
    }

    @Override
    public String toString() {
        return "Audit{" +
                "id=" + id +
                ", change_msg='" + change_msg + '\'' +
                ", changed_by=" + changed_by +
                ", person_id=" + person_id +
                ", when_occurred='" + when_occurred + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChange_msg() {
        return change_msg;
    }

    public void setChange_msg(String change_msg) {
        this.change_msg = change_msg;
    }

    public Long getChanged_by() {
        return changed_by;
    }

    public void setChanged_by(Long changed_by) {
        this.changed_by = changed_by;
    }

    public Long getPerson_id() {
        return person_id;
    }

    public void setPerson_id(Long person_id) {
        this.person_id = person_id;
    }

    public String getWhen_occurred() {
        return when_occurred;
    }

    public void setWhen_occurred(String when_occurred) {
        this.when_occurred = when_occurred;
    }
}