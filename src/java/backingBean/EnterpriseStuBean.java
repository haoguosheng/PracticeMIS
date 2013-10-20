/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Enterstudent;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import tools.SQLTool;
import tools.StaticFields;

/**
 *
 * @author Idea
 */
@Named
@SessionScoped
public class EnterpriseStuBean implements java.io.Serializable {

    private Enterstudent entStu;
    private SQLTool<Enterstudent> esDao;
    private Integer id;
    private String requirement;
    private String payment;
    private String other;
    private int studnum;
    private int positionid;
    private int enterid;
    private List<Enterstudent> enterStudList;

    @PostConstruct
    public void init() {
        esDao = new SQLTool<>();
    }

    public synchronized String addEnterpriseNeed(int enterid) {
        this.enterid=enterid;
        esDao.executUpdate("insert into enterstudent (enterid, requirement, payment, other, studnum, positionid) values("
                + enterid + ", '" + this.requirement + "', '" + this.payment + "', '" + this.other + "', " + this.studnum + ", " + this.positionid + ")");
        return null;
    }

    public List<Enterstudent> getEnterStudList() {
        this.enterStudList = esDao.getBeanListHandlerRunner("select * from Enterstudent where enterid=" + this.enterid, new Enterstudent());
        return enterStudList;
    }

    public String deleteNeed(int id) {
        this.esDao.executUpdate("delete from enterstudent" + StaticFields.currentGradeNum + "  where id=" + id);
        return null;
    }

    public void saveNeed(int id, String payment, String requirment, int num, String other, int positionId) {
        this.esDao.executUpdate("update enterstudent" + StaticFields.currentGradeNum + "  set payment='" + payment + "', Requirement='" + requirment
                + "',Other='" + other + "',Studnum=" + num + ", positionid=" + positionId
                + " where id=" + id);
    }

    public Enterstudent getEntStu() {
        return entStu;
    }

    public void setEntStu(Enterstudent entStu) {
        this.entStu = entStu;
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

    public int getStudnum() {
        return studnum;
    }

    public void setStudnum(int studnum) {
        this.studnum = studnum;
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
}
