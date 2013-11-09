/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import entitiesBeans.CheckrecordsLocal;
import entitiesBeans.CityLocal;
import entitiesBeans.EnterpriseLocal;
import entitiesBeans.NameofunitLocal;
import entitiesBeans.NewsLocal;
import entitiesBeans.PositionLocal;
import entitiesBeans.PracticenoteLocal;
import entitiesBeans.RoleinfoLocal;
import entitiesBeans.StuentrelLocal;
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
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    private String uno;
    private String password;
    private String name;
    private String email;
    private String phone;
    private Integer roleid=-1;
    private String nameofunitid;
    private String schoolId;
    private boolean loaded=false;

    private List<Stuentrel> stuentrelList;
    private List<Checkrecords> checkrecordsList4Student;
    private List<Checkrecords> checkrecordsList4Teacher;
    private List<News> newsList4Teacher;//适用老师
    private List<Enterprise> addedEnterprises;//添加的,老师和学生都适用
    private List<City> addedCitys;
    private List<News> addedNewses;
    private List<Nameofunit> addedNameofunits;
    private List<Position> addedPositions;
    private List<Practicenote> practicenoteList;
    private Roleinfo roleinfo;
    private Nameofunit nameofunit;
    private final StuentrelLocal seDao = new StuentrelLocal();
    private final PracticenoteLocal practDao = new PracticenoteLocal();
    private final CheckrecordsLocal checkDao = new CheckrecordsLocal();
    private final NewsLocal newsDao = new NewsLocal();
    private final EnterpriseLocal epDao = new EnterpriseLocal();
    private final NameofunitLocal nameDao = new NameofunitLocal();
    private final PositionLocal positionDao = new PositionLocal();
    private final CityLocal cityDao = new CityLocal();
    private final RoleinfoLocal roleDao = new RoleinfoLocal();

    public User() {
    }

    public User(String uno) {
        this.uno = uno;
    }

    public String getUno() {
        return uno;
    }

    public void setUno(String uno) {
        this.uno = uno;
        
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getRoleid() {
        return roleid;
    }

    public void setRoleid(int roleid) {
        this.roleid = roleid;
    }

    public String getNameofunitid() {
        return nameofunitid;
    }

    public void setNameofunitid(String nameofunitid) {
        this.nameofunitid = nameofunitid;
    }

    public List<Stuentrel> getStuentrelList() {
        if (stuentrelList == null) {
            stuentrelList = seDao.getList("select * from stuentrel" + getSchoolId() + " where stuno='" + uno + "'");
        }
        return stuentrelList;
    }

    public void setStuentrelList(List<Stuentrel> stuentrelList) {
        this.stuentrelList = stuentrelList;
    }

    public List<Practicenote> getPracticenoteList() {
        if (practicenoteList == null) {
            practicenoteList = practDao.getList("select * from practicenote" + this.getSchoolId() + " where stuno='" + uno + "'");
        }
        return practicenoteList;
    }

    public void setPracticenoteList(List<Practicenote> practicenoteList) {
        this.practicenoteList = practicenoteList;
    }

    public String getSchoolId() {
        if (null == schoolId) {
            setSchoolId(UserAnalysis.getSchoolId(uno));
        }
        return schoolId;
    }

    public List<Checkrecords> getCheckrecordsList4Student() {
        if (checkrecordsList4Student == null) {
            checkrecordsList4Student = checkDao.getList("select * from checkrecords" + StaticFields.currentGradeNum + this.getSchoolId() + " where stuno='" + uno + "'");
        }
        return checkrecordsList4Student;
    }

    public void setCheckrecordsList4Student(List<Checkrecords> checkrecordsList) {
        this.checkrecordsList4Student = checkrecordsList;
    }

    public List<News> getNewsList() {
        if (newsList4Teacher == null) {
            newsList4Teacher = newsDao.getList("select * from news" + StaticFields.currentGradeNum + "  where userno='" + uno + "'");
        }
        return newsList4Teacher;
    }

    public void setNewsList(List<News> newsList) {
        this.newsList4Teacher = newsList;
    }

    public List<Enterprise> getAddEnterprises() {
        if (addedEnterprises == null) {
            addedEnterprises = epDao.getList("select * from enterprise" + StaticFields.currentGradeNum + "  where userno='" + uno + "'");
        }
        return addedEnterprises;
    }

    public void setAddEnterprises(List<Enterprise> addEnterprises) {
        this.addedEnterprises = addEnterprises;
    }

    public List<City> getAddCitys() {
        if (addedCitys == null) {
            addedCitys = cityDao.getList("select * from city" + StaticFields.currentGradeNum + "  where userno='" + uno + "'");
        }
        return addedCitys;
    }

    public void setAddCitys(List<City> addCitys) {
        this.addedCitys = addCitys;
    }

    public List<News> getAddNewses() {
        if (addedNewses == null) {
            addedNewses = newsDao.getList("select * from news" + StaticFields.currentGradeNum + "  where userno='" + uno + "'");
        }
        return addedNewses;
    }

    public void setAddNewses(List<News> addNewses) {
        this.addedNewses = addNewses;
    }

    public List<Nameofunit> getAddNameofunits() {
        if (addedNameofunits == null) {
            addedNameofunits = nameDao.getList("select * from nameofunit" + StaticFields.currentGradeNum + "  where userno='" + uno + "'");
        }
        return addedNameofunits;
    }

    public void setAddNameofunits(List<Nameofunit> addNameofunits) {
        this.addedNameofunits = addNameofunits;
    }

    public List<Position> getAddPositions() {
        if (addedPositions == null) {
            addedPositions = positionDao.getList("select * from position" + StaticFields.currentGradeNum + "  where userno='" + uno + "'");
        }
        return addedPositions;
    }

    public void setAddPositions(List<Position> addPositions) {
        this.addedPositions = addPositions;
    }

    public Roleinfo getRoleinfo() {
        if (roleinfo == null) {
            dealId0();
        } else if (null==this.roleinfo.getId()) {
            dealId0();
        }
        return roleinfo;
    }
    /*
     *当对象是内在对象时处理外键Roleinfo
     */

    private void dealId0() {
        if (null == this.uno) {
            if (null == roleinfo) {
                this.roleinfo = new Roleinfo();
            }
        } else {
            roleinfo = roleDao.getList("select * from roleinfo" + StaticFields.currentGradeNum + "  where id=" + roleid).get(0);
        }
    }

    public void setRoleinfo(Roleinfo roleinfo) {
        this.roleinfo = roleinfo;
    }

    public Nameofunit getNameofunit() {
        if (nameofunit == null) {
          dealId0Nameofunit();
        }else if(null==nameofunit.getId()){
             dealId0Nameofunit();
        }
        return nameofunit;
    }
   /*
     *当对象是内在对象时处理外键Nameofunit
     */

    private void dealId0Nameofunit() {
        if (null == this.uno) {
            if (null == nameofunit) {
                this.nameofunit = new Nameofunit();
            }
        } else {
             nameofunit = nameDao.getList("select * from nameofunit" + StaticFields.currentGradeNum + "  where id='" + nameofunitid + "'").get(0);
        }
    }
    public void setNameofunit(Nameofunit nameofunit) {
        this.nameofunit = nameofunit;
    }

    public List<Checkrecords> getCheckrecordsList4Teacher() {
        checkrecordsList4Teacher = checkDao.getList("select * from checkrecords" + StaticFields.currentGradeNum + this.getSchoolId() + " where teachno='" + uno + "'");
        return checkrecordsList4Teacher;
    }

    public void setCheckrecordsList4Teacher(List<Checkrecords> checkrecordsList4Teacher) {
        this.checkrecordsList4Teacher = checkrecordsList4Teacher;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }
    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }
}
