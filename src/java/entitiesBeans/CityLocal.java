/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entitiesBeans;

import entities.City;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import tools.SQLTool;
import tools.StaticFields;

/**
 *
 * @author Idea
 */
public class CityLocal extends AbstractEntityBean<City> implements java.io.Serializable {

    private final SQLTool<City> myDao = new SQLTool<>();

    public void create(City entity) {
       myDao.executUpdate("insert into City" + StaticFields.currentGradeNum
                + "(name, pinyin, userno) values('"
                + entity.getName() + "','" + entity.getPinyin() + "','"
                + entity.getUserno() + "')");
    }

    public void edit(City entity) {
      myDao.executUpdate("update City" + StaticFields.currentGradeNum
                + " set name='" + entity.getName() + "', pinyin='" + entity.getPinyin() + "', userno='" + entity.getUserno() + "'"
                + " where id=" + entity.getId()
        );
    }

    public void remove(City entity) {
       myDao.executUpdate("delete from City" + StaticFields.currentGradeNum + " where id=" + entity.getId());
    }

    public City find(Object id) {
        List<City> tem = myDao.getBeanListHandlerRunner("select * from City" + StaticFields.currentGradeNum + " where id=" + id , new City());
        if (tem.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("查找失败！"));
            return null;
        } else {
            return tem.get(0);
        }
    }

    public List<City> findAll() {
        return myDao.getBeanListHandlerRunner("select * from City" + StaticFields.currentGradeNum+" order by pinyin", new City());
    }

    public List<City> getList(String sqlString) {
        return myDao.getBeanListHandlerRunner(sqlString, new City());
    }
}
