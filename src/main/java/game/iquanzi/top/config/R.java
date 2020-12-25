package game.iquanzi.top.config;

/**
 * 控件常量定义<br/>
 * @author Mr.Z
 * @version 1.0
 * @createDate 2020/12/15
 * @since JDK 1.8
 */
public class R {
    public interface ChessBoard {
        /**棋盘*/
        String ID_CHESSBOARD = "gridPane";
        /**棋手信息*/
        String ID_TABPANE = "tabPane";
        /**对方姓名*/
        String ID_PEERNAME = "peerName";
        /**自己姓名*/
        String ID_SELFNAME = "selfName";
    }

    public interface Lobby {
        /**所有用户面板*/
        String ID_USERSPANE = "usersPane";

        /**指定用户的信息面板*/
        String ID_PEERPANE = "peerpane";

        String ID_PEERINFOPANE = "peerInfoPane";

        String ID_PEERINFOANCHORPANE = "peerInfoAnchorPane";

        /**总比赛局数*/
        String ID_TOTALNUMS = "totalNums";
        /**胜利局数*/
        String ID_WONNUMS = "wonNums";
        /**失败局数*/
        String ID_LOSTNUMS = "lostNums";
    }
}
