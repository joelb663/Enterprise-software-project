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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import model.Person;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.net.URL;
import java.util.ResourceBundle;

public class PeopleListController implements Initializable, ControllerInterface {
    private static final Logger LOGGER = LogManager.getLogger();
    private final PersonGateway personGateway = MainController.getInstance().getPersonGateway();
    private Results results;
    private Person selectedPerson;

    @FXML
    private ListView<Person> list;

    @FXML
    private TextField searchBar;

    @FXML
    private Button firstButton;

    @FXML
    private Button lastButton;

    @FXML
    private Button previousButton;

    @FXML
    private Button nextButton;

    @FXML
    private Label resultLabel;

    public PeopleListController(Results results) {
        this.results = results;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        searchBar.setText(results.getSearchBy());
        ObservableList<Person> tempList = FXCollections.observableArrayList(results.getPersons());
        list.setItems(tempList);

        if (results.getPersons().size() == 0) {
            resultLabel.setText("No people found");
        } else{
            int startRecord  = (results.getCurrentPage() * 10) + 1;
            int endRecord = (results.getCurrentPage() + 1) * 10;

            if (results.getPersons().size() < 10)
                endRecord = (results.getCurrentPage() * 10) + results.getPersons().size();

            resultLabel.setText("Fetched records " + startRecord + " to " + endRecord + " out of " + results.getTotalElements() + " records");
        }

        if (results.getPersons().size() == 0) {
            firstButton.setDisable(true);
            lastButton.setDisable(true);
            previousButton.setDisable(true);
            nextButton.setDisable(true);
        }
        if (results.getCurrentPage() == 0)
            previousButton.setDisable(true);
        if (results.getCurrentPage() == results.getTotalPages() - 1)
            nextButton.setDisable(true);
    }

    @FXML
    void mouseClick(MouseEvent event) {
        selectedPerson = list.getSelectionModel().getSelectedItem();

        if (event.getButton().equals(MouseButton.PRIMARY)) {
            if (event.getClickCount() == 2) {
                if(!(selectedPerson == null)){
                    MainController.getInstance().switchView(ScreenEnums.PEOPLEDETAIL, new Object[]{results, selectedPerson.getId()});
                    LOGGER.info("READING <" + selectedPerson.getFirstName() + ">");
                } else {
                    Alerts.AlertInfo("ERROR: You did not select a person", "You did not select a person");
                }
            }
        }
    }

    @FXML
    void addPerson(ActionEvent event) {
        MainController.getInstance().switchView(ScreenEnums.PEOPLEDETAIL, new Object[]{results, null});
    }

    @FXML
    void deleteButton(ActionEvent event) {
        if (selectedPerson == null) {
            LOGGER.info("ERROR < You did not select a person to delete >");
        } else{
            try{
                personGateway.deletePerson(selectedPerson.getId());

            }catch(UnauthorizedException e) {
                Alerts.AlertInfo("ERROR: Delete person failed", "ERROR: Delete person failed");
                return;
            }
            LOGGER.info("DELETING <" + selectedPerson.getFirstName() + ">");
            selectedPerson = null;
            MainController.getInstance().switchView(ScreenEnums.PEOPLELIST, new Object[]{results});
        }
    }

    @FXML
    void firstButton(ActionEvent event){
        results.setCurrentPage(0);
        MainController.getInstance().switchView(ScreenEnums.PEOPLELIST, new Object[]{results});
    }

    @FXML
    void previousButton(ActionEvent event){
        results.setCurrentPage(results.getCurrentPage() - 1);
        MainController.getInstance().switchView(ScreenEnums.PEOPLELIST, new Object[]{results});
    }

    @FXML
    void nextButton(ActionEvent event){
        results.setCurrentPage(results.getCurrentPage() + 1);
        MainController.getInstance().switchView(ScreenEnums.PEOPLELIST, new Object[]{results});
    }

    @FXML
    void lastButton(ActionEvent event){
        results.setCurrentPage(results.getTotalPages() - 1);
        MainController.getInstance().switchView(ScreenEnums.PEOPLELIST, new Object[]{results});
    }

    @FXML
    void searchButton(ActionEvent event){
        LOGGER.info("SEARCHING <" + searchBar.getText() + ">");
        results.setCurrentPage(0);
        results.setSearchBy(searchBar.getText());
        MainController.getInstance().switchView(ScreenEnums.PEOPLELIST, new Object[]{results});
    }
}