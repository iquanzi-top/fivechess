package game.iquanzi.top;

import cn.hutool.core.lang.Assert;
import cn.hutool.crypto.SecureUtil;
import game.iquanzi.top.processor.ServerMessageProcessor;
import game.iquanzi.top.protocol.StringProtocol;
import game.iquanzi.top.service.MessageService;
import game.iquanzi.top.service.SessionService;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.smartboot.socket.transport.AioQuickClient;
import org.smartboot.socket.transport.AioSession;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * 登录控制器<br/>
 *
 * @author Mr.Z
 * @version 1.0
 * @createDate 2020/10/29
 * @since JDK 1.8
 */
@Slf4j
public class LoginController extends Application implements Initializable {
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;

    private SessionService chessSession;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.debug("加载----------------");
    }

    /**
     * 登录按钮事件
     * @param event 事件
     * @throws Exception 异常
     */
    public void loginBtnClick(ActionEvent event) throws Exception {
        String account = username.getText();
        String pwd = SecureUtil.md5(password.getText());

        MessageService.getInstance().userLogin(account, pwd);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        log.debug("启动五子棋窗口");
        URL resource = getClass().getClassLoader().getResource("layout/login.fxml");
        Assert.notNull(resource, "布局资源文件未找到");
        AnchorPane root = FXMLLoader.load(resource);

        primaryStage.setTitle("五子棋");
        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.setResizable(false);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            log.debug("监听到程序窗口关闭事件");
            getChessSession().getClient().shutdown();
        });

        connServer(primaryStage);
    }

    /**
     * 连接服务器
     * @param stage 场景对象
     */
    private void connServer(Stage stage) {
        log.debug("-----连接服务器-----");
        try {
            AioQuickClient<String> client = new AioQuickClient<>(
                    "192.168.18.38",
                    8080,
                    new StringProtocol(),
                    new ServerMessageProcessor(stage)
            );
            AioSession session = client.start();
            log.debug("自己端ID：{}", session.getSessionID());

            getChessSession().setClient(client);
            getChessSession().setSession(session);
        } catch (IOException e) {
            log.error("服务器连接失败，5秒钟后自动重连", e);

            try {
                Thread.sleep(5000L);
                connServer(stage);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void init() {
        log.debug("初始化数据");
        chessSession = SessionService.getInstance();
    }

    private SessionService getChessSession() {
        if (null == chessSession) {
            chessSession = SessionService.getInstance();
        }
        return chessSession;
    }

    public static void main(String[] args) {
        log.debug("五子棋程序启动");
        launch(args);
    }
}
