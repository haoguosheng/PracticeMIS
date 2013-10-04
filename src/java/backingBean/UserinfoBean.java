/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.User;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import tools.ForCallBean;
import tools.SQLTool;

/**
 *
 * @author HgsPc
 */
@ManagedBean
@SessionScoped
public class UserinfoBean implements Serializable {

    private SQLTool<User> userDao = new SQLTool<User>();
    private String userno;
    private int cityId;
    private LinkedHashMap<String, String> studentMap;
    private User user;

    public LinkedHashMap<String, String> getStudentMap() {
        List<User> usrList = userDao.getBeanListHandlerRunner("select * from student" + getUser().getSchoolId() + " where UNO in (select stuno from stuentrel" + getUser().getSchoolId() + " where ENTERID in (select id from enterprise where cityid=" + cityId + "))",getUser());
        Iterator<User> it = usrList.iterator();
        studentMap = new LinkedHashMap<String, String>();
        while (it.hasNext()) {
            User tem = it.next();
            tem.setSchoolId(user.getSchoolId());
            studentMap.put(tem.getName(), tem.getUno());
        }
        return studentMap;
    }

    public String submit() {
        userDao.executUpdate("update student" + getUser().getSchoolId() + " set password='" + getUser().getPassword() + "', email='" + getUser().getEmail() + "', name='" + getUser().getName() + "', phone='" + getUser().getPhone() + "', roleId=" + getUser().getRoleid() + ", nameofunitid='" + getUser().getNameofunitid() + "' where uno='" + getUser().getUno() + "'");
        FacesContext.getCurrentInstance().addMessage("globalMessages", new FacesMessage("修改成功！"));
        return null;
    }

    public User getUser() {
        if (null == user) {
            this.user = new ForCallBean().getUser();
        }
        return user;
    }

    public String getUserno() {
        return userno;
    }

    public void setUserno(String userno) {
        this.userno = userno;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
