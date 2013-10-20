/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Position;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import tools.PublicFields;
import tools.SQLTool;
import tools.StaticFields;

/**
 *
 * @author Idea
 */
@Named
@SessionScoped
public class PositionBackingBean implements java.io.Serializable {
   @Inject
   private  CheckLogin checkLogin;
    private SQLTool<Position> pDao;
    private LinkedHashMap<String, Integer> positionMap;
    private String newPosition ;
    private Position position;

    @PostConstruct
    public void init(){
         pDao = new SQLTool<>();
         newPosition = new PublicFields().getTag();
         position=new Position();
    }
    public String getNewPosition() {
        return newPosition;
    }

    public void setNewPosition(String newPosition) {
        this.newPosition=newPosition;
       
    }
    public String  saveNewPosition(){
         if (this.newPosition.length() > 0 && !this.newPosition.equals(new PublicFields().getTag())) {
            if (pDao.getBeanListHandlerRunner("select * from Position"+StaticFields.currentGradeNum+" where locate('" + this.newPosition + "',name)>0", position).size() <= 0) {
                Position myposition = new Position();
                myposition.setName(newPosition);
                myposition.setUserno(this.checkLogin.getUser().getUno());
                pDao.executUpdate("insert into position" +StaticFields.currentGradeNum+" (name, userno) values('" + myposition.getName() + "', '" + myposition.getUserno() + "')");
                this.positionMap = null;
            }
        } else {
            if (!this.newPosition.equals(new PublicFields().getTag())) {
                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("请输入职位名称,如果您点击的不是\"添加新职位\"，可以忽略本提示。"));
            }
        }
        return null;
    }

    public LinkedHashMap<String, Integer> getPositionMap() {
        if (null == this.positionMap||positionMap.isEmpty()) {
            this.positionMap = new LinkedHashMap<String, Integer>();
            List<Position> positionList = pDao.getBeanListHandlerRunner("select * from position"+StaticFields.currentGradeNum+"", position);
            Iterator<Position> it = positionList.iterator();
            while (it.hasNext()) {
                Position po = it.next();
                this.positionMap.put(po.getName(), po.getId());
            }
        }
        return positionMap;
    }
        /**
     * @return the checkLogin
     */
    public CheckLogin getCheckLogin() {
        return checkLogin;
    }

    /**
     * @param checkLogin the checkLogin to set
     */
    public void setCheckLogin(CheckLogin checkLogin) {
        this.checkLogin = checkLogin;
    }
}
