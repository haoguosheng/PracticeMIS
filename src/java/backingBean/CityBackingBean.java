/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.City;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import tools.ForCallBean;
import tools.PublicFields;
import tools.RepeatPaginator;
import tools.SQLTool;

/**
 *
 * @author myPC
 */
@ManagedBean
@ApplicationScoped
public class CityBackingBean {

    private SQLTool<City> cDao = new SQLTool<City>();
    private LinkedHashMap<String, Integer> cityMap;
    private String newCityName = new PublicFields().getTag();
    private List<City> ci;//褚强
    private static boolean isNull = true;
    private RepeatPaginator paginator;
    private String searchName = "请输入要选择城市";

    /**
     * @return the cityList
     */
    public LinkedHashMap<String, Integer> getCityMap() {
        if (null == cityMap) {
            cityMap = new LinkedHashMap<String, Integer>();
            String sql = "select * from city order by pinyin";
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
            if (cDao.getBeanListHandlerRunner("select * from city where locate('" + this.newCityName + "',name)>0", new City()).size() <= 0) {
                City city = new City();
                city.setName(newCityName);
                cDao.executUpdate("insert into city(name, userno) values('" + newCityName + "', '" + new ForCallBean().getUser().getUno() + "')");
                this.ci = null;
                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("已经把\"" + this.newCityName + "\"添加到城市列表！添加成功！"));
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
        City city = (City) cDao.getBeanListHandlerRunner("select * from city where id=" + id, new City()).get(0);
        city.setName(s);
        cDao.executUpdate("update city set name='" + s + "' , userno='" + new ForCallBean().getUser().getUno() + "' where id=" + id);
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

        if (cDao.executUpdate("delete from city where id=" + city.getId()) > 0) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage("globalMessages", new FacesMessage("删除成功"));
        } else {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage("globalMessages", new FacesMessage("此城市有企业，无法删除"));
        }
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
            this.ci = cDao.getBeanListHandlerRunner("select * from city order by id", new City());
        }
        if (null == this.ci && isNull == false) {
            this.ci = cDao.getBeanListHandlerRunner("select * from city where locate('" + this.searchName + "',name)>0", new City());
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
}
