/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entitiesBeans;

import entities.News;
import java.util.Calendar;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import tools.SQLTool;
import tools.StaticFields;

/**
 *
 * @author Idea
 */
public class NewsLocal extends AbstractEntityBean<News> implements java.io.Serializable {

    private final SQLTool<News> myDao = new SQLTool<>();

    public void create(News entity) {
        Calendar temCa = Calendar.getInstance();
        int month = temCa.get(Calendar.MONTH);
        String temMonth = month < 10 ? "0" + month : "" + month;
        String myDate = temCa.get(Calendar.YEAR) + "-" + temMonth.trim() + "-" + temCa.get(Calendar.DAY_OF_MONTH);
        entity.setContent(entity.getContent().replaceAll("'", "‘"));
        entity.setNewsTitle(entity.getNewsTitle().replaceAll("'", "‘"));
        myDao.executUpdate("insert into News" + StaticFields.currentGradeNum
                + "(content, inputdate, userno,newstitle) values('"
                + entity.getContent() + "','" + myDate + "','"
                + entity.getUserno() + "','" + entity.getNewsTitle() + "')");
    }

    public void edit(News entity) {
        entity.setContent(entity.getContent().replaceAll("'", "‘"));
        entity.setNewsTitle(entity.getNewsTitle().replaceAll("'", "‘"));
      Calendar temCa = Calendar.getInstance();
        int month = temCa.get(Calendar.MONTH);
        String temMonth = month < 10 ? "0" + month : "" + month;
        String myDate = temCa.get(Calendar.YEAR) + "-" + temMonth.trim() + "-" + temCa.get(Calendar.DAY_OF_MONTH);
        myDao.executUpdate("update News" + StaticFields.currentGradeNum
                + " set content='" + entity.getContent() + "', inputdate='" + myDate + "', userno='" + entity.getUserno() + "',newstitle='" + entity.getNewsTitle()
                + "' where id=" + entity.getId()
        );
    }

    public void remove(News entity) {
        myDao.executUpdate("delete from News" + StaticFields.currentGradeNum + " where id=" + entity.getId());
    }

    public News find(Object id) {
        List<News> tem = myDao.getBeanListHandlerRunner("select * from News" + StaticFields.currentGradeNum + " where id=" + id, new News());
        if (tem.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("查找失败！"));
            return null;
        } else {
            return tem.get(0);
        }
    }

    public List<News> findAll() {
        return myDao.getBeanListHandlerRunner("select * from News" + StaticFields.currentGradeNum + " order by inputdate", new News());
    }

    public List<News> getList(String sqlString) {
        return myDao.getBeanListHandlerRunner(sqlString, new News());
    }
}
