/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Nameofunit;
import java.util.Calendar;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import tools.SQLTool;
import tools.StaticFields;

/**
 *
 * @author Idea
 */
@ManagedBean
@SessionScoped
public class SystemSetupBena implements java.io.Serializable {

    boolean valid = false;
    private String cureentGrade = StaticFields.currentGradeNum;
    private SQLTool<Nameofunit> nameDAO=new SQLTool<Nameofunit>();

    public String getCureentGrade() {
        return cureentGrade;
    }

    public void setCureentGrade(String currentGrade) {
        this.cureentGrade = currentGrade.trim();
    }

    public void validator(FacesContext context, UIComponent toValidate, Object value) {
        String tem = (String) value;
        int setYear;
        try {
            setYear = Integer.valueOf(tem.trim());
            int currYear = Calendar.getInstance().get(Calendar.YEAR);
            if ((currYear - setYear) < 4 || currYear < setYear) {
                this.valid = false;
                throw new ValidatorException(new FacesMessage("输入的年份必须确保是已经有该年的毕业生信息！"));
            } else {
                try {
                    nameDAO.getBeanListHandlerRunner("select * from Nameofunit"+tem.trim(), null);
                    this.valid=true;
                } catch (Exception e) {
                    this.valid = false;
                    throw new ValidatorException(new FacesMessage("数据不存在"));
                }
            }
        } catch (Exception e) {
            this.valid = false;
            throw new ValidatorException(new FacesMessage("必须输入数字"));
        } finally {
        }
    }

    public String systemSetup() {
        if (this.valid) {
            StaticFields.currentGradeNum = this.cureentGrade.trim();
        } else {
            FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("存在问题，请仔细检查"));
        }
        return null;
    }
}
