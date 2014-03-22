/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.MyUser;
import entities.News;
import entities.Student;
import entities.Teacherinfo;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import sessionBeans.NewsFacadeLocal;
import tools.PublicFields;
import tools.RepeatPaginator;
import tools.StaticFields;

@Named
@SessionScoped
public class NewsBean implements Serializable {

    @EJB
    private NewsFacadeLocal newsEjb;
    private News news;
    private List<News> recentNewsList;
    private LinkedHashMap<String, List<News>> recentNewsMap;
    private LinkedHashMap<String, List<News>> recentWNewsMap = new LinkedHashMap<>();
    private LinkedHashMap<String, List<News>> recentLNewsMap = new LinkedHashMap<>();
    private LinkedHashMap<String, List<News>> recentGNewsMap = new LinkedHashMap<>();
    private News directNews;
    private RepeatPaginator paginator;
    private String searchName;
    @Inject
    PublicFields publicFields;
    MyUser user;
    HttpSession mySession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
    ResourceBundle resourceLocal;
    @PostConstruct
    public void init() {
        mySession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        int length = (int) (mySession.getAttribute("userNoLength"));
        if (length == StaticFields.teacherUnoLength) {
            user = (Teacherinfo) mySession.getAttribute("teacherUser");
        } else if (length == StaticFields.stuUnoLength1 || length == StaticFields.stuUnoLength2) {
            user = (Student) mySession.getAttribute("studentUser");
        }
        resourceLocal=ResourceBundle.getBundle("Bundle");
    }

    public String directToNews() {
        String newsId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("nId");
        directNews = newsEjb.find(Integer.parseInt(newsId));
        return "news.xhtml";
    }

    public String addNews() {
        this.news.setTeacherinfo((Teacherinfo) user);
        newsEjb.create(news);
        paginator = null;
        //需要获得其id
        News tem = newsEjb.getList("select * from news where newstitle='" + news.getNewstitle() + "' and userno='" + news.getTeacherinfo().getUno() + "'").get(0);
        publicFields.updateNewsList(tem, StaticFields.ADD);
        String messageString=resourceLocal.getString("add")+resourceLocal.getString("success");
        FacesContext.getCurrentInstance().addMessage("form1:add", new FacesMessage(messageString));
        return "addNews";
    }

    public LinkedHashMap<String, List<News>> getRecentNewsMap() {
        recentNewsMap = publicFields.getRecentNewsMap();
        return recentNewsMap;
    }

    public LinkedHashMap<String, List<News>> getRecentWNewsMap() {
        recentWNewsMap = publicFields.getRecentWNewsMap();
        return recentWNewsMap;
    }

    public LinkedHashMap<String, List<News>> getRecentLNewsMap() {
        recentLNewsMap = publicFields.getRecentLNewsMap();
        return recentLNewsMap;
    }

    public LinkedHashMap<String, List<News>> getRecentGNewsMap() {
        recentGNewsMap = publicFields.getRecentGNewsMap();
        return recentGNewsMap;
    }

    public String searchAll() {
        searchName = null;
        this.recentNewsList = null;
        paginator = null;
        return null;
    }

    public String search() {
        paginator = null;
        this.recentNewsList = null;
        return null;
    }

    public String delete(News news) {
        newsEjb.remove(news);
        publicFields.updateNewsList(news, StaticFields.DELETE);
        this.recentNewsList = null;
        this.recentNewsMap = null;
        paginator = null;
        return null;
    }

    public News getNews() {
        if (null == this.news) {
            this.news = new News();
        }
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }

    public List<News> getRecentNewsList() {
        if (null == this.searchName || this.searchName.trim().length() == 0) {
            this.recentNewsList = publicFields.getRecentNewsMap().get(this.user.getNameofunit().getId());
        } else {
            if (null == this.recentNewsList) {
                this.recentNewsList = new LinkedList<>();
            } else {
                this.recentNewsList.clear();
            }

            List<News> temNewsList = publicFields.getRecentNewsMap().get(this.user.getNameofunit().getId());
            for (News newsTem : temNewsList) {
                if (newsTem.getContent().contains(searchName) || newsTem.getNewstitle().contains(searchName)) {
                    this.recentNewsList.add(newsTem);
                }
            }
        }
        return recentNewsList;
    }

    public News getDirectNews() {
        return directNews;
    }

    public void setDirectNews(News directNews) {
        this.directNews = directNews;
    }

    public RepeatPaginator getPaginator() {
        if (paginator == null) {
            paginator = new RepeatPaginator(this.getRecentNewsList(), 10);
        }
        return paginator;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }
}
