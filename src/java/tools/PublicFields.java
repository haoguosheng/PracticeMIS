/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import entities.City;
import entities.Nameofunit;
import entities.News;
import entities.Position;
import entities.Resourceinfo;
import entities.Roleinfo;
import entities.User;
import entitiesBeans.CityLocal;
import entitiesBeans.NameofunitLocal;
import entitiesBeans.NewsLocal;
import entitiesBeans.PositionLocal;
import entitiesBeans.ResourceinfoLocal;
import entitiesBeans.RoleinfoLocal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 *
 * @author Idea
 */
@Named
@ApplicationScoped
public class PublicFields implements java.io.Serializable {

    private final Calendar c = Calendar.getInstance();
    private final int year = c.get(Calendar.YEAR), currentMonth = c.get(Calendar.MONTH);
    private int month = c.get(Calendar.MONTH);
    private LinkedHashMap<Integer, Integer> yearMap;
    private LinkedHashMap<Integer, Integer> monthMap;
    private final LinkedHashMap<Integer, Integer> dayMap = new LinkedHashMap<>();
    private static List<News> newsList;
    private static List<String> nameofunitIdList = new ArrayList<>();
    private static List<Nameofunit> nameofUnitList = new LinkedList<>();
    private static LinkedHashMap<Integer, HashMap<Resourceinfo, List<Resourceinfo>>> ResourcelistMap;//每个角色对应的功能菜单
    private static LinkedHashMap<String, Integer> roleNameIdMap;
    private static LinkedHashMap<String, Integer> positionMap;
    private static LinkedHashMap<Integer, String> reversePositionMap;
    private static List<City> cityList;
    private static List<Position> positionList;
    private static final PositionLocal positionDao = new PositionLocal();
    private static final CityLocal cityDao = new CityLocal();
    private static final ResourceinfoLocal resDao = new ResourceinfoLocal();
    private static final NameofunitLocal nameofUnitDao = new NameofunitLocal();
    private static final NewsLocal newsDao = new NewsLocal();
    private static final RoleinfoLocal roleDao = new RoleinfoLocal();
    private static LinkedHashMap<String, List<News>> recentNewsMap = new LinkedHashMap<>();
    private static LinkedHashMap<String, List<News>> recentWNewsMap = new LinkedHashMap<>();
    private static LinkedHashMap<String, List<News>> recentLNewsMap = new LinkedHashMap<>();
    private static LinkedHashMap<String, List<News>> recentGNewsMap = new LinkedHashMap<>();

    public static LinkedHashMap<String, List<News>> getRecentNewsMap() {
        if (recentNewsMap.isEmpty()) {
            List<News> recentNews = getNewsList();
            recentNewsMap = new LinkedHashMap<>();
            List<Nameofunit> schoolList = PublicFields.getSchoolUnitList();
            //unitNewsList存放每个学院的news，首先初始化，然后对所有news遍历，分别把相应的news放入其中
            for (int i = 0; i < schoolList.size(); i++) {
                recentNewsMap.put(schoolList.get(i).getId(), new LinkedList<News>());
            }
            for (int i = 0; i < recentNews.size(); i++) {
                News tem = recentNews.get(i);
                User user = tem.getInputor();
                recentNewsMap.get(user.getNameofunitid()).add(tem);
            }
        }
        return recentNewsMap;
    }

    public static LinkedHashMap<String, List<News>> getRecentWNewsMap() {
        List<News> recentNews = getNewsList();
        List<Nameofunit> schoolList = PublicFields.getSchoolUnitList();
        //unitNewsList存放每个学院的news，首先初始化，然后对所有news遍历，分别把相应的news放入其中
        for (int i = 0; i < schoolList.size(); i++) {
            if (schoolList.get(i).getMytype() == 0) {
                recentWNewsMap.put(schoolList.get(i).getId(), new LinkedList<News>());
            }
        }
        for (int i = 0; i < recentNews.size(); i++) {
            News tem = recentNews.get(i);
            User user = tem.getInputor();
            if (user.getNameofunit().getMytype() == 0) {
                recentWNewsMap.get(user.getNameofunitid()).add(tem);
            }
        }
        return recentWNewsMap;
    }

    public static LinkedHashMap<String, List<News>> getRecentLNewsMap() {
        List<News> recentNews = getNewsList();
        List<Nameofunit> schoolList = PublicFields.getSchoolUnitList();
        //unitNewsList存放每个学院的news，首先初始化，然后对所有news遍历，分别把相应的news放入其中
        for (int i = 0; i < schoolList.size(); i++) {
            if (schoolList.get(i).getMytype() == 1) {
                recentLNewsMap.put(schoolList.get(i).getId(), new LinkedList<News>());
            }
        }
        for (int i = 0; i < recentNews.size(); i++) {
            News tem = recentNews.get(i);
            User user = tem.getInputor();
            if (user.getNameofunit().getMytype() == 1) {
                recentLNewsMap.get(user.getNameofunitid()).add(tem);
            }
        }
        return recentLNewsMap;
    }

    public static LinkedHashMap<String, List<News>> getRecentGNewsMap() {
        List<News> recentNews = getNewsList();
        List<Nameofunit> schoolList = PublicFields.getSchoolUnitList();
        //unitNewsList存放每个学院的news，首先初始化，然后对所有news遍历，分别把相应的news放入其中
        for (int i = 0; i < schoolList.size(); i++) {
            if (schoolList.get(i).getMytype() == 2) {
                recentGNewsMap.put(schoolList.get(i).getId(), new LinkedList<News>());
            }
        }
        for (int i = 0; i < recentNews.size(); i++) {
            News tem = recentNews.get(i);
            User user = tem.getInputor();
            if (user.getNameofunit().getMytype() == 2) {
                recentGNewsMap.get(user.getNameofunitid()).add(tem);
            }
        }
        return recentGNewsMap;
    }

    private static void calcuListResList() {
        //获得角色种类，把每个角色的一个List放到Map里作为一个元素
        List<Roleinfo> roleList = roleDao.getList("select * from roleinfo");
        ResourcelistMap = new LinkedHashMap<>();
        for (int i = 0; i < roleList.size(); i++) {
            //准备第个角色的功能菜单;
            //准备父菜单
            String temSqlString = "select * from RESOURCEINFO where id in (" + roleList.get(i).getResouceids() + ") and parentid is null order by menuorder";
            List<Resourceinfo> parentResource = resDao.getList(temSqlString);
            //为每个父菜单准备子菜单
            LinkedHashMap<Resourceinfo, List<Resourceinfo>> menu = new LinkedHashMap();
            for (int j = 0; j < parentResource.size(); j++) {
                String temChildSqlString = "select * from RESOURCEINFO where parentid=" + parentResource.get(j).getId() + " and id in (" + roleList.get(i).getResouceids() + ")  order by menuorder";
                List<Resourceinfo> childrenResource = resDao.getList(temChildSqlString);
                menu.put(parentResource.get(j), childrenResource);
            }
            ResourcelistMap.put(roleList.get(i).getId(), menu);
        }
    }

    private static List<News> getNewsList() {
        if (null == newsList) {
            Calendar c1 = Calendar.getInstance();
            c1.add(Calendar.DAY_OF_MONTH, -StaticFields.newsDisplayDay);
            String sqlString = "select *  from news" + StaticFields.currentGradeNum + " where date(inputdate)>date('" + c1.get(Calendar.YEAR) + "-" + c1.get(Calendar.MONTH) + "-" + c1.get(Calendar.DAY_OF_MONTH) + "')";
            newsList = (ArrayList) newsDao.getList(sqlString);
        }
        return newsList;
    }

    public static List<String> getNameofunitIdList() {
        if (nameofunitIdList.isEmpty()) {
            nameofunitIdList = new SQLTool<Nameofunit>().getIdListHandlerRunner("select id from nameofunit" + StaticFields.currentGradeNum);
        }
        return nameofunitIdList;
    }

    public static List<Nameofunit> getSchoolUnitList() {
        if (null == nameofUnitList || nameofUnitList.isEmpty()) {
            nameofUnitList = nameofUnitDao.getList("select * from nameofunit" + StaticFields.currentGradeNum + "  where parentid='" + StaticFields.universityId + "'order by pri");
        }
        return nameofUnitList;
    }

    public static LinkedHashMap<Integer, HashMap<Resourceinfo, List<Resourceinfo>>> getReslistMap() {
        if (null == ResourcelistMap || ResourcelistMap.isEmpty()) {
            calcuListResList();
        }
        return ResourcelistMap;
    }

    public LinkedHashMap<Integer, Integer> getYearMap() {
        if (null == yearMap) {
            yearMap = new LinkedHashMap<>();
            yearMap.put(c.get(Calendar.YEAR), c.get(Calendar.YEAR));
            yearMap.put(c.get(Calendar.YEAR) - 1, c.get(Calendar.YEAR) - 1);
        }
        return yearMap;
    }

    public LinkedHashMap<Integer, Integer> getMonthMap() {
        if (null == monthMap || monthMap.isEmpty()) {
            monthMap = new LinkedHashMap<>();
            for (int i = 0; i < 12; i++) {
                monthMap.put(i + 1, i);
            }
        }
        return monthMap;
    }

    public LinkedHashMap<Integer, Integer> getDayMap() {
        dayMap.clear();
        Calendar c1 = Calendar.getInstance();
        c1.add(Calendar.YEAR, year - c.get(Calendar.YEAR));
        c1.add(Calendar.MONTH, month - currentMonth + 1);
        for (int i = 0; i < c1.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            dayMap.put(i + 1, i + 1);
        }
        return dayMap;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public LinkedHashMap<String, Integer> getRoleMap() {
        if (null == roleNameIdMap || roleNameIdMap.isEmpty()) {
            List<Roleinfo> role = roleDao.getList("select * from roleinfo" + StaticFields.currentGradeNum);
            if (null != role && role.size() > 0) {
                Iterator<Roleinfo> it = role.iterator();
                roleNameIdMap = new LinkedHashMap<>();
                while (it.hasNext()) {
                    Roleinfo ro1 = it.next();
                    roleNameIdMap.put(ro1.getName(), ro1.getId());
                }
            }
        }
        return roleNameIdMap;
    }

    public static void updateCityList(City city, int type) {
        switch (type) {
            case StaticFields.ADD:
                getCityList().add(city);
                break;
            case StaticFields.DELETE:
                getCityList().remove(city);
                break;
            case StaticFields.UPDATE:
                getCityList().remove(city);
                getCityList().add(city);
                break;
        }
    }

    public static void updatePositionList(Position position, int type) {
        switch (type) {
            case StaticFields.ADD:
                getPositionList().add(position);
                break;
            case StaticFields.DELETE:
                getPositionList().remove(position);
                break;
            case StaticFields.UPDATE:
                getPositionList().remove(position);
                getPositionList().add(position);
                break;
        }
    }

    public static void updateNewsList(News news, int type) {
        switch (type) {
            case StaticFields.ADD:
                getNewsList().add(news);
                recentNewsMap.get(news.getInputor().getNameofunitid()).add(news);
                break;
            case StaticFields.DELETE:
                getNewsList().remove(news);
                recentNewsMap.get(news.getInputor().getNameofunitid()).remove(news);
                break;
            case StaticFields.UPDATE:
                getNewsList().remove(news);
                getNewsList().add(news);
//                for (News temNews : getNewsList()) {
//                    if (temNews.getId() == news.getId()) {
//                        temNews.setNewsTitle(news.getNewsTitle());
//                        temNews.setContent(news.getContent());
//                        break;
//                    }
//                }
                recentNewsMap.get(news.getInputor().getNameofunitid()).remove(news);
                recentNewsMap.get(news.getInputor().getNameofunitid()).add(news);
                break;
        }
    }

    public static List<City> getCityList() {
        if (null == cityList || cityList.isEmpty()) {
            cityList = cityDao.findAll();
        }
        return cityList;
    }

    public static List<Position> getPositionList() {
        if (null == positionList || positionList.isEmpty()) {
            positionList = positionDao.findAll();
        }
        return positionList;
    }

    public static LinkedHashMap<String, Integer> getPositionMap() {
        if (null == positionMap || positionMap.isEmpty()) {
            positionMap = new LinkedHashMap<>();
            Iterator<Position> it = getPositionList().iterator();
            while (it.hasNext()) {
                Position po = it.next();
                positionMap.put(po.getName(), po.getId());
            }
        }
        return positionMap;
    }

    public static LinkedHashMap<Integer, String> getReversePositionMap() {
        if (null == reversePositionMap || reversePositionMap.isEmpty()) {
            reversePositionMap = new LinkedHashMap<>();
            Iterator<Position> it = getPositionList().iterator();
            while (it.hasNext()) {
                Position po = it.next();
                reversePositionMap.put(po.getId(), po.getName());
            }
        }
        return reversePositionMap;
    }
}
