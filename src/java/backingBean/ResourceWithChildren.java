/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Resourceinfo;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author myPC
 */

public class ResourceWithChildren  implements Serializable{

    private Resourceinfo parent;
    private List<Resourceinfo> resourceList;

    /**
     * @return the parent
     */
    public Resourceinfo getParent() {
        return parent;
    }

    /**
     * @return the resourceList
     */
    public List<Resourceinfo> getResourceList() {
        return resourceList;
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
    public void setResourceList(List<Resourceinfo> resourceList) {
        this.resourceList = resourceList;
    }
}
