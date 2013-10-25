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
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import tools.SQLTool;
import tools.StaticFields;
import tools.UserAnalysis;

/**
 *
 * @author myPC
 */
@Named
@SessionScoped
public class TeachSeeCheckRecords implements Serializable {

     @Inject
   private  CheckLogin checkLogin;
    private SQLTool<Checkrecords> checkDao;
    private SQLTool<City> cityDao;
    private SQLTool<User> userDao;
    private Checkrecords checkrecord;
    private City city;
    private User student;
    private List<Checkrecords> submittedRecordList;
    private String checkDate;
    private User loginUser;
    private String deleteRepDate, alterDate;
    private boolean readflag;

    @PostConstruct
    public void init() {
        checkDao = new SQLTool<Checkrecords>();
        cityDao = new SQLTool<City>();
        userDao = new SQLTool<User>();
        checkrecord = new Checkrecords();
        city = new City();
        student = new User();
        loginUser = checkLogin.getUser();
    }

    public String directToNote() {
        setReadflag(true);
        FacesContext context = FacesContext.getCurrentInstance();
        checkDate = context.getExternalContext().getRequestParameterMap().get("checkDate");
        String stuno = context.getExternalContext().getRequestParameterMap().get("studentNo");
        deleteRepDate = checkDate;
        alterDate = checkDate;
        String schoolId = UserAnalysis.getSchoolId(stuno);
        checkrecord = checkDao.getBeanListHandlerRunner("select * from checkrecords" + StaticFields.currentGradeNum + schoolId + " where stuno='" + stuno + "' and checkdate='" + checkDate + "'", getCheckrecord()).get(0);
        checkrecord.setSchoolId(schoolId);
        city = cityDao.getBeanListHandlerRunner("select * from city" + StaticFields.currentGradeNum + " where id in (select cityId from enterprise" + StaticFields.currentGradeNum + " where id in (select enterid from stuentrel" + StaticFields.currentGradeNum + schoolId + " where stuno='" + stuno + "'))", city).get(0);
        student = userDao.getBeanListHandlerRunner("select * from student" + StaticFields.currentGradeNum + schoolId + " where uno='" + stuno + "'", student).get(0);
        student.setSchoolId(schoolId);
        return "showCheckRecord.xhtml";
    }

    public String deleteSelectRecord() {
        String s = deleteRepDate;
        checkDao.executUpdate("delete from checkrecords" + StaticFields.currentGradeNum + loginUser.getSchoolId() + " where stuno='" + student.getUno() + "' and checkdate='" + s + "' and teachno='" + loginUser.getUno() + "'");
        submittedRecordList = checkDao.getBeanListHandlerRunner("select * from checkrecords" + StaticFields.currentGradeNum + loginUser.getSchoolId() + " where teachno='" + loginUser.getUno() + "'", checkrecord);
        readflag = true;
        return "viewCheckRecords.xhtml";
    }

    public String editSelectRecord() {
        readflag = false;
        return "showCheckRecord.xhtml";
    }

    public String alterSelectRecord() {
        String s = alterDate;
        checkDao.executUpdate("update checkrecords" + StaticFields.currentGradeNum + loginUser.getSchoolId() + " set checkcontent='" + checkrecord.getCheckcontent() + "', recommendation='" + checkrecord.getRecommendation() + "', rank='" + checkrecord.getRank() + "', remark='" + checkrecord.getRemark() + "' where stuno='" + student.getUno() + "' and checkdate='" + s + "' and teachno='" + loginUser.getUno() + "'");
        submittedRecordList = checkDao.getBeanListHandlerRunner("select * from checkrecords" + StaticFields.currentGradeNum + loginUser.getSchoolId() + " where teachno='" + loginUser.getUno() + "'", checkrecord);
        return "viewCheckRecords.xhtml";
    }

    /**
     * @return the submittedRecordList
     */
    public List<Checkrecords> getSubmittedRecordList() {
//        if (submittedRecordList == null) {
        submittedRecordList = checkDao.getBeanListHandlerRunner("select * from checkrecords" + StaticFields.currentGradeNum + loginUser.getSchoolId() + " where teachno='" + loginUser.getUno() + "' order by stuno", getCheckrecord());
//        }
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
     * @return the readflag
     */
    public boolean isReadflag() {
        return readflag;
    }

    /**
     * @param readflag the readflag to set
     */
    public void setReadflag(boolean readflag) {
        this.readflag = readflag;
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
