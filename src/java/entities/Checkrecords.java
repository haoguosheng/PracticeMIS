/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import entitiesBeans.UserLocal;
import java.io.Serializable;
import java.util.Date;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import tools.StaticFields;
import tools.UserAnalysis;

/**
 *
 * @author Administrator
 */
@Named
@SessionScoped
public class Checkrecords implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private Date checkdate;
    private String checkcontent;
    private String recommendation;
    private String rank;
    private String remark;
    private String stuno;
    private String teachno;
    private final UserLocal userLocal = new UserLocal();

    private User teacher;
    private User student;
    private String schoolId;

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
        if (teacher == null) {
            dealId0Teacher();
        }else if(null==this.teacher.getUno()){
            dealId0Teacher();
        }
        return teacher;
    }

    /*
     *当对象是内在对象时处理外键teacher
     */
    private void dealId0Teacher() {
        if (null==this.id ) {
            if (null == teacher) {
                this.teacher = new User();
            }
        } else {
            this.teacher = userLocal.getList("select * from teacherinfo" + StaticFields.currentGradeNum + " where uno='" + teachno + "'").get(0);
        }
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    public User getStudent() {
        if (student == null) {
           this.dealId0Student();
        }else if(null==this.student.getUno()){
            this.dealId0Student();
        }
        return student;
    }
 /*
     *当对象是内在对象时处理外键teacher
     */
    private void dealId0Student() {
        if (null==this.id ) {
            if (null == student) {
                this.student = new User();
            }
        } else {
             student = userLocal.getList("select * from student" + StaticFields.currentGradeNum + this.getSchoolId() + " where uno='" + stuno + "'").get(0);
        }
    }
    public void setStudent(User student) {
        this.student = student;
    }

    public String getSchoolId() {
        this.schoolId = UserAnalysis.getSchoolId(stuno);
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getRankName() {
        return StaticFields.rankString[Integer.parseInt(this.getRank())];
    }
}
