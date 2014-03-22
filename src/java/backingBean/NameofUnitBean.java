/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.MyUser;
import entities.Nameofunit;
import entities.Student;
import entities.Teacherinfo;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import sessionBeans.NameofunitFacadeLocal;
import tools.PublicFields;
import tools.StaticFields;

/**
 *
 * @author Idea
 */
@Named
@SessionScoped
public class NameofUnitBean implements java.io.Serializable {

    private Nameofunit nameofunit;
    private Nameofunit temUnit;
    private LinkedHashMap<String, String> classNameMap, schoolMap;
    private List<Nameofunit> classList, schoolList;
    @EJB
    private NameofunitFacadeLocal unitEjb;

    private String schoolId, userSchoolId, classId;
    private int canSeeAll;
    @Inject
    PublicFields publicFields;
    MyUser user;
    HttpSession mySession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);

    @PostConstruct
    public void init() {
        mySession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        int length = (int) (mySession.getAttribute("userNoLength"));
        if (length == StaticFields.teacherUnoLength) {
            user = (Teacherinfo) mySession.getAttribute("teacherUser");

        } else if (length == StaticFields.stuUnoLength1 || length == StaticFields.stuUnoLength2) {
            user = (Student) mySession.getAttribute("studentUser");
        }
    }

    public LinkedHashMap<String, String> getClassNameMap() {
        if (null != this.getSchoolId()) {
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

    public String save(Nameofunit unitPara) {
        unitEjb.edit(unitPara);
        this.classList = null;
        return null;
    }

    public List<Nameofunit> getClassList() {
        if (null != this.getSchoolId()) {
            if (classList != null) {
                classList.clear();
            }
            classList = unitEjb.getList("select * from nameofunit  where parentid ='" + this.getSchoolId() + "' order by pinyin");
        } else {
            classList = null;
        }
        return classList;
    }

    public List<Nameofunit> getSchoolList() {
        if (null == schoolList || schoolList.isEmpty()) {
            switch (this.user.getRoleinfo().getCanseeall()) {
                case StaticFields.CanSeeAll:
                    schoolList = unitEjb.getList("select * from nameofunit  where  parentid='" + StaticFields.universityId + "' and id!='000' order by pinyin");
                    break;
                case StaticFields.CanSeeOnlySchool://确定是教师
                    schoolList = unitEjb.getList("select * from nameofunit  where  id='" + ((Teacherinfo)user).getNameofunit().getId() + "' order by pinyin");
                    break;
                case StaticFields.CanSeeSelf:
                    schoolList = null;
                    break;
                case StaticFields.CanSeeNothing:
                    schoolList = null;
                default:
                    schoolList = null;
            }

        }
        return schoolList;
    }

    public String getSchoolNameById(String schoolId) {
        return unitEjb.find(schoolId).getName();
    }

    public String getSchoolId() {
        schoolId = user.getNameofunit().getId();
        return schoolId;
    }

    public void setSchoolId(String schoolId1) {
        if (null != schoolId1) {
            this.schoolId = (schoolId1.length() == 2) ? ("0" + schoolId1) : schoolId1;
        }
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }


    public String getUserSchoolId() {
        if (this.canSeeAll != StaticFields.CanSeeNothing) {//可以确定一定是教师，下面拿到的一定是学院的编号
            this.userSchoolId =this.user.getNameofunit().getId();
        }
        return userSchoolId;
    }

    public void setUserSchoolId(String userSchoolId) {
        this.userSchoolId = userSchoolId;
    }

    public String deleteClass(Nameofunit unitPara) {
        try {
            unitEjb.remove(unitPara);
            classList = null;
        } catch (Exception e) {
        }
        return "viewClasses.xhtml";
    }

    public String addClass() {
        if (unitEjb.getList("select * from nameofunit where name='" + temUnit.getName() + "' or id='" + temUnit.getId() + "' order by pinyin").size() > 0) {
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("添加新班级失败！班级已存在！"));
        } else {
            if (!temUnit.getNameofunit().getId().equals("0")) {
                this.temUnit.setTeacherinfo((Teacherinfo)this.user);
                unitEjb.create(temUnit);
                this.classList = null;
            } else {
                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("添加失败，请选择你要添加班级所在学院！"));
            }
        }
        return "viewClasses.xhtml";
    }

    public Nameofunit getNameofunit() {
        return nameofunit;
    }

    public void setNameofunit(Nameofunit nameofunit) {
        this.nameofunit = nameofunit;
    }

    public LinkedHashMap<String, String> getSchoolMap() {
        if (null == this.schoolMap || this.schoolMap.isEmpty()) {
            this.schoolMap = new LinkedHashMap();
        }
        switch (this.user.getRoleinfo().getCanseeall()) {
            case StaticFields.CanSeeAll: {
                List<Nameofunit> unit = publicFields.getSchoolUnitList();
                for (int i = 0; i < unit.size(); i++) {
                    Nameofunit tempP = unit.get(i);
                    this.schoolMap.put(tempP.getName(), tempP.getId());
                }
            }
            break;
            case StaticFields.CanSeeNothing: {
            }
            break;
            case StaticFields.CanSeeOnlySchool: {
                List<Nameofunit> unit = unitEjb.getList("select * from nameofunit where parentid='" + StaticFields.universityId + "' and id='" + this.user.getNameofunit().getId() + "' order by pinyin");
                for (int i = 0; i < unit.size(); i++) {
                    Nameofunit tempP = unit.get(i);
                    this.schoolMap.put(tempP.getName(), tempP.getId());
                }
            }
            break;
            case StaticFields.CanSeeSelf: {
                List<Nameofunit> unit = unitEjb.getList("select * from nameofunit where parentid='" + StaticFields.universityId + "' and id='" + this.user.getNameofunit().getId() + "' order by pinyin");
                for (int i = 0; i < unit.size(); i++) {
                    Nameofunit tempP = unit.get(i);
                    this.schoolMap.put(tempP.getName(), tempP.getId());
                }
            }
            break;
        }
        return schoolMap;
    }

    public void setSchoolMap(LinkedHashMap<String, String> schoolMap) {
        this.schoolMap = schoolMap;
    }

    public void setClassList(List<Nameofunit> classList) {
        this.classList = classList;
    }

    public Nameofunit getTemUnit() {
        if (null == temUnit) {
            temUnit = new Nameofunit();
        }
        return temUnit;
    }

    public void setTemUnit(Nameofunit temUnit) {
        this.temUnit = temUnit;
    }

    /**
     * @return the tempClassList
     */
    public List<Nameofunit> getTempClassList() {
        if (null == classList || classList.isEmpty()) {
            classList = unitEjb.getList("select * from nameofunit  where parentid ='" + temUnit.getNameofunit().getId() + "' order by pinyin");
        }
        return classList;
    }
}
