/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Resourceinfo;
import entities.Roleinfo;
import entities.User;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import tools.ForCallBean;
import tools.SQLTool;

/**
 *
 * @author myPC
 */
@SessionScoped
@ManagedBean
public class TopMenu implements Serializable {

    private SQLTool<Roleinfo> roleDao = new SQLTool<Roleinfo>();
    private LinkedList<ResourceWithChildren> resWithChildrenList;
    Resourceinfo resource = new Resourceinfo();

    /**
     * @return the listResList
     */
    public LinkedList<ResourceWithChildren> getResWithChildrenList() {
        if (null == this.resWithChildrenList) {
            this.calcuListResList();
        }
        return this.resWithChildrenList;
    }

    private void calcuListResList() {
        User user = new ForCallBean().getUser();
        String resourceIds = "," + roleDao.getIdListHandlerRunner("select resouceids from roleinfo where id=" + user.getRoleid()).get(0) + ",";
        LinkedList<ResourceWithChildren> result = new LinkedList<ResourceWithChildren>();
        LinkedList<ResourceWithChildren> readyResource = new ForCallBean().getListResList();
        for (int i = 0; i < readyResource.size(); i++) {
            ResourceWithChildren preparedRe = readyResource.get(i).clone();
            if (resourceIds.contains(","+String.valueOf(preparedRe.getParent().getId())+",")) {
                result.add(preparedRe);
                List<Resourceinfo> childrenRes = preparedRe.getChildrenResourceList();
                Iterator<Resourceinfo> itChild = childrenRes.iterator();
                while (itChild.hasNext()) {
                    Resourceinfo tem = itChild.next();
                    if (!resourceIds.contains(String.valueOf(tem.getId()))) {
                        childrenRes.remove(tem);
                    }
                }

            }
        }
        this.resWithChildrenList = result;
    }
}
