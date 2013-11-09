/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Position;
import entities.User;
import entitiesBeans.PositionLocal;
import java.util.LinkedHashMap;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
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

   
    @Inject
    private Position position;
 private @Inject User user;
    private final PositionLocal pDao = new PositionLocal();
    private LinkedHashMap<String, Integer> positionMap;
    private RepeatPaginator paginator;
    private List<Position> positionList;
    private String searchName;

    public String add() {
        if (pDao.getList("select * from position" + StaticFields.currentGradeNum + " where locate('" + this.position.getName() + "',name)>0").size() <= 0) {
            position.setUserno(this.getUser().getUno());
            pDao.create(position);
            paginator = null;
            this.positionMap = null;
            Position tem=pDao.getList("select * from position where name='"+position.getName()+"'").get(0);
            PublicFields.updatePositionList(tem, StaticFields.ADD);
        } else {
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("\"" + this.position.getName() + "\"已经存在类似的职位了！添加失败！"));
        }
        return null;
    }


 
    public User getUser() {
//                if (null == user) {
//            FacesContext context = FacesContext.getCurrentInstance();
//            HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
//            user = (User) session.getAttribute("myUser");
//        }
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
            this.positionList = PublicFields.getPositionList();
        } else {
            this.positionList = pDao.getList("select * from Position" + StaticFields.currentGradeNum + " where locate('" + this.getSearchName() + "',name)>0");
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
        return PublicFields.getPositionMap();
    }

}
