/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entitiesBeans;

import entities.Roleinfo;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import tools.SQLTool;
import tools.StaticFields;

/**
 *
 * @author Idea
 */
public class RoleinfoLocal extends AbstractEntityBean<Roleinfo>  implements java.io.Serializable{
       
    private final SQLTool<Roleinfo> myDao = new SQLTool<>();

    public void create(Roleinfo entity) {
        myDao.executUpdate("insert into Roleinfo" + StaticFields.currentGradeNum 
                + "(resourceIds, name, privilege,canseeall) values('"
                 + entity.getResouceids()+"','"+entity.getName()+ "'," + entity.getPrivilege()+ "," 
                + entity.getCanseeall()+ ")");
    }

    public void edit(Roleinfo entity) {
       myDao.executUpdate("update Roleinfo" + StaticFields.currentGradeNum 
                + " set name='" + entity.getName() + "', resourceIds='" + entity.getResouceids() 
                + "', privilege="  + entity.getPrivilege() + "',canseeall="+entity.getCanseeall()
                + " where id=" + entity.getId()
        );
    }

    public void remove(Roleinfo entity) {
       myDao.executUpdate("delete from Roleinfo" + StaticFields.currentGradeNum  + " where id=" + entity.getId());
    }

    public Roleinfo find(Object id) {
        List<Roleinfo> tem = myDao.getBeanListHandlerRunner("select * from Roleinfo" + StaticFields.currentGradeNum  + " where id=" + id, new Roleinfo());
        if (tem.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("查找失败！"));
            return null;
        } else {
            return tem.get(0);
        }
    }

    public List<Roleinfo> findAll() {
           return   myDao.getBeanListHandlerRunner("select * from Roleinfo" + StaticFields.currentGradeNum , new Roleinfo());
    }
    public List<Roleinfo> getList(String sqlString) {
        return myDao.getBeanListHandlerRunner(sqlString, new Roleinfo());
    }
}
