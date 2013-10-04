/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Resourceinfo;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author myPC
 */

public class ResourceWithChildren  implements Serializable{

    private Resourceinfo parent;
    private List<Resourceinfo> childrenResourceList;

    /**
     * @return the parent
     */
    public Resourceinfo getParent() {
        return parent;
    }

    /**
     * @return the resourceList
     */
    public List<Resourceinfo> getChildrenResourceList() {
        if(null==childrenResourceList){
            childrenResourceList=new LinkedList<Resourceinfo>();
        }
        return childrenResourceList;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(Resourceinfo parent) {
        this.parent = parent;
    }
    

    /**
     * @param resourceList the resourceList to set
     */
    public void setChildrenResourceList(List<Resourceinfo> resourceList) {
        this.childrenResourceList = resourceList;
    }
    @Override
    public ResourceWithChildren clone(){
        ResourceWithChildren res=new ResourceWithChildren();
        res.setParent(parent.clone());
        List<Resourceinfo> childrenResource=new LinkedList<Resourceinfo>();
        Iterator<Resourceinfo> it=this.getChildrenResourceList().iterator();
        while(it.hasNext()){
            childrenResource.add(it.next().clone());
        }
        res.setChildrenResourceList(childrenResource);
        return res;
    }
}
