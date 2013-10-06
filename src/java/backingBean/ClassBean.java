/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Nameofunit;
import entities.User;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import tools.ForCallBean;
import tools.SQLTool;

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
    private String schoolName;
    private String pinyin;
    private boolean readflag;
    private LinkedHashMap<String, String> schoolMap;
    private LinkedHashMap<String, String> classMap;

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
        this.classMap.clear();
    }

    /**
     * @return the schoolName
     */
    public String getSchoolName() {
        return schoolName;
    }

    /**
     * @param schoolName the schoolName to set
     */
    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
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
     * @return the readflag
     */
    public boolean isReadflag() {
        return readflag;
    }

    /**
     * @param readflag the readflag to set
     */
    public void setReadflag(boolean readflag) {
        this.readflag = readflag;
    }

    /**
     * @return the schoolMap
     */
    public LinkedHashMap<String, String> getSchoolMap() {
        if (null == this.schoolMap || this.schoolMap.isEmpty()) {
            this.schoolMap = new LinkedHashMap();
        }
        List<Nameofunit> unit = nameDao.getBeanListHandlerRunner("select * from nameofunit where parentid='000' and id!='000' order by id", nameofunit);
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
     * @return the classMap
     */
    public LinkedHashMap<String, String> getClassMap() {
        if (null == this.classMap || this.classMap.isEmpty()) {
            this.classMap = new LinkedHashMap();
        }
        List<Nameofunit> unit = nameDao.getBeanListHandlerRunner("select * from nameofunit where parentid='" + schoolId + "' order by name", nameofunit);
        for (int i = 0; i < unit.size(); i++) {
            Nameofunit tempP = unit.get(i);
            this.classMap.put(tempP.getName(), tempP.getId());
        }
        return classMap;
    }

    /**
     * @param classMap the classMap to set
     */
    public void setClassMap(LinkedHashMap<String, String> classMap) {
        this.classMap = classMap;
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
}
