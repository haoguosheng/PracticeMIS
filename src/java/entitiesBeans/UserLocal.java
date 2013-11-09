/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entitiesBeans;

import entities.User;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import tools.SQLTool;
import tools.StaticFields;
import tools.UserAnalysis;

/**
 *
 * @author Idea
 */
public class UserLocal extends AbstractEntityBean<User> implements java.io.Serializable {

    private final SQLTool<User> myDao = new SQLTool<>();

    public void create(User entity) {
        String tableName = UserAnalysis.getTableName(entity.getUno());
        String schoolId = UserAnalysis.getSchoolId(entity.getUno());
        if (tableName.contains("tea")) {
            myDao.executUpdate("insert into " + tableName + StaticFields.currentGradeNum
                    + "(uno, password, nameofunit, name, email, phone, roledId) values('"
                    + entity.getUno() + "','" + entity.getPassword() + "','" + entity.getNameofunitid()
                    + "','" + entity.getName() + "','" + entity.getEmail() + "','"
                    + entity.getPhone() + "'," + entity.getRoleid() + ")");
        } else {
            myDao.executUpdate("insert into " + tableName + StaticFields.currentGradeNum + schoolId
                    + "(uno, password, nameofunit, name, email, phone, roledId) values('"
                    + entity.getUno() + "','" + entity.getPassword() + "','" + entity.getNameofunitid()
                    + "','" + entity.getName() + "','" + entity.getEmail() + "','"
                    + entity.getPhone() + "'," + entity.getRoleid() + ")");
        }

    }

    public void edit(User entity) {
        String tableName = UserAnalysis.getTableName(entity.getUno());
        String schoolId = UserAnalysis.getSchoolId(entity.getUno());
        if (tableName.contains("tea")) {
            myDao.executUpdate("update " + tableName + StaticFields.currentGradeNum
                    + " set password='" + entity.getPassword() + "', nameofunitid='" + entity.getNameofunitid()
                    + "', name='" + entity.getName() + "', email='" + entity.getEmail()
                    + "', phone='" + entity.getPhone() + "', roledId=" + entity.getRoleid()
                    + " where uno='" + entity.getUno() + "'"
            );
        } else {
            myDao.executUpdate("update " + tableName + StaticFields.currentGradeNum + schoolId
                    + " set password='" + entity.getPassword() + "', nameofunitid='" + entity.getNameofunitid()
                    + "', name='" + entity.getName() + "', email='" + entity.getEmail()
                    + "', phone='" + entity.getPhone()
                    + "' where uno='" + entity.getUno() + "'"
            );
        }

    }

    public void remove(User entity) {
        String tableName = UserAnalysis.getTableName(entity.getUno());
        if (tableName.contains("tea")) {
            myDao.executUpdate("delete from " + tableName + StaticFields.currentGradeNum + " where uno='" + entity.getUno() + "'");
        } else {
            myDao.executUpdate("delete from " + tableName + StaticFields.currentGradeNum + UserAnalysis.getSchoolId(entity.getUno()) + " where uno='" + entity.getUno() + "'");
        }

    }

    public User find(Object uno) {
        String tableName = UserAnalysis.getTableName((String) uno);
        List<User> tem;
        if (tableName.contains("tea")) {
            tem = myDao.getBeanListHandlerRunner("select * from " + tableName + StaticFields.currentGradeNum + " where uno='" + (String) uno+"'", new User());
        } else {
            tem = myDao.getBeanListHandlerRunner("select * from " + tableName + StaticFields.currentGradeNum + UserAnalysis.getSchoolId((String) uno) + " where uno='" + (String) uno+"'", new User());
        }
        if (tem.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("查找失败！"));
            return null;
        } else {
            return tem.get(0);
        }
    }

    public List<User> findAll(String schoolId, String userNo) {
        String tableName = UserAnalysis.getTableName(userNo);
        return myDao.getBeanListHandlerRunner("select * from " + tableName + StaticFields.currentGradeNum + schoolId, new User());
    }

    public List<User> getList(String sqlString) {
        return myDao.getBeanListHandlerRunner(sqlString, new User());
    }
}
