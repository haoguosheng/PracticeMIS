/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.List;
import tools.SQLTool;
import tools.StaticFields;

/**
 *
 * @author Administrator
 */
public class Resourceinfo implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private int parentid;
    private String refas;
    private String comment;
    private String recommendrole;
    private Integer menuorder;
    private Resourceinfo parResourceinfo;
    private List<Resourceinfo> childResourceinfo;
    private SQLTool<Resourceinfo> resDao = new SQLTool<>();

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
     * @return the parResourceinfo
     */
    public Resourceinfo getParResourceinfo() {
        if (parResourceinfo == null) {
            parResourceinfo = resDao.getBeanListHandlerRunner("select * from resourceinfo" +StaticFields.currentGradeNum+"  where id=" + parentid, new Resourceinfo()).get(0);
        }
        return parResourceinfo;
    }

    /**
     * @param parResourceinfo the parResourceinfo to set
     */
    public void setParResourceinfo(Resourceinfo parResourceinfo) {
        this.parResourceinfo = parResourceinfo;
    }

    /**
     * @return the childResourceinfo
     */
    public List<Resourceinfo> getChildResourceinfo() {
        if (childResourceinfo == null) {
            childResourceinfo = resDao.getBeanListHandlerRunner("select * from resourceinfo" +StaticFields.currentGradeNum+"  where parentid=" + id, new Resourceinfo());
        }
        return childResourceinfo;
    }

    /**
     * @param childResourceinfo the childResourceinfo to set
     */
    public void setChildResourceinfo(List<Resourceinfo> childResourceinfo) {
        this.childResourceinfo = childResourceinfo;
    }

    @Override
    public Resourceinfo clone() {
        Resourceinfo resource = new Resourceinfo();
        resource.setComment(comment);
        resource.setId(id);
        resource.setName(name);
        resource.setParentid(parentid);
        resource.setRecommendrole(recommendrole);
        resource.setRefas(refas);
        resource.setMenuorder(menuorder);
        return resource;
    }
}
