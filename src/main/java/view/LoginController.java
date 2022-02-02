package view;

import PasswordHash.HashUtils;
import exceptions.Alerts;
import exceptions.UnauthorizedException;
import exceptions.UnknownException;
import gateway.LoginGateway;
import model.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable, ControllerInterface {
    private static final Logger LOGGER = LogManager.getLogger();

    @FXML
    private TextField userName;

    @FXML
    private PasswordField passwd;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userName.setPromptText("username");
        passwd.setPromptText("*******");
    }

    @FXML
    void LoginButton(ActionEvent event) {
        String username = userName.getText();
        String HashedPassword = HashUtils.getCryptoHash(passwd.getText(), "SHA-256");
        Session session;

        try{
            session = LoginGateway.login(username, HashedPassword);
        }catch(UnauthorizedException e) {
            Alerts.AlertInfo("ERROR: Authentication failed", "Username or password is incorrect");
            return;
        } catch(UnknownException e1) {
            Alerts.AlertInfo("Login failed!", "Something bad happened: " + e1.getMessage());
            return;
        }

        MainController.getInstance().setSession(session);
        MainController.getInstance().switchView(ScreenEnums.PEOPLELIST, null);
        LOGGER.info("<" + userName.getText() + "> LOGGED IN");
    }
}