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

    private @Inject User user;

    private Set<Entry<Resourceinfo, List<Resourceinfo>>> resWithChildrenMap;
    Resourceinfo resource = new Resourceinfo();

    public Set<Entry<Resourceinfo, List<Resourceinfo>>> getResWithChildrenList() {
        if (null == this.resWithChildrenMap || this.resWithChildrenMap.isEmpty()) {
            resWithChildrenMap = PublicFields.getReslistMap().get(getUser().getRoleid()).entrySet();
        }
        return this.resWithChildrenMap;
    }

    public User getUser() {
//            FacesContext context = FacesContext.getCurrentInstance();
//            HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
//            user = (User) session.getAttribute("myUser");
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
