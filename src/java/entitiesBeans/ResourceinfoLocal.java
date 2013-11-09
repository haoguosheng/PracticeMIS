/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entitiesBeans;

import entities.Resourceinfo;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import tools.SQLTool;
import tools.StaticFields;

/**
 *
 * @author Idea
 */
public class ResourceinfoLocal extends AbstractEntityBean<Resourceinfo>  implements java.io.Serializable{

    private final SQLTool<Resourceinfo> myDao = new SQLTool<>();

    public void create(Resourceinfo entity) {
        //防止出现"'"使SQL语句无法执行
        entity.setComment(entity.getComment().replaceAll("'", "‘"));
      myDao.executUpdate("insert into Resourceinfo" + StaticFields.currentGradeNum 
                + "(name, parentid, refas, comment, recommendrole, menuorder) values('"
                + entity.getName() + "'," + entity.getParentid() + ",'"
                + "','" + entity.getRefas() + "','" + entity.getComment() + "','"
                + entity.getRecommendrole() + "'," + entity.getMenuorder() + ")");
    }

    public void edit(Resourceinfo entity) {
        entity.setComment(entity.getComment().replaceAll("'", "‘"));
    myDao.executUpdate("update Resourceinfo" + StaticFields.currentGradeNum 
                + " set name='" + entity.getName() + "', parentid='" + entity.getParentid() + "', refas='" + entity.getRefas()
                + "', comment='" + entity.getComment() + "',' recommendrole='" + entity.getRecommendrole()
                + "', menuorder=" + entity.getMenuorder()
                + " where id=" + entity.getId()
        );
    }

    public void remove(Resourceinfo entity) {
      myDao.executUpdate("delete from Resourceinfo" + StaticFields.currentGradeNum + " where id=" + entity.getId());
    }

    public Resourceinfo find(Object id) {
        List<Resourceinfo> tem = myDao.getBeanListHandlerRunner("select * from Resourceinfo" + StaticFields.currentGradeNum  + " where id=" + id, new Resourceinfo());
        if (tem.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("查找失败！"));
            return null;
        } else {
            return tem.get(0);
        }
    }

    public List<Resourceinfo> findAll() {
        return myDao.getBeanListHandlerRunner("select * from Resourceinfo" + StaticFields.currentGradeNum , new Resourceinfo());
    }

    public List<Resourceinfo> getList(String sqlString) {
        return myDao.getBeanListHandlerRunner(sqlString, new Resourceinfo());
    }
}
