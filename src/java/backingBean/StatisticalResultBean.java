/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Checkrecords;
import entities.Stuentrel;
import entities.User;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import tools.ForCallBean;
import tools.SQLTool;
import tools.StaticFields;

/**
 *
 * @author Administrator
 */
@ManagedBean
@SessionScoped
public class StatisticalResultBean implements Serializable {
  @ManagedProperty(value = "#{checkLogin}")
    private CheckLogin checkLogin;
    private SQLTool<Checkrecords> checkDao = new SQLTool<Checkrecords>();
    private SQLTool<User> userDao = new SQLTool<User>();
    private SQLTool<Stuentrel> stuentrelDao = new SQLTool<Stuentrel>();
    private LinkedHashMap<String, String> teacherMap;
    private ArrayList<Checkrecords> checkList;
    private String[] rankString = new String[]{"优秀", "良好", "及格", "不及格"};
    private String teacherNo;
    private Checkrecords checkRecord = new Checkrecords();
    private Stuentrel stuentrel = new Stuentrel();

    public LinkedHashMap<String, String> getTeacherMap() {
        if (null == teacherMap) {
            teacherMap = new LinkedHashMap<String, String>();
            switch (this.checkLogin.getUser().getRoleinfo().getCanseeall()) {
                case 0: {
                    List<User> userList = userDao.getBeanListHandlerRunner("select * from teacherinfo"+StaticFields.currentGradeNum+" where roleid != 2", new User());
                    for (Iterator<User> it = userList.iterator(); it.hasNext();) {
                        User tempUser = it.next();
                        teacherMap.put(tempUser.getName(), tempUser.getUno());
                    }
                }
                break;
                case 1: {
                    teacherMap.put(this.checkLogin.getUser().getName(), this.checkLogin.getUser().getUno());
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
        if (teacherNo != null) {
            if (!teacherNo.equals("unRecord")) {
                if (null == checkList) {
                    checkList = new ArrayList<Checkrecords>();
                    List<Checkrecords> tempList = checkDao.getBeanListHandlerRunner("select * from checkrecords" +StaticFields.currentGradeNum+ this.checkLogin.getUser().getSchoolId() + " where teachNo = '" + teacherNo + "'", checkRecord);
                    for (Iterator<Checkrecords> it = tempList.iterator(); it.hasNext();) {
                        Checkrecords Checkrecord = it.next();
                        checkList.add(Checkrecord);
                    }
                } else {
                    checkList.clear();
                    List<Checkrecords> tempList = checkDao.getBeanListHandlerRunner("select * from checkrecords" +StaticFields.currentGradeNum+ this.checkLogin.getUser().getSchoolId() + " where teachNo = '" + teacherNo + "'", checkRecord);
                    for (Iterator<Checkrecords> it = tempList.iterator(); it.hasNext();) {
                        Checkrecords Checkrecord = it.next();
                        checkList.add(Checkrecord);
                    }
                }
                for (Checkrecords s : checkList) {
                    s.setSchoolId(this.checkLogin.getUser().getSchoolId());
                }
           } 
            else {
                if (this.checkLogin.getUser().getRoleid() == 1)
                    return null;
                else{
                if (null == checkList) {
                    checkList = new ArrayList<Checkrecords>();
                    List<Stuentrel> stuList = stuentrelDao.getBeanListHandlerRunner("select * from stuentrel" +StaticFields.currentGradeNum+ this.checkLogin.getUser().getSchoolId() + " where  stuno not in ( select stuno from checkrecords" +StaticFields.currentGradeNum+this.checkLogin.getUser().getSchoolId() + ")", stuentrel);
                    for (Iterator<Stuentrel> it = stuList.iterator(); it.hasNext();) {
                        Stuentrel tempuser = it.next();
                        Checkrecords c = new Checkrecords();
                        c.setStuno(tempuser.getStuno());
                        checkList.add(c);
                    }
                } else {
                    checkList.clear();
                    List<Stuentrel> stuList = stuentrelDao.getBeanListHandlerRunner("select * from stuentrel" +StaticFields.currentGradeNum+ this.checkLogin.getUser().getSchoolId() + " where  stuno not in ( select stuno from checkrecords" +StaticFields.currentGradeNum+ this.checkLogin.getUser().getSchoolId() + ")", stuentrel);
                    for (Iterator<Stuentrel> it = stuList.iterator(); it.hasNext();) {
                        Stuentrel tempuser = it.next();
                        Checkrecords c = new Checkrecords();
                        c.setStuno(tempuser.getStuno());
                        checkList.add(c);
                    }
                }
            }
        }
            for (Checkrecords s : checkList) {
                s.setSchoolId(this.checkLogin.getUser().getSchoolId());
            }
            return checkList;
        } else {
            if (null == checkList) {
                checkList = new ArrayList<Checkrecords>();
                List<Checkrecords> tempList = checkDao.getBeanListHandlerRunner("select * from checkrecords" +StaticFields.currentGradeNum+ this.checkLogin.getUser().getSchoolId() + " where stuno = '" + this.checkLogin.getUser().getUno() + "'", checkRecord);
                for (Iterator<Checkrecords> it = tempList.iterator(); it.hasNext();) {
                    Checkrecords Checkrecord = it.next();
                    checkList.add(Checkrecord);
                }
            } else {
                checkList.clear();
                List<Checkrecords> tempList = checkDao.getBeanListHandlerRunner("select * from checkrecords" +StaticFields.currentGradeNum+ this.checkLogin.getUser().getSchoolId() + " where stuno = '" + this.checkLogin.getUser().getUno() + "'", checkRecord);
                for (Iterator<Checkrecords> it = tempList.iterator(); it.hasNext();) {
                    Checkrecords Checkrecord = it.next();
                    checkList.add(Checkrecord);
                }
                for (Checkrecords s : checkList) {
                    s.setSchoolId(this.checkLogin.getUser().getSchoolId());
                }
            }
            return checkList;
        }
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
     * @return the checkLogin
     */
    public CheckLogin getCheckLogin() {
        return checkLogin;
    }

    /**
     * @param checkLogin the checkLogin to set
     */
    public void setCheckLogin(CheckLogin checkLogin) {
        this.checkLogin = checkLogin;
    }
}
