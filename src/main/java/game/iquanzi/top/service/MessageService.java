package game.iquanzi.top.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import game.iquanzi.top.dict.MessageTypeDict;
import game.iquanzi.top.dto.GameInviteResultDto;
import game.iquanzi.top.dto.LoginDto;
import game.iquanzi.top.dto.OutDto;
import lombok.extern.slf4j.Slf4j;
import org.smartboot.socket.transport.AioSession;
import org.smartboot.socket.transport.WriteBuffer;

import java.io.IOException;
import java.text.MessageFormat;

import static game.iquanzi.top.dict.MessageTypeDict.Req.*;
import static game.iquanzi.top.dict.MessageTypeDict.Resp.GAME_FIVE_CHESS_RESP;

/**
 * 向服务器发送消息的服务<br/>
 * -----------------------------------------------------------------------------
 *
 * @author Mr.Z
 * @version 1.0
 * @date 2020-12-23
 * @since JDK 1.8
 */
@Slf4j
public class MessageService {

    private static SessionService chessSession;

    private MessageService() {
        if (null == chessSession) {
            chessSession = SessionService.getInstance();
        }
    }

    private static class MessageServiceHolder {
        private static MessageService instance = new MessageService();
    }

    /**
     * 获取实例
     * @return 实例对象
     */
    public static MessageService getInstance() {
        return MessageServiceHolder.instance;
    }

    /**
     * 被邀请者向服务器发送游戏邀请的回应消息
     * @param accept ture：接受邀请；false：拒绝邀请
     * @param pid 邀请人ID
     * @param uid 被邀请人ID
     */
    public void sendInviteResult(boolean accept, int pid, int uid) {
        GameInviteResultDto result = new GameInviteResultDto();
        result.setPid(pid);
        result.setUid(uid);
        result.setR(accept);

        OutDto<GameInviteResultDto> out = new OutDto<>();
        out.setT(GAME_FIVE_CHESS_RESP);
        out.setD(result);

        sendMsg2Server(out);
    }

    /**
     * 向服务其发送心跳消息
     */
    public void sendHeartMsg() {
        OutDto<String> out = new OutDto<>();
        out.setT(HEART_PING);
        out.setD("ping");
        sendMsg2Server(out);
    }

    /**
     * 发送测试消息到服务器
     */
    public void sendTestMsg() {
        String msg = MessageFormat.format("当前时间：{0}", DateUtil.now());
        OutDto<String> out = new OutDto<>();
        out.setD(msg);
        out.setT(MessageTypeDict.Test.TEST);
        sendMsg2Server(out);
    }

    /**
     * 邀请用户开始游戏
     * @param data peer用户信息
     */
    public void inviteUserGame(JSONObject data) {
        OutDto<Long> out = new OutDto<>();
        out.setD(data.getJSONObject("user").getLong("uid"));
        out.setT(GAME_FIVE_CHESS_INVITE);
        sendMsg2Server(out);
    }

    /**
     * 用户登录
     * @param account 帐号
     * @param pwd 密码
     */
    public void userLogin(String account, String pwd) {
        log.debug("用户认证，用户名：{}，密码：{}", account, pwd);
        LoginDto loginDto = LoginDto.builder().p(pwd).u(account).build();

        OutDto<LoginDto> out = new OutDto<>();
        out.setD(loginDto);
        out.setT(MessageTypeDict.Req.LOGIN);

        chessSession.setUserName(account);
        chessSession.setPassword(pwd);

        sendMsg2Server(out);
    }

    /**
     * 分页获取在线用户
     * @param page 页码
     */
    public void getOnlineUsers(int page) {
        log.debug("加载第{}页的在线用户", page);
        OutDto<Integer> out = new OutDto<>();
        out.setD(page);
        out.setT(ONLINE_USERS);

        sendMsg2Server(out);
    }

    /**
     * 发送消息到服务器
     * @param out 响应对象
     */
    private void sendMsg2Server(OutDto<?> out) {
        try {
            AioSession session = chessSession.getSession();
            assert session != null;

            byte[] msgBody = out.toString().getBytes();
            if (!session.isInvalid()) {
                WriteBuffer writer = session.writeBuffer();
                writer.writeInt(msgBody.length);
                writer.write(msgBody);
                writer.flush();
            } else {
                log.debug("连接会话失效");
            }
        } catch (IOException e) {
            log.error("发送消息失败", e);
        }
    }
}
