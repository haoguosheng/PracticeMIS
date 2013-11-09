/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import entitiesBeans.CityLocal;
import entitiesBeans.EnterstudentLocal;
import entitiesBeans.UserLocal;
import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import tools.StaticFields;
import tools.UserAnalysis;

/**
 *
 * @author Administrator
 */
@Named
@SessionScoped
public class Enterprise implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private Integer cityId = 0;
    private String enterurl;
    private String contactname;
    private String contacttelephone;
    private String contactaddress;
    private String userno;
    private String pinyin;
    private City city;
    private final CityLocal cityLocal = new CityLocal();
    private List<Enterstudent> enterstudentList;
    private final EnterstudentLocal entStuLocal = new EnterstudentLocal();
    private User inputor;
    private final UserLocal userLocal = new UserLocal();


    public Enterprise() {
    }

    public Enterprise(Integer id) {
        this.id = id;
    }
   

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getEnterurl() {
        return enterurl;
    }

    public void setEnterurl(String enterurl) {
        this.enterurl = enterurl;
    }

    public String getContactname() {
        return contactname;
    }

    public void setContactname(String contactname) {
        this.contactname = contactname;
    }

    public String getContacttelephone() {
        return contacttelephone;
    }

    public void setContacttelephone(String contacttelephone) {
        this.contacttelephone = contacttelephone;
    }

    public String getContactaddress() {
        return contactaddress;
    }

    public void setContactaddress(String contactaddress) {
        this.contactaddress = contactaddress;
    }

    public String getUserno() {
        return userno;
    }

    public void setUserno(String userno) {
        this.userno = userno;
    }

    public City getCity() {
        if (city == null) {
            this.dealId0();
        } else if (null == this.city.getId()) {
            this.dealId0();
        }
        return city;
    }
    /*
     *当对象是内在对象时处理外键City
     */

    private void dealId0() {
        if (null == this.id) {
            if (null == city) {
                this.city = new City();
            }
        } else {
            this.city = cityLocal.getList("select * from city" + StaticFields.currentGradeNum + " where id=" + this.getCityId()).get(0);
        }
    }

    public void setCity(City city) {
        this.city = city;
    }

    public List<Enterstudent> getEnterstudentList() {
        if (null == enterstudentList && null != id) {
            if (id != 0) {
                enterstudentList = entStuLocal.getList("select * from enterstudent" + StaticFields.currentGradeNum + " where enterid=" + id);
            }
        }
        return enterstudentList;
    }

    public void setEnterstudentList(List<Enterstudent> enterstudentList) {
        this.enterstudentList = enterstudentList;
    }

    public User getInputor() {
        if (inputor == null) {
            this.dealId0Inputor();
        } else if (null == this.inputor.getUno()) {
            this.dealId0Inputor();
        }
        return inputor;
    }
    /*
     *当对象是内在对象时处理外键inputor
     */

    private void dealId0Inputor() {
        if (null == this.id) {
            if (null == inputor) {
                this.inputor = new User();
            }
        } else {
            String tableName = UserAnalysis.getTableName(this.getUserno());
            if (tableName.contains("studen")) {
                String temSchoolId = UserAnalysis.getSchoolId(this.getUserno());
                this.inputor = userLocal.getList("select * from " + tableName + StaticFields.currentGradeNum + temSchoolId + " where  uno='" + this.getUserno() + "'").get(0);
            } else if (tableName.contains("tea")) {
                this.inputor = userLocal.getList("select * from " + tableName + StaticFields.currentGradeNum + " where  uno='" + this.getUserno() + "'").get(0);
            }
        }
    }

    public void setInputor(User myUser) {
        this.inputor = myUser;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }
}
