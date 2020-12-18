package game.iquanzi.top.service;

import game.iquanzi.top.dto.LoginResultDto;
import game.iquanzi.top.pojo.UserPojo;
import game.iquanzi.top.util.HibernateUtil;

import java.util.Objects;

/**
 * 用户服务<br/>
 * @author Mr.Z
 * @version 1.0
 * @date 2020/12/15
 * @since JDK 1.8
 */
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

    private static UserPojo findUser(int userId) {
        String hql = "from UserPojo where uid=?0";
        return HibernateUtil.queryOne(UserPojo.class, hql, new Object[]{userId});
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
