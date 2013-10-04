/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Resourceinfo;
import entities.Roleinfo;
import entities.User;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import tools.ForCallBean;
import tools.SQLTool;

/**
 *
 * @author myPC
 */
@SessionScoped
@ManagedBean
public class TopMenu implements Serializable {

    private SQLTool<Resourceinfo> resDao = new SQLTool<Resourceinfo>();
    private SQLTool<Roleinfo> roleDao = new SQLTool<Roleinfo>();
    private ArrayList<ResourceWithChildren> listResList;
    private List<Resourceinfo> freeResList;
    Resourceinfo resource=new Resourceinfo();

    /**
     * @return the listResList
     */
    public ArrayList<ResourceWithChildren> getListResList() {
        if (null == this.listResList) {
            this.calcuListResList();
        }
        return this.listResList;
    }

    private ArrayList<ResourceWithChildren> calcuListResList() {
        User user = new ForCallBean().getUser();
        String resourceIds = roleDao.getIdListHandlerRunner("select resouceids from roleinfo where id=" + user.getRoleid()).get(0);
        List<Resourceinfo> rootNames = resDao.getBeanListHandlerRunner("select * from resourceinfo where parentid is null order by MENUORDER", resource);//把双亲拿来
        ArrayList<ResourceWithChildren> tem = new ArrayList<ResourceWithChildren>();
        Iterator it = rootNames.iterator();
        int i = 0;
        while (it.hasNext()) {
            ResourceWithChildren rwc = new ResourceWithChildren();
            rwc.setParent((Resourceinfo) (it.next()));
            rwc.setResourceList(resDao.getBeanListHandlerRunner("select * from resourceinfo where id in(" + resourceIds + ") and parentid=" + rwc.getParent().getId() + " and canBeSeenbyAll!=1 order by MENUORDER",resource));
            tem.add(rwc);
            i++;
        }
        this.listResList = tem;
        return listResList;
    }

    /**
     * @return the freeResList
     */
    public List<Resourceinfo> getFreeResList() {
        if (null == freeResList) {
            freeResList = resDao.getBeanListHandlerRunner("select * from resourceinfo where canBeSeenbyAll=1", resource);
        }
        return freeResList;
    }
}
