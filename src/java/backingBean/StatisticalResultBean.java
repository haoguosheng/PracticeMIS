/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Checkrecords;
import entities.User;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import tools.ForCallBean;
import tools.SQLTool;

/**
 *
 * @author Administrator
 */
@ManagedBean
@SessionScoped
public class StatisticalResultBean implements Serializable {

    private SQLTool<Checkrecords> checkDao = new SQLTool<Checkrecords>();
    private SQLTool<User> userDao = new SQLTool<User>();
    private LinkedHashMap<String, Integer> teacherMap;
    private ArrayList<Checkrecords> checkList;
    private String[] rankString = new String[]{"优秀", "良好", "及格", "不及格"};
    private String teacherNo;
    private User loginUser;
    private Checkrecords checkRecord=new Checkrecords();

    public LinkedHashMap<String, Integer> getTeacherMap() {
        if (null == teacherMap) {
            teacherMap = new LinkedHashMap<String, Integer>();
            switch (this.getLoginUser().getRoleinfo().getCanseeall()) {
                case 0: {
                    List<User> userList = userDao.getBeanListHandlerRunner("select * from teacherinfo where roleid != 2", new User());
                    for (Iterator<User> it = userList.iterator(); it.hasNext();) {
                        User tempUser = it.next();
                        teacherMap.put(tempUser.getName(), Integer.valueOf(tempUser.getUno()));
                    }
                }
                break;
                case 1: {
                    teacherMap.put(this.loginUser.getName(),Integer.valueOf(this.loginUser.getUno()));
                }
                break;
            }
        }
        return teacherMap;
    }

    public void setUnList() {
    }

    /**
     * @return the checkList
     */
    public ArrayList<Checkrecords> getCheckList() {
        User myUser = this.getLoginUser();
        if (teacherNo != null) {
            if (!teacherNo.equals("unRecord")) {
                if (null == checkList) {
                    checkList = new ArrayList<Checkrecords>();
                    List<Checkrecords> tempList = checkDao.getBeanListHandlerRunner("select * from checkrecords" + myUser.getSchoolId() + " where teacherNo = '" + teacherNo + "'", checkRecord);
                    for (Iterator<Checkrecords> it = tempList.iterator(); it.hasNext();) {
                        Checkrecords Checkrecord = it.next();
                        checkList.add(Checkrecord);
                    }
                } else {
                    checkList.clear();
                    List<Checkrecords> tempList = checkDao.getBeanListHandlerRunner("select * from checkrecords" + myUser.getSchoolId() + " where teacherNo = '" + teacherNo + "'",checkRecord);
                    for (Iterator<Checkrecords> it = tempList.iterator(); it.hasNext();) {
                        Checkrecords Checkrecord = it.next();
                        checkList.add(Checkrecord);
                    }
                }
            } else {
                if (null == checkList) {
                    checkList = new ArrayList<Checkrecords>();
                    List<User> stuList = userDao.getBeanListHandlerRunner("select * from student" + myUser.getSchoolId() + " where roleid = 2 and nameofunitid!=15 and userno not in ( select stuno from checkrecords" + myUser.getSchoolId() + ")", this.getLoginUser());
                    for (Iterator<User> it = stuList.iterator(); it.hasNext();) {
                        User tempuser = it.next();
                        Checkrecords c = new Checkrecords();
                        c.setStuno(tempuser.getUno());
                        checkList.add(c);
                    }
                } else {
                    checkList.clear();
                    List<User> stuList = userDao.getBeanListHandlerRunner("select * from student" + myUser.getSchoolId() + " where roleid = 2 and nameofunitid!=15 and userno not in ( select stuno from checkrecords" + myUser.getSchoolId() + ")", this.getLoginUser());
                    for (Iterator<User> it = stuList.iterator(); it.hasNext();) {
                        User tempuser = it.next();
                        Checkrecords c = new Checkrecords();
                        c.setStuno(tempuser.getUno());
                        checkList.add(c);
                    }
                }
            }
            return checkList;
        }
        return null;
    }

    /**
     * @param checkList the checkList to set
     */
    public void setCheckList(ArrayList<Checkrecords> checkList) {
        this.checkList = checkList;
    }

    /**
     * @return the teacherNo
     */
    public String getTeacherNo() {
        return teacherNo;
    }

    /**
     * @param teacherNo the teacherNo to set
     */
    public void setTeacherNo(String teacherNo) {
        this.teacherNo = teacherNo;
    }

    /**
     * @return the rankString
     */
    public String[] getRankString() {
        return rankString;
    }

    /**
     * @param rankString the rankString to set
     */
    public void setRankString(String[] rankString) {
        this.rankString = rankString;
    }

    /**
     * @return the loginUser
     */
    public User getLoginUser() {
        if (null == this.loginUser) {
            this.loginUser = new ForCallBean().getUser();
        }
        return loginUser;
    }
}
