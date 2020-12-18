package game.iquanzi.top;

import cn.hutool.json.JSONUtil;
import game.iquanzi.top.pojo.UserPojo;
import game.iquanzi.top.service.UserService;
import game.iquanzi.top.util.HibernateUtil;
import junit.framework.TestCase;

import java.util.List;

/**
 * 用户信息测试类<br/>
 * @author Mr.Z
 * @version 1.0
 * @createDate 2020/12/10
 * @since JDK 1.8
 */
public class TestUser extends TestCase {

    public void testUserAdd() {
        int uid = 1240;
        String hql = "from UserPojo where uid=?0";
        UserPojo pojo = HibernateUtil.queryOne(UserPojo.class, hql, new Object[]{uid});
        if (null == pojo) {
            pojo = new UserPojo();
            pojo.setUid(uid);
            pojo.setUserName("Mr.Z");
            pojo.setPortrait("");
            pojo.setNickName("大白");
            pojo.setToken("token_1240");

            boolean rs = HibernateUtil.add(pojo);
            System.out.println("添加结果：" + rs);
        } else {
            pojo.setNickName("大大白");
            pojo.setToken("token_1240");
            boolean rs = HibernateUtil.update(pojo);
            System.out.println("已存在对象，且更新完成，" + rs);
        }
        String listSQL = "from UserPojo";
        List<UserPojo> list = HibernateUtil.queryList(UserPojo.class, listSQL, null);

        System.out.println("查询结果：" + JSONUtil.toJsonStr(list));
    }

    public void testUserGet() {
        String hql = "from UserPojo";
        UserPojo userPojo = HibernateUtil.queryOne(UserPojo.class, hql, null);
        System.out.println(JSONUtil.toJsonStr(userPojo));
    }

    public void testCurUser() {
        UserPojo userPojo = UserService.curUser();
        System.out.println(JSONUtil.toJsonStr(userPojo));
    }
}
