/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Student;
import entities.Teacherinfo;
import java.io.Serializable;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import sessionBeans.StudentFacadeLocal;
import sessionBeans.TeacherinfoFacadeLocal;
import tools.StaticFields;

/**
 *
 * @author myPC
 */
@Named
@RequestScoped
public class CheckLogin implements Serializable {

    @EJB
    private TeacherinfoFacadeLocal teachEjb;
    @EJB
    private StudentFacadeLocal stuEjb;
    private String userNo;
    private String userPassword;
    private String rand;
    @Inject
    TopMenu topMenu;

    private boolean rendered = true;
ResourceBundle resourceLocal;
    private HttpSession session = null;

    @PostConstruct
    public void init() {
        session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        session.setAttribute("tryTime", 0);
        resourceLocal=ResourceBundle.getBundle("Bundle");
    }

    private boolean isUserlegal() {
        boolean result = false;
        if (this.userNo.length() == StaticFields.teacherUnoLength) {//可能是教师登录
            Teacherinfo teacher = teachEjb.find(this.userNo);
            if (teacher.getPassword().equals(this.userPassword)) {
                session.setAttribute(StaticFields.sessionTeacher, teacher);
                session.setAttribute(StaticFields.userNoLength, this.userNo.length());
                result = true;
            } else {
                session.setAttribute(StaticFields.sessionTeacher, null);
                result = false;
            }
        } else if (this.userNo.length() == StaticFields.stuUnoLength1 || this.userNo.length() == StaticFields.stuUnoLength2) {//可能是学生登录
            Student student = stuEjb.find(this.userNo);
            if (student.getPassword().equals(this.userPassword)) {
                session.setAttribute(StaticFields.sessionStudent, student);
                session.setAttribute(StaticFields.userNoLength, this.userNo.length());
                result = true;
            } else {
                session.setAttribute(StaticFields.sessionStudent, null);
                result = false;
            }
        }
        if (result == false) {
            FacesContext.getCurrentInstance().addMessage("loginForm:loginButtonMsg", new FacesMessage(resourceLocal.getString("#{bundle.ivalidUP}")));
        }
        return result;
    }

    public String validateUser() {
        String genRand = ((String) session.getAttribute("rand")).trim();//验证码
        int tryTime = (int) (session.getAttribute("tryTime"));
        if (tryTime++ < 200) {//尝试登录未超过规定次数
            this.rendered = true;
            if (genRand.equals(this.rand)) {//验证码正确
                if (isUserlegal()) {//用户存在
                    topMenu.setLogined(true);
                    session.setAttribute("tryTime", 0);
                    return "/operation/main?faces-redirect=true";
                } else {//用户名或密码错误
                    topMenu.setLogined(false);
                    String messageString=resourceLocal.getString("add")+resourceLocal.getString("success");
                    FacesContext.getCurrentInstance().addMessage("loginForm:loginButtonMsg", new FacesMessage(resourceLocal.getString("#{bundle.ivalidUP}")));
                    this.rand = null;
                    return null;
                }
            } else {//验证码错误
                this.rand = null;
                FacesContext.getCurrentInstance().addMessage("loginForm:validatorCode", new FacesMessage(resourceLocal.getString("#{bundle.invValiCode}")));
                return null;
            }
        } else {
            this.rendered = false;
            FacesContext.getCurrentInstance().addMessage("loginForm:loginButtonMsg", new FacesMessage(resourceLocal.getString("#{bundle.tryFaild")));
            this.rand = null;
            return null;
        }
    }

    public String logout() {
        session.invalidate();
        return "/login/login?faces-redirect=true";

    }

    public void setRand(String rand) {
        this.rand = rand;
    }

    public String getRand() {
        return this.rand;
    }

    public boolean isRendered() {
        return rendered;
    }

    public void setRendered(boolean rendered) {
        this.rendered = rendered;
    }

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
}
