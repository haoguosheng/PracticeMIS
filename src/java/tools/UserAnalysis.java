/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import entities.Nameofunit;
import entities.Roleinfo;
import entities.User;
import entitiesBeans.NameofunitLocal;
import entitiesBeans.RoleinfoLocal;
import entitiesBeans.UserLocal;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author Administrator
 */
@ApplicationScoped
public class UserAnalysis implements java.io.Serializable {

    private static final NameofunitLocal nameDao = new NameofunitLocal();
    private static final UserLocal userDao = new UserLocal();

    private static final RoleinfoLocal roleDao = new RoleinfoLocal();

    public static String getSchoolId(String uno) {
        String schoolId;
        if (uno.trim().length() == StaticFields.teacherUnoLength) {
            String sql = "select * from teacherinfo" + StaticFields.currentGradeNum + "  where uno = '" + uno + "'";
            schoolId = userDao.getList(sql).get(0).getNameofunitid();
        } else {
            if (uno.trim().length() == StaticFields.stuUnoLength1) {
                schoolId = "0" + uno.substring(2, 4);
            } else if (uno.trim().length() == StaticFields.stuUnoLength2) {
                schoolId = uno.substring(2, 5);
            } else {
                schoolId = null;
            }
        }
        return schoolId;
    }

    public static String getTableName(String uno) {
        if (uno.length() == StaticFields.teacherUnoLength) {
            return "teacherinfo";
        } else if (uno.length() == StaticFields.stuUnoLength1 || uno.length() == StaticFields.stuUnoLength2) {
            return "student";
        } else {
            return null;
        }
    }

    public static String getSchoolName(String uno) {
        String temp;
        if (uno.length() == StaticFields.teacherUnoLength) {
            String sql = "select * from teacherinfo" + StaticFields.currentGradeNum + "  where uno = '" + uno + "'";
            List<User> teacherList = userDao.getList(sql);
            temp = teacherList.get(0).getNameofunitid();
        } else {
            if (uno.length() == StaticFields.stuUnoLength1) {
                temp = "0" + uno.substring(2, 4);
            } else if (uno.length() == StaticFields.stuUnoLength2) {
                temp = uno.substring(2, 5);
            } else {
                return null;
            }
        }
        String s = "select * from nameofunit" + StaticFields.currentGradeNum + "  where id='" + temp + "'";
        List<Nameofunit> list = nameDao.getList(s);
        return list.get(0).getName();
    }

    public static String getRoleName(String uno) {
        String temp;
        int roleid;
        if (uno.length() == StaticFields.teacherUnoLength) {
            String sql = "select * from teacherinfo" + StaticFields.currentGradeNum + "  where uno = '" + uno + "'";
            List<User> teacherList = userDao.getList(sql);
            roleid = teacherList.get(0).getRoleid();
        } else {
            if (uno.length() == StaticFields.stuUnoLength1) {
                temp = "0" + uno.substring(2, 4);
            } else if (uno.length() == StaticFields.stuUnoLength2) {
                temp = uno.substring(2, 5);
            } else {
                return null;
            }
            String sql = "select * from student" + StaticFields.currentGradeNum + temp + " where uno='" + uno + "'";
            List<User> studentList = userDao.getList(sql);
            roleid = studentList.get(0).getRoleid();
        }
        String s = "select * from roleinfo" + StaticFields.currentGradeNum + "  where id=" + roleid;
        List<Roleinfo> roleList = roleDao.getList(s);
        return roleList.get(0).getName();
    }
}
