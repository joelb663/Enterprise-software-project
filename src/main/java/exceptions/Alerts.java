package exceptions;

import javafx.scene.control.Alert;

public class Alerts {

    public static void AlertInfo(String title, String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
