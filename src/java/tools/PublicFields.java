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
import entities.Teacherinfo;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import sessionBeans.CityFacadeLocal;
import sessionBeans.NameofunitFacadeLocal;
import sessionBeans.NewsFacadeLocal;
import sessionBeans.PositionFacadeLocal;
import sessionBeans.ResourceinfoFacadeLocal;
import sessionBeans.RoleinfoFacadeLocal;

/**
 *
 * @author Idea
 */
@Named
@ApplicationScoped
public class PublicFields implements java.io.Serializable {
    @EJB
    private PositionFacadeLocal positionEjb;
    @EJB
    private CityFacadeLocal cityEjb;
    @EJB
    private ResourceinfoFacadeLocal resourceEjb;
    @EJB
    private NameofunitFacadeLocal nameofUnitEjb;
    @EJB
    private NewsFacadeLocal newsEjb;
    @EJB
    private RoleinfoFacadeLocal roleinfoEjb;
    
    private final Calendar c = Calendar.getInstance();
    private final int year = c.get(Calendar.YEAR), currentMonth = c.get(Calendar.MONTH);
    private int month = c.get(Calendar.MONTH);
    private LinkedHashMap<Integer, Integer> yearMap;
    private LinkedHashMap<Integer, Integer> monthMap;
    private final LinkedHashMap<Integer, Integer> dayMap = new LinkedHashMap<>();
    private  List<News> newsList;
    private  List<String> nameofunitIdList = new ArrayList<>();
    private  List<Nameofunit> nameofUnitList = new LinkedList<>();
    private  LinkedHashMap<Integer, HashMap<Resourceinfo, List<Resourceinfo>>> ResourcelistMap;//每个角色对应的功能菜单
    private  LinkedHashMap<String, Integer> roleNameIdMap;
    private  LinkedHashMap<String, Integer> positionMap;
    private  LinkedHashMap<Integer, String> reversePositionMap;
    private  LinkedList<City> provinceList;
    private  List<Position> positionList;
   
    private  LinkedHashMap<String, List<News>> recentNewsMap = new LinkedHashMap<>();
    private  LinkedHashMap<String, List<News>> recentWNewsMap = new LinkedHashMap<>();
    private  LinkedHashMap<String, List<News>> recentLNewsMap = new LinkedHashMap<>();
    private  LinkedHashMap<String, List<News>> recentGNewsMap = new LinkedHashMap<>();

    public  LinkedHashMap<String, List<News>> getRecentNewsMap() {
        if (recentNewsMap.isEmpty()) {
            List<News> recentNews = getNewsList();
            recentNewsMap = new LinkedHashMap<>();
            List<Nameofunit> schoolList = this.getSchoolUnitList();
            //unitNewsList存放每个学院的news，首先初始化，然后对所有news遍历，分别把相应的news放入其中
            for (int i = 0; i < schoolList.size(); i++) {
                recentNewsMap.put(schoolList.get(i).getId(), new LinkedList<News>());
            }
            if (recentNews != null) {
                for (int i = 0; i < recentNews.size(); i++) {
                    News tem = recentNews.get(i);
                    Teacherinfo user = tem.getTeacherinfo();
                    recentNewsMap.get(user.getNameofunit().getId()).add(tem);
                }
            }
        }
        return recentNewsMap;
    }

    public  LinkedHashMap<String, List<News>> getRecentWNewsMap() {
        List<News> recentNews = getNewsList();
        List<Nameofunit> schoolList = this.getSchoolUnitList();
        //unitNewsList存放每个学院的news，首先初始化，然后对所有news遍历，分别把相应的news放入其中
        for (int i = 0; i < schoolList.size(); i++) {
            if (schoolList.get(i).getMytype() == 0) {
                recentWNewsMap.put(schoolList.get(i).getId(), new LinkedList<News>());
            }
        }
        for (int i = 0; i < recentNews.size(); i++) {
            News tem = recentNews.get(i);
           Teacherinfo user = tem.getTeacherinfo();
            if (user.getNameofunit().getMytype() == 0) {
                recentWNewsMap.get(user.getNameofunit().getId()).add(tem);
            }
        }
        return recentWNewsMap;
    }

    public  LinkedHashMap<String, List<News>> getRecentLNewsMap() {
        List<News> recentNews = getNewsList();
        List<Nameofunit> schoolList = this.getSchoolUnitList();
        //unitNewsList存放每个学院的news，首先初始化，然后对所有news遍历，分别把相应的news放入其中
        for (int i = 0; i < schoolList.size(); i++) {
            if (schoolList.get(i).getMytype() == 1) {
                recentLNewsMap.put(schoolList.get(i).getId(), new LinkedList<News>());
            }
        }
        for (int i = 0; i < recentNews.size(); i++) {
            News tem = recentNews.get(i);
            Teacherinfo user = tem.getTeacherinfo();
            if (user.getNameofunit().getMytype() == 1) {
                recentLNewsMap.get(user.getNameofunit().getId()).add(tem);
            }
        }
        return recentLNewsMap;
    }

    public  LinkedHashMap<String, List<News>> getRecentGNewsMap() {
        List<News> recentNews = getNewsList();
        List<Nameofunit> schoolList =this.getSchoolUnitList();
        //unitNewsList存放每个学院的news，首先初始化，然后对所有news遍历，分别把相应的news放入其中
        for (int i = 0; i < schoolList.size(); i++) {
            if (schoolList.get(i).getMytype() == 2) {
                recentGNewsMap.put(schoolList.get(i).getId(), new LinkedList<News>());
            }
        }
        for (int i = 0; i < recentNews.size(); i++) {
            News tem = recentNews.get(i);
           Teacherinfo user = tem.getTeacherinfo();
            if (user.getNameofunit().getMytype() == 2) {
                recentGNewsMap.get(user.getNameofunit().getId()).add(tem);
            }
        }
        return recentGNewsMap;
    }

    private  void calcuListResList() {
        //获得角色种类，把每个角色的一个List放到Map里作为一个元素
        List<Roleinfo> roleList = roleinfoEjb.findAll();
        ResourcelistMap = new LinkedHashMap<>();
        for (int i = 0; i < roleList.size(); i++) {
            //准备第个角色的功能菜单;
            //准备父菜单
            String temSqlString = "select * from RESOURCEINFO where id in (" + roleList.get(i).getResouceids() + ") and parentid is null order by menuorder";
            List<Resourceinfo> parentResource = resourceEjb.getList(temSqlString);
            //为每个父菜单准备子菜单
            LinkedHashMap<Resourceinfo, List<Resourceinfo>> menu = new LinkedHashMap();
            for (int j = 0; j < parentResource.size(); j++) {
                String temChildSqlString = "select * from RESOURCEINFO where parentid=" + parentResource.get(j).getId() + " and id in (" + roleList.get(i).getResouceids() + ")  order by menuorder";
                List<Resourceinfo> childrenResource = resourceEjb.getList(temChildSqlString);
                menu.put(parentResource.get(j), childrenResource);
            }
            ResourcelistMap.put(roleList.get(i).getId(), menu);
        }
    }

    private  List<News> getNewsList() {
        if (null == newsList) {
            Calendar c1 = Calendar.getInstance();
            c1.add(Calendar.DAY_OF_MONTH, -StaticFields.newsDisplayDay);
            String sqlString = "select *  from news where date(inputdate)>date('" + c1.get(Calendar.YEAR) + "-" + c1.get(Calendar.MONTH) + "-" + c1.get(Calendar.DAY_OF_MONTH) + "')";
            newsList = newsEjb.getList(sqlString);
        }
        return newsList;
    }

    public  List<String> getNameofunitIdList() {
        if (nameofunitIdList.isEmpty()) {
            List<Nameofunit> temp = nameofUnitEjb.findAll();//new SQLTool<Nameofunit>().getBeanListHandlerRunner("select * from nameofunit" + StaticFields.currentGradeNum, new Nameofunit());
            for(Nameofunit t:temp){
                nameofunitIdList.add(t.getId());
            }
        }
        return nameofunitIdList;
    }

    public  List<Nameofunit> getSchoolUnitList() {
        if (null == nameofUnitList || nameofUnitList.isEmpty()) {
            nameofUnitList = nameofUnitEjb.getList("select * from nameofunit  where parentid='" + StaticFields.universityId + "'order by pri");
        }
        return nameofUnitList;
    }

    public  LinkedHashMap<Integer, HashMap<Resourceinfo, List<Resourceinfo>>> getReslistMap() {
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
            List<Roleinfo> role = roleinfoEjb.getList("select * from roleinfo");
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

    public  void updatePositionList(Position position, int type) {
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

    public  void updateNewsList(News news, int type) {
        switch (type) {
            case StaticFields.ADD:
                getNewsList().add(news);
                getRecentNewsMap().get(news.getTeacherinfo().getNameofunit().getId()).add(news);
                break;
            case StaticFields.DELETE:
                getNewsList().remove(news);
                getRecentNewsMap().get(news.getTeacherinfo().getNameofunit().getId()).remove(news);
                break;
            case StaticFields.UPDATE:
                getNewsList().remove(news);
                getNewsList().add(news);
                getRecentNewsMap().get(news.getTeacherinfo().getNameofunit().getId()).remove(news);
                getRecentNewsMap().get(news.getTeacherinfo().getNameofunit().getId()).add(news);
                break;
        }
        
    }

    public  LinkedList<City> getProvinceList() {
        if (null == provinceList || provinceList.isEmpty()) {
            provinceList=new LinkedList<>();
            List<City> tem= cityEjb.findAllProvince();
            for(City city:tem){
                provinceList.add(city);
            }
        }
        return provinceList;
    }

    public  List<Position> getPositionList() {
        if (null == positionList || positionList.isEmpty()) {
            positionList = positionEjb.findAll();
        }
        return positionList;
    }

    public  LinkedHashMap<String, Integer> getPositionMap() {
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

    public  LinkedHashMap<Integer, String> getReversePositionMap() {
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
