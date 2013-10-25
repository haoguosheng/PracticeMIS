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
public class Stuentrel implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String stuno;
    private String schoolId;
    private Integer entstuid;
    private Enterstudent enterStudents;
    private User student;
    private List<Practicenote> practiceNoteList;
    private final SQLTool<Enterstudent> epDao = new SQLTool<>();
    private final SQLTool<User> userDao = new SQLTool<>();
    private final SQLTool<Practicenote> practiceNoteDao = new SQLTool<>();

    public Enterstudent getEnterStudents() {
        if (enterStudents == null) {
            enterStudents = epDao.getBeanListHandlerRunner("select * from Enterstudent" + StaticFields.currentGradeNum + "  where id=" + this.getEntstuid(), new Enterstudent()).get(0);
        }
        return enterStudents;
    }

    public User getStudent() {
        if (student == null) {
            student = userDao.getBeanListHandlerRunner("select * from student" + StaticFields.currentGradeNum + schoolId + " where uno='" + stuno + "'", new User()).get(0);
        }
        return student;
    }

    public List<Practicenote> getPracticeNoteList() {
        if (null == practiceNoteList) {
            practiceNoteList = practiceNoteDao.getBeanListHandlerRunner("select * from Practicenote where studentEntId=" + this.id, new Practicenote());
        }
        return practiceNoteList;
    }

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

    public void setEnterprise(Enterstudent enterprise) {
        this.enterStudents = enterprise;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public void setPracticeNoteList(List<Practicenote> practiceNote) {
        this.practiceNoteList = practiceNote;
    }

    /**
     * @return the entstuid
     */
    public Integer getEntstuid() {
        return entstuid;
    }

    /**
     * @param entstuid the entstuid to set
     */
    public void setEntstuid(Integer entstuid) {
        this.entstuid = entstuid;
    }

}
