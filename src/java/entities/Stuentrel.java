/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import tools.SQLTool;

/**
 *
 * @author Administrator
 */
public class Stuentrel implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String stuno;
    private int enterid;
    private String schoolId;
    private Enterprise enterprise;
    private User student;
    private SQLTool<Enterprise> epDao = new SQLTool<Enterprise>();
    private SQLTool<User> userDao = new SQLTool<User>();

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

    public int getEnterid() {
        return enterid;
    }

    public void setEnterid(int enterid) {
        this.enterid = enterid;
    }

    public Enterprise getEnterprise() {
        if (enterprise == null) {
            enterprise = epDao.getBeanListHandlerRunner("select * from enterprise where id=" + this.enterid, new Enterprise()).get(0);
        }
        return enterprise;
    }

    public void setEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
    }

    /**
     * @return the student
     */
    public User getStudent() {
        if (student == null) {
            student = userDao.getBeanListHandlerRunner("select * from student" + schoolId + " where uno='" + stuno + "'", new User()).get(0);
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
        return schoolId;
    }

    /**
     * @param schoolId the schoolId to set
     */
    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }
}
