/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Checkrecords;
import entities.City;
import entities.User;
import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import tools.ForCallBean;
import tools.SQLTool;
import tools.UserAnalysis;

/**
 *
 * @author myPC
 */
@ManagedBean
@SessionScoped
public class StuSeeCheckRecord implements Serializable {

    private SQLTool<Checkrecords> checkDao = new SQLTool<Checkrecords>();
    private SQLTool<City> cityDao = new SQLTool<City>();
    private SQLTool<User> userDao = new SQLTool<User>();
    private Checkrecords checkrecord = new Checkrecords();
    private City city = new City();
    private User student = new User();
    private List<Checkrecords> submittedRecordList;
    private String checkDate;
    private User loginUser = new ForCallBean().getUser();

    public String directToNote() {
        FacesContext context = FacesContext.getCurrentInstance();
        checkDate = context.getExternalContext().getRequestParameterMap().get("checkDate");
        String stuno = context.getExternalContext().getRequestParameterMap().get("studentNo");
        String teachno = context.getExternalContext().getRequestParameterMap().get("teacherNo");
        String schoolId = UserAnalysis.getSchoolId(stuno);
        checkrecord = checkDao.getBeanListHandlerRunner("select * from checkrecords" + schoolId + " where stuno='" + stuno + "' and checkdate='" + checkDate + "' and teachno='" + teachno + "'", getCheckrecord()).get(0);
        checkrecord.setSchoolId(schoolId);
        city = cityDao.getBeanListHandlerRunner("select * from city where id in (select cityId from enterprise where id in (select enterid from stuentrel" + schoolId + " where stuno='" + stuno + "'))", city).get(0);
        student = userDao.getBeanListHandlerRunner("select * from student" + schoolId + " where uno='" + stuno + "'", student).get(0);
        student.setSchoolId(schoolId);
        return "stuSeeCheckRecord.xhtml";
    }

    /**
     * @return the submittedRecordList
     */
    public List<Checkrecords> getSubmittedRecordList() {
        submittedRecordList = checkDao.getBeanListHandlerRunner("select * from checkrecords" + loginUser.getSchoolId() + " where stuno='" + loginUser.getUno() + "' order by checkDate", getCheckrecord());
        for (Checkrecords c : submittedRecordList) {
            c.setSchoolId(loginUser.getSchoolId());
        }
        return submittedRecordList;
    }

    /**
     * @param submittedRecordList the submittedRecordList to set
     */
    public void setSubmittedRecordList(List<Checkrecords> submittedRecordList) {
        this.submittedRecordList = submittedRecordList;
    }

    /**
     * @return the checkrecord
     */
    public Checkrecords getCheckrecord() {
        return checkrecord;
    }

    /**
     * @param checkrecord the checkrecord to set
     */
    public void setCheckrecord(Checkrecords checkrecord) {
        this.checkrecord = checkrecord;
    }

    /**
     * @return the city
     */
    public City getCity() {
        return city;
    }

    /**
     * @param city the city to set
     */
    public void setCity(City city) {
        this.city = city;
    }

    /**
     * @return the student
     */
    public User getStudent() {
        return student;
    }

    /**
     * @param student the student to set
     */
    public void setStudent(User student) {
        this.student = student;
    }

    /**
     * @return the checkDate
     */
    public String getCheckDate() {
        return checkDate;
    }

    /**
     * @param checkDate the checkDate to set
     */
    public void setCheckDate(String checkDate) {
        this.checkDate = checkDate;
    }
}
