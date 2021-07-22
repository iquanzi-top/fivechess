package game.iquanzi.top.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import game.iquanzi.top.component.ChessDialog;
import game.iquanzi.top.service.MessageService;
import game.iquanzi.top.service.SessionService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.smartboot.socket.transport.AioQuickClient;

import java.net.URL;

import static game.iquanzi.top.config.R.Lobby.ID_PAGING;

/**
 * 游戏大厅控制器<br/>
 * @author Mr.Z
 * @version 1.0
 * @date 2020/12/17
 * @since JDK 1.8
 */
@Slf4j
public class LobbyController extends Application {
    private SessionService chessSession;

    @FXML
    private Label totalNums;
    @FXML
    private Label wonNums;
    @FXML
    private Label lostNums;
    @FXML
    private Pagination paging;

    private Stage curStage;

    /**
     * 显示窗口
     * @throws Exception 异常
     */
    public void showWindow(final Stage stage) throws Exception {
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
            if (StrUtil.isNotBlank(childId) && childId.equalsIgnoreCase(ID_PAGING)) {
                paging = (Pagination)child;
                paging.setStyle("-fx-padding: 1, 0;");
                paging.setPageFactory(this::createGridPane);
            }
        });

        primaryStage.setTitle("五子棋");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            log.debug("监听到程序窗口关闭事件");
            AioQuickClient<String> client = getChessSession().getClient();
            if (null != client) {
                client.shutdown();
            }
            System.exit(0);
        });

        curStage = primaryStage;
    }

    /**
     * 创建在线用户表格
     * @param pageIndex 页码
     * @return 表格面板
     */
    private GridPane createGridPane(int pageIndex) {
        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(true);
        gridPane.setPrefWidth(560);
        gridPane.setPrefHeight(560);
        gridPane.setAlignment(Pos.TOP_CENTER);

        ObservableList<ColumnConstraints> columnConstraints = gridPane.getColumnConstraints();
        ColumnConstraints cc = new ColumnConstraints();
        cc.setHalignment(HPos.CENTER);
        cc.setHgrow(Priority.SOMETIMES);
        cc.setPrefWidth(180);
        cc.setMaxWidth(180);
        cc.setMinWidth(180);
        columnConstraints.add(cc);
        columnConstraints.add(cc);
        columnConstraints.add(cc);

        ObservableList<RowConstraints> rowConstraints = gridPane.getRowConstraints();

        RowConstraints rc = new RowConstraints();
        rc.setValignment(VPos.CENTER);
        rc.setVgrow(Priority.SOMETIMES);
        rc.setPrefHeight(180);
        rc.setMaxHeight(180);
        rc.setMinHeight(180);
        rowConstraints.add(rc);
        rowConstraints.add(rc);
        rowConstraints.add(rc);

        gridPane.setId("usersPane_" + pageIndex);

        Platform.runLater(() -> loadData(pageIndex));
        return gridPane;
    }

    /**
     * 加载在线用户数据
     */
    private void loadData(int pageIndex) {
        // 在消息响应时刷新UI层数据
        MessageService.getInstance().getOnlineUsers(pageIndex);
    }

    private SessionService getChessSession() {
        if (null == chessSession) {
            chessSession = SessionService.getInstance();
        }
        return chessSession;
    }

    /**
     * 邀请游戏按钮点击事件
     * @param actionEvent 事件对象
     */
    public void inviteBtnClick(ActionEvent actionEvent) {
        log.debug("事件：{}", actionEvent.getEventType().getName());
        Scene scene = wonNums.getScene();
        Object userData = scene.lookup("#peerPane").getUserData();

        ChessDialog.Response resp = ChessDialog.showConfirmDialog(curStage, "发送邀请？", "游戏邀请");
        if (resp == ChessDialog.Response.YES) {
            log.info("点击了确定按钮");
            MessageService.getInstance().inviteUserGame(JSONUtil.parseObj(userData));
        } else if (resp == ChessDialog.Response.NO) {
            log.info("点击了取消按钮");
        }
    }

    /**
     * 个人中心按钮点击事件
     * @param actionEvent 事件对象
     */
    public void selfBtnClick(ActionEvent actionEvent) {
        log.debug("点击了【{}】按钮", "个人中心");
        //todo 需要进行页面调转
    }
}
