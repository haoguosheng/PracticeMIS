/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Idea
 */
@Named
@RequestScoped
public class MyNavigate implements java.io.Serializable{

    HttpSession mySession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);

    public String addNewCity() {
        return "addCity";
    }

    public String toEnterNeed() {
        return "addEnterpriseNeed";
    }

    public String addPosition() {
        return "addPosition";
    }

    public String addNews() {
        return "addNews";
    }

    public String toAddEnterprise() {
        return "addEnterpriseInfo";
    }

    public String direct2BackupImport() {
        return "importStudent";
    }
}
