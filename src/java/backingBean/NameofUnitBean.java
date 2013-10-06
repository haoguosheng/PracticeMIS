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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import tools.ForCallBean;
import tools.SQLTool;
import tools.StaticFields;

/**
 *
 * @author Idea
 */
@ManagedBean
@SessionScoped
public class NameofUnitBean implements java.io.Serializable {

    private LinkedHashMap<String, String> classNameMap, schoolMap;
    private List<Nameofunit> classList, schoolList;
    private SQLTool<Nameofunit> nameDAO = new SQLTool<Nameofunit>();
    private Nameofunit unit = new Nameofunit();
    private String schoolId, classId;

    /**
     * @return the classNameMap
     */
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
        if (null != schoolId) {
            classList = nameDAO.getBeanListHandlerRunner("select * from nameofunit"+StaticFields.currentGradeNum+" where parentid ='" + this.getSchoolId() + "' order by name", unit);
        } else {
            classList = null;
        }
        return classList;
    }

    public List<Nameofunit> getSchoolList() {
        if (null == schoolList) {
            User myUser=new ForCallBean().getUser();
            switch (myUser.getRoleinfo().getCanseeall()) {
                case StaticFields.CanSeeAll:
                     schoolList = nameDAO.getBeanListHandlerRunner("select * from nameofunit"+StaticFields.currentGradeNum+" where  parentid='" + StaticFields.universityId + "' order by pinyin", unit);
                    break;
                case StaticFields.CanSeeOnlySchool:
                    schoolList = nameDAO.getBeanListHandlerRunner("select * from nameofunit"+StaticFields.currentGradeNum+" where  parentid='" + myUser.getNameofunitid() + "' order by pinyin", unit);
                    break;
                case StaticFields.CanSeeSelf:
                    schoolList=null;
                    break;
            }
           
        }
        return schoolList;
    }

    /**
     * @return the schoolId
     */
    public String getSchoolId() {
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
        if (null == schoolMap) {
            this.schoolMap = new LinkedHashMap<String, String>();
            Iterator<Nameofunit> it = this.getSchoolList().iterator();
            while (it.hasNext()) {
                Nameofunit temunit = it.next();
                schoolMap.put(temunit.getName(), temunit.getId());
            }
        }
        return schoolMap;
    }
}
