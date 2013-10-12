/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.City;
import entities.User;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import tools.PublicFields;
import tools.RepeatPaginator;
import tools.SQLTool;
import tools.StaticFields;

/**
 *
 * @author myPC
 */
@Named
@SessionScoped
public class CityBackingBean implements java.io.Serializable {

    @Inject
   private  CheckLogin checkLogin;
    private SQLTool<City> cDao ;
    private LinkedHashMap<String, Integer> cityMap;
    private String newCityName = new PublicFields().getTag();
    private List<City> ci;//褚强
    private static boolean isNull = true;
    private RepeatPaginator paginator;
    private String searchName = "请输入要选择城市";

    /**
     * @return the cityList
     */
    @PostConstruct
    public void init(){
        cDao = new SQLTool<>();
    }
    public LinkedHashMap<String, Integer> getCityMap() {
        if (null == cityMap||cityMap.isEmpty()) {
            cityMap = new LinkedHashMap<>();
            String sql = "select * from city" + StaticFields.currentGradeNum + " order by pinyin";
            List<City> listCity = cDao.getBeanListHandlerRunner(sql, new City());
            for (Iterator<City> it = listCity.iterator(); it.hasNext();) {
                City city2 = it.next();
                cityMap.put(city2.getName(), city2.getId());
            }
        }
        return cityMap;
    }

    public String getNewCityName() {
        return newCityName;
    }

    public void setNewCityName(String newCityName) {
        this.newCityName = newCityName.trim();
    }

    public String add() {
        if (this.newCityName.length() > 0 && !this.newCityName.equals(new PublicFields().getTag())) {
            if (cDao.getBeanListHandlerRunner("select * from city" + StaticFields.currentGradeNum + " where locate('" + this.newCityName + "',name)>0", new City()).size() <= 0) {
                City city = new City();
                city.setName(newCityName);
                User temUser=this.getCheckLogin().getUser();
                cDao.executUpdate("insert into city" + StaticFields.currentGradeNum + " (name, userno) values('" + newCityName + "', '" + temUser.getUno() + "')");
                this.ci = null;
                //       FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("已经把\"" + this.newCityName + "\"添加到城市列表！添加成功！"));
            } else {
                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("\"" + this.newCityName + "\"已经存在类似的城市了！添加失败！"));
            }
        } else {
            if (!this.newCityName.equals(new PublicFields().getTag())) {
                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("请输入城市名称,如果您点击的不是\"添加新城市\"，可以忽略本提示。"));
            }
        }
        this.newCityName = "左边没有就在这里添加";
        paginator = null;
        this.cityMap = null;
        this.ci = null;
        isNull = true;
        return null;
    }

    public void save(int id, String s) {
        User temUser=this.getCheckLogin().getUser();
        City city = (City) cDao.getBeanListHandlerRunner("select * from city" + StaticFields.currentGradeNum + " where id=" + id, new City()).get(0);
        city.setName(s);
        cDao.executUpdate("update city" + StaticFields.currentGradeNum + "  set name='" + s + "' , userno='" + temUser.getUno() + "' where id=" + id);
        this.ci = null;
//        Iterator<City> it = this.ci.iterator();
//        while (it.hasNext()) {
//            City temCity = it.next();
//            if (temCity.getId() == id) {
//                temCity.setName(s);
//                break;
//            }
//        }
//        Iterator<Entry<String,Integer>> it1=this.cityMap.entrySet().iterator();
//        while(it1.hasNext()){
//            Entry<String,Integer> temCity=it1.next();
//            if(temCity.getValue()==id){
//                temCity..setName(s);
//                break;
//            }
//        }
        this.cityMap = null;
    }

    public String deleteRow(City city) {
        cDao.executUpdate("delete from city" + StaticFields.currentGradeNum + "  where id=" + city.getId());
//        if (cDao.executUpdate("delete from city" +StaticFields.currentGradeNum+"  where id=" + city.getId()) > 0) {
//            FacesContext context = FacesContext.getCurrentInstance();
//            context.addMessage("globalMessages", new FacesMessage("删除成功"));
//        } else {
//            FacesContext context = FacesContext.getCurrentInstance();
//            context.addMessage("globalMessages", new FacesMessage("此城市有企业，无法删除"));
//        }
        // this.ci=null;
        if (ci != null) {
            Iterator<City> it = this.ci.iterator();
            while (it.hasNext()) {
                City temCity = it.next();
                if (temCity.getId() == city.getId()) {
                    this.ci.remove(city);
                    break;
                }
            }
        }
        this.cityMap = null;
        paginator = null;
        return null;
    }

    /**
     * @return the ci
     */
    public List<City> getCi() {
        if (null == this.ci && isNull == true) {
            this.ci = cDao.getBeanListHandlerRunner("select * from city" + StaticFields.currentGradeNum + " order by id", new City());
        }
        if (null == this.ci && isNull == false) {
            this.ci = cDao.getBeanListHandlerRunner("select * from city" + StaticFields.currentGradeNum + " where locate('" + this.searchName + "',name)>0", new City());
        }
        return this.ci;
    }

    /**
     * @param ci the ci to set
     */
    public void setCi(List<City> ci) {
        this.ci = ci;
    }

    /**
     * @return the searchName
     */
    public String getSearchName() {
        return searchName;
    }

    /**
     * @param searchName the searchName to set
     */
    public void setSearchName(String searchName) {
        this.searchName = searchName.trim();
        this.ci = null;
    }

    public String search() {
        paginator = null;
        isNull = false;
        this.ci = null;
        return null;
    }

    public String searchAll() {
        isNull = true;
        searchName = "请输入要选择城市";
        newCityName = "左边没有就在这里添加";
        this.ci = null;
        paginator = null;
        return null;
    }

    /**
     * @return the paginator
     */
    public RepeatPaginator getPaginator() {
        if (paginator == null) {
            paginator = new RepeatPaginator(this.getCi(), 10);
            paginator.init();
        }
        return paginator;
    }

    /**
     * @param paginator the paginator to set
     */
    public void setPaginator(RepeatPaginator paginator) {
        this.paginator = paginator;
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
