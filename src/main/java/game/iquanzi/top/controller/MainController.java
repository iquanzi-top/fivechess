package game.iquanzi.top.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import game.iquanzi.top.dto.OutDto;
import game.iquanzi.top.pojo.UserPojo;
import game.iquanzi.top.service.SessionService;
import game.iquanzi.top.service.UserService;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.smartboot.socket.transport.AioQuickClient;
import org.smartboot.socket.transport.WriteBuffer;

import java.io.IOException;
import java.math.RoundingMode;
import java.net.URL;
import java.util.HashMap;
import java.util.Optional;

import static game.iquanzi.top.config.R.ChessBoard.*;

/**
 * @author Mr.Z
 */
@Slf4j
public class MainController extends Application {
    private static Stage stage = new Stage();
    /**已下棋子Map*/
    private static HashMap<Integer, String> chessMap = new HashMap<>();

    /**窗口宽度*/
    private static final int BORDER_WIDTH = 840;

    /**窗口高度*/
    private static final int BORDER_HEIGHT = 640;

    /**节点半径*/
    private static final int HALF_R = 40;

    /**组件列表*/
    private static ObservableList<Node> list;

    /**步数，0为黑棋*/
    private int step = 0;

    private SessionService chessSession;

    @Override
    public void start(Stage primaryStage) throws Exception{
        URL resource = getClass().getClassLoader().getResource("layout/chessboard.fxml");
        Assert.notNull(resource, "布局资源文件未找到");

        AnchorPane root = FXMLLoader.load(resource);
        list = root.getChildren();

        list.forEach(node -> {
            String nodeId = node.getId();
            log.debug("控件ID：{}", nodeId);
            if (null != nodeId) {
                if (nodeId.equalsIgnoreCase(ID_CHESSBOARD)) {
                    node.setOnMouseClicked(event -> {
                        double pX = event.getX();
                        double pY = event.getY();

                        // 动态添加组件
                        try {
                            Circle chess = getChess(pX, pY);
                            list.add(chess);

                            // 判断胜负
                            log.debug("当前落子情况：{}", JSONUtil.toJsonStr(chessMap));

                            // 大于9步棋子之后，才进行判断胜负
                            if (step > 8) {
                                boolean win = win((step - 1) % 2);
                                Assert.isFalse(win, (step - 1) % 2 == 0 ? "黑方胜利" : "白方胜利");
                            }
                        } catch (Exception e) {
                            log.error(e.getMessage());
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("对弈结束");
                            alert.setHeaderText("头部内容");
                            alert.setContentText(e.getMessage());
                            Optional<ButtonType> result = alert.showAndWait();
                            if (result.get() == ButtonType.OK) {
                                log.debug("点击了确定按钮，可以重新开始");
                            } else {
                                log.debug("点击了取消按钮，可以重新开始");
                            }
                        }
                    });
                } else if (nodeId.equalsIgnoreCase(ID_TABPANE)) {
                    ObservableList<Node> children = ((Pane) node).getChildren();
                    children.forEach(child -> {
                        String childId = child.getId();
                        log.debug("子控件：{}，控件类型：{}", childId, child.getStyleClass().toString());
                        if (null != childId && childId.equalsIgnoreCase(ID_PEERNAME)) {
                            ((Label)child).textProperty().setValue("对方名字");
                        } else if (null != childId && childId.equalsIgnoreCase(ID_SELFNAME)) {
                            UserPojo curUser = UserService.curUser();
                            ((Label)child).textProperty().bind(Bindings.format(curUser.getUserName()));
                        } else {
                            log.debug("跳过{}控件", childId);
                        }
                    });
                }
            } else {
                log.debug("组件:[{}]没有绑定事件", "未设定ID");
            }
        });

        primaryStage.setTitle("五子棋");
        primaryStage.setScene(new Scene(root, BORDER_WIDTH, BORDER_HEIGHT));
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
    }

    private SessionService getChessSession() {
        if (null == chessSession) {
            chessSession = SessionService.getInstance();
        }
        return chessSession;
    }

    /**
     * 获取棋子
     * @param x 鼠标点击横坐标
     * @param y 鼠标点击纵坐标
     * @return 棋子
     */
    private Circle getChess(double x, double y) {
        double tileWidth = (x - 40) / HALF_R;
        double tileHeight = (y - 40) / HALF_R;

        int xR = NumberUtil.round(tileWidth, 0, RoundingMode.HALF_DOWN).intValue();
        int yR = NumberUtil.round(tileHeight, 0, RoundingMode.HALF_DOWN).intValue();

        log.info("棋步：{}，判断落子位置：{}, {}", step, xR, yR);
        if (step == 0) {
            Assert.isTrue(
                    xR == 7 && yR == 7,
                    "第一颗棋子必须下在天元"
            );
        }

        double pX = xR * HALF_R + 40.0;
        double pY = yR * HALF_R + 40.0;
        log.info("点击坐标：{}, {}；点位坐标：{}, {}", x, y, pX, pY);

        int k = xR + yR * 15;
        String c = chessMap.get(k);
        Assert.isNull(c, "该点位已经有棋子了，请换点位");

        Circle circle = new Circle(pX, pY, 10);
        circle.setFill(step % 2 == 1 ? Color.WHITE : Color.BLACK);
        circle.setStroke(Color.BLACK);
        circle.setStrokeType(StrokeType.INSIDE);

        chessMap.put(k, step % 2 == 1 ? "白" : "黑");

        // fixme 向服务器发送消息，在某点位落子了
        try {
            sendMsg(k, step % 2 == 1 ? "白" : "黑");
        } catch (IOException e) {
            e.printStackTrace();
        }

        step++;
        return circle;
    }

    /**
     * 显示窗口
     * @throws Exception 异常
     */
    public void showWindow() throws Exception {
        start(stage);
    }

    /**
     * 向服务器发送落子消息
     * @param p 落子位置
     * @param color 棋子颜色
     * @throws IOException 异常
     */
    private void sendMsg(int p, String color) throws IOException {
        OutDto<String> out = new OutDto<>();
        out.setT(2);
        out.setD(p + "," + color);
        byte[] msgBody = out.toString().getBytes();

        if (!getChessSession().getSession().isInvalid()) {
            WriteBuffer writer = getChessSession().getSession().writeBuffer();
            writer.writeInt(msgBody.length);
            writer.write(msgBody);
            writer.flush();
        } else {
            // fixme 需要尝试重连
            log.debug("连接会话失效");
        }
    }

    /**
     * 判断胜负
     * @param type 持棋类型，0：黑棋，1：白棋
     * @return 持棋类型是否胜利
     */
    private boolean win(int type) {
        // 从0位查找
        for (int i = 0; i < 100; i++ ) {

            String chess = chessMap.get(i);
            // log.debug("从第{}个点开始判断，{}", i, StrUtil.isBlank(chess) ? "该点无棋子" : "该点棋子：" + chess);
            if (StrUtil.isBlank(chess) || !chess.equals(type == 0 ? "黑" : "白")) {
                continue;
            }

            if (i % 10 < 4) {
                // 只判断+1、+10、+11
                String v1_1 = chessMap.get(i + 1 * 1);
                String v1_2 = chessMap.get(i + 1 * 2);
                String v1_3 = chessMap.get(i + 1 * 3);
                String v1_4 = chessMap.get(i + 1 * 4);
                log.debug("判断点位：{}-->{}, {}-->{}, {}-->{}, {}-->{}",
                        i+1, StrUtil.blankToDefault(v1_1, "无子"),
                        i+2, StrUtil.blankToDefault(v1_2, "无子"),
                        i+3, StrUtil.blankToDefault(v1_3, "无子"),
                        i+4, StrUtil.blankToDefault(v1_4, "无子"));
                if (chess.equals(v1_1) && chess.equals(v1_2) && chess.equals(v1_3) && chess.equals(v1_4)) {
                    return true;
                }

                String v2_1 = chessMap.get(i + 10 * 1);
                String v2_2 = chessMap.get(i + 10 * 2);
                String v2_3 = chessMap.get(i + 10 * 3);
                String v2_4 = chessMap.get(i + 10 * 4);
                log.debug("判断点位：{}-->{}, {}-->{}, {}-->{}, {}-->{}",
                        i+10, StrUtil.blankToDefault(v2_1, "无子"),
                        i+20, StrUtil.blankToDefault(v2_2, "无子"),
                        i+30, StrUtil.blankToDefault(v2_3, "无子"),
                        i+40, StrUtil.blankToDefault(v2_4, "无子"));
                if (chess.equals(v2_1) && chess.equals(v2_2) && chess.equals(v2_3) && chess.equals(v2_4)) {
                    return true;
                }

                String v3_1 = chessMap.get(i + 11);
                String v3_2 = chessMap.get(i + 11 * 2);
                String v3_3 = chessMap.get(i + 11 * 3);
                String v3_4 = chessMap.get(i + 11 * 4);
                log.debug("判断点位：{}-->{}, {}-->{}, {}-->{}, {}-->{}",
                        i+11, StrUtil.blankToDefault(v3_1, "无子"),
                        i+22, StrUtil.blankToDefault(v3_2, "无子"),
                        i+33, StrUtil.blankToDefault(v3_3, "无子"),
                        i+44, StrUtil.blankToDefault(v3_4, "无子"));
                if (chess.equals(v3_1) && chess.equals(v3_2) && chess.equals(v3_3) && chess.equals(v3_4)) {
                    return true;
                }
            } else if (i % 10 > 6) {
                // +9、+10
                String v1_1 = chessMap.get(i + 9 * 1);
                String v1_2 = chessMap.get(i + 9 * 2);
                String v1_3 = chessMap.get(i + 9 * 3);
                String v1_4 = chessMap.get(i + 9 * 4);
                log.debug("判断点位：{}-->{}, {}-->{}, {}-->{}, {}-->{}",
                        i+9, StrUtil.blankToDefault(v1_1, "无子"),
                        i+18, StrUtil.blankToDefault(v1_2, "无子"),
                        i+27, StrUtil.blankToDefault(v1_3, "无子"),
                        i+36, StrUtil.blankToDefault(v1_4, "无子"));
                if (chess.equals(v1_1) && chess.equals(v1_2) && chess.equals(v1_3) && chess.equals(v1_4)) {
                    return true;
                }

                String v2_1 = chessMap.get(i + 10 * 1);
                String v2_2 = chessMap.get(i + 10 * 2);
                String v2_3 = chessMap.get(i + 10 * 3);
                String v2_4 = chessMap.get(i + 10 * 4);
                log.debug("判断点位：{}-->{}, {}-->{}, {}-->{}, {}-->{}",
                        i+10, StrUtil.blankToDefault(v2_1, "无子"),
                        i+20, StrUtil.blankToDefault(v2_2, "无子"),
                        i+30, StrUtil.blankToDefault(v2_3, "无子"),
                        i+40, StrUtil.blankToDefault(v2_4, "无子"));
                if (chess.equals(v2_1) && chess.equals(v2_2) && chess.equals(v2_3) && chess.equals(v2_4)) {
                    return true;
                }
            } else {
                // +1、+9、+10、+11
                String v1_1 = chessMap.get(i + 1);
                String v1_2 = chessMap.get(i + 2);
                String v1_3 = chessMap.get(i + 3);
                String v1_4 = chessMap.get(i + 4);
                log.debug("判断点位：{}-->{}, {}-->{}, {}-->{}, {}-->{}",
                        i+1, StrUtil.blankToDefault(v1_1, "无子"),
                        i+2, StrUtil.blankToDefault(v1_2, "无子"),
                        i+3, StrUtil.blankToDefault(v1_3, "无子"),
                        i+4, StrUtil.blankToDefault(v1_4, "无子"));
                if (chess.equals(v1_1) && chess.equals(v1_2) && chess.equals(v1_3) && chess.equals(v1_4)) {
                    return true;
                }

                String v2_1 = chessMap.get(i + 9 * 1);
                String v2_2 = chessMap.get(i + 9 * 2);
                String v2_3 = chessMap.get(i + 9 * 3);
                String v2_4 = chessMap.get(i + 9 * 4);
                log.debug("判断点位：{}-->{}, {}-->{}, {}-->{}, {}-->{}",
                        i+9, StrUtil.blankToDefault(v2_1, "无子"),
                        i+18, StrUtil.blankToDefault(v2_2, "无子"),
                        i+27, StrUtil.blankToDefault(v2_3, "无子"),
                        i+36, StrUtil.blankToDefault(v2_4, "无子"));
                if (chess.equals(v2_1) && chess.equals(v2_2) && chess.equals(v2_3) && chess.equals(v2_4)) {
                    return true;
                }

                String v3_1 = chessMap.get(i + 10);
                String v3_2 = chessMap.get(i + 20);
                String v3_3 = chessMap.get(i + 30);
                String v3_4 = chessMap.get(i + 40);
                log.debug("判断点位：{}-->{}, {}-->{}, {}-->{}, {}-->{}",
                        i+10, StrUtil.blankToDefault(v3_1, "无子"),
                        i+20, StrUtil.blankToDefault(v3_2, "无子"),
                        i+30, StrUtil.blankToDefault(v3_3, "无子"),
                        i+40, StrUtil.blankToDefault(v3_4, "无子"));
                if (chess.equals(v3_1) && chess.equals(v3_2) && chess.equals(v3_3) && chess.equals(v3_4)) {
                    return true;
                }

                String v4_1 = chessMap.get(i + 11 * 1);
                String v4_2 = chessMap.get(i + 11 * 2);
                String v4_3 = chessMap.get(i + 11 * 3);
                String v4_4 = chessMap.get(i + 11 * 4);
                log.debug("判断点位：{}-->{}, {}-->{}, {}-->{}, {}-->{}",
                        i+11, StrUtil.blankToDefault(v4_1, "无子"),
                        i+22, StrUtil.blankToDefault(v4_2, "无子"),
                        i+33, StrUtil.blankToDefault(v4_3, "无子"),
                        i+44, StrUtil.blankToDefault(v4_4, "无子"));
                if (chess.equals(v4_1) && chess.equals(v4_2) && chess.equals(v4_3) && chess.equals(v4_4)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void logoutBtnClick(ActionEvent event) {
        log.debug("点击了退出按钮，当前stage状态：{}", stage.isShowing() ? "显示" : "未显示");
        getChessSession().getClient().shutdown();
        stage.close();
    }
}
