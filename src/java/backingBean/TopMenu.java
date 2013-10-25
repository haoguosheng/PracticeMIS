/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Resourceinfo;
import entities.User;
import java.io.Serializable;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import tools.PublicFields;

/**
 *
 * @author myPC
 */
@SessionScoped
@Named
public class TopMenu implements Serializable {

    @Inject
   private  CheckLogin checkLogin;

    private Set<Entry<Resourceinfo, List<Resourceinfo>>> resWithChildrenMap;
    Resourceinfo resource = new Resourceinfo();

    public Set<Entry<Resourceinfo, List<Resourceinfo>>> getResWithChildrenList() {
        if (null == this.resWithChildrenMap || this.resWithChildrenMap.isEmpty()) {
            User myUser = checkLogin.getUser();
            resWithChildrenMap = PublicFields.getReslistMap().get(myUser.getRoleid()).entrySet();
        }
        return this.resWithChildrenMap;
    }

    /**
     * @return the checkLogin
     */
    public CheckLogin getCheckLogin() {
        return checkLogin;
    }

    /**
     * @param checkLogin the checkLogin to set
     */
    public void setCheckLogin(CheckLogin checkLogin) {
        this.checkLogin = checkLogin;
    }
}
