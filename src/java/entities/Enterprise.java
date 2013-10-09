/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.List;
import tools.SQLTool;
import tools.StaticFields;

/**
 *
 * @author Administrator
 */
public class Enterprise implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private int cityId;
    private String enterurl;
    private String contactname;
    private String contacttelephone;
    private String contactaddress;
    private String userno;
    private String schoolId;
    private City city;
    private List<Enterstudent> enterstudentList;
    private List<Practicenote> practicenoteList;
    private List<Stuentrel> stuentrelList;
    private SQLTool<City> cityDao = new SQLTool<City>();
    private SQLTool<Enterstudent> esDao = new SQLTool<Enterstudent>();
    private SQLTool<Practicenote> practDao = new SQLTool<Practicenote>();
    private SQLTool<Stuentrel> selDao = new SQLTool<Stuentrel>();
    private User myUser;

    public Enterprise() {
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the cityId
     */
    public int getCityId() {
        return cityId;
    }

    /**
     * @param cityId the cityId to set
     */
    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    /**
     * @return the enterurl
     */
    public String getEnterurl() {
        return enterurl;
    }

    /**
     * @param enterurl the enterurl to set
     */
    public void setEnterurl(String enterurl) {
        this.enterurl = enterurl;
    }

    /**
     * @return the contactname
     */
    public String getContactname() {
        return contactname;
    }

    /**
     * @param contactname the contactname to set
     */
    public void setContactname(String contactname) {
        this.contactname = contactname;
    }

    /**
     * @return the contacttelephone
     */
    public String getContacttelephone() {
        return contacttelephone;
    }

    /**
     * @param contacttelephone the contacttelephone to set
     */
    public void setContacttelephone(String contacttelephone) {
        this.contacttelephone = contacttelephone;
    }

    /**
     * @return the contactaddress
     */
    public String getContactaddress() {
        return contactaddress;
    }

    /**
     * @param contactaddress the contactaddress to set
     */
    public void setContactaddress(String contactaddress) {
        this.contactaddress = contactaddress;
    }

    /**
     * @return the userno
     */
    public String getUserno() {
        return userno;
    }

    /**
     * @param userno the userno to set
     */
    public void setUserno(String userno) {
        this.userno = userno;
    }

    /**
     * @return the city
     */
    public City getCity() {
        if (city == null&&cityId!=0) {
            city = cityDao.getBeanListHandlerRunner("select * from city"+new StaticFields().currentGradeNum+" where id=" + cityId, new City()).get(0);
        }
        return city;
    }

    /**
     * @param city the city to set
     */
    public void setCity(City city) {
        this.city = city;
    }

    /**
     * @return the enterstudentList
     */
    public List<Enterstudent> getEnterstudentList() {
        if (enterstudentList == null) {
            enterstudentList = esDao.getBeanListHandlerRunner("select * from enterstudent"+StaticFields.currentGradeNum+" where enterid=" + id, new Enterstudent());
        }
        return enterstudentList;
    }

    /**
     * @param enterstudentList the enterstudentList to set
     */
    public void setEnterstudentList(List<Enterstudent> enterstudentList) {
        this.enterstudentList = enterstudentList;
    }

    /**
     * @return the practicenoteList
     */
    public List<Practicenote> getPracticenoteList() {
        if (practicenoteList == null) {
            practicenoteList = practDao.getBeanListHandlerRunner("select * from practicenote" +StaticFields.currentGradeNum+ schoolId + " where enterid=" + id, new Practicenote());
        }
        return practicenoteList;
    }

    /**
     * @param practicenoteList the practicenoteList to set
     */
    public void setPracticenoteList(List<Practicenote> practicenoteList) {
        this.practicenoteList = practicenoteList;
    }

    /**
     * @return the stuentrelList
     */
    public List<Stuentrel> getStuentrelList() {
        if (stuentrelList == null) {
            stuentrelList = selDao.getBeanListHandlerRunner("select * from stuentrel" +StaticFields.currentGradeNum+ schoolId + " where enterid=" + id, new Stuentrel());
            for (Stuentrel s : stuentrelList) {
                s.setSchoolId(schoolId);
            }
        }
        return stuentrelList;
    }

    /**
     * @param stuentrelList the stuentrelList to set
     */
    public void setStuentrelList(List<Stuentrel> stuentrelList) {
        this.stuentrelList = stuentrelList;
    }

    /**
     * @return the schoolId
     */
    public String getSchoolId() {
        return schoolId;
    }

    /**
     * @param schoolId the schoolId to set
     */
    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    /**
     * @return the myUser
     */
    public User getMyUser() {
        return myUser;
    }

    /**
     * @param myUser the myUser to set
     */
    public void setMyUser(User myUser) {
        this.myUser = myUser;
    }
}
