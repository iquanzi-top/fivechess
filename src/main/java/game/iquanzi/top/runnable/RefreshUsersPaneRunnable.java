package game.iquanzi.top.runnable;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import lombok.extern.slf4j.Slf4j;

/**
 * 刷新在线用户面板数据<br/>
 * -----------------------------------------------------------------------------
 * @author Mr.Z
 * @version 1.0
 * @date 2021-01-04
 * @since JDK 1.8
 */
@Slf4j
public class RefreshUsersPaneRunnable implements Runnable {
    private int rIndex, cIndex;
    private GridPane parent;
    private Node node;

    public RefreshUsersPaneRunnable(Node node, int rIndex, int cIndex, GridPane parent) {
        this.node = node;
        this.rIndex = rIndex;
        this.cIndex = cIndex;
        this.parent = parent;
    }

    @Override
    public void run() {
        Platform.runLater(() -> {
            parent.add(node, cIndex, rIndex);
        });
    }
}
