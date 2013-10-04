/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import backingBean.EnterpriseInfo;
import backingBean.ResourceWithChildren;
import entities.Resourceinfo;
import entities.User;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Idea
 */
@ManagedBean
@SessionScoped
public class ForCallBean implements java.io.Serializable {

    FacesContext context = FacesContext.getCurrentInstance();
    HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
    HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
    private User user;
    private SQLTool<Resourceinfo> resDao = new SQLTool<Resourceinfo>();
    private LinkedList<ResourceWithChildren> listResList;
    Resourceinfo resource = new Resourceinfo();

    /**
     * @return the listResList
     */
    public LinkedList<ResourceWithChildren> getListResList() {
        if (null == this.listResList) {
            this.calcuListResList();
        }
        return this.listResList;
    }

    private void calcuListResList() {
        List<Resourceinfo> parentResource = resDao.getBeanListHandlerRunner("select * from resourceinfo where parentid is null order by MENUORDER", resource);//把双亲拿来
        List<Resourceinfo> childrenResource = resDao.getBeanListHandlerRunner("select * from resourceinfo where parentid is not null", resource);
        LinkedList<ResourceWithChildren> tem = new LinkedList<ResourceWithChildren>();
        Iterator<Resourceinfo> parentIt = parentResource.iterator();
        int i = 0;
        while (parentIt.hasNext()) {
            ResourceWithChildren rwc = new ResourceWithChildren();
            Resourceinfo pareTem=parentIt.next();
            rwc.setParent(pareTem);
            Iterator<Resourceinfo> childrenIt=childrenResource.iterator();
            while(childrenIt.hasNext()){
                Resourceinfo temch=childrenIt.next();
                if(temch.getParentid()==pareTem.getId()){
                    rwc.getChildrenResourceList().add(temch);
                }
            }
            tem.add(rwc);
        }
        this.listResList = tem;
    }

    public User getUser() {
        if (null == user) {
            user = (User) session.getAttribute("myUser");
            if (null == user) {
                try {
                    response.sendRedirect("login.xhtml");
                } catch (IOException ex) {
                    Logger.getLogger(EnterpriseInfo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return user;
    }

    public Integer getReason() {
        return (Integer) session.getAttribute("reason");
    }

    public void setReason(int reason) {
        session.setAttribute("reason", reason);
    }
}
