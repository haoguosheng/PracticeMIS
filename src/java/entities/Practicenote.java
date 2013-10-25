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
    private Integer stuEntId;
    private String detail;
    private Date submitdate;
    private String schoolId;
    private Stuentrel stuEntRelation;
    private final SQLTool<Stuentrel> stuEntRelDao = new SQLTool<>();

    public Stuentrel getStuEntRelation() {
        if (null == stuEntRelation) {
            this.stuEntRelation = this.stuEntRelDao.getBeanListHandlerRunner("select * from Stuentrel" + StaticFields.currentGradeNum + schoolId + " where id=" + this.id, stuEntRelation).get(0);
        }
        return stuEntRelation;
    }

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

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public void setStuEntRelation(Stuentrel stuEntRelation) {
        this.stuEntRelation = stuEntRelation;
    }

    /**
     * @return the stuEntId
     */
    public Integer getStuEntId() {
        return stuEntId;
    }

    /**
     * @param stuEntId the stuEntId to set
     */
    public void setStuEntId(Integer stuEntId) {
        this.stuEntId = stuEntId;
    }
}
