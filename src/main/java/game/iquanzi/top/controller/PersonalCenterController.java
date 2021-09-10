package game.iquanzi.top.controller;

import cn.hutool.core.lang.Assert;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;

/**
 * 个人中心控制器<br/>
 * -----------------------------------------------------------------------------
 *
 * @author Mr.Z
 * @version 1.0
 * @date 2021-09-06
 * @since JDK 1.8
 */
@Slf4j
public class PersonalCenterController extends Application {
    private Stage curStage;

    @FXML
    private HBox topHBox;
    @FXML
    private ListView mainListView;

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL resource = getClass().getClassLoader().getResource("layout/personal_center.fxml");
        Assert.notNull(resource, "布局资源文件未找到");

        VBox root = FXMLLoader.load(resource);
        topHBox = (HBox)root.getChildren().get(0);

        topHBox.getChildren().add(new Button("按钮1"));
        topHBox.getChildren().add(new Button("按钮2"));
        topHBox.getChildren().add(new Button("按钮3"));
        topHBox.setPadding(new Insets(10));
        topHBox.setStyle("-fx-background-color: #66ccff;");

        primaryStage.setTitle("五子棋");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();

        curStage = primaryStage;
    }

    /**
     * 显示窗口
     * @throws Exception 异常
     */
    public void showWindow(final Stage stage) throws Exception {
        log.debug("显示个人中心页面");
        start(stage);
    }
}
