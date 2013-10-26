/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.Date;
import tools.SQLTool;
import tools.StaticFields;
import tools.UserAnalysis;

/**
 *
 * @author Administrator
 */
public class Practicenote implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String detail;
    private Date submitdate;
    private Integer studententid;
    private String schoolId;
    private Stuentrel stuEntRel;
    private String stuUno;
    private final SQLTool<Stuentrel> seDao = new SQLTool<>();

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

    /**
     * @return the schoolId
     */
    public String getSchoolId() {
        if(null==schoolId){
            schoolId=UserAnalysis.getSchoolId(getStuUno());
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
     * @return the studententid
     */
    public Integer getStudententid() {
        return studententid;
    }

    /**
     * @param studententid the studententid to set
     */
    public void setStudententid(Integer studententid) {
        this.studententid = studententid;
    }

    public Stuentrel getStuEntRel() {
        if (null == this.stuEntRel) {
            stuEntRel=seDao.getBeanListHandlerRunner("select * from stuentrel" + StaticFields.currentGradeNum + getSchoolId() + " where id=" + studententid, new Stuentrel()).get(0);
        }
        return stuEntRel;
    }

    public void setStuEntRel(Stuentrel stuEntRel) {
        this.stuEntRel = stuEntRel;
    }

    /**
     * @param StuUno the StuUno to set
     */
    public void setStuUno(String StuUno) {
        this.stuUno = StuUno;
    }

    /**
     * @return the StuUno
     */
    public String getStuUno() {
        return stuUno;
    }
}
