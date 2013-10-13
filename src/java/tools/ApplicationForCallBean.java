/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import backingBean.ResourceWithChildren;
import entities.Nameofunit;
import entities.Resourceinfo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Idea
 */
@ManagedBean
@SessionScoped
public class ApplicationForCallBean {

    /**
     * @return the unitIdList
     */
    private static List<String> unitIdList = new ArrayList<String>();
    private static SQLTool<Resourceinfo> resDao = new SQLTool<Resourceinfo>();
    private static SQLTool<Nameofunit> nameofUnitDao=new SQLTool<Nameofunit>();
    private static LinkedList<ResourceWithChildren> listResList;
    private static Resourceinfo resource = new Resourceinfo();

    /**
     * @return the listResList
     */
    public static LinkedList<ResourceWithChildren> getListResList() {
        if (null == listResList) {
            calcuListResList();
        }
        return listResList;
    }

    private static void calcuListResList() {
        List<Resourceinfo> parentResource = resDao.getBeanListHandlerRunner("select * from resourceinfo" +StaticFields.currentGradeNum+"  where parentid is null order by MENUORDER", resource);//把双亲拿来
        List<Resourceinfo> childrenResource = resDao.getBeanListHandlerRunner("select * from resourceinfo" +StaticFields.currentGradeNum+"  where parentid is not null", resource);
        LinkedList<ResourceWithChildren> tem = new LinkedList<ResourceWithChildren>();
        Iterator<Resourceinfo> parentIt = parentResource.iterator();
        int i = 0;
        while (parentIt.hasNext()) {
            ResourceWithChildren rwc = new ResourceWithChildren();
            Resourceinfo pareTem = parentIt.next();
            rwc.setParent(pareTem);
            Iterator<Resourceinfo> childrenIt = childrenResource.iterator();
            while (childrenIt.hasNext()) {
                Resourceinfo temch = childrenIt.next();
                if (temch.getParentid() == pareTem.getId()) {
                    rwc.getChildrenResourceList().add(temch);
                }
            }
            tem.add(rwc);
        }
        listResList = tem;
    }

    public static List<String> getUnitIdList() {
        if (unitIdList.isEmpty()) {
            unitIdList=nameofUnitDao.getIdListHandlerRunner("select id from nameofunit" +StaticFields.currentGradeNum);
        }
        return unitIdList;
    }
}
