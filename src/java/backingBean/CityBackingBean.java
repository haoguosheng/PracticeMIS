/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.City;
import entities.User;
import entitiesBeans.CityLocal;
import entitiesBeans.UserLocal;
import java.util.LinkedHashMap;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import tools.PublicFields;
import tools.RepeatPaginator;
import tools.StaticFields;

/**
 *
 * @author myPC 提供关于城市的list 提供关于城市的查询
 */
@Named
@SessionScoped
public class CityBackingBean implements java.io.Serializable {

   
    private @Inject City city;
    private @Inject
    User user;
    private final CityLocal cDao = new CityLocal();
    private LinkedHashMap<String, Integer> cityMap;
    private List<City> cityList;
    private RepeatPaginator paginator;
    private String searchName = StaticFields.citySelect;

    public LinkedHashMap<String, Integer> getCityMap() {
        if (null == cityMap || cityMap.isEmpty()) {
            cityMap = new LinkedHashMap<>();
            List<City> listCity = PublicFields.getCityList();
            for (City city2 : listCity) {
                cityMap.put(city2.getName(), city2.getId());
            }
        }
        return cityMap;
    }

    public String add() {
        if (cDao.getList("select * from city" + StaticFields.currentGradeNum + " where locate('" + this.city.getName() + "',name)>0").size() <= 0) {
            city.setUserno(this.getUser().getUno());
            cDao.create(city);
            paginator = null;
            this.cityMap = null;
            City tem = cDao.getList("select * from city where name='" + city.getName() + "'").get(0);
            PublicFields.updateCityList(tem, StaticFields.ADD);
        } else {
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("\"" + this.city.getName() + "\"已经存在类似的城市了！添加失败！"));
        }
        return null;
    }

    public void save(City city) {
        City cityTem = (City) cDao.find(city.getId());
        cityTem.setUserno(this.getUser().getUno());
        cDao.edit(cityTem);
        PublicFields.updateCityList(cityTem, StaticFields.UPDATE);
        this.cityMap = null;
        this.cityList = null;
        this.paginator=null;
    }

    public String deleteRow(City city) {
        cDao.remove(city);
        PublicFields.updateCityList(city, StaticFields.DELETE);
        this.cityList = null;
        this.cityMap = null;
        paginator = null;
        return null;
    }

    public List<City> getCityList() {
        if (null == this.searchName || this.searchName.trim().length() == 0 || this.searchName.equals(StaticFields.citySelect)) {
            this.cityList = PublicFields.getCityList();
        } else {
            this.cityList = cDao.getList("select * from city" + StaticFields.currentGradeNum + " where locate('" + this.searchName + "',name)>0");
        }
        return this.cityList;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName.trim();
        this.cityList = null;
    }

    public String search() {
        paginator = null;
        this.cityList = null;
        return null;
    }

    public String searchAll() {
        searchName = null;
        this.cityList = null;
        paginator = null;
        return null;
    }

    public RepeatPaginator getPaginator() {
        if (paginator == null) {
            paginator = new RepeatPaginator(this.getCityList(), 10);
        }
        return paginator;
    }

    public void setPaginator(RepeatPaginator paginator) {
        this.paginator = paginator;
    }

    public User getUser() {
//            FacesContext context = FacesContext.getCurrentInstance();
//            HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
//            user = (User) session.getAttribute("myUser");
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
    private boolean finded = false;
    User temUser;

    public boolean buttonShoworNot(City city) {
        if (!finded) {
            temUser = new UserLocal().find(user.getUno());
            finded=true;
        }
        return temUser.getRoleinfo().getCanseeall() == 0 || city.getUserno().equals( user.getUno());
    }
}
