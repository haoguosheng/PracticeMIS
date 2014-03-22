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
import entities.Practicenotes;
import entities.Resourceinfo;
import entities.Roleinfo;
import entities.Student;
import entities.Stuentrel;
import entities.Teacherinfo;
import java.io.Serializable;
import java.sql.Connection;
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
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import sessionBeans.CheckrecordsFacadeLocal;
import sessionBeans.CityFacadeLocal;
import sessionBeans.EnterpriseFacadeLocal;
import sessionBeans.EnterstudentFacadeLocal;
import sessionBeans.NameofunitFacadeLocal;
import sessionBeans.NewsFacadeLocal;
import sessionBeans.PositionFacadeLocal;
import sessionBeans.PracticenotesFacadeLocal;
import sessionBeans.ResourceinfoFacadeLocal;
import sessionBeans.RoleinfoFacadeLocal;
import sessionBeans.StudentFacadeLocal;
import sessionBeans.StuentrelFacadeLocal;
import sessionBeans.TeacherinfoFacadeLocal;
import tools.MySessionListener;
import tools.PublicFields;

/**
 *
 * @author myPC
 */
@Named
@RequestScoped
public class BackupBean implements Serializable {

    @EJB
    private TeacherinfoFacadeLocal teacherEjb;
    @EJB
    private PracticenotesFacadeLocal practiceEjb;
    @EJB
    private CheckrecordsFacadeLocal chkRcdEjb;
    @EJB
    private StuentrelFacadeLocal stuRelEjb;
    @EJB
    private CityFacadeLocal cityEjb;
    @EJB
    private EnterpriseFacadeLocal enterEjb;
    @EJB
    private NameofunitFacadeLocal unitEjb;
    @EJB
    private PositionFacadeLocal posiEjb;
    @EJB
    private ResourceinfoFacadeLocal resEjb;
    @EJB
    private RoleinfoFacadeLocal roleEjb;
    @EJB
    private EnterstudentFacadeLocal enstuEjb;
    @EJB
    private NewsFacadeLocal newsEjb;
    @EJB
    private StudentFacadeLocal stuEjb;
    private String backupYear;
    private String recoverYear;
    private LinkedHashMap<String, String> yearMap;
    @Inject
    private PublicFields publicFields;
    private Connection conn = null;
    private ResultSet rs = null;
    private DataSource dataSource = null;
    private Statement stat;

    @PostConstruct
    public void init() {
        if (conn == null) {
            try {
                Context ctx = new InitialContext();
                dataSource = (DataSource) ctx.lookup("jdbc/Enterprise");//这里要注意JNDI名称的大小写问题
                conn = dataSource.getConnection();
                stat = conn.createStatement();
            } catch (NameNotFoundException nfe) {
            } catch (SQLException | NamingException e) {
            }
        }
    }

    public String recoverStu() {
        //需要进一步完成
        return null;
    }

    @PreDestroy
    public void beforeDestroy() {
        try {
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(BackupBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String backupStu() {
        try {
            String lastTwoNumber = backupYear.trim().substring(2, 4);
//判断数据是否存在
            if (teacherEjb.getList("select * from Student where locate('" + lastTwoNumber + "',uno)=1").size() > 0) {
                this.invalidatorOthers();
                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("系统正在维护中……"));
                rs = stat.executeQuery("select tablename from sys.systables");
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
                    stat.executeUpdate("Create Table Student" + backupYear + "(UNO varchar(10) not null primary key,Password varchar(20),NameofUnitId char(3) references nameofunit" + backupYear + "(id),Name varchar(30),Email varchar(20),Phone varchar(15),RoleId Integer references roleinfo" + backupYear + "(id) default 2)");
                    stat.executeUpdate("Create table CheckRecords" + backupYear + "(id integer not null primary key,stuNo varchar(10) references Student" + backupYear + "(uno),teachNo varchar(10) references TeacherInfo" + backupYear + "(uno),checkDate date,checkContent varchar(1000),recommendation varchar(500),rank varchar(10), remark varchar(200), state Integer)");
                    stat.executeUpdate("Create Table PracticeNote" + backupYear + "(id integer not null primary key,StuNo varchar(10) references Student" + backupYear + "(uno),Detail varchar(2000),SubmitDate date default date(current_date), state Integer)");
                    stat.executeUpdate("Create Table StuEntRel" + backupYear + "(Id Integer not null primary key,StuNo VARCHAR(10) references Student" + backupYear + "(uno),entstuid Integer references enterstudent" + backupYear + "(Id),cityid Integer references city" + backupYear + "(id))");
                    this.copyDatetoNewTables();
                    FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("数据备份成功！"));
                }
            } else {//数据不存在
                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("不存在要备份的数据！"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(BackupBean.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (null != rs) {
                    rs.close();
                }
                if (null != stat) {
                    stat.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(BackupBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    private void copyDatetoNewTables() {
        List<Nameofunit> schoolList = publicFields.getSchoolUnitList();
        List<Nameofunit> unitList = unitEjb.findAll();
        List<Resourceinfo> resList = resEjb.findAll();
        List<Roleinfo> roleList = roleEjb.findAll();
        List<City> cityList = cityEjb.findAll();
        List<Enterprise> enterList = enterEjb.findAll();
        List<Position> posiList = posiEjb.findAll();
        List<Enterstudent> enstuList = enstuEjb.findAll();
        List<Teacherinfo> teachList = teacherEjb.getList("select * from teacherinfo");
        List<News> newsList = newsEjb.findAll();
        for (Nameofunit n : unitList) {
            unitEjb.executUpdate("insert into nameofunit" + backupYear + "(id, name, parentid, pinyin, pri, userno) values('" + n.getId() + "','" + n.getName() + "','" + n.getNameofunit().getId() + "','" + n.getPinyin() + "'," + n.getPri() + ",'" + n.getTeacherinfo().getUno() + "')");
        }
        for (Resourceinfo r : resList) {
            resEjb.executUpdate("insert into resourceinfo" + backupYear + "(id, name, parentid, refas, comment, recommendrole, menuorder) values(" + r.getId() + ",'" + r.getName() + "'," + r.getResourceinfo().getId() + ",'" + r.getRefas() + "','" + r.getRefas() + "','" + r.getComment() + "','" + r.getRecommendrole() + "'," + r.getMenuorder() + ")");
        }
        for (Roleinfo r : roleList) {
            roleEjb.executUpdate("insert into roleinfo" + backupYear + "(id, resouceids, name, privilege, canseeall) values(" + r.getId() + ",'" + r.getResouceids() + "','" + r.getName() + "'," + r.getPrivilege() + "," + r.getCanseeall() + ")");
        }
        for (City c : cityList) {
            cityEjb.executUpdate("insert into city" + backupYear + "(id, name, pinyin, parentid) values(" + c.getId() + ",'" + c.getName() + "','" + c.getPinyin() + "','" + c.getCity().getId() + "')");
        }
        for (Enterprise e : enterList) {
            enterEjb.executUpdate("insert into enterprise" + backupYear + "(id, name, enterurl, contactname, contacttelephone, contactaddress, userno, cityid) values(" + e.getId() + ",'" + e.getName() + "','" + e.getEnterurl() + "','" + e.getContactname() + "','" + e.getContacttelephone() + "','" + e.getContactaddress() + "','" + e.getUserno() + "'," + e.getCity().getId() + ")");
        }
        for (Position p : posiList) {
            posiEjb.executUpdate("insert into position" + backupYear + "(id, name, pinyin, userno) values(" + p.getId() + ",'" + p.getName() + "','" + p.getPinyin() + "','" + p.getUserno() + "')");
        }
        for (Enterstudent e : enstuList) {
            enstuEjb.executUpdate("insert into enterstudent" + backupYear + "(id, enterid, payment, other, studnum, positionid, requirement) values(" + e.getId() + "," + e.getEnterprise().getId() + ",'" + e.getPayment() + "','" + e.getOther() + "'," + e.getStudnum() + "," + e.getPosition().getId() + ",'" + e.getRequirement() + "')");
        }
        for (Teacherinfo t : teachList) {
            teacherEjb.executUpdate("insert into teachinfo" + backupYear + "(uno, password, name ,email, phone, roleid, nameofunitid) values('" + t.getUno() + "','" + t.getPassword() + "','" + t.getName() + "','" + t.getEmail() + "','" + t.getPhone() + "'," + t.getRoleinfo().getId() + ",'" + t.getNameofunit().getId() + "')");
        }
        for (News n : newsList) {
            newsEjb.executUpdate("insert into news" + backupYear + "(id, content, inputdate, userno, newstitle) values(" + n.getId() + ",'" + n.getContent() + "','" + n.getInputdate() + "','" + n.getTeacherinfo().getUno() + "','" + n.getNewstitle() + "')");
        }
        String likeuno = backupYear.substring(2, 4);
        for (int i = 0; i < schoolList.size(); i++) {
            String sId = schoolList.get(i).getId();
            List<Student> stuList = stuEjb.getList("select * from student" + sId + " where locate('" + likeuno + "',uno)=1");
            for (Student s : stuList) {
                stuEjb.executUpdate("insert into student" + backupYear + sId + "(uno, password, name ,email, phone, roleid, nameofunitid) values('" + s.getUno() + "','" + s.getPassword() + "','" + s.getName() + "','" + s.getEmail() + "','" + s.getPhone() + "'," + s.getRoleinfo().getId() + ",'" + s.getNameofunit().getId() + "')");
            }
            List<Checkrecords> checkList = chkRcdEjb.getList("select * from checkrecords" + sId + " where locate('" + likeuno + "',stuno)=1");
            for (Checkrecords c : checkList) {
                chkRcdEjb.executUpdate("insert into checkrecords" + backupYear + sId + "(checkdate, checkcontent, recommendation, rank, remark, stuno, teachno, state) values('" + c.getCheckdate() + "','" + c.getCheckcontent() + "','" + c.getRecommendation() + "','" + c.getRank() + "','" + c.getRemark() + "','" + c.getStudent().getUno() + "','" + c.getTeacherinfo().getUno() + "'," + c.getState() + ")");
            }
            List<Practicenotes> practList = practiceEjb.getList("select * from practicenote" + sId + " where locate('" + likeuno + "',stuno)=1");
            for (Practicenotes p : practList) {
                practiceEjb.executUpdate("insert into practicenote" + backupYear + sId + "(detail, submitdate, entstuid, state) values('" + p.getDetail() + "','" + p.getSubmitdate() + "'," + p.getStudent().getUno() + "," + p.getState() + ")");
            }
            List<Stuentrel> stuenList = stuRelEjb.getList("select * from stuentrel" + sId + " where locate('" + likeuno + "',stuno)=1");
            for (Stuentrel s : stuenList) {
                stuRelEjb.executUpdate("insert into stuentrel" + backupYear + sId + "(stuno, entstuid, cityId) values('" + s.getStudent().getUno() + "'," + s.getEnterstudent().getId() + "," + s.getCity().getId() + ")");
            }
        }
    }

 //   public String recoverStu() {
//        if ("current".equals(recoverYear)) {
//            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("已恢复到当前数据。"));
//            return "/login/login";
//        } else {
//            //验证数据是否存在
//            if (teacherEjb.getList("select * from Student" + StaticFields.currentGradeNum + "026 where locate('" + recoverYear.trim().substring(2) + "',uno)=1").size() > 0) {
//                invalidatorOthers();
//                StaticFields.currentGradeNum = recoverYear;
//                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("完成恢复.当前数据是" + StaticFields.currentGradeNum + "年的数据！您必须知道正确的登录用户名和密码！"));
//                return "/login/login";
//            } else {
//                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage(recoverYear + "年的数据不存在！无法恢复！"));
//                return null;
//            }
//        }
    //   }
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

    public String getBackupYear() {
        return backupYear;
    }

    public void setBackupYear(String backupYear) {
        this.backupYear = backupYear;
    }

    public String getRecoverYear() {
        return recoverYear;
    }

    public void setRecoverYear(String recoverYear) {
        this.recoverYear = recoverYear;
    }
}
