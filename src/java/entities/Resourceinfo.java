/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import entitiesBeans.ResourceinfoLocal;
import java.io.Serializable;
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
public class Resourceinfo implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private Integer parentid=0;
    private String refas;
    private String comment;
    private String recommendrole;
    private Integer menuorder=0;
    
    private Resourceinfo parent;
    private List<Resourceinfo> children;
    private final ResourceinfoLocal myLocal = new ResourceinfoLocal();

    public Resourceinfo() {
    }

    public Resourceinfo(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRefas() {
        return refas;
    }

    public void setRefas(String refas) {
        this.refas = refas;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRecommendrole() {
        return recommendrole;
    }

    public void setRecommendrole(String recommendrole) {
        this.recommendrole = recommendrole;
    }

    public Integer getMenuorder() {
        return menuorder;
    }

    public void setMenuorder(Integer menuorder) {
        this.menuorder = menuorder;
    }

    public int getParentid() {
        return parentid;
    }

    public void setParentid(int parentid) {
        this.parentid = parentid;
    }

    /**
     * @return the parent
     */
    public Resourceinfo getParResourceinfo() {
        if (parent == null) {
            List<Resourceinfo> resourceList= myLocal.getList("select * from resourceinfo" +StaticFields.currentGradeNum+"  where id=" + parentid);
            if(resourceList.isEmpty()){
                return null;
            }else{
                parent=resourceList.get(0);
            }
        }
        return parent;
    }

    public void setParResourceinfo(Resourceinfo parResourceinfo) {
        this.parent = parResourceinfo;
    }

    public List<Resourceinfo> getChildResourceinfo() {
        if (children == null) {
            children = myLocal.getList("select * from resourceinfo" +StaticFields.currentGradeNum+"  where parentid=" + id);
        }
        return children;
    }

    public void setChildResourceinfo(List<Resourceinfo> childResourceinfo) {
        this.children = childResourceinfo;
    }
}
