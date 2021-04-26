package game.iquanzi.top.processor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import game.iquanzi.top.component.ChessDialog;
import game.iquanzi.top.controller.LobbyController;
import game.iquanzi.top.dto.LoginResultDto;
import game.iquanzi.top.dto.OnlineUserResultDto;
import game.iquanzi.top.dto.OutDto;
import game.iquanzi.top.pojo.OnlineUserPojo;
import game.iquanzi.top.pojo.UserPojo;
import game.iquanzi.top.runnable.RefreshUserInfoPaneRunnable;
import game.iquanzi.top.runnable.RefreshUsersPaneRunnable;
import game.iquanzi.top.service.UserService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.transport.AioSession;

import java.util.List;

import static game.iquanzi.top.dict.MessageTypeDict.Req.*;
import static game.iquanzi.top.dict.MessageTypeDict.Resp.LOGIN_RESP;
import static game.iquanzi.top.dict.MessageTypeDict.Resp.ONLINE_USERS_RESP;
import static game.iquanzi.top.dict.MessageTypeDict.Test.TEST_RESP;

/**
 * 服务端消息处理<br/>
 * @author Mr.Z
 * @version 1.0
 * @date 2020/12/11
 * @since JDK 1.8
 */
@Slf4j
public class ServerMessageProcessor implements MessageProcessor<String> {
    private Stage stage;
    /**每页用户数*/
    private static final int PAGE_SIZE = 9;

    public ServerMessageProcessor(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void process(AioSession aioSession, String msg) {
        log.info("收到来自：{}的消息：{}", aioSession.getSessionID(), msg);
        msgProcess(stage, aioSession, msg);
    }

    @Override
    public void stateEvent(AioSession aioSession, StateMachineEnum stateMachineEnum, Throwable throwable) {
        log.info("连接状态变化：{}, {}, {}",
                aioSession.getSessionID(),
                stateMachineEnum.name(),
                null == throwable ? "无异常" : throwable.getMessage()
        );
        connStateHandler(aioSession, stateMachineEnum);
    }

    /**
     * 连接状态变化处理
     * @param aioSession 会话session
     * @param stateMachineEnum 状态
     */
    private void connStateHandler(AioSession aioSession, StateMachineEnum stateMachineEnum) {
        String stateStr;
        switch (stateMachineEnum) {
            case NEW_SESSION:
                stateStr = "连接已建立并构建Session对象";
                break;
            case REJECT_ACCEPT:
                stateStr = "拒绝接受连接,仅Server端有效";
                break;
            case INPUT_SHUTDOWN:
                stateStr = "读通道已被关闭";
                break;
            case SESSION_CLOSED:
                stateStr = "会话关闭成功";
                // 此时将本地用户信息中的token置空
                boolean logoutRs = UserService.logout();
                log.debug("用户退出操作：{}", logoutRs ? "成功" : "失败");
                break;
            case INPUT_EXCEPTION:
                stateStr = "读操作异常";
                break;
            case SESSION_CLOSING:
                stateStr = "会话正在关闭中";
                break;
            case ACCEPT_EXCEPTION:
                stateStr = "服务端接受连接异常";
                break;
            case DECODE_EXCEPTION:
                stateStr = "协议解码异常";
                break;
            case OUTPUT_EXCEPTION:
                stateStr = "写操作异常";
                break;
            case PROCESS_EXCEPTION:
                stateStr = "业务处理异常";
                break;
            default:
                stateStr = "未知异常";
                break;
        }
        log.info("终端：{}的状态变化为：{}", aioSession.getSessionID(), stateStr);
    }

    /**
     * 消息处理
     * @param stage 场景对象
     * @param session 会话对象
     * @param msg 消息
     */
    private void msgProcess(Stage stage, AioSession session, String msg) {
        log.info("数据：{}", msg);
        OutDto dto = JSONUtil.toBean(msg, OutDto.class);
        int t = dto.getT();
        switch (t) {
            case LOGIN:
                break;
            case CHESS_STEP:
                break;
            case GAME_FIVE_CHESS_INVITE:
                //五子棋游戏邀请
                //服务端发送给客户端的消息，此消息内，标明游戏邀请发起者、发起时间等信息。
                //客户端收到该消息后，需弹出提示对话框，用户可选择接受邀请、拒绝邀请，如果在限定时间内，没有响应该消息，那么当做拒绝邀请操作。
                break;
            case GAME_CHINESS_CHESS_INVITE:
                //象棋游戏邀请
                break;
            case LOGIN_RESP:
                // 登录成功，本地保存用户信息
                Object respDto = dto.getD();
                if (null != respDto) {
                    LoginResultDto d = JSONUtil.toBean(respDto.toString(), LoginResultDto.class);
                    log.info("登录结果：{}", JSONUtil.toJsonStr(d));

                    // 保存到本地
                    UserService.saveUser(d);

                    // 跳转界面
                    Platform.runLater(() -> {
                        stage.hide();
                        LobbyController lobby = new LobbyController();
                        try {
                            lobby.showWindow(stage);
                        } catch (Exception e) {
                            e.printStackTrace();
                            log.error("显示窗口异常{}", e.getMessage());
                        }
                    });
                } else {
                    log.error("登录失败，稍后重试！");
                    Platform.runLater(() -> {
                        ChessDialog.showMessageDialog(stage, "登录失败，请稍后重试！", "失败");
                    });
                }
                break;
            case ONLINE_USERS_RESP:
                log.debug("在线用户数据：{}", msg);
                OnlineUserResultDto resultDto = JSONUtil.toBean(dto.getD().toString(), OnlineUserResultDto.class);
                int curPageIndex = resultDto.getCurPageIndex();
                GridPane pane = (GridPane) (stage.getScene().lookup("#usersPane_" + curPageIndex));

                VBox vBox = (VBox) (stage.getScene().lookup("#peerPane"));
                showPeersData(resultDto.getUsers(), pane, vBox);
                break;
            case TEST_RESP:
                log.debug("收到测试响应消息：{}", msg);
                break;
            default:
                log.debug("不支持的消息");
        }
    }

    /**
     * 显示在线用户信息
     */
    private void showPeersData(List<OnlineUserPojo> users, GridPane usersPane, VBox vBox) {
        int rowIndex = 0, columnIndex = 0;
        for (OnlineUserPojo pojo : users) {
            UserPojo user = pojo.getUser();

            FlowPane flowPane = new FlowPane();
            flowPane.setUserData(JSONUtil.toJsonStr(pojo));

            ImageView imageView = new ImageView();
            imageView.setFitHeight(140.0);
            imageView.setFitWidth(180.0);
            imageView.setPickOnBounds(true);
            imageView.setPreserveRatio(true);
            Image image = new Image(user.getPortrait(), 180.0, 140.0, true, false);

            imageView.setImage(image);

            Label label = new Label();
            label.setText(StrUtil.blankToDefault(user.getNickName(), user.getUserName()));
            label.setPrefWidth(180.0);
            label.setPrefHeight(40.0);
            label.setAlignment(Pos.CENTER);
            label.setTextAlignment(TextAlignment.CENTER);

            flowPane.getChildren().add(imageView);
            flowPane.getChildren().add(label);
            Insets insets = new Insets(10, 0, 0, 0);
            flowPane.setPadding(insets);
            flowPane.setAlignment(Pos.CENTER);
            flowPane.setId(String.valueOf(user.getUid()));

            flowPane.setOnMouseClicked(event -> {
                FlowPane fp = (FlowPane) event.getSource();
                log.debug("点击用户头像，该用户信息：{}", fp.getUserData());
                //刷新用户的相关信息
                Platform.runLater(new RefreshUserInfoPaneRunnable(vBox, fp.getUserData().toString()));
            });

            Platform.runLater(new RefreshUsersPaneRunnable(flowPane, rowIndex, columnIndex, usersPane));

            if (columnIndex < 2) {
                columnIndex++;
            }
            if (columnIndex == 2) {
                rowIndex++;
                columnIndex = 0;
            }
        }
    }
}
