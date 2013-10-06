/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Nameofunit;
import entities.User;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import tools.ForCallBean;
import tools.SQLTool;
import tools.StaticFields;

/**
 *
 * @author myPC
 */
@ManagedBean
@SessionScoped
public class ClassBean implements Serializable {

    private SQLTool<Nameofunit> nameDao = new SQLTool<Nameofunit>();
    private Nameofunit nameofunit = new Nameofunit();
    private User loginUser = new ForCallBean().getUser();
    private String schoolId, classId;
    private String pinyin;
    private String newClass;
    private LinkedHashMap<String, String> schoolMap;
    private List<Nameofunit> classList;

    public String deleteClass() {
        FacesContext context = FacesContext.getCurrentInstance();
        String s = context.getExternalContext().getRequestParameterMap().get("cId");
        nameDao.executUpdate("delete from nameofunit where id='" + s + "'");
        classList = nameDao.getBeanListHandlerRunner("select * from nameofunit where parentid='" + StaticFields.currentGradeNum + schoolId + "' order by name", nameofunit);
        return null;
    }

    public String addClass() {
        if (nameDao.getBeanListHandlerRunner("select * from nameofunit where name='" + newClass + "'", nameofunit).size() > 0) {
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("该班级已经存在，请重新添加！"));
        } else if (schoolId.length() == 3) {
            if (nameDao.executUpdate("insert into nameofunit(id, name, parentid, pinyin, userno) values('" + classId + "', '" + newClass + "', '" + schoolId + "', '" + pinyin + "','" + loginUser.getUno() + "')") > 0) {
                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("添加新班级成功！"));
                classId="";
                newClass="";
                pinyin="";
            } else {
                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("添加新班级失败，请重新添加！"));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("添加新班级失败，请重新添加！"));
        }
        return null;
    }

    /**
     * @return the nameofunit
     */
    public Nameofunit getNameofunit() {
        return nameofunit;
    }

    /**
     * @param nameofunit the nameofunit to set
     */
    public void setNameofunit(Nameofunit nameofunit) {
        this.nameofunit = nameofunit;
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
    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
        this.getClassList().clear();
    }

    /**
     * @return the pinyin
     */
    public String getPinyin() {
        return pinyin;
    }

    /**
     * @param pinyin the pinyin to set
     */
    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    /**
     * @return the schoolMap
     */
    public LinkedHashMap<String, String> getSchoolMap() {
        if (null == this.schoolMap || this.schoolMap.isEmpty()) {
            this.schoolMap = new LinkedHashMap();
        }
        List<Nameofunit> unit = nameDao.getBeanListHandlerRunner("select * from nameofunit where parentid='000' and id!='000' order by name", nameofunit);
        for (int i = 0; i < unit.size(); i++) {
            Nameofunit tempP = unit.get(i);
            this.schoolMap.put(tempP.getName(), tempP.getId());
        }
        return schoolMap;
    }

    /**
     * @param schoolMap the schoolMap to set
     */
    public void setSchoolMap(LinkedHashMap<String, String> schoolMap) {
        this.schoolMap = schoolMap;
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
     * @return the classList
     */
    public List<Nameofunit> getClassList() {
        if (null == this.classList || this.classList.isEmpty()) {
            this.classList = new LinkedList<>();
        }
        classList = nameDao.getBeanListHandlerRunner("select * from nameofunit where parentid='" + StaticFields.currentGradeNum + schoolId + "' order by name", nameofunit);
        return classList;
    }

    /**
     * @param classList the classList to set
     */
    public void setClassList(List<Nameofunit> classList) {
        this.classList = classList;
    }

    /**
     * @return the newClass
     */
    public String getNewClass() {
        return newClass;
    }

    /**
     * @param newClass the newClass to set
     */
    public void setNewClass(String newClass) {
        this.newClass = newClass;
    }
}
