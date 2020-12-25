package game.iquanzi.top.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import game.iquanzi.top.service.MessageService;
import game.iquanzi.top.service.SessionService;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;

import static game.iquanzi.top.config.R.Lobby.*;

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

    private SessionService chessSession;

    @FXML
    private GridPane usersPane;

    @FXML
    private Label totalNums;
    @FXML
    private Label wonNums;
    @FXML
    private Label lostNums;

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

        AnchorPane root = FXMLLoader.load(resource);

        ObservableList<Node> childs = root.getChildren();
        childs.forEach(child -> {
            String childId = child.getId();
            log.debug("一级组件ID：{}", childId);
            if (StrUtil.isNotBlank(childId) && childId.equalsIgnoreCase(ID_PEERPANE)) {
                ObservableList<Node> childrens = ((VBox) child).getChildren();
                childrens.forEach(c -> {
                    String cId = c.getId();
                    log.debug("二级组件ID：{}", cId);
                    if (StrUtil.isNotBlank(cId) && cId.equalsIgnoreCase(ID_PEERINFOPANE)) {
                        Node content = ((TitledPane) c).getContent();
                        log.debug("三级组件ID：{}", content.getId());
                        ObservableList<Node> chs = ((AnchorPane) content).getChildren();
                        chs.forEach(ch -> {
                            String id = ch.getId();
                            if (StrUtil.isNotBlank(id) && id.equalsIgnoreCase(ID_TOTALNUMS)) {
                                totalNums = ((Label)ch);
                                log.debug("总数：{}", totalNums.getText());
                            }

                            if (StrUtil.isNotBlank(id) && id.equalsIgnoreCase(ID_WONNUMS)) {
                                wonNums = ((Label)ch);
                                log.debug("胜数：{}", wonNums.getText());
                            }

                            if (StrUtil.isNotBlank(id) && id.equalsIgnoreCase(ID_LOSTNUMS)) {
                                lostNums = ((Label)ch);
                                log.debug("败数：{}", lostNums.getText());
                            }
                        });
                    }
                });
            } else if (StrUtil.isNotBlank(childId) && childId.equalsIgnoreCase(ID_USERSPANE)) {
                usersPane = (GridPane)child;
            }
        });

        primaryStage.setTitle("五子棋");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            log.debug("监听到程序窗口关闭事件");
            getChessSession().getClient().shutdown();
        });

        loadData();
    }

    private void loadData() {
        // fixme 从服务器获取在线用户数据，并进行展示
        MessageService.getInstance().getOnlineUsers(0);

        showPeersData();
    }

    /**
     * 显示在线用户信息
     */
    private void showPeersData() {
        FlowPane flowPane = new FlowPane();
        flowPane.setUserData("数据内容");

        ImageView imageView = new ImageView();
        imageView.setFitHeight(145.0);
        imageView.setFitWidth(200.0);
        imageView.setPickOnBounds(true);
        imageView.setPreserveRatio(true);
        Image image = new Image("/images/t.jpeg", 200.0, 150.0, true, false);

        imageView.setImage(image);

        Label label = new Label();
        label.setText("王五");
        label.setPrefWidth(200.0);
        label.setPrefHeight(50.0);
        label.setAlignment(Pos.CENTER);
        label.setTextAlignment(TextAlignment.CENTER);

        flowPane.getChildren().add(imageView);
        flowPane.getChildren().add(label);
        Insets insets = new Insets(5, 0, 0, 0);
        flowPane.setPadding(insets);
        flowPane.setAlignment(Pos.CENTER);
        flowPane.setId("f_01");

        flowPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FlowPane fp = (FlowPane) event.getSource();
                log.debug("点击事件：{}", fp.getUserData());
            }
        });

        usersPane.add(flowPane, 0, 2);
    }

    private SessionService getChessSession() {
        if (null == chessSession) {
            chessSession = SessionService.getInstance();
        }
        return chessSession;
    }

    public void inviteBtnClick(ActionEvent actionEvent) {
        log.debug("事件：{}", actionEvent.getEventType().getName());
        if (ObjectUtil.isNotNull(wonNums)) {
            wonNums.setText("60");
        } else {
            log.error("wonNums为空");
        }

        /*MainController mainWin = new MainController();
        try {
            mainWin.showWindow();
            stage.hide();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}
