/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Resourceinfo;
import entities.Roleinfo;
import entities.User;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import tools.ApplicationForCallBean;
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
        User myUser = new ForCallBean().getUser();
        String resourceIds = "," + roleDao.getIdListHandlerRunner("select resouceids from roleinfo where id=" + myUser.getRoleid()).get(0) + ",";
        LinkedList<ResourceWithChildren> result = new LinkedList<ResourceWithChildren>();
        LinkedList<ResourceWithChildren> readyResource = ApplicationForCallBean.getListResList();
        for (int i = 0; i < readyResource.size(); i++) {
            ResourceWithChildren preparedRe = readyResource.get(i).clone();
            if (resourceIds.contains("," + String.valueOf(preparedRe.getParent().getId()) + ",")) {
                result.add(preparedRe);
                List<Resourceinfo> childrenRes = preparedRe.getChildrenResourceList();
                for (int j = 0; j < childrenRes.size(); j++) {
                    Resourceinfo tem =  childrenRes.get(j);
                    if (!resourceIds.contains(String.valueOf(tem.getId()))) {
                        childrenRes.remove(tem);
                    }
                }
            }
        }
        this.resWithChildrenList = result;
    }
}
