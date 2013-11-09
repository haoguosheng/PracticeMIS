/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entitiesBeans;

import entities.Position;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import tools.SQLTool;
import tools.StaticFields;

/**
 *
 * @author Idea
 */
public class PositionLocal extends AbstractEntityBean<Position> implements java.io.Serializable{

    private final SQLTool<Position> myDao = new SQLTool<>();

    public void create(Position entity) {
       myDao.executUpdate("insert into Position" + StaticFields.currentGradeNum 
                + "(name, pinyin, userno) values('"
                + entity.getName() + "','" + entity.getPinyin() + "','"
                + entity.getUserno() + "')");
    }

    public void edit(Position entity) {
        myDao.executUpdate("update Position" + StaticFields.currentGradeNum 
                + " set name='" + entity.getName() + "', pinyin='" + entity.getPinyin() + "', userno='" + entity.getUserno() + "'"
                + " where id=" + entity.getId()
        );
    }

    public void remove(Position entity) {
        myDao.executUpdate("delete from Position" + StaticFields.currentGradeNum + " where id=" + entity.getId());
    }

    public Position find(Object id) {
        List<Position> tem = myDao.getBeanListHandlerRunner("select * from Position" + StaticFields.currentGradeNum  + " where id=" + id, new Position());
        if (tem.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("查找失败！"));
            return null;
        } else {
            return tem.get(0);
        }
    }

    public List<Position> findAll() {
        return myDao.getBeanListHandlerRunner("select * from Position" + StaticFields.currentGradeNum +" order by pinyin", new Position());
    }

    public List<Position> getList(String sqlString) {
        return myDao.getBeanListHandlerRunner(sqlString, new Position());
    }
}
