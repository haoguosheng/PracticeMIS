/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Nameofunit;
import entities.User;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import tools.SQLTool;
import tools.StaticFields;

/**
 *
 * @author Idea
 */
@Named
@SessionScoped
public class NameofUnitBean implements java.io.Serializable {

    @Inject
    private CheckLogin checkLogin;
    private LinkedHashMap<String, String> classNameMap, schoolMap;
    private List<Nameofunit> classList, schoolList;
    private SQLTool<Nameofunit> nameDAO;
    private Nameofunit unit;
    private String schoolId, classId;
    private int canSeeAll;

    /**
     * @return the classNameMap
     */
    @PostConstruct
    public void init() {
        nameDAO = new SQLTool<Nameofunit>();
        unit = new Nameofunit();
    }

    public LinkedHashMap<String, String> getClassNameMap() {
        if (schoolId != null) {
            classNameMap = new LinkedHashMap();
            Iterator<Nameofunit> it = this.getClassList().iterator();
            while (it.hasNext()) {
                Nameofunit temunit = it.next();
                classNameMap.put(temunit.getName(), temunit.getId());
            }
            return classNameMap;
        } else {
            return null;
        }

    }

    public List<Nameofunit> getClassList() {
        if (null != this.getSchoolId()) {
            classList = nameDAO.getBeanListHandlerRunner("select * from nameofunit" + StaticFields.currentGradeNum + " where parentid ='" + this.getSchoolId() + "' order by pinyin", unit);
        } else {
            classList = null;
        }
        return classList;
    }

    public List<Nameofunit> getSchoolList() {
        User temUser=this.getCheckLogin().getUser();
        if (null == schoolList || schoolList.isEmpty()) {
            switch (temUser.getRoleinfo().getCanseeall()) {
                case StaticFields.CanSeeAll:
                    schoolList = nameDAO.getBeanListHandlerRunner("select * from nameofunit" + StaticFields.currentGradeNum + " where  parentid='" + StaticFields.universityId + "' and id!='000' order by pinyin", unit);
                    break;
                case StaticFields.CanSeeOnlySchool:
                    schoolList = nameDAO.getBeanListHandlerRunner("select * from nameofunit" + StaticFields.currentGradeNum + " where  id='" + temUser.getNameofunitid() + "' order by pinyin", unit);
                    break;
                case StaticFields.CanSeeSelf:
                    schoolList = null;
                    break;
                case StaticFields.CanSeeNothing:
                    schoolList=null;
                default:
                    schoolList = null;
            }

        }
        return schoolList;
    }

    /**
     * @return the schoolId
     */
    public String getSchoolId() {
        User temUser=this.getCheckLogin().getUser();
        if(this.canSeeAll!=StaticFields.CanSeeNothing){//可以确定一定是教师，下面拿到的一定是学院的编号
            this.schoolId=temUser.getNameofunitid();
        }
        return schoolId;
    }

    /**
     * @param schoolId the schoolId to set
     */
    public void setSchoolId(String schoolId1) {
        this.classList = null;
        if (null != schoolId1) {
            this.schoolId = (schoolId1.length() == 2) ? ("0" + schoolId1) : schoolId1;
        }
    }

    /**
     * @return the classId
     */
    public String getClassId() {
        return classId;
    }

    /**
     * @param classId the classId to set
     */
    public void setClassId(String classId) {
        this.classId = classId;
    }

    /**
     * @return the schoolMap
     */
    public LinkedHashMap<String, String> getSchoolMap() {
        if (null == schoolMap || this.schoolMap.isEmpty()) {
            this.schoolMap = new LinkedHashMap<String, String>();
            Iterator<Nameofunit> it = this.getSchoolList().iterator();
            while (it.hasNext()) {
                Nameofunit temunit = it.next();
                schoolMap.put(temunit.getName(), temunit.getId());
            }
        }
        return schoolMap;
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

    /**
     * @return the canSeeAll
     */
    public int getCanSeeAll() {
        User temUser=this.getCheckLogin().getUser();
        this.canSeeAll=temUser.getRoleinfo().getCanseeall();
        return canSeeAll;
    }

    /**
     * @param canSeeAll the canSeeAll to set
     */
    public void setCanSeeAll(int canSeeAll) {
        this.canSeeAll = canSeeAll;
    }
}
