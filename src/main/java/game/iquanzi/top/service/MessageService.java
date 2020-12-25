package game.iquanzi.top.service;

import game.iquanzi.top.dict.MessageTypeDict;
import game.iquanzi.top.dto.LoginDto;
import game.iquanzi.top.dto.OutDto;
import game.iquanzi.top.pojo.UserPojo;
import lombok.extern.slf4j.Slf4j;
import org.smartboot.socket.transport.WriteBuffer;

import java.io.IOException;

import static game.iquanzi.top.dict.MessageTypeDict.Req.ONLINE_USERS;

/**
 * 向服务器发送消息的服务<br/>
 * -----------------------------------------------------------------------------
 *
 * @author Mr.Z
 * @version 1.0
 * @createDate 2020-12-23
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

    public static MessageService getInstance() {
        return MessageServiceHolder.instance;
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
     * todo 分页获取在线用户
     * @param page 页吗
     */
    public void getOnlineUsers(int page) {
        UserPojo userPojo = UserService.curUser();

        OutDto<Integer> out = new OutDto<>();
        out.setD(page);
        out.setT(ONLINE_USERS);

        sendMsg2Server(out);
    }

    private void sendMsg2Server(OutDto<?> out) {
        try {
            byte[] msgBody = out.toString().getBytes();
            if (!chessSession.getSession().isInvalid()) {
                WriteBuffer writer = chessSession.getSession().writeBuffer();
                writer.writeInt(msgBody.length);
                writer.write(msgBody);
                writer.flush();
            } else {
                log.debug("连接会话失效");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
