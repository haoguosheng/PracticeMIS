/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Enterprise;
import entities.Enterstudent;
import entities.User;
import entitiesBeans.EnterstudentLocal;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import tools.RepeatPaginator;
import tools.StaticFields;

/**
 *
 * @author Idea
 */
@Named
@RequestScoped
public class EnterpriseStuBean implements java.io.Serializable {

    private @Inject
    Enterstudent entStu;
    private @Inject
    User user;
    private Enterprise enterprise;
    private RepeatPaginator paginator;
    private final EnterstudentLocal esDao = new EnterstudentLocal();
    
    HttpSession mySession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);

    public synchronized String addEnterpriseNeed() {
        entStu.setEnterid(this.getEnterprise().getId());
        esDao.create(entStu);
        return null;
    }


    public void deleteNeed(Enterstudent es) {
        this.esDao.remove(es);
        paginator = null;
    }

    public void saveNeed(Enterstudent es) {
        esDao.edit(es);
        paginator = null;
    }

    public Enterstudent getEntStu() {
        return entStu;
    }

    public void setEntStu(Enterstudent entStu) {
        this.entStu = entStu;
    }

    public Enterprise getEnterprise() {
        if (null == this.enterprise) {
            setEnterprise((Enterprise) mySession.getAttribute("myenterprise"));
        }
        return enterprise;
    }

    public void setEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
    }

    public RepeatPaginator getPaginator() {
        if (null == paginator) {
            paginator = new RepeatPaginator(entStu.getEnterStudList(), StaticFields.pageSize);
        }
        return paginator;
    }

    public boolean buttonShowOrNot() {
        return this.getEnterprise().getInputor().getUno().equals(user.getUno());
    }
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
  
}
