/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Nameofunit;
import entities.News;
import entities.User;
import java.io.Serializable;
import java.util.Calendar;
import java.util.LinkedHashMap;
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
    private SQLTool<Nameofunit> nameDao;
    private News news;
    private List<News> recentNews;
    private LinkedHashMap<String, List<News>> recentNewsMap;
    private LinkedHashMap<String, List<News>> recentWNewsMap = new LinkedHashMap<>();
    private LinkedHashMap<String, List<News>> recentLNewsMap = new LinkedHashMap<>();
    private LinkedHashMap<String, List<News>> recentGNewsMap = new LinkedHashMap<>();
    private News directNews;

    @PostConstruct
    public void init() {
        newsDao = new SQLTool<>();
        nameDao = new SQLTool<>();
        news = new News();
    }

    public String directToNews() {
        String newsId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("nId");
        directNews = newsDao.getBeanListHandlerRunner("select * from news where id=" + newsId, news).get(0);
        return "news.xhtml";
    }

    public String addNews() {
        User myUser = getCheckLogin().getUser();
        Calendar temCa = Calendar.getInstance();
        int month = temCa.get(Calendar.MONTH);
        String temMonth = month < 10 ? "0" + month : "" + month;
        String myData = temCa.get(Calendar.YEAR) + "-" + temMonth.trim() + "-" + temCa.get(Calendar.DAY_OF_MONTH);
        if (this.news.getContent().trim().length() >= 0) {
            String sqlString = "insert into news" + StaticFields.currentGradeNum + " (content, inputDate, userno, newstitle) values('"
                    + this.news.getContent().trim() + "','"
                    + myData + "', '"
                    + myUser.getUno().trim() + "','"
                    + this.news.getNewsTitle().trim() + "')";
            newsDao.executUpdate(sqlString);
            this.news = new News();
            //需要调整Map中的list
        } else {
            FacesContext.getCurrentInstance().addMessage("latestMessage", new FacesMessage("您需要加入消息的内容，而不能为空"));
        }
        return null;
    }

    public LinkedHashMap<String, List<News>> getRecentWNewsMap() {
        recentWNewsMap = PublicFields.getRecentWNewsMap();
        return recentWNewsMap;
    }
    
    public LinkedHashMap<String, List<News>> getRecentLNewsMap() {
        recentLNewsMap = PublicFields.getRecentLNewsMap();
        return recentLNewsMap;
    }
    
    public LinkedHashMap<String, List<News>> getRecentGNewsMap() {
        recentGNewsMap = PublicFields.getRecentGNewsMap();
        return recentGNewsMap;
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

    public LinkedHashMap<String, List<News>> getRecentNewsMap() {
        recentNewsMap = PublicFields.getRecentNewsMap();
        return recentNewsMap;
    }

    /**
     * @param recentNewsMap the recentNewsMap to set
     */
    public void setRecentNewsMap(LinkedHashMap<String, List<News>> recentNewsMap) {
        this.recentNewsMap = recentNewsMap;
    }

    /**
     * @return the directNews
     */
    public News getDirectNews() {
        return directNews;
    }

    /**
     * @param directNews the directNews to set
     */
    public void setDirectNews(News directNews) {
        this.directNews = directNews;
    }
}
