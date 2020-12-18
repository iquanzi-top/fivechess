package game.iquanzi.top.util;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import game.iquanzi.top.pojo.UserPojo;

import java.sql.*;
import java.text.MessageFormat;

/**
 * SQLite工具类<br/>
 * @author Mr.Z
 * @version 1.0
 * @createDate 2020/12/4
 * @since JDK 1.8
 */
public class SQLiteUtil {
    /**表是否存在*/
    private static final String DB_EXIST = "select count(*) from sqlite_master where type = \"table\" and name = \"{0}\";";

    /**
     * 创建表user，该表存储用户信息，包括自己和对弈过的用户
     */
    private static final StringBuilder DB_USER_ADD = new StringBuilder("drop table if exists user;")
            .append("create table user(")
            // 用户ID
            .append("uid integer primary key, ")
            // 用户名
            .append("user_name varchar(40), ")
            // 头像
            .append("portrait varchar(300), ")
            // 用户token
            .append("user_token varchar(50), ")
            // 记录创建时间
            .append("create_time datetime default (datetime(current_timestamp, 'localtime'))")
            .append(");");

    private static final StringBuilder DB_GAME_ADD = new StringBuilder("drop table if exists game;")
            .append("create table game(")
            .append("gid integer primary key, ")
            .append("create_time datetime default (datetime(current_timestamp, 'localtime')), ")
            .append("winer integer")
            .append(");");

    private static final StringBuilder DB_CHESS_STEP_ADD = new StringBuilder("drop table if exists chess_step;")
            .append("create table chess_step(")
            // 记录主键
            .append("cid integer primary key autoincrement, ")
            // 白棋用户ID
            .append("white_uid integer not null, ")
            // 黑棋用户ID
            .append("black_uid integer not null, ")
            // 对弈比赛ID
            .append("gid integer not null, ")
            // 棋子位置，左上角位置0，右下角位置224，天元位置112
            .append("position_id integer not null, ")
            // 棋子颜色，0：黑；1：白
            .append("chess_color integer not null, ")
            // 记录时间
            .append("create_time datetime default (datetime(current_timestamp, 'localtime'))")
            .append(");");

    private static String SERVER_PATH = Thread.currentThread().getContextClassLoader().getResource("").getPath().substring(1);
    private static String DB_PATH = SERVER_PATH + "/db/fivechess.db";
    private static Connection connection;
    private static Statement statement;

    static {
        connectDatabase();
        try {
            createDbUser();
            createDbGame();
            createDbChessStep();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建用户表
     * @throws SQLException 异常信息
     */
    private static void createDbUser() throws SQLException {
        String sqlUser = MessageFormat.format(DB_EXIST, "user");
        ResultSet rs = statement.executeQuery(sqlUser);
        if (rs.getInt(1) == 0) {
            statement.executeUpdate(DB_USER_ADD.toString());
            System.out.println("用户表创建成功");
        } else {
            System.out.println("用户表已经存在");
        }
    }

    /**
     * 创建棋局表
     * @throws SQLException 异常信息
     */
    private static void createDbGame() throws SQLException {
        String sqlGame = MessageFormat.format(DB_EXIST, "game");
        ResultSet rs = statement.executeQuery(sqlGame);
        if (rs.getInt(1) == 0) {
            statement.executeUpdate(DB_GAME_ADD.toString());
            System.out.println("棋局表创建成功");
        } else {
            System.out.println("棋局表已经存在");
        }
    }

    /**
     * 创建棋步表
     * @throws SQLException 异常信息
     */
    private static void createDbChessStep() throws SQLException {
        String sqlChessStep = MessageFormat.format(DB_EXIST, "chess_step");
        ResultSet rs = statement.executeQuery(sqlChessStep);
        if (rs.getInt(1) == 0) {
            statement.executeUpdate(DB_CHESS_STEP_ADD.toString());
            System.out.println("棋步表创建成功");
        } else {
            System.out.println("棋步表已经存在");
        }
    }

    /**
     * 连接数据库，只连接一次（通用方法）
     */
    private synchronized static void connectDatabase() {
        try {
            String driverClass = "org.sqlite.JDBC";
            Class.forName(driverClass);

            String url = "jdbc:sqlite:" + DB_PATH;
            connection = DriverManager.getConnection(url);
            statement = connection.createStatement();
            statement.setQueryTimeout(30);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭连接
     */
    public static void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            throw new NullPointerException("连接未开启");
        }
    }

    /**
     * 添加
     * @param user 用户对象
     */
    public static void addUser(UserPojo user) {
        try {
            String addUserSQL = "insert into user (uid, user_name, portrait, user_token) values ({0}, \"{1}\", \"{2}\", \"{3}\");";
            String sql = MessageFormat.format(addUserSQL, user.getUid(), user.getUserName(), user.getPortrait(), user.getToken());
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 编辑
     */
    public static void editUserToken(String nameStr, String numberStr,String boxId) {
        try {
            String updateUserSQL = "update user set box_name = '"+nameStr+"', box_number = '"
                    +numberStr+"', update_time = datetime() where id =  "+boxId;
            String sql = MessageFormat.format(updateUserSQL, nameStr, boxId);
            statement.executeUpdate(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 删除
     */
    public static void delBox(String boxId) {
        try {
            statement.executeUpdate("update collection_box set delete_flag = 1 ,update_time = datetime() where id =  "+boxId);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 查询
     */
    public static void getUserList() {
        try {
            String sql = "select uid, user_name, portrait, user_token, create_time from user order by uid asc";
            ResultSet rs = statement.executeQuery(sql);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                System.out.println("列名：" + metaData.getColumnName(i) + "，列类型：" + metaData.getColumnClassName(i));
            }
            while (rs.next()) {
                int uid = rs.getInt(1);
                String userName = rs.getString(2);
                String portrait = rs.getString(3);
                String token = rs.getString(4);
                Timestamp createTime = rs.getTimestamp(5);
                System.out.println("数据：" + uid + "," + userName + "," + DateUtil.format(createTime, DatePattern.NORM_DATETIME_PATTERN));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
