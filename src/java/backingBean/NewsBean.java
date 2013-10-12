/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.News;
import entities.User;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import tools.PublicFields;
import tools.SQLTool;
import tools.StaticFields;

@Named
@SessionScoped
public class NewsBean implements Serializable {

    @Inject
    private CheckLogin checkLogin;
    private SQLTool<News> newsDao;
    private News news;
    private List<News> recentNews;

    @PostConstruct
    public void init() {
        newsDao = new SQLTool<>();
        news = new News();
    }

    public String addNews() {
        User myUser = getCheckLogin().getUser();
        Calendar temCa = Calendar.getInstance();
        int month = temCa.get(Calendar.MONTH);
        String temMonth = month < 10 ? "0" + month : "" + month;
        String myData = temCa.get(Calendar.YEAR) + "-" + temMonth.trim() + "-" + temCa.get(Calendar.DAY_OF_MONTH);
        if (this.news.getContent().trim().length() >= 0) {
            String sqlString = "insert into news" + StaticFields.currentGradeNum + " (content, inputDate, userno, UnitId,newstitle) values('"
                    + this.news.getContent().trim() + "','"
                    + myData + "', '"
                    + myUser.getUno().trim() + "','"
                    + myUser.getNameofunitid().trim() + "','"
                    + this.news.getNewsTitle().trim() + "')";
            newsDao.executUpdate(sqlString);
            this.news = new News();
            //需要调整Map中的list
        } else {
            FacesContext.getCurrentInstance().addMessage("latestMessage", new FacesMessage("您需要加入消息的内容，而不能为空"));
        }
        return null;
    }

    public String delete(int id) {
        newsDao.executUpdate("delete from news where id=" + id);
        return null;
    }

    /**
     * @return the news
     */
    public News getNews() {
        return news;
    }

    /**
     * @param news the news to set
     */
    public void setNews(News news) {
        this.news = news;
    }

    /**
     * @return the checkLogin
     */
    public CheckLogin getCheckLogin() {
        return checkLogin;
    }

    /**
     * @param checkLogin the checkLogin to set
     */
    public void setCheckLogin(CheckLogin checkLogin) {
        this.checkLogin = checkLogin;
    }

    /**
     * @return the recentNews
     */
    public List<News> getRecentNews() {
        this.recentNews = PublicFields.getRecentNewsMap().get(this.checkLogin.getUser().getNameofunitid());
        return recentNews;
    }
}
