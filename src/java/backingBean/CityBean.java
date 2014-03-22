package backingBean;

import entities.City;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import sessionBeans.CityFacadeLocal;
import tools.StaticFields;

@Named
@SessionScoped
public class CityBean implements java.io.Serializable {

    @EJB
    CityFacadeLocal cityEjb;
    private City province, city, district;
    private List<City> districtList=new LinkedList<>();
    private List<City> cityList=new LinkedList<>();

    public String search() {
        this.cityList = cityEjb.getList("select * from city where locate('" + this.city.getName() + "',name)>0");
        return "cityInfo";
    }

    public String searchAll() {
        this.cityList = cityEjb.findAll();
        return "cityInfo";
    }
    public List<City> getDistrictList() {
        districtList.clear();
        if (getProvince().getId() != null && getProvince().getId() != 0 && getCity().getId() != null && getCity().getId() != 0) {
            districtList = getCity().getCityList();
        }
        return districtList;
    }

    public City getProvince() {
        if (null == this.province) {
            this.province = new City();
        }if(null!=this.province.getId()&&this.province.getId()!=0){
            this.province=cityEjb.find(this.province.getId());
        }
        return province;
    }

    public void setProvince(City province) {
        this.province = province;
    }

    public City getCity() {
        if (null == this.city) {
            this.city = new City();
        }else if(null!=this.city.getId()&&this.city.getId()!=0){
            this.city=cityEjb.find(this.city.getId());
        }
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public City getDistrict() {
        if (null == this.district) {
            this.district = new City();
        }else if(null!=this.district.getId()&&this.district.getId()!=0){
            this.district=cityEjb.find(this.district.getId());
        }
        return district;
    }

    public void setDistrict(City district) {
        this.district = district;
    }

    public List<City> getCityList() {
        if(null!=this.province&&null!=this.province.getId()&&0!=this.province.getId()){
            this.cityList=this.getProvince().getCityList();
        }
        return cityList;
    }

}
