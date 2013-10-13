/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.User;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import tools.SQLTool;
import tools.StaticFields;
import tools.UserAnalysis;

/**
 *
 * @author myPC
 */
@ManagedBean
@SessionScoped
public class CheckLogin implements Serializable {

    private User myUser;
    private Properties messagesPro = null;
    private String username, password, welcomeMess;
    private String rand;
    private int tryTime = 0;
    private boolean logined = false, rendered = true;
    private int tryTimeLimitedNumber = 8;
    private SQLTool<User> userDao = new SQLTool<User>();

    private boolean isUserlegal(String name, String password) {
        boolean result;
        if (name.trim().length() == 6) {
            String sql = "select * from teacherinfo where uno='" + name + "' and password='" + password + "'";
            List<User> userList = userDao.getBeanListHandlerRunner(sql, new User());
            if (null == userList) {
                result = false;
            } else {
                if (userList.size() > 0) {
                    this.myUser = userList.get(0);
                    result = true;
                } else {
                    result = false;
                }
            }
        } else {
            if (null == UserAnalysis.getSchoolId(name)) {
                result = false;
                FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("错误的用户名"));
            } else {
                String table = "student"+StaticFields.currentGradeNum + UserAnalysis.getSchoolId(name);
                String sql = "select * from " + table + " where uno='" + name + "' and password='" + password + "'";
                List<User> userList = userDao.getBeanListHandlerRunner(sql, new User());
                if (userList.size() > 0) {
                    this.myUser = userList.get(0);
                    result = true;
                } else {
                    result = false;
                }
            }
        }
        return result;
    }

    public String validateUser() {
        if (null == messagesPro) {
            this.propertyLoad();
        }
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        String genRand = ((String) session.getAttribute("rand")).trim();
        if (tryTime++ < tryTimeLimitedNumber) {//尝试登录未超过规定次数
            if (genRand.equals(this.rand)) {//验证码正确
                if (isUserlegal(this.username, this.password)) {//用户存在
                    String schoolId = UserAnalysis.getSchoolId(this.username);
                    String schoolName = UserAnalysis.getSchoolName(this.username);
                    this.getUser().setSchoolId(schoolId);
                    session.setAttribute("myUser", this.getUser());
                    session.setAttribute("userno", this.username);
                    this.welcomeMess = this.getUser().getName() + "," + schoolName + "," + UserAnalysis.getRoleName(this.username);
                    tryTime = 0;
                    this.logined = true;
                    return "/operation/main?faces-redirect=true";

                    // return;
                } else {//用户名或密码错误
                    context.addMessage("globalMessages", new FacesMessage(this.messagesPro.getProperty("ivalidUP")));
                    this.rand = null;
                    this.password = null;
                    return null;
                }
            } else {//验证码错误
                this.rand = null;
                this.password = null;
                context.addMessage("validatorCode", new FacesMessage(this.messagesPro.getProperty("invValiCode")));
                return null;
            }
        } else {
            this.rendered = false;
            context.addMessage("globalMessages", new FacesMessage(this.messagesPro.getProperty("tryFaild")));
            this.rand = null;
            this.password = null;
            return null;
        }
    }

    public String logout() {
        this.myUser = null;
        this.username = null;
        this.logined = false;
        this.password = null;
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
        session.invalidate();
        return "/login/login?faces-redirect=true";
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @param rand the rand to set
     */
    public void setRand(String rand) {
        this.rand = rand;
    }

    public String getRand() {
        return this.rand;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }

    /**
     * @return the welcomeMess
     */
    public String getWelcomeMess() {
        return welcomeMess;
    }

    private void propertyLoad() {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        this.messagesPro = new Properties();
        try {
            this.messagesPro.load(ec.getResourceAsStream("/WEB-INF/classes/messages.properties"));
        } catch (IOException ex) {
            Logger.getLogger(CheckLogin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the messagesPro
     */
    public Properties getMessagesPro() {
        return messagesPro;
    }

    /**
     * @param messagesPro the messagesPro to set
     */
    public void setMessagesPro(Properties messagesPro) {
        this.messagesPro = messagesPro;
    }

    /**
     * @return the rendered
     */
    public boolean isLogined() {
        return logined;
    }

    /**
     * @param rendered the rendered to set
     */
    public void setLogined(boolean rendered) {
        this.logined = rendered;
    }

    /**
     * @return the rendered
     */
    public boolean isRendered() {
        return rendered;
    }

    /**
     * @param rendered the rendered to set
     */
    public void setRendered(boolean rendered) {
        this.rendered = rendered;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return myUser;
    }
}
