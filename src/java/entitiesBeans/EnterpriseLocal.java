/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entitiesBeans;

import entities.Enterprise;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import tools.SQLTool;
import tools.StaticFields;

/**
 *
 * @author Idea
 */
public class EnterpriseLocal  extends AbstractEntityBean<Enterprise> implements java.io.Serializable{

    private final SQLTool<Enterprise> myDao = new SQLTool<>();

    public void create(Enterprise entity) {
      myDao.executUpdate("insert into Enterprise" + StaticFields.currentGradeNum 
                + "(name, enterurl, contactname, contacttelephone, contactaddress, userno, cityid) values('"
                + entity.getName() + "','" + entity.getEnterurl()
                + "','" + entity.getContactname() + "','" + entity.getContacttelephone() + "','"
                + entity.getContactaddress() + "','" + entity.getUserno() + "'," + entity.getCityId() + ")");
    }

    public void edit(Enterprise entity) {
     myDao.executUpdate("update Enterprise" + StaticFields.currentGradeNum 
                + " set  name='" + entity.getName() + "', enterurl='" + entity.getEnterurl()
                + "', contactname='" + entity.getContactname() + "', contacttelephone='" + entity.getContacttelephone()
                + "', contactaddress='" + entity.getContactaddress() + "', userno='" + entity.getUserno() + "', cityid=" + entity.getCityId()
                + " where id=" + entity.getId()
        );
    }

    public void remove(Enterprise entity) {
        int n = myDao.executUpdate("delete from Enterprise" + StaticFields.currentGradeNum  + " where id=" + entity.getId());
    }

    public Enterprise find(Object id) {
        List<Enterprise> tem = myDao.getBeanListHandlerRunner("select * from Enterprise" + StaticFields.currentGradeNum + " where id=" + id, new Enterprise());
        if (tem.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("查找失败！"));
            return null;
        } else {
            return tem.get(0);
        }
    }

    public List<Enterprise> findAll() {
        return myDao.getBeanListHandlerRunner("select * from Enterprise" + StaticFields.currentGradeNum , new Enterprise());
    }

    public List<Enterprise> getList(String sqlString) {
        return myDao.getBeanListHandlerRunner(sqlString, new Enterprise());
    }
}
