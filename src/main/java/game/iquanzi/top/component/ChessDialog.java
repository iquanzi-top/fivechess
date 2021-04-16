package game.iquanzi.top.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * 弹窗<br/>
 *
 * @author Mr.Z
 * @version 1.0
 * @date 2021-04-15
 * @since JDK 1.8
 */
public class ChessDialog {
    public enum Response {
        NO, YES, CANCEL
    }

    /**
     * 按钮
     */
    private static Response buttonSelected = Response.CANCEL;

    /**
     * 图标
     */
    private final static ImageView icon = new ImageView();

    /**
     * 弹窗对象
     */
    static class Dialog extends Stage {
        public Dialog(String title, Stage owner, Scene scene) {
            setTitle(title);
            initStyle(StageStyle.UTILITY);
            initModality(Modality.APPLICATION_MODAL);
            initOwner(owner);
            setResizable(false);
            setScene(scene);
            icon.setImage(new Image(getClass().getResourceAsStream("/images/i.png")));
        }

        /**
         * 显示
         */
        public void showDialog() {
            sizeToScene();
            centerOnScreen();
            showAndWait();
        }
    }

    static class Message extends Text {
        public Message(String msg) {
            super(msg);
            //自动换行的宽度
            setWrappingWidth(250);
        }
    }

    public static Response showConfirmDialog(Stage owner, String message, String title) {
        VBox vb = new VBox();
        Scene scene = new Scene(vb);
        final Dialog dial = new Dialog(title, owner, scene);
        vb.setPadding(new Insets(10, 10, 10, 10));
        vb.setSpacing(10);
        Button yesButton = new Button("确定");
        yesButton.setOnAction(event -> {
            dial.close();
            buttonSelected = Response.YES;
        });
        Button noButton = new Button("取消");
        noButton.setOnAction(event -> {
            dial.close();
            buttonSelected = Response.NO;
        });
        BorderPane bp = new BorderPane();
        HBox buttons = new HBox();
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(10);
        buttons.getChildren().addAll(yesButton, noButton);
        bp.setCenter(buttons);
        HBox msg = new HBox();
        msg.setSpacing(5);
        msg.getChildren().addAll(icon, new Message(message));
        vb.getChildren().addAll(msg, bp);
        dial.showDialog();
        return buttonSelected;
    }

    public static void showMessageDialog(Stage owner, String message, String title) {
        showMessageDialog(owner, new Message(message), title);
    }

    public static void showWebViewDialog(Stage owner, String text_, String title) {
        VBox vb = new VBox();
        Scene scene = new Scene(vb);
        final Dialog dial = new Dialog(title, owner, scene);
        vb.setPadding(new Insets(10, 10, 10, 10));
        vb.setSpacing(10);
        Button okButton = new Button("确定");
        okButton.setAlignment(Pos.CENTER);
        okButton.setOnAction(event -> dial.close());
        BorderPane bp = new BorderPane();
        bp.setCenter(okButton);
        HBox msg = new HBox();
        msg.setSpacing(5);
        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();
        engine.loadContent(text_);
        msg.getChildren().addAll(icon, webView);
        vb.getChildren().addAll(msg, bp);
        dial.setMinHeight(150);
        dial.setMaxHeight(150);
        dial.setHeight(150);
        dial.showDialog();
    }

    public static void showMessageDialog(Stage owner, Node message, String title) {
        VBox vb = new VBox();
        Scene scene = new Scene(vb);
        final Dialog dial = new Dialog(title, owner, scene);
        vb.setPadding(new Insets(10, 10, 10, 10));
        vb.setSpacing(10);
        Button okButton = new Button("确定");
        okButton.setOnAction(event -> dial.close());
        BorderPane bp = new BorderPane();
        bp.setCenter(okButton);
        HBox msg = new HBox();
        msg.setSpacing(5);
        msg.getChildren().addAll(icon, message);
        vb.getChildren().addAll(msg, bp);
        dial.showDialog();
    }
}
