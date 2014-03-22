/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.MyUser;
import entities.Nameofunit;
import entities.Student;
import entities.Teacherinfo;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import sessionBeans.NameofunitFacadeLocal;
import tools.StaticFields;

/**
 *
 * @author myPC
 */
@Named
@RequestScoped
public class SchoolBean implements Serializable {

    private Nameofunit nameofunit;
    private Nameofunit temSchool;
    @EJB
    private NameofunitFacadeLocal unitEjb;
    private List<Nameofunit> nameofunitList;
    private LinkedHashMap<String, Integer> mytypeMap;
    MyUser user;
    HttpSession mySession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);

    @PostConstruct
    public void init() {
        mySession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
         int length = (int) (mySession.getAttribute("userNoLength"));
        if (length == StaticFields.teacherUnoLength) {
            user = (Teacherinfo) mySession.getAttribute("teacherUser");

        } else if (length == StaticFields.stuUnoLength1 || length == StaticFields.stuUnoLength2) {
            user = (Student) mySession.getAttribute("studentUser");
        }
    }

    public String deleteSchool(Nameofunit unitPara) {
        unitEjb.remove(unitPara);
        nameofunitList = null;
        return null;
    }

    public String alterSchool(Nameofunit unitPara) {
        unitEjb.edit(unitPara);
        nameofunitList = null;
        return "viewSchools.xhtml";
    }

    public String addSchool() {
        if (null != this.nameofunit.getId()) {
            this.nameofunit.setTeacherinfo((Teacherinfo)this.user);
            unitEjb.create(nameofunit);
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
            nameofunitList = unitEjb.getList("select * from nameofunit where parentid='000' order by id");
        }
        return nameofunitList;
    }

    public void setNameofunitList(List<Nameofunit> nameofunitList) {
        this.nameofunitList = nameofunitList;
    }

    public Nameofunit getTemSchool() {
        if (null == temSchool) {
            temSchool = new Nameofunit();
        }
        return temSchool;
    }

    public void setTemSchool(Nameofunit temSchool) {
        this.temSchool = temSchool;
    }
}
