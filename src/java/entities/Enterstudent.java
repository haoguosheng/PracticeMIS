/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.LinkedHashMap;
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
    private final SQLTool<Enterstudent> entStuDao = new SQLTool<>();
    private final SQLTool<Position> pDao = new SQLTool<>();
    private List<Enterstudent> entStuList;
    private LinkedHashMap<String, Integer> positionMap;

    public Enterstudent() {
    }

    public Enterprise getEnterprise() {
        if (enterprise == null) {
            enterprise = epDao.getBeanListHandlerRunner("select * from enterprise" + StaticFields.currentGradeNum + " where id=" + enterid, new Enterprise()).get(0);
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
            position = pDao.getBeanListHandlerRunner("select * from position" + StaticFields.currentGradeNum + " where id=" + positionid, new Position()).get(0);
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
        if (null == this.entStuList || entStuList.isEmpty()) {
            this.entStuList = this.entStuDao.getBeanListHandlerRunner("select * from Enterstudent" + StaticFields.currentGradeNum + " where enterid=" + this.id, this);
        }
        return entStuList;
    }

    /**
     * @param entStuList the entStuList to set
     */
    public void setEntStuList(List<Enterstudent> entStuList) {
        this.entStuList = entStuList;
    }

    public LinkedHashMap<String, Integer> getPositionMap() {
        if (null == this.positionMap) {
            this.positionMap=new  LinkedHashMap<>();
            List<Enterstudent> reqStu = entStuDao.getBeanListHandlerRunner("select * from enterstudent" + StaticFields.currentGradeNum + " where enterid=" + this.enterid, new Enterstudent());
            String s = "";
            for (int j = 0; j < reqStu.size() - 1; j++) {
                s = s + reqStu.get(j).getPositionid() + ",";
            }
            s = s + reqStu.get(reqStu.size() - 1).getPositionid();
            List<Position> tempP = pDao.getBeanListHandlerRunner("select * from position" + StaticFields.currentGradeNum + " where id in(" + s + ")", new Position());
            for (Position t : tempP) {
                if (!this.positionMap.containsKey(t.getName())) {
                    this.positionMap.put(t.getName(), t.getId());

                }
            }
        }
        return positionMap;
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
}
