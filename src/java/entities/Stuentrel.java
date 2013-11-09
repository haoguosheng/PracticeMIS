/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import entitiesBeans.EnterstudentLocal;
import entitiesBeans.UserLocal;
import java.io.Serializable;
import java.util.List;
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
public class Stuentrel implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String stuno;
    private Integer entstuid;

    private User student;
    private final UserLocal userLocal = new UserLocal();
    private Enterstudent enterstu;
    private final EnterstudentLocal enterStuLocal = new EnterstudentLocal();

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

    public User getStudent() {
        if (null == student) {
            this.dealId0();
        } else if (null ==this.student.getUno() ) {
            this.dealId0();
        }
        return student;
    }
    /*
     *当对象是内在对象时处理外键Student
     */

    private void dealId0() {
        if (null == this.id) {
            if (null == student) {
                this.student = new User();
            }
        } else {
            List<User> studentList = userLocal.getList("select * from student" + StaticFields.currentGradeNum + UserAnalysis.getSchoolId(stuno) + " where uno='" + stuno + "'");
            if (!studentList.isEmpty()) {
                this.student = studentList.get(0);
            }
        }
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public int getEntstuid() {
        return entstuid;
    }

    public void setEntstuid(int entstuid) {
        this.entstuid = entstuid;
    }

    public Enterstudent getEnterstu() {
        if (null == enterstu) {
            this.dealId0Enterstu();
        } else if (null==this.enterstu.getId() ) {
            this.dealId0Enterstu();
        }
        return enterstu;
    }
    /*
     *当对象是内在对象时处理外键Enterstu
     */

    private void dealId0Enterstu() {
        if (null == this.id) {
            if (null == enterstu) {
                this.enterstu = new Enterstudent();
            }
        } else {
            List<Enterstudent> entStuList = enterStuLocal.getList("select * from enterstudent" + StaticFields.currentGradeNum + " where id=" + this.entstuid);
            if (!entStuList.isEmpty()) {
                enterstu = entStuList.get(0);
            }
        }
    }

    public void setEnterstu(Enterstudent enterstu) {
        this.enterstu = enterstu;
    }
}
