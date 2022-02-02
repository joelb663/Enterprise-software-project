package view;

import gateway.PersonGateway;
import gateway.Results;
import model.Audit;
import model.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import model.Person;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger();
    private static MainController instance = null;
    private Session session;
    private PersonGateway personGateway;

    @FXML
    private BorderPane rootPane;

    private MainController() {
        this.personGateway = new PersonGateway();
    }

    public static MainController getInstance() {
        if(instance == null)
            instance = new MainController();
        return instance;
    }

    public void switchView(ScreenEnums screenType, Object[] arg){
        String file = "";
        ControllerInterface controller = null;

        switch (screenType) {
            case LOGIN:
                file = "/login.fxml";
                controller = new LoginController();
                break;
            case PEOPLELIST:
                file = "/PeopleList.fxml";

                Results results;
                if (arg == null){
                    results = personGateway.fetchPeople(0, "");
                } else{
                    Results x = (Results) arg[0];
                    results = personGateway.fetchPeople(x.getCurrentPage(), x.getSearchBy());
                }
                controller = new PeopleListController(results);
                break;
            case PEOPLEDETAIL:
                file = "/PersonDetail.fxml";

                Results x = (Results) arg[0];
                results = personGateway.fetchPeople(x.getCurrentPage(), x.getSearchBy());

                if (arg[1] == null){
                    controller = new PersonDetailController(results);
                } else{
                    Person person = personGateway.fetchPersonById((Integer) arg[1]);
                    ArrayList<Audit> auditsById = personGateway.fetchAuditTrailById(person.getId());
                    controller = new PersonDetailController(person, auditsById, results);
                }
                break;
        }

        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(file));
        loader.setController(controller);
        Parent rootNode = null;
        try {
            rootNode = loader.load();
            rootPane.setCenter(rootNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        switchView(ScreenEnums.LOGIN,null);
    }

    public Session getSession() {
        return session;
    }
    public void setSession(Session session) {
        this.session = session;
    }

    public PersonGateway getPersonGateway() {
        return personGateway;
    }
    public void setPersonGateway(PersonGateway personGateway) {
        this.personGateway = personGateway;
    }
}