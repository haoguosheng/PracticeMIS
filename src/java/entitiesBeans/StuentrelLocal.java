/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entitiesBeans;

import entities.Stuentrel;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import tools.SQLTool;
import tools.StaticFields;

/**
 *
 * @author Idea
 */
public class StuentrelLocal extends AbstractEntityBean<Stuentrel> implements java.io.Serializable{
    
    private final SQLTool<Stuentrel> myDao = new SQLTool<>();

    public void create(Stuentrel entity, String schoolId) {
       myDao.executUpdate("insert into Stuentrel" + StaticFields.currentGradeNum + schoolId
                + "(stuno, entstuid) values('"
                + entity.getStuno() + "'," + entity.getEntstuid() +  ")");
    }

    public void edit(Stuentrel entity, String schoolId) {
       myDao.executUpdate("update Stuentrel" + StaticFields.currentGradeNum + schoolId
                + " set stuno='" + entity.getStuno() + "', entstuid=" + entity.getEntstuid()
                + " where id=" + entity.getId()
        );
    }

    public void remove(Stuentrel entity, String schoolId) {
      myDao.executUpdate("delete from Stuentrel" + StaticFields.currentGradeNum + schoolId + " where id=" + entity.getId());
    }

    public Stuentrel find(Object id, String schoolId) {
        List<Stuentrel> tem = myDao.getBeanListHandlerRunner("select * from Stuentrel" + StaticFields.currentGradeNum + schoolId + " where id=" + id, new Stuentrel());
        if (tem.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("查找失败！"));
            return null;
        } else {
            return tem.get(0);
        }
    }

    public List<Stuentrel> findAll(String schoolId) {
           return   myDao.getBeanListHandlerRunner("select * from Stuentrel" + StaticFields.currentGradeNum + schoolId, new Stuentrel());
    }
    public List<Stuentrel> getList(String sqlString) {
        return myDao.getBeanListHandlerRunner(sqlString, new Stuentrel());
    }
}
