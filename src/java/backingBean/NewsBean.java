/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.News;
import java.io.Serializable;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import tools.PublicFields;
import tools.SQLTool;
import tools.StaticFields;

@Named
@RequestScoped
public class NewsBean implements Serializable {

    @Inject
    private CheckLogin checkLogin;
    private SQLTool<News> newsDao;
    private News news;
    private List<News> recentNews;
    private LinkedHashMap<String, List<News>> recentNewsMap;

    @PostConstruct
    public void init() {
        newsDao = new SQLTool<>();
        news = new News();
    }

    public String addNews() {
        if (this.news.getContent().trim().length() >= 0) {
            newsDao.executUpdate("insert into news" + StaticFields.currentGradeNum + " (content, inputDate, userno, UnitId,newstitle) values('"
                    + this.news.getContent() + "', "
                    + Calendar.getInstance().getTime() + ", '"
                    + this.checkLogin.getUser().getUno() + "','"
                    + this.checkLogin.getUser().getNameofunitid() + "'"
                    + this.news.getNewsTitle());
            this.news = new News();
            //需要调整Map中的list
        } else {
            FacesContext.getCurrentInstance().addMessage("latestMessage", new FacesMessage("您需要加入消息的内容，而不能为空"));
        }
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
}
