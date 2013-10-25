/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Enterprise;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 *
 * @author Administrator
 */
@Named
@ApplicationScoped
public class EnterpriseCity  implements java.io.Serializable{
    private Enterprise ent;
    private int cityId;
    
    /**
     * @return the ent
     */
    public Enterprise getEnt() {
        return ent;
    }

    /**
     * @param ent the ent to set
     */
    public void setEnt(Enterprise ent) {
        this.ent = ent;
    }

    /**
     * @return the cityId
     */
    public int getCityId() {
        return cityId;
    }

    /**
     * @param cityId the cityId to set
     */
    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

 
}
