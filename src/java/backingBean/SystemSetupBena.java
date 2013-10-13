/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import tools.StaticFields;

/**
 *
 * @author Idea
 */
@ManagedBean
@SessionScoped
public class SystemSetupBena implements java.io.Serializable{
    private String cureentGrade;

    public String getCureentGrade() {
        this.cureentGrade= StaticFields.currentGradeNum;
        return cureentGrade;
    }

    public void setCureentGrade(String currentGrade) {
        StaticFields.currentGradeNum=currentGrade;
        this.cureentGrade = currentGrade;
    }
    public String systemSetup(){
        return null;
    }
}
