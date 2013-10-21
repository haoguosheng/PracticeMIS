/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Checkrecords;
import entities.Nameofunit;
import entities.Practicenote;
import entities.Stuentrel;
import entities.User;
import java.io.Serializable;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import tools.ConnectionManager;
import tools.SQLTool;
import tools.StaticFields;

/**
 *
 * @author myPC
 */
@Named
@SessionScoped
public class SchoolBean implements Serializable {

    @Inject
    private CheckLogin checkLogin;
    private SQLTool<Nameofunit> nameDao;
    private SQLTool<User> userDao;
    private SQLTool<Checkrecords> checkDao;
    private SQLTool<Practicenote> pDao;
    private SQLTool<Stuentrel> seDao;
    private Nameofunit nameofunit;
    private List<Nameofunit> nameofunitList;
    private User loginUser;
    private String schoolId;
    private String schoolName;
    private String pinyin;
    private String mytype;
    private LinkedHashMap<String, Integer> mytypeMap;

    @PostConstruct
    public void init() {
        nameDao = new SQLTool<>();
        userDao = new SQLTool<>();
        checkDao = new SQLTool<>();
        pDao = new SQLTool<>();
        seDao = new SQLTool<>();
        nameofunit = new Nameofunit();
        loginUser = checkLogin.getUser();
    }

    public String deleteSchool() {
        FacesContext context = FacesContext.getCurrentInstance();
        String sId = context.getExternalContext().getRequestParameterMap().get("schoolId");
        if (userDao.getBeanListHandlerRunner("select * from student" + sId, new User()).isEmpty()
                && checkDao.getBeanListHandlerRunner("select * from checkrecords" + sId, new Checkrecords()).isEmpty()
                && pDao.getBeanListHandlerRunner("select * from practicenote" + sId, new Practicenote()).isEmpty()
                && seDao.getBeanListHandlerRunner("select * from stuentrel" + sId, new Stuentrel()).isEmpty()) {
            Statement stat = null;
            try {
                stat = ConnectionManager.getDataSource().getConnection().createStatement();
                stat.executeUpdate("DROP table CheckRecords" + sId);
                stat.executeUpdate("DROP Table PracticeNote" + sId);
                stat.executeUpdate("DROP Table StuEntRel" + sId);
                stat.executeUpdate("DROP TABLE Student" + sId);
                nameDao.executUpdate("delete from nameofunit where id='" + sId + "'");
                //  FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("删除学院成功！"));

            } catch (SQLException ex) {
                Logger.getLogger(SchoolBean.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    stat.close();
                } catch (SQLException ex) {
                    Logger.getLogger(SchoolBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("删除学院失败！"));
        }
        nameofunitList = nameDao.getBeanListHandlerRunner("select * from nameofunit where parentid='000' order by id", nameofunit);
        return "viewSchools.xhtml";
    }

    public String alterSchool(String sId, String sName, String sPinyin) {
        if (sId != null && sName != null && sPinyin != null) {
            nameDao.executUpdate("update nameofunit set name='" + sName + "',pinyin='" + sPinyin + "'" + " where id='" + sId + "'");
//            if (nameDao.executUpdate("update nameofunit" + StaticFields.currentGradeNum + "  set name='" + sName + "',pinyin='" + sPinyin + "'" + " where id='" + sId + "'") > 0) {
//                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("修改学院成功！"));
//            } else {
//                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("修改学院失败！"));
//            }
        } else if (sId != null && sName != null) {
            nameDao.executUpdate("update nameofunit set name='" + sName + "' where id='" + sId + "'");
//            if (nameDao.executUpdate("update nameofunit" + StaticFields.currentGradeNum + "  set name='" + sName + "' where id='" + sId + "'") > 0) {
//                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("修改学院成功！"));
//            } else {
//                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("修改学院失败！"));
//            }
        } else if (sId != null && sPinyin != null) {
            nameDao.executUpdate("update nameofunit set pinyin='" + sPinyin + "'" + " where id='" + sId + "'");
//            if (nameDao.executUpdate("update nameofunit" + StaticFields.currentGradeNum + "  set pinyin='" + sPinyin + "'" + " where id='" + sId + "'") > 0) {
//                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("修改学院成功！"));
//            } else {
//                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("修改学院失败！"));
//            }
        } else {
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("修改学院失败！"));
        }
        nameofunitList = nameDao.getBeanListHandlerRunner("select * from nameofunit where parentid='000' order by id", nameofunit);
        return "viewSchools.xhtml";
    }

    public String addSchool() {
        if (schoolId != null && schoolName != null) {
            if (nameDao.getBeanListHandlerRunner("select * from nameofunit where id='" + schoolId + "'", nameofunit).size() > 0) {
                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("添加新学院失败！学院编号已经被使用！"));
            } else {
                Statement stat = null;
                try {
                    stat = ConnectionManager.getDataSource().getConnection().createStatement();
                } catch (Exception e) {
                    FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("数据库连接出错！"));
                }
                try {
                    stat.executeUpdate("Create Table Student" + schoolId + "(UNO varchar(10) not null primary key,Password varchar(20),NameofUnitId varchar(10) references nameofunit(id),Name varchar(50),Email varchar(50),Phone varchar(20),RoleId Integer references roleinfo(id) default 2)");
                } catch (Exception e) {
                    FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("新建表时出错，可能该表已经存在！继续执行剩下的操作"));
                }
                try {
                    stat.executeUpdate("create table CheckRecords" + schoolId + "(id integer not null generated always as identity(start with 1, increment by 1) primary key,stuNo varchar(10) references Student" + StaticFields.currentGradeNum + schoolId + "(uno),teachNo varchar(10) references TeacherInfo (uno),checkDate date,checkContent varchar(1000),recommendation varchar(500),rank varchar(10), remark varchar(200))");
                } catch (Exception e) {
                    FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("新建表时出错，可能该表已经存在！继续执行剩下的操作"));
                }
                try {
                    stat.executeUpdate("Create Table PracticeNote" + schoolId + "(id integer not null generated always as identity(start with 1, increment by 1) primary key,StuNo varchar(10) references Student" + StaticFields.currentGradeNum + schoolId + "(uno),Detail varchar(2000),SubmitDate date default date(current_date),EnterId Integer references Enterprise(ID),PositionId Integer references Position(ID))");
                } catch (Exception e) {
                    FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("新建表时出错，可能该表已经存在！继续执行剩下的操作"));
                }
                try {
                    stat.executeUpdate("Create Table StuEntRel" + schoolId + "(Id Integer not null generated always as identity (start with 1, increment by 1) primary key,StuNo VARCHAR(10) references Student" + StaticFields.currentGradeNum + schoolId + "(uno),EnterID Integer references Enterprise(Id))");
                } catch (Exception e) {
                    FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("新建表时出错，可能该表已经存在！继续执行剩下的操作"));
                }
                try {
                    nameDao.executUpdate("insert into nameofunit" + " (id, name, parentid, pinyin,mytype, userno) values('" + schoolId + "', '" + schoolName + "', '000', '" + pinyin + "','" + getMytype() + "','" + getLoginUser().getUno() + "')");
                    nameofunitList = nameDao.getBeanListHandlerRunner("select * from nameofunit" + " where parentid='000' order by id", nameofunit);
                    schoolId = "";
                    schoolName = "";
                    pinyin = "";
                } catch (Exception ex) {
                    FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("添加新学院最终失败！"));
                } finally {
                    try {
                        stat.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(SchoolBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } else {
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("添加新学院失败！请输入正确的学院编号，学院名称！"));
        }
        return "viewSchools.xhtml";
    }

    public LinkedHashMap<String, Integer> getMytypeMap() {
        if (null == mytypeMap) {
            mytypeMap = new LinkedHashMap<>();
            mytypeMap.put(StaticFields.cateString[0], 0);
            mytypeMap.put(StaticFields.cateString[1], 1);
            mytypeMap.put(StaticFields.cateString[2], 2);
        }
        return mytypeMap;
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
            nameofunitList = nameDao.getBeanListHandlerRunner("select * from nameofunit where parentid='000' order by id", nameofunit);
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

    /**
     * @return the loginUser
     */
    public User getLoginUser() {
        return this.checkLogin.getUser();
    }

    /**
     * @return the mytype
     */
    public String getMytype() {
        return mytype;
    }

    /**
     * @param mytype the mytype to set
     */
    public void setMytype(String mytype) {
        this.mytype = mytype;
    }
}
