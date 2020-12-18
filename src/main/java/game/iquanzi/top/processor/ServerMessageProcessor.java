package game.iquanzi.top.processor;

import cn.hutool.json.JSONUtil;
import game.iquanzi.top.MainController;
import game.iquanzi.top.controller.LobbyController;
import game.iquanzi.top.dto.LoginResultDto;
import game.iquanzi.top.dto.OutDto;
import game.iquanzi.top.service.UserService;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.transport.AioSession;

import static game.iquanzi.top.dict.MessageTypeDict.*;

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
        OutDto dto = JSONUtil.toBean(msg, OutDto.class);
        int t = dto.getT();
        log.info("数据：{}", dto.toString());
        switch (t) {
            case LOGIN:
                break;
            case CHESS_STEP:
                break;
            case LOGIN_RESP:
                // 登录成功，本地保存用户信息
                LoginResultDto d = JSONUtil.toBean(dto.getD().toString(), LoginResultDto.class);
                log.info("登录结果：{}", JSONUtil.toJsonStr(d));

                // 保存到本地
                UserService.saveUser(d);

                // 跳转界面
                Platform.runLater(() -> {
                    /*MainController mainWin = new MainController();
                    try {
                        mainWin.showWindow();
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("显示主窗口异常{}", e.getMessage());
                    }*/
                    LobbyController lobby = new LobbyController();
                    try {
                        lobby.showWindow();
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("显示窗口异常{}", e.getMessage());
                    }

                    stage.hide();
                });
                break;
            default:
                log.debug("不支持的消息");
        }
    }
}
