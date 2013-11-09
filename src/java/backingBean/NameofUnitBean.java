/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Nameofunit;
import entities.User;
import entitiesBeans.NameofunitLocal;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import tools.PublicFields;
import tools.StaticFields;

/**
 *
 * @author Idea
 */
@Named
@RequestScoped
public class NameofUnitBean implements java.io.Serializable {

    @Inject
    private Nameofunit nameofunit;
    private @Inject
    User user;
    private Nameofunit temUnit;
    private LinkedHashMap<String, String> classNameMap, schoolMap;
    private List<Nameofunit> classList, schoolList;
    private final NameofunitLocal nameDAO = new NameofunitLocal();

    private String schoolId, userSchoolId, classId;
    private int canSeeAll;

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
        nameDAO.edit(unitPara);
        this.classList = null;
        return null;
    }

    public List<Nameofunit> getClassList() {
        if (null != this.getSchoolId() && (null == classList || classList.isEmpty())) {
            classList = nameDAO.getList("select * from nameofunit" + StaticFields.currentGradeNum + " where parentid ='" + this.getSchoolId() + "' order by pinyin");
        } else {
            classList = null;
        }
        return classList;
    }

    public List<Nameofunit> getSchoolList() {
        if (null == schoolList || schoolList.isEmpty()) {
            switch (getUser().getRoleinfo().getCanseeall()) {
                case StaticFields.CanSeeAll:
                    schoolList = nameDAO.getList("select * from nameofunit" + StaticFields.currentGradeNum + " where  parentid='" + StaticFields.universityId + "' and id!='000' order by pinyin");
                    break;
                case StaticFields.CanSeeOnlySchool:
                    schoolList = nameDAO.getList("select * from nameofunit" + StaticFields.currentGradeNum + " where  id='" + user.getNameofunitid() + "'");
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
        return nameDAO.find(schoolId).getName();
    }

    public String getSchoolId() {
        schoolId = user.getSchoolId();
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

    public User getUser() {
//        FacesContext context = FacesContext.getCurrentInstance();
//        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
//        user = (User) session.getAttribute("myUser");
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUserSchoolId() {
        if (this.canSeeAll != StaticFields.CanSeeNothing) {//可以确定一定是教师，下面拿到的一定是学院的编号
            this.userSchoolId = getUser().getNameofunitid();
        }
        return userSchoolId;
    }

    public void setUserSchoolId(String userSchoolId) {
        this.userSchoolId = userSchoolId;
    }

    public String deleteClass(Nameofunit unitPara) {
        try {
            nameDAO.remove(unitPara);
            classList = null;
        } catch (Exception e) {
        }
        return "viewClasses.xhtml";
    }

    public String addClass() {
        if (nameDAO.getList("select * from nameofunit where name='" + temUnit.getName() + "' or id='" + temUnit.getId() + "'").size() > 0) {
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("添加新班级失败！班级已存在！"));
        } else {
            if (!temUnit.getParentid().equals("0")) {
                this.temUnit.setUserno(getUser().getUno());
                nameDAO.create(temUnit);
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
        switch (getUser().getRoleinfo().getCanseeall()) {
            case StaticFields.CanSeeAll: {
                List<Nameofunit> unit = PublicFields.getSchoolUnitList();
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
                List<Nameofunit> unit = nameDAO.getList("select * from nameofunit where parentid='" + StaticFields.universityId + "' and id='" + this.getUser().getSchoolId() + "' order by pinyin");
                for (int i = 0; i < unit.size(); i++) {
                    Nameofunit tempP = unit.get(i);
                    this.schoolMap.put(tempP.getName(), tempP.getId());
                }
            }
            break;
            case StaticFields.CanSeeSelf: {
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

}
