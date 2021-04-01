package game.iquanzi.top.service;

import org.smartboot.socket.transport.AioQuickClient;
import org.smartboot.socket.transport.AioSession;

/**
 * 会话session服务<br/>
 * @author Mr.Z
 * @version 1.0
 * @date 2020/10/30
 * @since JDK 1.8
 */
public class SessionService {
    private String userName;
    private String password;
    private AioQuickClient<String> client;
    private AioSession session;

    private SessionService() {
    }

    private static class SessionServiceHolder {
        private static SessionService instance = new SessionService();
    }

    public static SessionService getInstance() {
        return SessionServiceHolder.instance;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AioQuickClient<String> getClient() {
        return client;
    }

    public void setClient(AioQuickClient<String> client) {
        this.client = client;
    }

    public AioSession getSession() {
        return session;
    }

    public void setSession(AioSession session) {
        this.session = session;
    }
}
