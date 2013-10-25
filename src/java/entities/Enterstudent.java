/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import tools.SQLTool;
import tools.StaticFields;

/**
 *
 * @author Administrator
 */
public class Enterstudent implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String requirement;
    private String payment;
    private String other;
    private int studnum;
    private int positionid;
    private int enterid;
    private Enterprise enterprise;
    private Position position;
    private final SQLTool<Enterprise> epDao = new SQLTool<>();
    private final SQLTool<Position> pDao = new SQLTool<>();
    private final SQLTool<Stuentrel> seDao = new SQLTool<>();
    private List<Stuentrel> stuEntList = new ArrayList<>();
    private int schoolId;

    public Enterstudent() {
    }

    public Enterstudent(Integer id) {
        this.id = id;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
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

    public int getStudnum() {
        return studnum;
    }

    public void setStudnum(int studnum) {
        this.studnum = studnum;
    }

    public Enterprise getEnterprise() {
        if (enterprise == null) {
            enterprise = epDao.getBeanListHandlerRunner("select * from enterprise" + StaticFields.currentGradeNum + " where id=" + enterid, new Enterprise()).get(0);
        }
        return enterprise;
    }

    public void setEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
    }

    public Position getPosition() {
        if (position == null) {
            position = pDao.getBeanListHandlerRunner("select * from position" + StaticFields.currentGradeNum + " where id=" + positionid, new Position()).get(0);
        }
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRequirement() {
        return requirement;
    }

    public List<Stuentrel> getStuEntList() {
        if (null == this.stuEntList || this.stuEntList.isEmpty()) {
            this.stuEntList = seDao.getBeanListHandlerRunner("select * from Stuentrel" + StaticFields.currentGradeNum + getSchoolId() + " where entstuid=" + this.id, new Stuentrel());
        }
        return stuEntList;
    }

    /**
     * @param stuEntList the stuEntList to set
     */
    public void setStuEntList(List<Stuentrel> stuEntList) {
        this.stuEntList = stuEntList;
    }

    public int getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(int schoolId) {
        this.schoolId = schoolId;
    }
}
