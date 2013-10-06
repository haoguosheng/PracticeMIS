/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.Date;
import tools.SQLTool;
import tools.StaticFields;

/**
 *
 * @author Administrator
 */
public class News implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String content;
    private Date inputdate;
    private String userno;
    private User teacher;
    private SQLTool<User> userDao = new SQLTool<User>();

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

    /**
     * @return the teacher
     */
    public User getTeacher() {
        if(teacher == null){
            teacher = userDao.getBeanListHandlerRunner("select * from teacherinfo" +StaticFields.currentGradeNum+"  where uno='" + userno +"'", new User()).get(0);
        }
        return teacher;
    }

    /**
     * @param teacher the teacher to set
     */
    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }
}
