/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entitiesBeans;

import entities.Practicenote;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import tools.SQLTool;
import tools.StaticFields;

/**
 *
 * @author Idea
 */
public class PracticenoteLocal extends AbstractEntityBean<Practicenote> implements java.io.Serializable {

    private final SQLTool<Practicenote> myDao = new SQLTool<>();

    public void create(Practicenote entity, String schoolId,String submittedDate) {
        //防止出现"'"使SQL语句无法执行
        entity.setDetail(entity.getDetail().replaceAll("'", "‘"));
        myDao.executUpdate("insert into Practicenote" + StaticFields.currentGradeNum + schoolId
                + "(stuno, detail, submitdate, studententid) values('"
                + entity.getStuno() + "','" + entity.getDetail() + "','" + submittedDate
                + "'," + entity.getStudententid() + ")");
       
    }

    public void edit(Practicenote entity, String schoolId) {
        entity.setDetail(entity.getDetail().replaceAll("'", "‘"));
      myDao.executUpdate("update Practicenote" + StaticFields.currentGradeNum + schoolId
                + " set stuno='" + entity.getStuno() + "', detail='" + entity.getDetail() 
                + "', studententid=" + entity.getStudententid()
                + " where id=" + entity.getId()
        );
    }

    public void remove(Practicenote entity, String schoolId) {
       myDao.executUpdate("delete from Practicenote" + StaticFields.currentGradeNum + schoolId + " where id=" + entity.getId());
    }

    public Practicenote find(Object id, String schoolId) {
        List<Practicenote> tem = myDao.getBeanListHandlerRunner("select * from Practicenote" + StaticFields.currentGradeNum + schoolId + " where id=" + id, new Practicenote());
        if (tem.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("查找失败！"));
            return null;
        } else {
            return tem.get(0);
        }
    }

    public List<Practicenote> findAll(String schoolId) {
        return myDao.getBeanListHandlerRunner("select * from Practicenote" + StaticFields.currentGradeNum + schoolId, new Practicenote());
    }

    public List<Practicenote> getList(String sqlString) {
        return myDao.getBeanListHandlerRunner(sqlString, new Practicenote());
    }
}
