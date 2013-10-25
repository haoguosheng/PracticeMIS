/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import backingBean.UserinfoBean;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import tools.SQLTool;
import tools.StaticFields;

/**
 *
 * @author Administrator
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    private String uno;
    private String password;
    private String name;
    private String email;
    private String phone;
    private int roleid;
    private String nameofunitid;
    private String schoolId;
    private List<Stuentrel> stuentrelList;
    private List<Practicenote> practicenoteList;
    private List<Checkrecords> checkrecordsList;//老师和学生都适用
    private List<News> newsList;//适用老师
    private List<Enterprise> addEnterprises;//添加的,老师和学生都适用
    private List<City> addCitys;
    private List<News> addNewses;
    private List<Nameofunit> addNameofunits;
    private List<Position> addPositions;
    private Roleinfo roleinfo;
    private Nameofunit nameofunit;
    private final SQLTool<Stuentrel> seDao = new SQLTool<>();
    private final SQLTool<Practicenote> practDao = new SQLTool<>();
    private final SQLTool<Checkrecords> checkDao = new SQLTool<>();
    private final SQLTool<News> newsDao = new SQLTool<>();
    private final SQLTool<Enterprise> epDao = new SQLTool<>();
    private final SQLTool<Nameofunit> nameDao = new SQLTool<>();
    private final SQLTool<Position> positionDao = new SQLTool<>();
    private final SQLTool<City> cityDao = new SQLTool<>();
    private final SQLTool<Roleinfo> roleDao = new SQLTool<>();

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
            stuentrelList = seDao.getBeanListHandlerRunner("select * from stuentrel" + schoolId + " where stuno='" + uno + "'", new Stuentrel());
        }
        if (stuentrelList.isEmpty()) {
            try {
                FacesContext context = FacesContext.getCurrentInstance();
                HttpSession mySession = (HttpSession) context.getExternalContext().getSession(true);
                HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
                mySession.invalidate();
                response.sendRedirect("selectMyEnterprise.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(UserinfoBean.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
        return stuentrelList;
    }

    public void setStuentrelList(List<Stuentrel> stuentrelList) {
        this.stuentrelList = stuentrelList;
    }

    public List<Practicenote> getPracticenoteList() {
        if (practicenoteList == null) {
            practicenoteList = practDao.getBeanListHandlerRunner("select * from practicenote" + schoolId + " where stuno='" + uno + "'", new Practicenote());
        }
        return practicenoteList;
    }

    public void setPracticenoteList(List<Practicenote> practicenoteList) {
        this.practicenoteList = practicenoteList;
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
     * @return the checkrecordsList
     */
    public List<Checkrecords> getCheckrecordsList() {
        if (checkrecordsList == null) {
            if (uno.length() == 6) {
                checkrecordsList = checkDao.getBeanListHandlerRunner("select * from checkrecords" + StaticFields.currentGradeNum + schoolId + " where teachno='" + uno + "'", new Checkrecords());
            } else {
                checkrecordsList = checkDao.getBeanListHandlerRunner("select * from checkrecords" + StaticFields.currentGradeNum + schoolId + " where stuno='" + uno + "'", new Checkrecords());
            }
        }
        return checkrecordsList;
    }

    /**
     * @param checkrecordsList the checkrecordsList to set
     */
    public void setCheckrecordsList(List<Checkrecords> checkrecordsList) {
        this.checkrecordsList = checkrecordsList;
    }

    /**
     * @return the newsList
     */
    public List<News> getNewsList() {
        if (newsList == null) {
            newsList = newsDao.getBeanListHandlerRunner("select * from news" + StaticFields.currentGradeNum + "  where userno='" + uno + "'", new News());
        }
        return newsList;
    }

    /**
     * @param newsList the newsList to set
     */
    public void setNewsList(List<News> newsList) {
        this.newsList = newsList;
    }

    /**
     * @return the addEnterprises
     */
    public List<Enterprise> getAddEnterprises() {
        if (addEnterprises == null) {
            addEnterprises = epDao.getBeanListHandlerRunner("select * from enterprise" + StaticFields.currentGradeNum + "  where userno='" + uno + "'", new Enterprise());
        }
        return addEnterprises;
    }

    /**
     * @param addEnterprises the addEnterprises to set
     */
    public void setAddEnterprises(List<Enterprise> addEnterprises) {
        this.addEnterprises = addEnterprises;
    }

    /**
     * @return the addCitys
     */
    public List<City> getAddCitys() {
        if (addCitys == null) {
            addCitys = cityDao.getBeanListHandlerRunner("select * from city" + StaticFields.currentGradeNum + "  where userno='" + uno + "'", new City());
        }
        return addCitys;
    }

    /**
     * @param addCitys the addCitys to set
     */
    public void setAddCitys(List<City> addCitys) {
        this.addCitys = addCitys;
    }

    /**
     * @return the addNewses
     */
    public List<News> getAddNewses() {
        if (addNewses == null) {
            addNewses = newsDao.getBeanListHandlerRunner("select * from news" + StaticFields.currentGradeNum + "  where userno='" + uno + "'", new News());
        }
        return addNewses;
    }

    /**
     * @param addNewses the addNewses to set
     */
    public void setAddNewses(List<News> addNewses) {
        this.addNewses = addNewses;
    }

    /**
     * @return the addNameofunits
     */
    public List<Nameofunit> getAddNameofunits() {
        if (addNameofunits == null) {
            addNameofunits = nameDao.getBeanListHandlerRunner("select * from nameofunit" + StaticFields.currentGradeNum + "  where userno='" + uno + "'", new Nameofunit());
        }
        return addNameofunits;
    }

    /**
     * @param addNameofunits the addNameofunits to set
     */
    public void setAddNameofunits(List<Nameofunit> addNameofunits) {
        this.addNameofunits = addNameofunits;
    }

    /**
     * @return the addPositions
     */
    public List<Position> getAddPositions() {
        if (addPositions == null) {
            addPositions = positionDao.getBeanListHandlerRunner("select * from position" + StaticFields.currentGradeNum + "  where userno='" + uno + "'", new Position());
        }
        return addPositions;
    }

    /**
     * @param addPositions the addPositions to set
     */
    public void setAddPositions(List<Position> addPositions) {
        this.addPositions = addPositions;
    }

    /**
     * @return the roleinfo
     */
    public Roleinfo getRoleinfo() {
        if (roleinfo == null) {
            roleinfo = roleDao.getBeanListHandlerRunner("select * from roleinfo" + StaticFields.currentGradeNum + "  where id=" + roleid, new Roleinfo()).get(0);
        }
        return roleinfo;
    }

    /**
     * @param roleinfo the roleinfo to set
     */
    public void setRoleinfo(Roleinfo roleinfo) {
        this.roleinfo = roleinfo;
    }

    /**
     * @return the nameofunit
     */
    public Nameofunit getNameofunit() {
        if (nameofunit == null) {
            nameofunit = nameDao.getBeanListHandlerRunner("select * from nameofunit" + StaticFields.currentGradeNum + "  where id='" + nameofunitid + "'", new Nameofunit()).get(0);
        }
        return nameofunit;
    }

    /**
     * @param nameofunit the nameofunit to set
     */
    public void setNameofunit(Nameofunit nameofunit) {
        this.nameofunit = nameofunit;
    }
}
