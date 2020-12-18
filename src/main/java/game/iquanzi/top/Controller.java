package game.iquanzi.top;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * @author Mr.Z
 */
@Slf4j
public class Controller implements Initializable {
    @FXML
    public Button logoutBtn;

    @FXML
    private TextField myTextField;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logoutBtn.setOnMouseClicked(event -> log.debug("点击了退出按钮"));
    }

    public void showDateTime(ActionEvent event) {
        System.out.println("点击按钮事件");
        Date now = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");
        String dateStr = df.format(now);
        myTextField.setText(dateStr);
    }
}
