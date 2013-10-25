/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Checkrecords;
import entities.City;
import entities.Enterprise;
import entities.Enterstudent;
import entities.Nameofunit;
import entities.News;
import entities.Position;
import entities.Practicenote;
import entities.Resourceinfo;
import entities.Roleinfo;
import entities.Stuentrel;
import entities.User;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import tools.ConnectionManager;
import tools.MySessionListener;
import tools.PublicFields;
import tools.SQLTool;
import tools.StaticFields;

/**
 *
 * @author myPC
 */
@Named
@SessionScoped
public class BackupBean implements Serializable {

    private SQLTool<User> userDao;
    private SQLTool<Practicenote> praDao;
    private SQLTool<Checkrecords> chkDao;
    private SQLTool<Stuentrel> stuRelDao;
    private SQLTool<City> cityDao;
    private SQLTool<Enterprise> enterDao;
    private SQLTool<Nameofunit> unitDao;
    private SQLTool<Position> posiDao;
    private SQLTool<Resourceinfo> resDao;
    private SQLTool<Roleinfo> roleDao;
    private SQLTool<Enterstudent> enstuDao;
    private SQLTool<News> newsDao;
    private String backupYear;
    private String recoverYear;
    private LinkedHashMap<String, String> yearMap;

    @PostConstruct
    public void init() {
        userDao = new SQLTool<>();
        praDao = new SQLTool<>();
        chkDao = new SQLTool<>();
        stuRelDao = new SQLTool<>();
        cityDao = new SQLTool<>();
        enterDao = new SQLTool<>();
        unitDao = new SQLTool<>();
        posiDao = new SQLTool<>();
        resDao = new SQLTool<>();
        roleDao = new SQLTool<>();
        enstuDao = new SQLTool<>();
        newsDao = new SQLTool<>();
    }

    public String backupStu() {
        Statement stat = null;
        try {
            String lastTwoNumber = backupYear.trim().substring(2, 4);
//判断数据是否存在
            if (userDao.getBeanListHandlerRunner("select * from Student" + StaticFields.currentGradeNum + "026 where locate('" + lastTwoNumber + "',uno)=1", new User()).size() > 0) {
                this.invalidatorOthers();
                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("系统正在维护中……"));
                List<Nameofunit> schoolList = PublicFields.getSchoolUnitList();
                stat = ConnectionManager.getDataSource().getConnection().createStatement();
                ResultSet rs = stat.executeQuery("select tablename from sys.systables");
                List<String> temp = new LinkedList<>();
                while (rs.next()) {
                    temp.add(rs.getString("tablename"));
                }
                if (temp.contains("NameOfUnit" + backupYear)) {
                    FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("该年毕业的学生实习信息已经备份！"));
                } else {
                    stat.executeUpdate("Create Table NameOfUnit" + backupYear + "(Id char(3) not null primary key,Name varchar(50) not null,ParentId char(3) references nameofunit" + backupYear + "(id),Pinyin varchar(20),Pri Integer not null default 0,Userno varchar(20))");
                    stat.executeUpdate("Create table RESOURCEINFO" + backupYear + "(id integer not null primary key,NAME VARCHAR(100),PARENTID INTEGER references RESOURCEINFO" + backupYear + "(id),REFAS VARCHAR(100),COMMENT VARCHAR(500),RECOMMENDROLE VARCHAR(20),MENUORDER INTEGER)");
                    stat.executeUpdate("Create table Roleinfo" + backupYear + "(id integer not null primary key,RESOUCEIDS VARCHAR(1000),NAME VARCHAR(20),PRIVILEGE INTEGER,CANSEEALL INTEGER)");
                    stat.executeUpdate("Create table city" + backupYear + "(id integer not null primary key,NAME VARCHAR(20),PINYIN VARCHAR(50),USERNO VARCHAR(10))");
                    stat.executeUpdate("Create table enterprise" + backupYear + "(id integer not null primary key,NAME VARCHAR(100),ENTERURL VARCHAR(50),CONTACTNAME VARCHAR(20),CONTACTTELEPHONE VARCHAR(20),CONTACTADDRESS VARCHAR(100),USERNO VARCHAR(10),CITYID INTEGER references city" + backupYear + "(id))");
                    stat.executeUpdate("Create table position" + backupYear + "(id integer not null primary key,NAME VARCHAR(500),PINYIN VARCHAR(50),USERNO VARCHAR(10))");
                    stat.executeUpdate("Create table enterstudent" + backupYear + "(Id integer not null primary key,EnterId Integer not null references Enterprise" + backupYear + "(Id),Requirement Varchar(1000),Payment Varchar(500),Other Varchar(1000),StudNum Integer,PositionId Integer references Position" + backupYear + "(Id))");
                    stat.executeUpdate("Create Table TeacherInfo" + backupYear + "(UNO varchar(10) not null primary key,Password varchar(20),NameofUnitId char(3) references nameofUnit" + backupYear + "(id),Name varchar(30),Email varchar(20),Phone varchar(15),RoleId Integer references roleinfo" + backupYear + "(id))");
                    stat.executeUpdate("Create table news" + backupYear + "(id integer not null primary key,CONTENT VARCHAR(1000),INPUTDATE DATE,USERNO VARCHAR(10) references teacherinfo" + backupYear + "(uno),NEWSTITLE VARCHAR(50) DEFAULT '最新消息' NOT NULL)");
                    for (int i = 0; i < schoolList.size(); i++) {
                        stat.executeUpdate("Create Table Student" + backupYear + schoolList.get(i).getId() + "(UNO varchar(10) not null primary key,Password varchar(20),NameofUnitId char(3) references nameofunit" + backupYear + "(id),Name varchar(30),Email varchar(20),Phone varchar(15),RoleId Integer references roleinfo" + backupYear + "(id) default 2)");
                        stat.executeUpdate("Create table CheckRecords" + backupYear + schoolList.get(i).getId() + "(id integer not null primary key,stuNo varchar(10) references Student" + backupYear + schoolList.get(i).getId() + "(uno),teachNo varchar(10) references TeacherInfo" + backupYear + "(uno),checkDate date,checkContent varchar(1000),recommendation varchar(500),rank varchar(10), remark varchar(200))");
                        stat.executeUpdate("Create Table PracticeNote" + backupYear + schoolList.get(i).getId() + "(id integer not null primary key,StuNo varchar(10) references Student" + backupYear + schoolList.get(i).getId() + "(uno),Detail varchar(2000),SubmitDate date default date(current_date),EnterId Integer references Enterprise" + backupYear + "(ID),PositionId Integer references Position" + backupYear + "(ID))");
                        stat.executeUpdate("Create Table StuEntRel" + backupYear + schoolList.get(i).getId() + "(Id Integer not null primary key,StuNo VARCHAR(10) references Student" + backupYear + schoolList.get(i).getId() + "(uno),entstuid Integer references enterstudent" + backupYear + "(Id))");
                    }
                    this.copyDatetoNewTables(schoolList);
                    FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("数据备份成功！"));
                }
            } else {//数据不存在
                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("不存在要备份的数据！"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(BackupBean.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (stat != null) {
                    stat.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(BackupBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    private void copyDatetoNewTables(List<Nameofunit> schoolList) {

        List<Nameofunit> unitList = unitDao.getBeanListHandlerRunner("select * from nameofunit", new Nameofunit());
        List<Resourceinfo> resList = resDao.getBeanListHandlerRunner("select * from resourceinfo", new Resourceinfo());
        List<Roleinfo> roleList = roleDao.getBeanListHandlerRunner("select * from roleinfo", new Roleinfo());
        List<City> cityList = cityDao.getBeanListHandlerRunner("select * from city", new City());
        List<Enterprise> enterList = enterDao.getBeanListHandlerRunner("select * from enterprise", new Enterprise());
        List<Position> posiList = posiDao.getBeanListHandlerRunner("select * from position", new Position());
        List<Enterstudent> enstuList = enstuDao.getBeanListHandlerRunner("select * from enterstudent", new Enterstudent());
        List<User> teachList = userDao.getBeanListHandlerRunner("select * from teacherinfo", new User());
        List<News> newsList = newsDao.getBeanListHandlerRunner("select * from news", new News());
        for (Nameofunit n : unitList) {
            unitDao.executUpdate("insert into nameofunit" + backupYear + "(id, name, parentid, pinyin, pri, userno) values('" + n.getId() + "','" + n.getName() + "','" + n.getParentid() + "','" + n.getPinyin() + "'," + n.getPri() + ",'" + n.getUserno() + "')");
        }
        for (Resourceinfo r : resList) {
            resDao.executUpdate("insert into resourceinfo" + backupYear + "(id, name, parentid, refas, comment, recommendrole, menuorder) values(" + r.getId() + ",'" + r.getName() + "'," + r.getParentid() + ",'" + r.getRefas() + "','" + r.getRefas() + "','" + r.getComment() + "','" + r.getRecommendrole() + "'," + r.getMenuorder() + ")");
        }
        for (Roleinfo r : roleList) {
            roleDao.executUpdate("insert into roleinfo" + backupYear + "(id, resouceids, name, privilege, canseeall) values(" + r.getId() + ",'" + r.getResouceids() + "','" + r.getName() + "'," + r.getPrivilege() + "," + r.getCanseeall() + ")");
        }
        for (City c : cityList) {
            cityDao.executUpdate("insert into city" + backupYear + "(id, name, pinyin, userno) values(" + c.getId() + ",'" + c.getName() + "','" + c.getPinyin() + "','" + c.getUserno() + "')");
        }
        for (Enterprise e : enterList) {
            enterDao.executUpdate("insert into enterprise" + backupYear + "(id, name, enterurl, contactname, contacttelephone, contactaddress, userno, cityid) values(" + e.getId() + ",'" + e.getName() + "','" + e.getEnterurl() + "','" + e.getContactname() + "','" + e.getContacttelephone() + "','" + e.getContactaddress() + "','" + e.getUserno() + "'," + e.getCityId() + ")");
        }
        for (Position p : posiList) {
            posiDao.executUpdate("insert into position" + backupYear + "(id, name, pinyin, userno) values(" + p.getId() + ",'" + p.getName() + "','" + p.getPinyin() + "','" + p.getUserno() + "')");
        }
        for (Enterstudent e : enstuList) {
            enstuDao.executUpdate("insert into enterstudent" + backupYear + "(id, enterid, payment, other, studnum, positionid, requirement) values(" + e.getId() + "," + e.getEnterid() + ",'" + e.getPayment() + "','" + e.getOther() + "'," + e.getStudnum() + "," + e.getPositionid() + ",'" + e.getRequirement() + "')");
        }
        for (User t : teachList) {
            userDao.executUpdate("insert into teachinfo" + backupYear + "(uno, password, name ,email, phone, roleid, nameofunitid) values('" + t.getUno() + "','" + t.getPassword() + "','" + t.getName() + "','" + t.getEmail() + "','" + t.getPhone() + "'," + t.getRoleid() + ",'" + t.getNameofunitid() + "')");
        }
        for (News n : newsList) {
            newsDao.executUpdate("insert into news" + backupYear + "(id, content, inputdate, userno, newstitle) values(" + n.getId() + ",'" + n.getContent() + "','" + n.getInputdate() + "','" + n.getUserno() + "','" + n.getNewsTitle() + "')");
        }
        String likeuno = backupYear.substring(2, 4);
        for (int i = 0; i < schoolList.size(); i++) {
            String sId = schoolList.get(i).getId();

            List<User> stuList = userDao.getBeanListHandlerRunner("select * from student" + sId + " where locate('" + likeuno + "',uno)=1", new User());
            for (User s : stuList) {
                userDao.executUpdate("insert into student" + backupYear + sId + "(uno, password, name ,email, phone, roleid, nameofunitid) values('" + s.getUno() + "','" + s.getPassword() + "','" + s.getName() + "','" + s.getEmail() + "','" + s.getPhone() + "'," + s.getRoleid() + ",'" + s.getNameofunitid() + "')");
            }
            List<Checkrecords> checkList = chkDao.getBeanListHandlerRunner("select * from checkrecords" + sId + " where locate('" + likeuno + "',stuno)=1", new Checkrecords());
            for (Checkrecords c : checkList) {
                chkDao.executUpdate("insert into checkrecords" + backupYear + sId + "(checkdate, checkcontent, recommendation, rank, remark, stuno, teachno) values('" + c.getCheckdate() + "','" + c.getCheckcontent() + "','" + c.getRecommendation() + "','" + c.getRank() + "','" + c.getRemark() + "','" + c.getStuno() + "','" + c.getTeachno() + "')");
            }
            List<Practicenote> practList = praDao.getBeanListHandlerRunner("select * from practicenote" + sId + " where locate('" + likeuno + "',stuno)=1", new Practicenote());
            for (Practicenote p : practList) {
                praDao.executUpdate("insert into practicenote" + backupYear + sId + "(detail, submitdate, entstuid) values('" + p.getDetail() + "','" + p.getSubmitdate() + "'," + p.getStudententid()+ ")");
            }
            List<Stuentrel> stuenList = stuRelDao.getBeanListHandlerRunner("select * from stuentrel" + sId + " where locate('" + likeuno + "',stuno)=1", new Stuentrel());
            for (Stuentrel s : stuenList) {
                stuRelDao.executUpdate("insert into stuentrel" + backupYear + sId + "(stuno, entstuid) values('" + s.getStuno() + "'," + s.getEntstuid());
            }
        }
    }

    public String recoverStu() {
        if ("current".equals(recoverYear)) {
            StaticFields.currentGradeNum = "";
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("已恢复到当前数据。"));
            return "/login/login";
        } else {
            //验证数据是否存在
            if (userDao.getBeanListHandlerRunner("select * from Student" + StaticFields.currentGradeNum + "026 where locate('" + recoverYear.trim().substring(2) + "',uno)=1", new User()).size() > 0) {
                invalidatorOthers();
                StaticFields.currentGradeNum = recoverYear;
                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("完成恢复.当前数据是" + StaticFields.currentGradeNum + "年的数据！您必须知道正确的登录用户名和密码！"));
                return "/login/login";
            } else {
                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage(recoverYear + "年的数据不存在！无法恢复！"));
                return null;
            }
        }
    }

    private void invalidatorOthers() {
        for (HttpSession s : MySessionListener.getAllSession()) {
            if (!FacesContext.getCurrentInstance().getExternalContext().getSessionId(true).equals(s.getId())) {
                s.invalidate();
            }
        }
    }

    public LinkedHashMap<String, String> getYearMap() {
        if (null == yearMap) {
            yearMap = new LinkedHashMap<>();
            Calendar c = Calendar.getInstance();
            for (int i = 2009; i <= c.get(Calendar.YEAR) - 4; i++) {
                yearMap.put(i + "", i + "");
            }
        }
        return yearMap;
    }

    /**
     * @return the backupYear
     */
    public String getBackupYear() {
        return backupYear;
    }

    /**
     * @param backupYear the backupYear to set
     */
    public void setBackupYear(String backupYear) {
        this.backupYear = backupYear;
    }

    /**
     * @return the recoverYear
     */
    public String getRecoverYear() {
        return recoverYear;
    }

    /**
     * @param recoverYear the recoverYear to set
     */
    public void setRecoverYear(String recoverYear) {
        this.recoverYear = recoverYear;
    }
}
