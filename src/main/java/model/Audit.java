package model;

public class Audit {
    private String change_msg;
    private int changed_by;
    private int person_id;
    private String when_occurred;

    public Audit(String change_msg, int changed_by, int person_id, String when_occurred) {
        this.change_msg = change_msg;
        this.changed_by = changed_by;
        this.person_id = person_id;
        this.when_occurred = when_occurred;
    }

    @Override
    public String toString() {
        return "Date/Time: " + when_occurred + "\tchanged by user: " + changed_by + "\tDescription: " + change_msg;
    }

    public String getChange_msg() {
        return change_msg;
    }

    public void setChange_msg(String change_msg) {
        this.change_msg = change_msg;
    }

    public int getChanged_by() {
        return changed_by;
    }

    public void setChanged_by(int changed_by) {
        this.changed_by = changed_by;
    }

    public int getPerson_id() {
        return person_id;
    }

    public void setPerson_id(int person_id) {
        this.person_id = person_id;
    }

    public String getWhen_occurred() {
        return when_occurred;
    }

    public void setWhen_occurred(String when_occurred) {
        this.when_occurred = when_occurred;
    }
}