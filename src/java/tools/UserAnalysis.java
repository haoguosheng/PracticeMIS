/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import entities.Nameofunit;
import entities.Roleinfo;
import entities.User;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class UserAnalysis {

    private static SQLTool<User> userDao = new SQLTool<User>();
    private static User csUser = new User();
    private static SQLTool<Nameofunit> nameDao = new SQLTool<Nameofunit>();
    private static Nameofunit csNameofUnit = new Nameofunit();
    private static SQLTool<Roleinfo> roleDao = new SQLTool<Roleinfo>();
    private static Roleinfo csRoleinfo = new Roleinfo();

    public static String getSchoolId(String uno) {
        String schoolId;
        if (uno.trim().length() == StaticFields.teacherUnoLength1) {
            String sql = "select * from teacherinfo" + StaticFields.currentGradeNum + "  where uno = '" + uno + "'";
            schoolId = userDao.getBeanListHandlerRunner(sql, csUser).get(0).getNameofunitid();
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
        if (uno.length() == StaticFields.teacherUnoLength1) {
            return "teacherinfo";
        } else if (uno.length() == StaticFields.stuUnoLength1 || uno.length() == StaticFields.stuUnoLength2) {
            return "student";
        } else {
            return null;
        }
    }

    public static String getSchoolName(String uno) {
        String temp;
        //      System.out.println("SchoolId11============================" + uno);
        if (uno.length() == StaticFields.teacherUnoLength1) {
            String sql = "select * from teacherinfo" + StaticFields.currentGradeNum + "  where uno = '" + uno + "'";
            List<User> teacherList = userDao.getBeanListHandlerRunner(sql, csUser);
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
        List<Nameofunit> list = nameDao.getBeanListHandlerRunner(s, csNameofUnit);
        return list.get(0).getName();
    }

    public static String getRoleName(String uno) {
        String temp;
        int roleid;
        if (uno.length() == StaticFields.teacherUnoLength1) {
            String sql = "select * from teacherinfo" + StaticFields.currentGradeNum + "  where uno = '" + uno + "'";
            List<User> teacherList = userDao.getBeanListHandlerRunner(sql, csUser);
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
            List<User> studentList = userDao.getBeanListHandlerRunner(sql, csUser);
            roleid = studentList.get(0).getRoleid();
        }
        String s = "select * from roleinfo" + StaticFields.currentGradeNum + "  where id=" + roleid;
        List<Roleinfo> roleList = roleDao.getBeanListHandlerRunner(s, csRoleinfo);
        return roleList.get(0).getName();
    }
}
