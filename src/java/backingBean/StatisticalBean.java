/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Checkrecords;
import entities.MyUser;
import entities.Student;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import sessionBeans.CheckrecordsFacadeLocal;
import sessionBeans.StudentFacadeLocal;
import sessionBeans.TeacherinfoFacadeLocal;


/**
 *
 * @author myPC
 */
@Named
@RequestScoped
public class StatisticalBean implements Serializable {

    private String classId;
    private String staResult;
    private List<Student> studentsList;
    @EJB
    private TeacherinfoFacadeLocal teacherEjb;
    @EJB
    private StudentFacadeLocal sutdentEjb;
    @EJB
    private CheckrecordsFacadeLocal checkRecordsEjb;

    public List<Student> getStudentsList() {
        if (studentsList != null) {
            studentsList.clear();
        }
        studentsList = sutdentEjb.getList("select * from student where nameofunitid='" + classId + "'");
        return studentsList;
    }

    public String getGradeByStuno(MyUser stu) {
        List<Checkrecords> cList = checkRecordsEjb.getList("select * from checkrecords where stuno='" + stu.getUno() + "'");
        if (cList != null && cList.size() > 0) {
            String s = cList.get(0).getRank();
            switch (s) {
                case "0":
                    return "优秀";
                case "1":
                    return "良好";
                case "2":
                    return "及格";
                case "3":
                    return "不及格";
            }
        }
        return "";
    }

    public String getTeachByStuno(MyUser stu) {
        List<Checkrecords> cList = checkRecordsEjb.getList("select * from checkrecords where stuno='" + stu.getUno() + "'");
        if (cList != null && cList.size() > 0) {
            Checkrecords c = cList.get(0);
            return c.getTeacherinfo().getName();
        }
        return "";
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    /**
     * @return the staResult
     */
    public String getStaResult() {
        if (studentsList != null) {
            studentsList.clear();
        }
        double stuA = 0;
        double stuB = 0;
        double stuC = 0;
        double stuD = 0;
        double stuN = 0;
        double sum = 0;
        studentsList = sutdentEjb.getList("select * from student where nameofunitid='" + classId + "'");
        for (Student stu : studentsList) {
            List<Checkrecords> cList = checkRecordsEjb.getList("select * from checkrecords where stuno='" + stu.getUno() + "'");
            if (cList != null && cList.size() > 0) {
                String s = cList.get(0).getRank();
                switch (s) {
                    case "0":
                        stuA = stuA + 1;
                        break;
                    case "1":
                        stuB = stuB + 1;
                        break;
                    case "2":
                        stuC = stuC + 1;
                        break;
                    case "3":
                        stuD = stuD + 1;
                        break;
                }
            }
        }
        sum = studentsList.size();
        stuN = sum - stuA - stuB - stuC - stuD;
        DecimalFormat df = new DecimalFormat("0.00");
        String s0;
        String s1;
        String s2;
        String s3;
        String sN;
        if (stuA != 0) {
            s0 = df.format(stuA / sum * 100);
        } else {
            s0 = "0.00";
        }
        if (stuB != 0) {
            s1 = df.format(stuB / sum * 100);
        } else {
            s1 = "0.00";
        }
        if (stuC != 0) {
            s2 = df.format(stuC / sum * 100);
        } else {
            s2 = "0.00";
        }
        if (stuD != 0) {
            s3 = df.format(stuD / sum * 100);
        } else {
            s3 = "0.00";
        }
        if (stuN != 0) {
            sN = df.format(100 - Double.parseDouble(s0) - Double.parseDouble(s1) - Double.parseDouble(s2) - Double.parseDouble(s3));
        } else {
            sN = "0.00";
        }
        staResult = "优秀：" + (int)stuA + "人，占" + s0 + "%； 良好：" + (int)stuB + "人，占" + s1 + "%； 及格：" + (int)stuC + "人，占" + s2 + "%； 不及格：" + (int)stuD + "人，占" + s3 + "%； 未实习：" + (int)stuN + "人，占" + sN + "%";
        return staResult;
    }
}
