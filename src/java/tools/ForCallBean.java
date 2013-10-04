/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import backingBean.EnterpriseInfo;
import entities.User;
import java.io.IOException;
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

    /**
     * @return the reason
     */
    public Integer getReason() {
        return (Integer) session.getAttribute("reason");
    }

    /**
     * @param reason the reason to set
     */
    public void setReason(int reason) {
        session.setAttribute("reason", reason);
    }
}
