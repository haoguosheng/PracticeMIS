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
public class Checkrecords implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private Date checkdate;
    private String checkcontent;
    private String recommendation;
    private String rank;
    private String remark;
    private String teachno;
    private String stuno;
    private User teacher;
    private User student;
    private String schoolId;
    private SQLTool<User> userDao = new SQLTool<User>();

    public Checkrecords() {
    }

    public Checkrecords(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCheckdate() {
        return checkdate;
    }

    public void setCheckdate(Date checkdate) {
        this.checkdate = checkdate;
    }

    public String getCheckcontent() {
        return checkcontent;
    }

    public void setCheckcontent(String checkcontent) {
        this.checkcontent = checkcontent;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTeachno() {
        return teachno;
    }

    public void setTeachno(String teachno) {
        this.teachno = teachno;
    }

    public String getStuno() {
        return stuno;
    }

    public void setStuno(String stuno) {
        this.stuno = stuno;
    }

    public User getTeacher() {
        if(teacher == null){
            teacher = userDao.getBeanListHandlerRunner("select * from teacherinfo"+StaticFields.currentGradeNum+" where uno='" + teachno + "'", new User()).get(0);
        }
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    public User getStudent() {
        if(student == null){
            student = userDao.getBeanListHandlerRunner("select * from student"+StaticFields.currentGradeNum+ schoolId +" where uno='" + stuno + "'", new User()).get(0);
        }
        return student;
    }

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
