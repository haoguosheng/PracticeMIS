/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entitiesBeans;

import entities.Nameofunit;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import tools.SQLTool;
import tools.StaticFields;

/**
 *
 * @author Idea
 */

public class NameofunitLocal  extends AbstractEntityBean<Nameofunit> implements java.io.Serializable{

    private final SQLTool<Nameofunit> myDao = new SQLTool<>();

    public void create(Nameofunit entity) {
        myDao.executUpdate("insert into Nameofunit" + StaticFields.currentGradeNum 
                + "(name,parentid, pinyin,pri, userno,mytype) values('"
                + entity.getName() + "','" + entity.getParentid() + "','" + entity.getPinyin() + "'," + entity.getPri() + ",'"
                + entity.getUserno() + "'," + entity.getMytype() + ")");
    }

    public void edit(Nameofunit entity) {
        myDao.executUpdate("update Nameofunit" + StaticFields.currentGradeNum 
                + " set name='" + entity.getName() + "', pinyin='" + entity.getPinyin() + "', userno='" + entity.getUserno()
                + "', parentid='" + entity.getParentid() + "',pri=" + entity.getPri() + ",mytype=" + entity.getMytype()
                + " where id='" + entity.getId()+"'"
        );
    }

    public void remove(Nameofunit entity) {
       myDao.executUpdate("delete from Nameofunit" + StaticFields.currentGradeNum + " where id='" + entity.getId()+"'");
    }

    public Nameofunit find(Object id) {
        List<Nameofunit> tem = myDao.getBeanListHandlerRunner("select * from Nameofunit" + StaticFields.currentGradeNum + " where id='" + id+"'", new Nameofunit());
        if (tem.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("查找失败！"));
            return null;
        } else {
            return tem.get(0);
        }
    }

    public List<Nameofunit> findAll() {
        return myDao.getBeanListHandlerRunner("select * from Nameofunit" + StaticFields.currentGradeNum +" order by pinyin", new Nameofunit());
    }

    public List<Nameofunit> getList(String sqlString) {
        return myDao.getBeanListHandlerRunner(sqlString, new Nameofunit());
    }
}
