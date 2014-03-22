
import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author hgs
 */
@Named
@RequestScoped
public class newJsf implements Serializable{
    public String OK(){
        FacesContext.getCurrentInstance().addMessage("form1:ok", new FacesMessage("asfk;afs"));
        return "newjsf";
    }
}
