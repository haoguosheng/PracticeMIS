/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.User;
import entitiesBeans.UserLocal;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import tools.StaticFields;
import tools.UserAnalysis;

/**
 *
 * @author myPC
 */
@Named
@RequestScoped
public class CheckLogin implements Serializable {

    @Inject
    private User user;

    private Properties messagesPro;
    private String welcomeMess;
    private String rand;
    private int tryTime = 0;
    private boolean rendered = true;
    private final int tryTimeLimitedNumber = 200;
    private final UserLocal userDao = new UserLocal();

    private boolean isUserlegal() {
        boolean result;
        if (user.getUno().trim().length() == StaticFields.teacherUnoLength) {
            String sql = "select * from teacherinfo" + StaticFields.currentGradeNum + " where uno='" + user.getUno() + "' and password='" + user.getPassword() + "'";
            try {
                List<User> userList = userDao.getList(sql);
                if (null == userList) {
                    result = false;
                } else {
                    if (userList.size() > 0) {
                        User temUser = userList.get(0);
                        user.setRoleid(temUser.getRoleid());
                        user.setNameofunitid(temUser.getNameofunitid());
                        user.setPhone(temUser.getPhone());
                        user.setEmail(temUser.getEmail());
                        user.setName(temUser.getName());
                        user.setSchoolId(temUser.getSchoolId());
                        user.setLoaded(true);
                        result = true;
                    } else {
                        result = false;
                    }
                }
            } catch (Exception e) {
                result = false;
                FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("查询出错，请联系管理员！"));
                return result;
            }
        } else {
            if (null == UserAnalysis.getSchoolId(user.getUno())) {
                result = false;
                FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("错误的用户名"));
            } else {
                String table = "student" + StaticFields.currentGradeNum + UserAnalysis.getSchoolId(user.getUno());
                String sql = "select * from " + table + " where uno='" + user.getUno() + "' and password='" + user.getPassword() + "'";
                List<User> userList = userDao.getList(sql);
                if (null != userList && userList.size() > 0) {
                        User temUser = userList.get(0);
                        user.setRoleid(temUser.getRoleid());
                        user.setNameofunitid(temUser.getNameofunitid());
                        user.setPhone(temUser.getPhone());
                        user.setEmail(temUser.getEmail());
                        user.setName(temUser.getName());
                        user.setSchoolId(temUser.getSchoolId());
                        user.setLoaded(true);
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
            this.rendered = true;
            if (genRand.equals(this.rand)) {//验证码正确
                if (isUserlegal()) {//用户存在
                    User temUser = new UserLocal().find(user.getUno());
                    temUser.setLoaded(true);
                    String schoolName = UserAnalysis.getSchoolName(user.getUno());
                    session.setAttribute("myUser", temUser);
                    session.setAttribute("userno", user.getUno());
                    this.welcomeMess = this.getUser().getName() + "," + schoolName + "," + UserAnalysis.getRoleName(user.getUno());
                    tryTime = 0;
                    return "/operation/main?faces-redirect=true";

                    // return;
                } else {//用户名或密码错误
                    context.addMessage("globalMessages", new FacesMessage(this.messagesPro.getProperty("ivalidUP")));
                    this.rand = null;
                    return null;
                }
            } else {//验证码错误
                this.rand = null;
                context.addMessage("validatorCode", new FacesMessage(this.messagesPro.getProperty("invValiCode")));
                return null;
            }
        } else {
            this.rendered = false;
            context.addMessage("globalMessages", new FacesMessage(this.messagesPro.getProperty("tryFaild")));
            this.rand = null;
            return null;
        }
    }

    public String logout() {
        this.user = null;
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
        session.invalidate();
        return "/login/login?faces-redirect=true";
        
    }

    public void setRand(String rand) {
        this.rand = rand;
    }

    public String getRand() {
        return this.rand;
    }

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

    public Properties getMessagesPro() {
        return messagesPro;
    }

    public void setMessagesPro(Properties messagesPro) {
        this.messagesPro = messagesPro;
    }

    public boolean isRendered() {
        return rendered;
    }

    public void setRendered(boolean rendered) {
        this.rendered = rendered;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
