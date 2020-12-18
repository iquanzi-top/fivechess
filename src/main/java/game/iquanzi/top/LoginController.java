package game.iquanzi.top;

import cn.hutool.core.lang.Assert;
import cn.hutool.crypto.SecureUtil;
import game.iquanzi.top.dict.MessageTypeDict;
import game.iquanzi.top.dto.LoginDto;
import game.iquanzi.top.dto.OutDto;
import game.iquanzi.top.processor.ServerMessageProcessor;
import game.iquanzi.top.protocol.StringProtocol;
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
import org.smartboot.socket.transport.WriteBuffer;

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

        log.debug("用户认证，用户名：{}，密码：{}", account, pwd);
        LoginDto loginDto = LoginDto.builder().p(pwd).u(account).build();

        OutDto<LoginDto> out = new OutDto<>();
        out.setD(loginDto);
        out.setT(MessageTypeDict.LOGIN);

        getChessSession().setUserName(account);
        getChessSession().setPassword(pwd);

        byte[] msgBody = out.toString().getBytes();
        if (!getChessSession().getSession().isInvalid()) {
            WriteBuffer writer = getChessSession().getSession().writeBuffer();
            writer.writeInt(msgBody.length);
            writer.write(msgBody);
            writer.flush();
        } else {
            log.debug("连接会话失效");
        }
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

        /*Platform.runLater(() -> {
            UserPojo user = new UserPojo();
            user.setUid(222);
            user.setUserName("王五");
            user.setToken("token_222");
            SQLiteUtil.addUser(user);
            SQLiteUtil.getUserList();
        });*/

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
                    "127.0.0.1",
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
