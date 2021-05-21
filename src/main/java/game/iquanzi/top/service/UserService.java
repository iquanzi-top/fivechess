package game.iquanzi.top.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import game.iquanzi.top.dto.LoginResultDto;
import game.iquanzi.top.pojo.UserPojo;
import game.iquanzi.top.util.HibernateUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

/**
 * 用户服务<br/>
 * @author Mr.Z
 * @version 1.0
 * @date 2020/12/15
 * @since JDK 1.8
 */
@Slf4j
public class UserService {
    /**
     * 本地保存用户信息
     * @param dto 登录结果对象
     */
    public static void saveUser(LoginResultDto dto) {
        UserPojo pojo = findUser(dto.getId());
        if (Objects.isNull(pojo)) {
            pojo = dto2pojo(dto);
            HibernateUtil.add(pojo);
        } else {
            pojo.setToken(dto.getTk());
            HibernateUtil.update(pojo);
        }
    }

    /**
     * 获取当前用户信息
     * @return 用户信息
     */
    public static UserPojo curUser() {
        String hql = "from UserPojo where token != ''";
        return HibernateUtil.queryOne(UserPojo.class, hql, null);
    }

    /**
     * 用户退出
     * @return true：成功；false：失败
     */
    public static boolean logout() {
        UserPojo userPojo = curUser();
        if (null != userPojo) {
            userPojo.setToken("");
            return HibernateUtil.update(userPojo);
        }
        return true;
    }

    private static UserPojo findUser(int userId) {
        String hql = "from UserPojo where uid=?0";
        List<UserPojo> list = HibernateUtil.queryList(UserPojo.class, hql, new Object[]{userId});
        list.forEach(p -> {
            log.debug("用户信息：{}", p.toString());
        });
        return CollectionUtil.isEmpty(list) ? null : list.get(0);
    }

    private static UserPojo dto2pojo(LoginResultDto dto) {
        UserPojo pojo = new UserPojo();
        pojo.setUid(dto.getId());
        pojo.setUserName(dto.getU());
        pojo.setNickName(dto.getN());
        pojo.setPortrait(dto.getP());
        pojo.setToken(dto.getTk());
        pojo.setCreateTime(dto.getCt());
        return pojo;
    }
}
