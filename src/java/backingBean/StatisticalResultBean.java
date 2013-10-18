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
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import tools.PublicFields;
import tools.SQLTool;
import tools.StaticFields;

/**
 *
 * @author Administrator
 */
@Named
@SessionScoped
public class StatisticalResultBean implements Serializable {

    @Inject
    private CheckLogin checkLogin;
    private SQLTool<Checkrecords> checkDao;
    private SQLTool<User> userDao;
    private SQLTool<Stuentrel> stuentrelDao;
    private LinkedHashMap<String, String> teacherMap;
    private ArrayList<Checkrecords> checkList;
    private String[] rankString;
    private String teacherNo;
    private Checkrecords checkRecord;
    private Stuentrel stuentrel;

    @PostConstruct
    public void init() {
        checkDao = new SQLTool<Checkrecords>();
        userDao = new SQLTool<User>();
        stuentrelDao = new SQLTool<Stuentrel>();
        rankString = new String[]{"优秀", "良好", "及格", "不及格"};
        checkRecord = new Checkrecords();
        stuentrel = new Stuentrel();
    }

    public LinkedHashMap<String, String> getTeacherMap() {
        if (null == teacherMap || teacherMap.isEmpty()) {
            teacherMap = new LinkedHashMap<String, String>();
            switch (this.checkLogin.getUser().getRoleinfo().getCanseeall()) {
                case 0: {
                    List<User> userList = userDao.getBeanListHandlerRunner("select * from teacherinfo" + StaticFields.currentGradeNum, new User());
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
            if (!teacherNo.equals("unRecord")) {//未检查的学生
                if (null == checkList || checkList.isEmpty()) {
                    checkList = new ArrayList<Checkrecords>();
                    List<Checkrecords> tempList = checkDao.getBeanListHandlerRunner("select * from checkrecords" + StaticFields.currentGradeNum + this.checkLogin.getUser().getSchoolId() + " where teachNo = '" + teacherNo + "'", checkRecord);
                    for (Iterator<Checkrecords> it = tempList.iterator(); it.hasNext();) {
                        Checkrecords Checkrecord = it.next();
                        checkList.add(Checkrecord);
                    }
                } else {
                    checkList.clear();
                    List<Checkrecords> tempList = checkDao.getBeanListHandlerRunner("select * from checkrecords" + StaticFields.currentGradeNum + this.checkLogin.getUser().getSchoolId() + " where teachNo = '" + teacherNo + "'", checkRecord);
                    for (Iterator<Checkrecords> it = tempList.iterator(); it.hasNext();) {
                        Checkrecords Checkrecord = it.next();
                        checkList.add(Checkrecord);
                    }
                }
                for (Checkrecords s : checkList) {
                    s.setSchoolId(this.checkLogin.getUser().getSchoolId());
                }
            } else {
                if (this.checkLogin.getUser().getRoleid() == PublicFields.getStudentRole()) {
                    return null;
                } else {//=============
                    if (null == checkList || checkList.isEmpty()) {
                        checkList = new ArrayList<Checkrecords>();
                    } else {
                        checkList.clear();
                    }
                    List<Stuentrel> stuList = stuentrelDao.getBeanListHandlerRunner("select * from stuentrel" + StaticFields.currentGradeNum + this.checkLogin.getUser().getSchoolId() + " where  stuno not in ( select stuno from checkrecords" + StaticFields.currentGradeNum + this.checkLogin.getUser().getSchoolId() + ")", stuentrel);
                    for (Iterator<Stuentrel> it = stuList.iterator(); it.hasNext();) {
                        Stuentrel tempuser = it.next();
                        Checkrecords c = new Checkrecords();
                        c.setStuno(tempuser.getStuno());
                        checkList.add(c);
                    }
                }//============
            }
            for (Checkrecords s : checkList) {
                s.setSchoolId(this.checkLogin.getUser().getSchoolId());
            }
            return checkList;
        } else {
            if (null == checkList || checkList.isEmpty()) {
                checkList = new ArrayList<Checkrecords>();
                List<Checkrecords> tempList = checkDao.getBeanListHandlerRunner("select * from checkrecords" + StaticFields.currentGradeNum + this.checkLogin.getUser().getSchoolId() + " where stuno = '" + this.checkLogin.getUser().getUno() + "'", checkRecord);
                for (Iterator<Checkrecords> it = tempList.iterator(); it.hasNext();) {
                    Checkrecords Checkrecord = it.next();
                    checkList.add(Checkrecord);
                }
            } else {
                checkList.clear();
                List<Checkrecords> tempList = checkDao.getBeanListHandlerRunner("select * from checkrecords" + StaticFields.currentGradeNum + this.checkLogin.getUser().getSchoolId() + " where stuno = '" + this.checkLogin.getUser().getUno() + "'", checkRecord);
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
