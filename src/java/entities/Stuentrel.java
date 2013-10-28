/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import tools.SQLTool;
import tools.StaticFields;
import tools.UserAnalysis;

/**
 *
 * @author Administrator
 */
public class Stuentrel implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String stuno;
    private Integer entstuid;
    private String schoolId;
    private User student;
    private Enterstudent enterstu;
    private final SQLTool<User> userDao = new SQLTool<>();
    private final SQLTool<Enterstudent> esDao = new SQLTool<>();

    public Stuentrel() {
    }

    public Stuentrel(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStuno() {
        return stuno;
    }

    public void setStuno(String stuno) {
        this.stuno = stuno;
    }

    /**
     * @return the student
     */
    public User getStudent() {
        if (null == this.student) {
            student = userDao.getBeanListHandlerRunner("select * from student" + StaticFields.currentGradeNum + getSchoolId() + " where uno='" + stuno + "'", new User()).get(0);
        }
        return student;
    }

    /**
     * @param student the student to set
     */
    public void setStudent(User student) {
        this.student = student;
    }

    /**
     * @return the schoolId
     */
    public String getSchoolId() {
        if (null == schoolId) {
            schoolId = UserAnalysis.getSchoolId(stuno);
        }
        return schoolId;
    }

    /**
     * @param schoolId the schoolId to set
     */
    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    /**
     * @return the entstuid
     */
    public int getEntstuid() {
        return entstuid;
    }

    /**
     * @param entstuid the entstuid to set
     */
    public void setEntstuid(int entstuid) {
        this.entstuid = entstuid;
    }

    /**
     * @return the enterstu
     */
    public Enterstudent getEnterstu() {
        if (this.entstuid != 0) {
            enterstu = esDao.getBeanListHandlerRunner("select * from enterstudent" + StaticFields.currentGradeNum + " where id=" + this.entstuid, new Enterstudent()).get(0);
        }
        return enterstu;
    }

    /**
     * @param enterstu the enterstu to set
     */
    public void setEnterstu(Enterstudent enterstu) {
        this.enterstu = enterstu;
    }
}
