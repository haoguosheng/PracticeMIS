/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import entitiesBeans.PositionLocal;
import entitiesBeans.PracticenoteLocal;
import entitiesBeans.StuentrelLocal;
import java.io.Serializable;
import java.util.Date;
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
public class Practicenote implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String detail;
    private Date submitdate;
    private Integer studententid;
    private Integer positionId=0;
    private String stuUno;
    private String schoolId;
    
    private Stuentrel stuEntRel;
    private final StuentrelLocal seLocal = new StuentrelLocal();
    private Position position;
    private final PositionLocal positionLocal=new PositionLocal();
    private User student;
    private final PracticenoteLocal myLocal=new PracticenoteLocal();
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

      public Integer getStudententid() {
        return studententid;
    }

    public void setStudententid(Integer studententid) {
        this.studententid = studententid;
    }

    public Stuentrel getStuEntRel() {
        if (null == this.stuEntRel) {
            List<Stuentrel> stuentList=seLocal.getList("select * from stuentrel" + StaticFields.currentGradeNum + UserAnalysis.getSchoolId(this.stuUno) + " where id=" + studententid);
            if(stuentList.isEmpty()){
                return null;
            }else{
                stuEntRel=stuentList.get(0);
            }
        }
        return stuEntRel;
    }

    public void setStuEntRel(Stuentrel stuEntRel) {
        this.stuEntRel = stuEntRel;
    }

    public Integer getPositionId() {
        return positionId;
    }

    public void setPositionId(Integer positionId) {
        this.positionId = positionId;
    }

    public Position getPosition() {
        if (null==position) {
            this.dealId0();
        } else if (null==this.position.getId()) {
            this.dealId0();
        }
        return position;
    }
    /*
     *当对象是内在对象时处理外键position
     */

    private void dealId0() {
        if (null == this.id) {
            if (null == position) {
                this.position = new Position();
            }
        } else {
            position = positionLocal.getList("select * from position" + StaticFields.currentGradeNum + "  where id='" + this.getPositionId() + "'").get(0);
        }
    }
    public void setPosition(Position position) {
        this.position = position;
    }

    public String getStuno() {
        return stuUno;
    }

    public void setStuno(String stuno) {
        this.stuUno = stuno;
    }
    public User getStudent() {
        return student;
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
}
