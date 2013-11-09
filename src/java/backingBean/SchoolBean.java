/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Nameofunit;
import entities.User;
import entitiesBeans.CheckrecordsLocal;
import entitiesBeans.NameofunitLocal;
import entitiesBeans.PracticenoteLocal;
import entitiesBeans.StuentrelLocal;
import entitiesBeans.UserLocal;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import tools.ConnectionManager;
import tools.StaticFields;

/**
 *
 * @author myPC
 */
@Named
@RequestScoped
public class SchoolBean implements Serializable {

    @Inject
    private Nameofunit nameofunit;
    private @Inject
    User user;
    private Nameofunit temSchool;
    private final NameofunitLocal nameDao = new NameofunitLocal();
    private final UserLocal userDao = new UserLocal();
    private final CheckrecordsLocal checkDao = new CheckrecordsLocal();
    private final PracticenoteLocal pDao = new PracticenoteLocal();
    private final StuentrelLocal seDao = new StuentrelLocal();

    private List<Nameofunit> nameofunitList;
    private LinkedHashMap<String, Integer> mytypeMap;

    public String deleteSchool(Nameofunit unitPara) {
        String sId = unitPara.getId();
        if (userDao.getList("select * from student" + sId).isEmpty()
                && checkDao.getList("select * from checkrecords" + sId).isEmpty()
                && pDao.getList("select * from practicenote" + sId).isEmpty()
                && seDao.getList("select * from stuentrel" + sId).isEmpty()) {
            Statement stat = null;
            Connection mycon = null;
            try {
                mycon = ConnectionManager.getDataSource().getConnection();
            } catch (SQLException ex) {
                Logger.getLogger(SchoolBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                stat = mycon.createStatement();
                stat.executeUpdate("DROP table CheckRecords" + sId);
                stat.executeUpdate("DROP Table PracticeNote" + sId);
                stat.executeUpdate("DROP Table StuEntRel" + sId);
                stat.executeUpdate("DROP TABLE Student" + sId);
                nameDao.remove(unitPara);

            } catch (SQLException ex) {
                Logger.getLogger(SchoolBean.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    stat.close();
                    mycon.close();
                } catch (SQLException ex) {
                    Logger.getLogger(SchoolBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("删除学院失败！"));
        }
        nameofunitList = null;
        return null;
    }

    public String alterSchool(Nameofunit unitPara) {
        nameDao.edit(unitPara);
        nameofunitList = null;
        return "viewSchools.xhtml";
    }

    public String addSchool() {
        if (null != this.nameofunit.getId()) {
            if (null != nameDao.find(this.nameofunit.getId())) {
                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("添加新学院失败！学院编号已经被使用！"));
            } else {
                Connection mycon = null;
                try {
                    mycon = ConnectionManager.getDataSource().getConnection();
                } catch (SQLException ex) {
                    Logger.getLogger(SchoolBean.class.getName()).log(Level.SEVERE, null, ex);
                }
                Statement stat = null;
                try {
                    stat = mycon.createStatement();
                } catch (SQLException e) {
                    FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("数据库连接出错！"));
                }
                try {
                    stat.executeUpdate("Create Table Student" + this.nameofunit.getId() + "(UNO varchar(10) not null primary key,Password varchar(20),NameofUnitId varchar(10) references nameofunit" + StaticFields.currentGradeNum + "(id),Name varchar(50),Email varchar(50),Phone varchar(20),RoleId Integer references roleinfo" + StaticFields.currentGradeNum + "(id) default 2)");
                } catch (SQLException e) {
                    FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("新建表时出错，可能该表已经存在！继续执行剩下的操作"));
                }
                try {
                    stat.executeUpdate("create table CheckRecords" + this.nameofunit.getId() + "(id integer not null generated always as identity(start with 1, increment by 1) primary key,stuNo varchar(10) references Student" + StaticFields.currentGradeNum + this.nameofunit.getId() + "(uno),teachNo varchar(10) references TeacherInfo" + StaticFields.currentGradeNum + "(uno),checkDate date,checkContent varchar(1000),recommendation varchar(500),rank varchar(10), remark varchar(200))");
                } catch (SQLException e) {
                    FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("新建表时出错，可能该表已经存在！继续执行剩下的操作"));
                }
                try {
                    stat.executeUpdate("Create Table PracticeNote" + this.nameofunit.getId() + "(id integer not null generated always as identity(start with 1, increment by 1) primary key,StuNo varchar(10) references Student" + StaticFields.currentGradeNum + this.nameofunit.getId() + "(uno),Detail varchar(2000),SubmitDate date default date(current_date),EnterId Integer references Enterprise" + StaticFields.currentGradeNum + "(ID),PositionId Integer references Position" + StaticFields.currentGradeNum + "(ID))");
                } catch (SQLException e) {
                    FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("新建表时出错，可能该表已经存在！继续执行剩下的操作"));
                }
                try {
                    stat.executeUpdate("Create Table StuEntRel" + this.nameofunit.getId() + "(Id Integer not null generated always as identity (start with 1, increment by 1) primary key,StuNo VARCHAR(10) references Student" + StaticFields.currentGradeNum + this.nameofunit.getId() + "(uno),EntstuID entstuid Integer references enterstudent" + StaticFields.currentGradeNum + "(Id))");
                } catch (SQLException e) {
                    FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("新建表时出错，可能该表已经存在！继续执行剩下的操作"));
                }
                try {
                    nameDao.executUpdate("insert into nameofunit" + " (id, name, parentid, pinyin,mytype, userno) values('" + this.nameofunit.getId() + "', '" + this.nameofunit.getName() + "', '000', '" + this.nameofunit.getPinyin() + "','" + this.nameofunit.getMytype() + "','" + getUser().getUno() + "')");
                    nameofunitList = nameDao.getList("select * from nameofunit" + " where parentid='000' order by id");
                } catch (Exception ex) {
                    FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("添加新学院最终失败！"));
                } finally {
                    try {
                        stat.close();
                        mycon.close();
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

    public Nameofunit getNameofunit() {
        return nameofunit;
    }

    public void setNameofunit(Nameofunit nameofunit) {
        this.nameofunit = nameofunit;
    }

    public List<Nameofunit> getNameofunitList() {
        if (nameofunitList == null) {
            nameofunitList = nameDao.getList("select * from nameofunit where parentid='000' order by id");
        }
        return nameofunitList;
    }

    public void setNameofunitList(List<Nameofunit> nameofunitList) {
        this.nameofunitList = nameofunitList;
    }

    public User getUser() {
//                if (null == user) {
//            FacesContext context = FacesContext.getCurrentInstance();
//            HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
//            user = (User) session.getAttribute("myUser");
//        }
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public Nameofunit getTemSchool() {
        if(null==temSchool){
            temSchool=new Nameofunit();
        }
        return temSchool;
    }
    public void setTemSchool(Nameofunit temSchool) {
        this.temSchool = temSchool;
    }
}
