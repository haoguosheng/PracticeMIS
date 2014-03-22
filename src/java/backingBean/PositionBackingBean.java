/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.MyUser;
import entities.Position;
import entities.Student;
import entities.Teacherinfo;

import java.util.LinkedHashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import sessionBeans.PositionFacadeLocal;
import tools.PublicFields;
import tools.RepeatPaginator;
import tools.StaticFields;

/**
 *
 * @author Idea
 */
@Named
@SessionScoped
public class PositionBackingBean implements java.io.Serializable {
    private Position position;
    @EJB
    private PositionFacadeLocal positionEjb;
    private LinkedHashMap<String, Integer> positionMap;
    private RepeatPaginator paginator;
    private List<Position> positionList;
    private String searchName;

    MyUser user;
    HttpSession mySession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
    @Inject
    PublicFields publicFields;

    @PostConstruct
    public void init() {
        mySession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
         int length = (int) (mySession.getAttribute("userNoLength"));
        if (length == StaticFields.teacherUnoLength) {
            user = (Teacherinfo) mySession.getAttribute("teacherUser");

        } else if (length == StaticFields.stuUnoLength1 || length == StaticFields.stuUnoLength2) {
            user = (Student) mySession.getAttribute("studentUser");
        }
    }

    public String add() {
        if (positionEjb.getList("select * from position  where locate('" + this.position.getName() + "',name)>0 order by id").size() <= 0) {
            position.setUserno(this.user.getUno());
            positionEjb.create(position);
            paginator = null;
            this.positionMap = null;
            Position tem = positionEjb.getList("select * from position where name='" + position.getName() + "'").get(0);

            publicFields.updatePositionList(tem, StaticFields.ADD);
        } else {
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("\"" + this.position.getName() + "\"已经存在类似的职位了！添加失败！"));
        }
        return null;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public RepeatPaginator getPaginator() {
        if (paginator == null) {
            paginator = new RepeatPaginator(this.getPositionList(), 10);
        }
        return paginator;
    }

    public void setPaginator(RepeatPaginator paginator) {
        this.paginator = paginator;
    }

    public List<Position> getPositionList() {
        if (null == this.getSearchName() || this.getSearchName().trim().length() == 0) {
            this.positionList = publicFields.getPositionList();
        } else {
            this.positionList = positionEjb.getList("select * from Position  where locate('" + this.getSearchName() + "',name)>0 order by id");
        }
        return positionList;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }

    public LinkedHashMap<String, Integer> getPositionMap() {
        return publicFields.getPositionMap();
    }

}
