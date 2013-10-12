/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import entities.Nameofunit;
import entities.News;
import entities.Resourceinfo;
import entities.Roleinfo;
import entities.User;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
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
public class PublicFields implements java.io.Serializable{

    private String tag = "左边没有就在这里添加";
    private Calendar c = Calendar.getInstance();
    private int year = c.get(Calendar.YEAR), month = c.get(Calendar.MONTH), currentMonth = month;
    private LinkedHashMap<Integer, Integer> yearMap;
    private LinkedHashMap<Integer, Integer> monthMap;
    private LinkedHashMap<Integer, Integer> dayMap = new LinkedHashMap<>();
    private static List<String> unitIdList = new ArrayList<>();
    private static List<Nameofunit> myunitList = new LinkedList<>();
    private static SQLTool<Resourceinfo> resDao = new SQLTool<>();
    private static SQLTool<Nameofunit> nameofUnitDao = new SQLTool<>();
    private static SQLTool<News> newsDao = new SQLTool<>();
    private static LinkedHashMap<Integer, HashMap<Resourceinfo, List<Resourceinfo>>> ReslistMap;//每个角色对应的功能菜单
    private static SQLTool<Roleinfo> roleDao = new SQLTool<>();
    private static LinkedHashMap<String, List<News>>  recentNewsMap = new LinkedHashMap<>();
    
 
    public static LinkedHashMap<String,List<News>> getRecentNewsMap() {
       List<News> recentNews= getNewsList();
        List<Nameofunit> schoolList=PublicFields.getSchoolUnitList();
        //unitNewsList存放每个学院的news，首先初始化，然后对所有news遍历，分别把相应的news放入其中
        for (int i = 0; i < schoolList.size(); i++) {
              recentNewsMap.put(schoolList.get(i).getId(), new LinkedList<News>());
        }
        for (int i = 0; i < recentNews.size(); i++) {
            News tem=recentNews.get(i);
            User user=tem.getTeacher();
            recentNewsMap.get(user.getNameofunitid()).add(tem);
        }
        return recentNewsMap;
    }

    private static void calcuListResList() {
        //获得角色种类，把每个角色的一个List放到Map里作为一个元素
        List<Roleinfo> roleList = roleDao.getBeanListHandlerRunner("select * from roleinfo", new Roleinfo());
        ReslistMap = new LinkedHashMap<>();
        for (int i = 0; i < roleList.size(); i++) {
            //准备第个角色的功能菜单;
            //准备父菜单
            String temSqlString = "select * from RESOURCEINFO where id in (" + roleList.get(i).getResouceids() + ") and parentid is null order by menuorder";
            List<Resourceinfo> parentResource = resDao.getBeanListHandlerRunner(temSqlString, new Resourceinfo());
            //为每个父菜单准备子菜单
            LinkedHashMap<Resourceinfo, List<Resourceinfo>> menu = new LinkedHashMap();
            for (int j = 0; j < parentResource.size(); j++) {
                String temChildSqlString = "select * from RESOURCEINFO where parentid=" + parentResource.get(j).getId() + " and id in (" + roleList.get(i).getResouceids() + ")  order by menuorder";
                List<Resourceinfo> childrenResource = resDao.getBeanListHandlerRunner(temChildSqlString, new Resourceinfo());
                menu.put(parentResource.get(j), childrenResource);
            }
            ReslistMap.put(roleList.get(i).getId(), menu);
        }
    }

    private static List<News> getNewsList() {
        Calendar c1 = Calendar.getInstance();
        c1.add(Calendar.DAY_OF_MONTH, -StaticFields.newsDisplayDay);
        String sqlString = "select *  from news" + StaticFields.currentGradeNum + " where date(inputdate)>date('" + c1.get(Calendar.YEAR) + "-" + c1.get(Calendar.MONTH) + "-" + c1.get(Calendar.DAY_OF_MONTH) + "')";
        List<News> temNewslist = newsDao.getBeanListHandlerRunner(sqlString, new News());
        return temNewslist;
    }

    public static List<String> getUnitIdList() {
        if (unitIdList.isEmpty()) {
            unitIdList = nameofUnitDao.getIdListHandlerRunner("select id from nameofunit" + StaticFields.currentGradeNum);
        }
        return unitIdList;
    }

    public static List<Nameofunit> getSchoolUnitList() {
        if (myunitList.isEmpty()) {
            myunitList = nameofUnitDao.getBeanListHandlerRunner("select * from nameofunit" + StaticFields.currentGradeNum + "  where parentid='" + StaticFields.universityId + "'order by pri", new Nameofunit());
        }
        return  myunitList;
    }

    /**
     * @return the ReslistMap
     */
    public static LinkedHashMap<Integer, HashMap<Resourceinfo, List<Resourceinfo>>> getReslistMap() {
        if (null == ReslistMap || ReslistMap.isEmpty()) {
            calcuListResList();
        }
        return ReslistMap;
    }

    /**
     * @return the yearMap
     */
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

    /**
     * @return the tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * @return the month
     */
    public int getMonth() {
        return month;
    }

    /**
     * @param month the month to set
     */
    public void setMonth(int month) {
        this.month = month;
    }
}
