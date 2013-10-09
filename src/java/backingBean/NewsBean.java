/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.News;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import tools.SQLTool;
import tools.StaticFields;

@Named
@RequestScoped
public class NewsBean implements Serializable {

   @Inject
   private  CheckLogin checkLogin;
    private SQLTool<News> newsDao;
    private News news;
    private List<News> recentNews;

    @PostConstruct
    public void init() {
        newsDao = new SQLTool<News>();
        recentNews = new ArrayList<News>();
        news = new News();
    }

    public String addNews() {
        if (this.news.getContent().trim().length() >= 0) {
            newsDao.executUpdate("insert into news" + StaticFields.currentGradeNum + " (content, inputDate, userno, UnitId) values('"
                    + this.news.getContent() + "', " 
                    +Calendar.getInstance().getTime() + ", '" 
                    + this.checkLogin.getUser().getUno() + "','"
                    +this.checkLogin.getUser().getNameofunitid()+"'");
            this.news = new News();
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
     * @return the recentNews
     */
    public List<News> getRecentNews() {
        if (this.recentNews.isEmpty()) {
            Calendar c1 = Calendar.getInstance();
            c1.add(Calendar.DAY_OF_MONTH, -30);
            String sqlString = "select *  from news" + StaticFields.currentGradeNum + " where date(inputdate)>date('" + c1.get(Calendar.YEAR) + "-" + c1.get(Calendar.MONTH) + "-" + c1.get(Calendar.DAY_OF_MONTH) + "')";
            this.recentNews = newsDao.getBeanListHandlerRunner(sqlString, this.news);
        }
        return recentNews;
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
}
