package game.iquanzi.top.util;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.List;

/**
 * Hibernate工具类<br/>
 * @author Mr.Z
 * @version 1.0
 * @createDate 2020/12/10
 * @since JDK 1.8
 */
@Slf4j
public class HibernateUtil {
    /**会话工厂*/
    private static SessionFactory sessionFactory = null;

    /**ThreadLocal可以隔离多个线程的数据共享，因此不需要对线程进行同步*/
    private static ThreadLocal<Session> sessions = new ThreadLocal<>();

    static {
        try {
            // 读取配置文件
            Configuration cfg = new Configuration().configure("hibernate.cfg.xml");
            // 获取会话工厂
            sessionFactory = cfg.buildSessionFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取一个会话
     * @return 会话
     */
    public static Session getSession() {
        Session session = sessions.get();
        if (null == session || !session.isOpen()) {
            session = sessionFactory.openSession();
            sessions.set(session);
        }
        return session;
    }

    /**
     * 关闭会话
     */
    public static void closeSession() {
        Session session = sessions.get();
        if (null != session) {
            session.close();
        }
        sessions.remove();
    }

    /**
     * 删除对象
     * @param obj 对象
     * @return true：成功；false：失败
     */
    public static boolean delete(Object obj) {
        Transaction ts = null;
        boolean rs = false;
        try (Session session = getSession()) {
            ts = session.beginTransaction();
            session.delete(obj);
            ts.commit();
            rs = true;
        } catch (Exception e) {
            if (null != ts) {
                ts.rollback();
            }
        }
        return rs;
    }

    /**
     * 更新对象
     * @param obj 对象
     * @return true：成功；false：失败
     */
    public static boolean update(Object obj) {
        Transaction ts = null;
        boolean rs = false;
        try (Session session = getSession()) {
            ts = session.beginTransaction();
            session.update(obj);
            ts.commit();
            rs = true;
        } catch (Exception e) {
            log.error("更新数据出错：{}", e.getMessage(), e);
            if (null != ts) {
                ts.rollback();
            }
        }
        return rs;
    }

    public static boolean add(Object obj) {
        Transaction ts = null;
        boolean rs = false;
        try (Session session = getSession()) {
            System.out.println("当前会话状态：" + session.isOpen());
            ts = session.beginTransaction();
            session.save(obj);
            ts.commit();
            rs = true;
        } catch (Exception e) {
            if (null != ts) {
                ts.rollback();
            }
            e.printStackTrace();
        }
        return rs;
    }

    /**
     * 执行HQL语句，返回单条记录
     * @param clazz 返回结果对象类型
     * @param hql HQL语句
     * @param params 参数
     * @param <T> 结果对象类型
     * @return 对象
     */
    public static<T> T queryOne(Class<T> clazz, String hql, Object[] params) {
        T obj = null;
        try (Session session = getSession()) {
            Query<T> query = session.createQuery(hql, clazz);
            if (null != params) {
                for (int i = 0; i < params.length; i++) {
                    query.setParameter(i, params[i]);
                }
            }
            obj = query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * 执行HQL语句，返回集合记录
     * @param clazz 返回结果对象类型
     * @param hql HQL语句
     * @param params 参数
     * @param <T> 结果对象类型
     * @return 结果对象集合
     */
    public static<T> List<T> queryList(Class<T> clazz, String hql, Object[] params) {
        List<T> list;
        try (Session session = getSession()) {
            Query<T> query = session.createQuery(hql, clazz);
            if (null != params) {
                for (int i = 0; i < params.length; i++) {
                    query.setParameter(i, params[i]);
                }
            }
            list = query.list();
        }
        return list;
    }
}
