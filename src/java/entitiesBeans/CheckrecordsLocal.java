/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entitiesBeans;

import entities.Checkrecords;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import tools.SQLTool;
import tools.StaticFields;

/**
 *
 * @author Idea
 */
public class CheckrecordsLocal extends AbstractEntityBean<Checkrecords> implements java.io.Serializable{

    private final SQLTool<Checkrecords> myDao = new SQLTool<>();

    public void create(Checkrecords entity, String schoolId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String s = sdf.format(entity.getCheckdate().getTime());
        //防止出现"'"使SQL语句无法执行
        entity.setCheckcontent(entity.getCheckcontent().replaceAll("'", "‘"));
        entity.setRecommendation(entity.getRecommendation().replaceAll("'", "‘"));
        entity.setRemark(entity.getRemark().replaceAll("'", "‘"));
       myDao.executUpdate("insert into checkrecords" + StaticFields.currentGradeNum + schoolId
                + "(stuno, teachno, checkdate, checkcontent, recommendation, rank, remark) values('"
                + entity.getStuno() + "','" + entity.getTeachno() + "','" + s
                + "','" + entity.getCheckcontent() + "','" + entity.getRecommendation() + "','"
                + entity.getRank() + "','" + entity.getRemark() + "')");
    }

    public void edit(Checkrecords entity, String schoolId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String s = sdf.format(entity.getCheckdate().getTime());
         entity.setCheckcontent(entity.getCheckcontent().replaceAll("'", "‘"));
        entity.setRecommendation(entity.getRecommendation().replaceAll("'", "‘"));
        entity.setRemark(entity.getRemark().replaceAll("'", "‘"));
        myDao.executUpdate("update checkrecords" + StaticFields.currentGradeNum + schoolId
                + " set stuno='" + entity.getStuno() + "', teachno='" + entity.getTeachno() + "', checkdate='" + s
                + "', checkcontent='" + entity.getCheckcontent() + "',' recommendation='" + entity.getRecommendation()
                + "', rank='" + entity.getRank() + "', remark='" + entity.getRemark() + "'"
                + " where id=" + entity.getId()
        );
    }

    public void remove(Checkrecords entity, String schoolId) {
       myDao.executUpdate("delete from checkrecords" + StaticFields.currentGradeNum + schoolId + " where id=" + entity.getId());
    }

    public Checkrecords find(Object id, String schoolId) {
        List<Checkrecords> tem = myDao.getBeanListHandlerRunner("select * from Checkrecords" + StaticFields.currentGradeNum + schoolId + " where id=" + id, new Checkrecords());
        if (tem.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("查找失败！"));
            return null;
        } else {
            return tem.get(0);
        }
    }

    public List<Checkrecords> findAll(String schoolId) {
           return   myDao.getBeanListHandlerRunner("select * from Checkrecords" + StaticFields.currentGradeNum + schoolId, new Checkrecords());
    }
    public List<Checkrecords> getList(String sqlString){
        return   myDao.getBeanListHandlerRunner(sqlString, new Checkrecords());
    }
}
