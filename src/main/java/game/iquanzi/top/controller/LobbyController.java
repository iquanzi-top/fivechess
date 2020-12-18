package game.iquanzi.top.controller;

import cn.hutool.core.lang.Assert;
import game.iquanzi.top.service.SessionService;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 游戏大厅控制器<br/>
 * @author Mr.Z
 * @version 1.0
 * @date 2020/12/17
 * @since JDK 1.8
 */
@Slf4j
public class LobbyController extends Application {
    private static Stage stage = new Stage();

    public WebView webView;

    private SessionService chessSession;

    /**
     * 显示窗口
     * @throws Exception 异常
     */
    public void showWindow() throws Exception {
        start(stage);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL resource = getClass().getClassLoader().getResource("layout/lobby.fxml");
        Assert.notNull(resource, "布局资源文件未找到");

        FlowPane root = FXMLLoader.load(resource);

        primaryStage.setTitle("五子棋");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();

        webView = (WebView)root.lookup("#webView");
        webView.getEngine().load("https://blog.iquanzi.top");

        WebView webView2 = (WebView)root.lookup("#webView2");
        webView2.getEngine().load("https://itools.iquanzi.top");

        primaryStage.setOnCloseRequest(event -> {
            log.debug("监听到程序窗口关闭事件");
            getChessSession().getClient().shutdown();
        });
    }

    private SessionService getChessSession() {
        if (null == chessSession) {
            chessSession = SessionService.getInstance();
        }
        return chessSession;
    }
}
