package view;

import exceptions.Alerts;
import exceptions.UnauthorizedException;
import gateway.PersonGateway;
import gateway.Results;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import model.Audit;
import model.Person;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.net.URL;
import java.sql.Timestamp;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class PersonDetailController implements Initializable, ControllerInterface {
    private final PersonGateway personGateway = MainController.getInstance().getPersonGateway();
    private static final Logger LOGGER = LogManager.getLogger();
    private Person selectedPerson;
    private ArrayList<Audit> audits;
    private Timestamp lastModified;
    private Results results;

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField birthDate;

    @FXML
    private Label age;

    @FXML
    private Label id;

    @FXML
    private ListView<Audit> auditListView;

    @FXML
    private Label resultText;

    public PersonDetailController(Results results) {
        this.results = results;
    }

    public PersonDetailController(Person selectedPerson, ArrayList<Audit> audits, Results results) {
        this.selectedPerson = selectedPerson;
        this.audits = audits;
        this.results = results;
        lastModified = Timestamp.valueOf(selectedPerson.getLastModified());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (selectedPerson == null){
            firstName.setPromptText("John");
            lastName.setPromptText("Doe");
            birthDate.setPromptText("YYYY-MM-DD");

            resultText.setText("No audit trail records have been created yet for that person");
        }
        else{
            id.setText(String.valueOf(selectedPerson.getId()));
            firstName.setText(selectedPerson.getFirstName());
            lastName.setText(selectedPerson.getLastName());
            birthDate.setText(String.valueOf(selectedPerson.getDateOfBirth()));
            age.setText(String.valueOf(selectedPerson.getAge()));

            resultText.setText("Here are the audit trail records for that person");
            ObservableList<Audit> tempList = FXCollections.observableArrayList(audits);
            auditListView.setItems(tempList);
        }
    }

    @FXML
    void SaveButton(ActionEvent event) {
        String fName = firstName.getText();
        String lName = lastName.getText();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate bDay;

        try{
            bDay = LocalDate.parse(birthDate.getText(), format);
        }catch (DateTimeException e){
            Alerts.AlertInfo("ERROR: Add person failed", "ERROR: invalid format for date");
            return;
        }

        if (!Person.isValidFirstName(fName)) {
            Alerts.AlertInfo("ERROR: Add person failed", "ERROR: First name must be between 1 and 100 characters");
            return;
        } else if (!Person.isValidLastName(lName)){
            Alerts.AlertInfo("ERROR: Add person failed", "ERROR: Last name must be between 1 and 100 characters");
            return;
        } else if (!Person.isValidDateOfBirth(bDay)){
            Alerts.AlertInfo("ERROR: Add person failed", "ERROR: Date given was in the future");
            return;
        }

        if (selectedPerson == null){
            try{
                personGateway.addPerson(fName, lName, birthDate.getText());
            }catch(UnauthorizedException e) {
                Alerts.AlertInfo("ERROR: Add person failed", "ERROR: Add person failed");
                return;
            }
            LOGGER.info("CREATING <" + fName + ">");
        }
        else{
            //this code sends in all the fields to update because of the not null in the entity model
            HashMap<String, String> personFields = new HashMap<>();

            personFields.put("firstName", fName);
            personFields.put("lastName", lName);
            personFields.put("dateOfBirth", birthDate.getText());

            Timestamp lastModified2 = Timestamp.valueOf(personGateway.fetchPersonById(selectedPerson.getId()).getLastModified());

            if(!lastModified.equals(lastModified2)) {
                Alerts.AlertInfo("ERROR: Update Person Failed", "Person has been modified by someone else, try again.");
                MainController.getInstance().switchView(ScreenEnums.PEOPLELIST, new Object[]{results});
                return;
            }

            try{
                personGateway.updatePerson(selectedPerson.getId(), personFields);
            }catch(UnauthorizedException e) {
                Alerts.AlertInfo("ERROR: Update person failed", "ERROR: Update person failed");
                return;
            }
            LOGGER.info("UPDATING <" + selectedPerson.getFirstName() + ">");
        }
        MainController.getInstance().switchView(ScreenEnums.PEOPLELIST, new Object[]{results});
    }

    @FXML
    void cancelButton(ActionEvent event) {
        MainController.getInstance().switchView(ScreenEnums.PEOPLELIST, new Object[]{results});
    }
}