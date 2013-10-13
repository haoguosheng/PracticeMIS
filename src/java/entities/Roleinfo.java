/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.List;
import tools.SQLTool;
import tools.StaticFields;

/**
 *
 * @author Administrator
 */
public class Roleinfo implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String resouceids;
    private String name;
    private Integer privilege;
    private Integer canseeall;
    private String schoolId;
    private List<User> studentList;
    private List<User> teachertList;
    private SQLTool<User> userDao = new SQLTool<User>();

    public Roleinfo() {
    }

    public Roleinfo(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getResouceids() {
        return resouceids;
    }

    public void setResouceids(String resouceids) {
        this.resouceids = resouceids;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrivilege() {
        return privilege;
    }

    public void setPrivilege(Integer privilege) {
        this.privilege = privilege;
    }

    public Integer getCanseeall() {
        return canseeall;
    }

    public void setCanseeall(Integer canseeall) {
        this.canseeall = canseeall;
    }

    /**
     * @return the studentList
     */
    public List<User> getStudentList() {
        if (getStudentList() == null) {
            studentList = userDao.getBeanListHandlerRunner("select * from student" +StaticFields.currentGradeNum+ schoolId + " where roleid=" + id, new User());
        }
        return studentList;
    }

    /**
     * @param studentList the studentList to set
     */
    public void setStudentList(List<User> studentList) {
        this.studentList = studentList;
    }

    /**
     * @return the teachertList
     */
    public List<User> getTeachertList() {
        if (teachertList == null) {
            teachertList = userDao.getBeanListHandlerRunner("select * from teacherinfo" +StaticFields.currentGradeNum+"  where roleid=" + id, new User());
        }
        return teachertList;
    }

    /**
     * @param teachertList the teachertList to set
     */
    public void setTeachertList(List<User> teachertList) {
        this.teachertList = teachertList;
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
    }
}
