/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entitiesBeans;

import entities.Enterstudent;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import tools.SQLTool;
import tools.StaticFields;

/**
 *
 * @author Idea
 */
public class EnterstudentLocal   extends AbstractEntityBean<Enterstudent> implements java.io.Serializable {

    private final SQLTool<Enterstudent> myDao = new SQLTool<>();

    public void create(Enterstudent entity) {
        entity.setPayment(entity.getPayment().replaceAll("'", "‘"));
        entity.setRequirement(entity.getRequirement().replaceAll("'", "‘"));
      myDao.executUpdate("insert into Enterstudent" + StaticFields.currentGradeNum 
                + "(enterid,payment,other,studnum,positionid,requirement) values("
                + entity.getEnterid() + ",'" + entity.getPayment() + "','" + entity.getOther()
                + "'," + entity.getStudnum() + "," + entity.getPositionid() + ",'"
                + entity.getRequirement() + "')");
    }

    public void edit(Enterstudent entity) {
       myDao.executUpdate("update Enterstudent" + StaticFields.currentGradeNum 
                + " set enterid=" + entity.getEnterid() + ", payment='" + entity.getPayment() + "', other='" + entity.getOther()
                + "', studnum=" + entity.getStudnum() + ", positionid=" + entity.getPositionid()
                + ", requirement='" + entity.getRequirement() + "'"
                + " where id=" + entity.getId()
        );
    }

    public void remove(Enterstudent entity) {
        myDao.executUpdate("delete from Enterstudent" + StaticFields.currentGradeNum + " where id=" + entity.getId());
    }

    public Enterstudent find(Object id) {
        List<Enterstudent> tem = myDao.getBeanListHandlerRunner("select * from Enterstudent" + StaticFields.currentGradeNum  + " where id=" + id, new Enterstudent());
        if (tem.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("查找失败！"));
            return null;
        } else {
            return tem.get(0);
        }
    }

    public List<Enterstudent> findAll() {
        return myDao.getBeanListHandlerRunner("select * from Enterstudent" + StaticFields.currentGradeNum , new Enterstudent());
    }

    public List<Enterstudent> getList(String sqlString) {
        return myDao.getBeanListHandlerRunner(sqlString, new Enterstudent());
    }
}
