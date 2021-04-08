package game.iquanzi.top.runnable;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;

/**
 * 刷新用户信息面板数据<br/>
 * @author Mr.Z
 * @version 1.0
 * @date 2021/04/01
 * @since JDK 1.8
 */
@Slf4j
public class RefreshUserInfoPaneRunnable implements Runnable{
    private String data;
    private VBox vBox;

    public RefreshUserInfoPaneRunnable(VBox vBox, String data) {
        this.data = data;
        this.vBox = vBox;
    }

    @Override
    public void run() {
        Platform.runLater(() -> {
            JSONObject info = JSONUtil.parseObj(data);
            log.debug("信息：{}", info);
            vBox.setUserData(data);
            ((TitledPane) vBox.lookup("#peerInfoPane")).setText(info.getJSONObject("user").getStr("nickName"));
            ((Label)vBox.lookup("#totalNums")).setText(info.getStr("totalNums"));
            ((Label)vBox.lookup("#wonNums")).setText(info.getStr("wonNums"));
            ((Label)vBox.lookup("#lostNums")).setText(info.getStr("lostNums"));
        });
    }
}
