/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.Date;
import tools.SQLTool;
import tools.StaticFields;

/**
 *
 * @author Administrator
 */
public class Practicenote implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String detail;
    private Date submitdate;
    private String stuno;
    private int positionid;
    private int enterid;
    private String schoolId;
    private Enterprise enterprise;
    private Position position;
    private User student;
    private SQLTool<Enterprise> epDao = new SQLTool<Enterprise>();
    private SQLTool<Position> positionDao = new SQLTool<Position>();
    private SQLTool<User> userDao = new SQLTool<User>();

    public Practicenote() {
    }

    public Practicenote(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Date getSubmitdate() {
        return submitdate;
    }

    public void setSubmitdate(Date submitdate) {
        this.submitdate = submitdate;
    }

    public String getStuno() {
        return stuno;
    }

    public void setStuno(String stuno) {
        this.stuno = stuno;
    }

    public int getPositionid() {
        return positionid;
    }

    public void setPositionid(int positionid) {
        this.positionid = positionid;
    }

    public int getEnterid() {
        return enterid;
    }

    public void setEnterid(int enterid) {
        this.enterid = enterid;
    }

    /**
     * @return the enterprise
     */
    public Enterprise getEnterprise() {
        if (enterprise == null) {
            enterprise = epDao.getBeanListHandlerRunner("select * from enterprise" +StaticFields.currentGradeNum+"  where id=" + enterid, new Enterprise()).get(0);
        }
        return enterprise;
    }

    /**
     * @param enterprise the enterprise to set
     */
    public void setEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
    }

    /**
     * @return the position
     */
    public Position getPosition() {
        if (position == null) {
            position = positionDao.getBeanListHandlerRunner("select * from position" +StaticFields.currentGradeNum+"  where id=" + positionid, new Position()).get(0);
        }
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * @return the student
     */
    public User getStudent() {
        if (student == null) {
            student = userDao.getBeanListHandlerRunner("select * from student" +StaticFields.currentGradeNum+ schoolId + " where uno='" + stuno + "'", new User()).get(0);
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
