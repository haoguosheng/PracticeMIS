/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.MyUser;
import entities.Resourceinfo;
import entities.Student;
import entities.Teacherinfo;
import java.io.Serializable;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import tools.PublicFields;
import tools.StaticFields;

/**
 *
 * @author myPC
 */
@SessionScoped
@Named
public class TopMenu implements Serializable {

    private Set<Entry<Resourceinfo, List<Resourceinfo>>> resWithChildrenMap;
    Resourceinfo resource = new Resourceinfo();
    private MyUser user;
    HttpSession mySession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
    @Inject
    PublicFields publicFields;
    private boolean logined;

    @PostConstruct
    public void init() {
        mySession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        int length = (int) (mySession.getAttribute("userNoLength"));
        if (length == StaticFields.teacherUnoLength) {
            setUser((Teacherinfo) mySession.getAttribute("teacherUser"));
        } else if (length == StaticFields.stuUnoLength1 || length == StaticFields.stuUnoLength2) {
            setUser((Student) mySession.getAttribute("studentUser"));
        }
    }

    public Set<Entry<Resourceinfo, List<Resourceinfo>>> getResWithChildrenList() {
        if (null == this.resWithChildrenMap || this.resWithChildrenMap.isEmpty()) {
            resWithChildrenMap = publicFields.getReslistMap().get(getUser().getRoleinfo().getId()).entrySet();
        }
        return this.resWithChildrenMap;
    }

    public MyUser getUser() {
        if (null == this.user) {
            this.init();
        }
        return user;
    }

    public void setUser(MyUser user) {
        this.user = user;
    }

    public boolean isLogined() {
        return logined;
    }

    public void setLogined(boolean logined) {
        this.logined = logined;
    }
}
