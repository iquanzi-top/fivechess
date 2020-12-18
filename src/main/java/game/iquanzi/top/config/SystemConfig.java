package game.iquanzi.top.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 系统配置<br/>
 * @author Mr.Z
 * @version 1.0
 * @createDate 2020/10/30
 * @since JDK 1.8
 */
public class SystemConfig {
    private String appKey;
    private String appSecret;

    private SystemConfig() {
        InputStream in = getClass().getClassLoader().getResourceAsStream("system.properties");
        Properties properties = new Properties();
        try {
            properties.load(in);
            String key = properties.getProperty("rongcloud.app.key");
            String secret = properties.getProperty("rongcloud.app.secret");
            this.appKey = key;
            this.appSecret = secret;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class SystemConfigHolder {
        private static SystemConfig instance = new SystemConfig();
    }

    /**
     * 获取配置对象
     * @return 系统配置对象
     */
    public static SystemConfig getInstance() {
        return SystemConfigHolder.instance;
    }

    public String getAppKey() {
        return appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }
}
