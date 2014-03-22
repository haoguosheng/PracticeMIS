/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Enterprise;
import entities.Enterstudent;
import entities.MyUser;
import entities.Student;
import entities.Teacherinfo;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import sessionBeans.EnterstudentFacadeLocal;
import tools.RepeatPaginator;
import tools.StaticFields;

/**
 *
 * @author Idea
 */
@Named
@SessionScoped
public class EnterpriseStuBean implements java.io.Serializable {

    Enterstudent entStu;
    private Enterprise enterprise;
    private RepeatPaginator paginator;
    @EJB
    private EnterstudentFacadeLocal esDao;
    HttpSession mySession ;
    MyUser user;

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

    public String directNeed(Enterprise enter) {
        this.enterprise = enter;
        return "enterpriseNeedInfo";
    }

    public synchronized String addEnterpriseNeed() {
        entStu.setEnterprise(this.getEnterprise());
        esDao.create(entStu);
        enterprise.getEnterstudentList().clear();
        return "enterpriseNeedInfo.xhtml";
    }

    public String deleteNeed(Enterstudent es) {
        this.esDao.remove(es);
        enterprise.getEnterstudentList().clear();
        return null;
    }

    public String saveNeed(Enterstudent es) {
        esDao.edit(es);
        enterprise.getEnterstudentList().clear();
        return null;
    }

    public Enterstudent getEntStu() {
        return entStu;
    }

    public void setEntStu(Enterstudent entStu) {
        this.entStu = entStu;
    }

    public Enterprise getEnterprise() {
////        if (null == this.enterprise) {
//            setEnterprise((Enterprise) mySession.getAttribute("myenterprise"));
////        }
        return enterprise;
    }

    public void setEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
    }

    public RepeatPaginator getPaginator() {
        paginator = null;
        paginator = new RepeatPaginator(enterprise.getEnterstudentList(), StaticFields.pageSize);
        return paginator;
    }

    public boolean buttonShowOrNot() {
        return this.getEnterprise().getUserno().equals(user.getUno());
    }

}
