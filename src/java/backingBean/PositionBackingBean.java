/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Position;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import tools.ForCallBean;
import tools.PublicFields;
import tools.SQLTool;
import tools.StaticFields;

/**
 *
 * @author Idea
 */
@ManagedBean
@SessionScoped
public class PositionBackingBean implements java.io.Serializable {

    private SQLTool<Position> pDao = new SQLTool<Position>();
    private LinkedHashMap<String, Integer> positionMap;
    private String newPosition = new PublicFields().getTag();
    private Position position=new Position();

    public String getNewPosition() {
        return newPosition;
    }

    public void setNewPosition(String newPosition) {
        this.newPosition = newPosition.trim();
        if (this.newPosition.length() > 0 && !this.newPosition.equals(new PublicFields().getTag())) {
            if (pDao.getBeanListHandlerRunner("select * from Position"+StaticFields.currentGradeNum+" where locate('" + this.newPosition + "',name)>0", position).size() <= 0) {
                Position myposition = new Position();
                myposition.setName(newPosition);
                myposition.setUserno(new ForCallBean().getUser().getUno());
                pDao.executUpdate("insert into position" +StaticFields.currentGradeNum+" (name, userno) values('" + myposition.getName() + "', '" + myposition.getUserno() + "')");
                this.positionMap = null;
                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("已经把" + this.newPosition + "添加到左边列表框，请选择！"));
            }
//            else {
//                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage(this.newPosition + "已经存在类似的职位了！请从左边选择！"));
//            }
        } else {
            if (!this.newPosition.equals(new PublicFields().getTag())) {
                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("请输入职位名称,如果您点击的不是\"添加新职位\"，可以忽略本提示。"));
            }
        }
    }

    public LinkedHashMap<String, Integer> getPositionMap() {
        if (null == this.positionMap) {
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
}
