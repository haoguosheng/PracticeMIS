/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Nameofunit;
import entities.User;
import java.io.Serializable;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import tools.ConnectionManager;
import tools.ForCallBean;
import tools.SQLTool;
import tools.StaticFields;

/**
 *
 * @author myPC
 */
@ManagedBean
@SessionScoped
public class SchoolBean implements Serializable {

    private SQLTool<Nameofunit> nameDao = new SQLTool<Nameofunit>();
    private Nameofunit nameofunit = new Nameofunit();
    private List<Nameofunit> nameofunitList;
    private User loginUser = new ForCallBean().getUser();
    private String schoolId;
    private String schoolName;
    private String pinyin;
    private boolean readflag;
    private String deleteSchool, alterSchool;

    public String directToNote() {
        readflag = true;
        FacesContext context = FacesContext.getCurrentInstance();
        String sId = context.getExternalContext().getRequestParameterMap().get("schoolId");
        deleteSchool = sId;
        alterSchool = sId;
        nameofunit = nameDao.getBeanListHandlerRunner("select * from nameofunit where id='" + sId + "'", nameofunit).get(0);
        nameofunit.setSchoolId(schoolId);
        return "showSchool.xhtml";
    }

    public String deleteSchool() {
        Statement stat;
        try {
            stat = ConnectionManager.getDataSource().getConnection().createStatement();
            stat.executeUpdate("DROP table CheckRecords" + deleteSchool);
            stat.executeUpdate("DROP Table PracticeNote" + deleteSchool);
            stat.executeUpdate("DROP Table StuEntRel" + deleteSchool);
            stat.executeUpdate("DROP TABLE Student" + deleteSchool);
            nameDao.executUpdate("delete from nameofunit where id='" + deleteSchool + "'");
            stat.close();
        } catch (SQLException ex) {
            Logger.getLogger(SchoolBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        nameofunitList = nameDao.getBeanListHandlerRunner("select * from nameofunit where parentid='000' and id!='000' order by id", nameofunit);
        readflag = true;
        return "viewSchools.xhtml";
    }

    public String editSchool() {
        readflag = false;
        return "showSchool.xhtml";
    }

    public String alterSchool() {
        nameDao.executUpdate("update nameofunit set name='" + nameofunit.getName() + "',pinyin='" + nameofunit.getPinyin() + "'" + " where id='" + alterSchool + "'");
        nameofunitList = nameDao.getBeanListHandlerRunner("select * from nameofunit where parentid='000' and id!='000' order by id", nameofunit);
        return "viewSchools.xhtml";
    }

    public String addSchool() {
        if (nameDao.getBeanListHandlerRunner("select * from nameofunit where id='" + schoolId + "'", nameofunit).size() > 0) {
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("该学院已经存在，请重新添加！"));
        } else {
            Statement stat;
            try {
                stat = ConnectionManager.getDataSource().getConnection().createStatement();
                stat.executeUpdate("Create Table Student" +StaticFields.currentGradeNum+ schoolId + "(UNO varchar(10) not null primary key,Password varchar(20),NameofUnitId char(3) references nameofunit(id),Name varchar(30),Email varchar(20),Phone varchar(15),RoleId Integer references roleinfo(id) default 2)");
                stat.executeUpdate("create table CheckRecords" +StaticFields.currentGradeNum+ schoolId + "(id integer not null generated always as identity(start with 1, increment by 1) primary key,stuNo varchar(10) references Student" +StaticFields.currentGradeNum+schoolId + "(uno),teachNo varchar(10) references TeacherInfo (uno),checkDate date,checkContent varchar(1000),recommendation varchar(500),rank varchar(10), remark varchar(200))");
                stat.executeUpdate("Create Table PracticeNote" +StaticFields.currentGradeNum+ schoolId + "(id integer not null generated always as identity(start with 1, increment by 1) primary key,StuNo varchar(10) references Student" +StaticFields.currentGradeNum+ schoolId + "(uno),Detail varchar(2000),SubmitDate date default date(current_date),EnterId Integer references Enterprise(ID),PositionId Integer references Position(ID))");
                stat.executeUpdate("Create Table StuEntRel" +StaticFields.currentGradeNum+ schoolId + "(Id Integer not null generated always as identity (start with 1, increment by 1) primary key,StuNo VARCHAR(10) references Student" +StaticFields.currentGradeNum+ schoolId + "(uno),EnterID Integer references Enterprise(Id))");
                nameDao.executUpdate("insert into nameofunit(id, name, parentid, pinyin, userno) values('" + schoolId + "', '" + schoolName + "', '000', '" + pinyin + "','" + loginUser.getUno() + "')");
                stat.close();
            } catch (SQLException ex) {
                Logger.getLogger(SchoolBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("添加新学院成功！"));
            schoolId = null;
            schoolName = null;
            pinyin = null;
        }
        return null;
    }

    /**
     * @return the nameofunit
     */
    public Nameofunit getNameofunit() {
        return nameofunit;
    }

    /**
     * @param nameofunit the nameofunit to set
     */
    public void setNameofunit(Nameofunit nameofunit) {
        this.nameofunit = nameofunit;
    }

    /**
     * @return the nameofunitList
     */
    public List<Nameofunit> getNameofunitList() {
        if (nameofunitList == null) {
            nameofunitList = nameDao.getBeanListHandlerRunner("select * from nameofunit where parentid='000' and id!='000' order by id", nameofunit);
        }
        return nameofunitList;
    }

    /**
     * @param nameofunitList the nameofunitList to set
     */
    public void setNameofunitList(List<Nameofunit> nameofunitList) {
        this.nameofunitList = nameofunitList;
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
     * @return the schoolName
     */
    public String getSchoolName() {
        return schoolName;
    }

    /**
     * @param schoolName the schoolName to set
     */
    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    /**
     * @return the pinyin
     */
    public String getPinyin() {
        return pinyin;
    }

    /**
     * @param pinyin the pinyin to set
     */
    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
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
}
