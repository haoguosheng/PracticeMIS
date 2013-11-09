/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.News;
import entities.User;
import entitiesBeans.NewsLocal;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import tools.PublicFields;
import tools.RepeatPaginator;
import tools.StaticFields;

@Named
@SessionScoped
public class NewsBean implements Serializable {

    @Inject
    private News news;
    private @Inject User user;
    private final NewsLocal newsDao = new NewsLocal();

    private List<News> recentNewsList;
    private LinkedHashMap<String, List<News>> recentNewsMap;
    private LinkedHashMap<String, List<News>> recentWNewsMap = new LinkedHashMap<>();
    private LinkedHashMap<String, List<News>> recentLNewsMap = new LinkedHashMap<>();
    private LinkedHashMap<String, List<News>> recentGNewsMap = new LinkedHashMap<>();
    private News directNews;
    private RepeatPaginator paginator;
    private String searchName;

    public String directToNews() {
        String newsId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("nId");
        directNews = newsDao.find(newsId);
        return "news.xhtml";
    }

    public String addNews() {
        this.news.setUserno(getUser().getUno().trim());
        newsDao.create(news);
        paginator = null;
        News tem = newsDao.getList("select * from news where newstitle='" + news.getNewsTitle() + "' and userno='" + news.getUserno() + "'").get(0);
        PublicFields.updateNewsList(tem, StaticFields.ADD);
        return null;
    }

    public LinkedHashMap<String, List<News>> getRecentNewsMap() {
        recentNewsMap = PublicFields.getRecentNewsMap();
        return recentNewsMap;
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
        newsDao.remove(news);
        PublicFields.updateNewsList(news, StaticFields.DELETE);
        this.recentNewsList = null;
        this.recentNewsMap = null;
        paginator = null;
        return null;
    }

    public News getNews() {
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }

    public User getUser() {
//        if (null == user) {
//            FacesContext context = FacesContext.getCurrentInstance();
//            HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
//            user = (User) session.getAttribute("myUser");
//        }
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<News> getRecentNewsList() {
        if (null == this.searchName || this.searchName.trim().length() == 0) {
            this.recentNewsList = PublicFields.getRecentNewsMap().get(this.getUser().getNameofunitid());
        } else {
            if (null == this.recentNewsList) {
                this.recentNewsList = new LinkedList<>();
            } else {
                this.recentNewsList.clear();
            }
            List<News> temNewsList = PublicFields.getRecentNewsMap().get(this.getUser().getNameofunitid());
            for (News newsTem : temNewsList) {
                if (newsTem.getContent().contains(searchName) || newsTem.getNewsTitle().contains(searchName)) {
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
