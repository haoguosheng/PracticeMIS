/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.List;
import tools.SQLTool;

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
    private SQLTool<Enterprise> epDao = new SQLTool<Enterprise>();
    private SQLTool<Enterstudent> entStuDao = new SQLTool<Enterstudent>();
    private SQLTool<Practicenote> practiceNoteDao = new SQLTool<Practicenote>();
    private SQLTool<Stuentrel> stuEntRelDao = new SQLTool<Stuentrel>();
    private SQLTool<Position> pDao = new SQLTool<Position>();
    private List<Enterstudent> entStuList;
    private List<Practicenote> practicenoteList;
    private List<Stuentrel> stuEntRelList;

    public Enterstudent() {
    }

    public Enterstudent(Integer id) {
        this.id = id;
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

    /**
     * @return the enterprise
     */
    public Enterprise getEnterprise() {
        if (enterprise == null) {
            enterprise = epDao.getBeanListHandlerRunner("select * from enterprise where id=" + enterid, new Enterprise()).get(0);
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
            position = pDao.getBeanListHandlerRunner("select * from position where id=" + positionid, new Position()).get(0);
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
     * @return the entStuList
     */
    public List<Enterstudent> getEntStuList() {
        if (null == this.entStuList) {
            this.entStuList = this.entStuDao.getBeanListHandlerRunner("select * from Enterstudent where enterid=" + this.id, this);
        }
        return entStuList;
    }

    /**
     * @param entStuList the entStuList to set
     */
    public void setEntStuList(List<Enterstudent> entStuList) {
        this.entStuList = entStuList;
    }

    /**
     * @return the practicenoteList
     */
    public List<Practicenote> getPracticenoteList() {
        if (null == this.practicenoteList) {
            this.practicenoteList = this.practiceNoteDao.getBeanListHandlerRunner("select * from Practicenote where enterid=" + this.id, new Practicenote());
        }
        return practicenoteList;
    }

    /**
     * @param practicenoteList the practicenoteList to set
     */
    public void setPracticenoteList(List<Practicenote> practicenoteList) {
        this.practicenoteList = practicenoteList;
    }

    /**
     * @return the stuEntRelList
     */
    public List<Stuentrel> getStuEntRelList() {
        if (null == this.stuEntRelList) {
            this.stuEntRelList = this.stuEntRelDao.getBeanListHandlerRunner("select * from Stuentrel where enterid=" + this.id, new Stuentrel());
        }
        return stuEntRelList;
    }

    /**
     * @param stuEntRelList the stuEntRelList to set
     */
    public void setStuEntRelList(List<Stuentrel> stuEntRelList) {
        this.stuEntRelList = stuEntRelList;
    }
}
