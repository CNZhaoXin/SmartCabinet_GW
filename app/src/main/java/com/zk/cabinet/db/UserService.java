package com.zk.cabinet.db;

import com.zk.cabinet.bean.User;
import com.zk.cabinet.dao.UserDao;
import com.zk.common.utils.LogUtil;

import java.util.List;

public class UserService extends BaseService<User, Long> {
    public static final int ONE_DAY = 24 * 60 * 60 * 1000;
    private static volatile UserService instance;//单例

    private UserService() {
    }

    public static UserService getInstance() {
        if (instance == null) {
            synchronized (UserService.class) {//保证异步处理安全操作
                if (instance == null) {
                    instance = new UserService();
                }
            }
        }
        return instance;
    }

    public User queryByUserId(Long userId) {

        if (userId == null) return null;

        List<User> list = query(UserDao.Properties.Id.eq(userId));
        User user = null;
        if (list != null && list.size() > 0) {
            user = list.get(0);
        }
        return user;
    }

    public User queryByWebId(Long webId) {

        if (webId == null) return null;

        List<User> list = query(UserDao.Properties.WebId.eq(webId));
        User user = null;
        if (list != null && list.size() > 0) {
            user = list.get(0);
        }
        return user;
    }

    public List<User> queryByUserCodeList(String userCode) {
        if (userCode == null) return null;
        return query(UserDao.Properties.UserCode.eq(userCode));
    }

    public User queryByUserCode(String userCode) {

        if (userCode == null) return null;

        List<User> list = query(UserDao.Properties.UserCode.eq(userCode));
        User user = null;
        if (list != null && list.size() > 0) {
            user = list.get(0);
        }
        return user;
    }

    public User queryByPwd(String userCode, String pwd) {

        if (userCode == null || pwd == null) return null;
        List<User> list = query(UserDao.Properties.UserCode.eq(userCode),
                UserDao.Properties.Password.eq(pwd));
        User user = null;
        if (list != null && list.size() > 0) {
            user = list.get(0);
        }
        return user;
    }

    public void insertOrUpdate(final List<User> userList) {
        if (getDao() == null || getDaoSession() == null)
            LogUtil.Companion.getInstance().d(TAG, CHECK_INIT);
        else {
            getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    User UserTemp;
                    for (User user : userList) {
                        try {
                            getDao().insert(user);
                        } catch (Exception e) {
                            e.printStackTrace();
                            UserTemp = queryByWebId(user.getWebId());
                            if (UserTemp != null) {

                                UserTemp.setUserCode(user.getUserCode());
                                UserTemp.setUserName(user.getUserName());
                                UserTemp.setCabinet(user.getCabinet());
                                UserTemp.setUserType(user.getUserType());
                                UserTemp.setPassword(user.getPassword());
                                UserTemp.setCardID(user.getCardID());
                                UserTemp.setFingerPrint(user.getFingerPrint());
                                UserTemp.setFaceInfo(user.getFaceInfo());
                                UserTemp.setModifyTime(user.getModifyTime());

                                update(UserTemp);
                            }
                        }
                    }
                }
            });
        }
    }
}
