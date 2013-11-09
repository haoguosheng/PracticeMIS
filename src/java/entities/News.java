/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import entitiesBeans.NewsLocal;
import entitiesBeans.UserLocal;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import tools.StaticFields;

/**
 *
 * @author Administrator
 */
@Named
@SessionScoped
public class News implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String content;
    private Date inputdate;
    private String userno;
    private String newsTitle;
    private final NewsLocal myLocal=new NewsLocal();

    private User inputor;
    private final UserLocal userLocal = new UserLocal();

    public News() {
    }

    public News(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getInputdate() {
        return inputdate;
    }

    public void setInputdate(Date inputdate) {
        this.inputdate = inputdate;
    }

    public String getUserno() {
        return userno;
    }

    public void setUserno(String userno) {
        this.userno = userno;
    }

    public User getInputor() {
        if (inputor == null) {
            this.dealId0();
        } else if (null == this.inputor.getUno() ) {
            this.dealId0();
        }
        return inputor;
    }
    /*
     *当对象是内在对象时处理外键inputor
     */

    private void dealId0() {
        if (null == this.getId()) {
            if (null == inputor) {
                this.inputor = new User();
            }
        } else {
            List<User> userTem = userLocal.getList("select * from teacherinfo" + StaticFields.currentGradeNum + "  where uno='" + this.getUserno() + "'");
            if (userTem.isEmpty()) {
                inputor = null;
            } else {
                inputor = userTem.get(0);
            }
        }
    }

    public void setInputor(User inputor) {
        this.inputor = inputor;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }
}
