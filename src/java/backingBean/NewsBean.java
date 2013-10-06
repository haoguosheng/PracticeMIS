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
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import tools.ForCallBean;
import tools.SQLTool;
import tools.StaticFields;

@ManagedBean
@ViewScoped
public class NewsBean implements Serializable {

    private SQLTool<News> newsDao = new SQLTool<News>();
    private News news = new News();
    private List<News> recentNews = new ArrayList<News>();

    public String addNews() {
        if (this.news.getContent().trim().length() >= 0) {
            this.news.setInputdate(Calendar.getInstance().getTime());
            this.news.setUserno(new ForCallBean().getUser().getUno());
            newsDao.executUpdate("insert into news" +StaticFields.currentGradeNum+" (content, inputDate, userno) values('" + this.news.getContent() + "', " + this.news.getInputdate() + ", '" + this.news.getUserno() + "'");
            FacesContext.getCurrentInstance().addMessage("latestMessage", new FacesMessage("添加成功，您可以继续添加"));
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
            String sqlString = "select *  from news"+StaticFields.currentGradeNum+" where date(inputdate)>date('" + c1.get(Calendar.YEAR) + "-" + c1.get(Calendar.MONTH) + "-" + c1.get(Calendar.DAY_OF_MONTH) + "')";
            this.recentNews = newsDao.getBeanListHandlerRunner(sqlString, this.news);
        }
        return recentNews;
    }
}
