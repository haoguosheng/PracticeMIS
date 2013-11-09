/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

/**
 *
 * @author Idea
 */
public class StaticFields implements java.io.Serializable{
    public static final String universityId="000";
    public static final String cityTag="如果没有就添加";
    public static final int CanSeeAll=0,CanSeeOnlySchool=1,CanSeeSelf=2,CanSeeNothing=3;
    public static String currentGradeNum="";
    public static String schoolExample="026";
    public static int stuUnoLength1=8;
    public static int stuUnoLength2=9;
    public static int teacherUnoLength=6;
    public static int newsDisplayDay=90;
     public static final int teacherRole=2,adminRole=4,studentRole=3,schoolAdminRole=1;
    public static final int selectedEnt = 6;//学生选择的企业数目不能超过该值
    public static final String[] rankString = new String[]{"优秀", "良好", "及格", "不及格"};
    public static final String[] cateString = new String[]{"文科", "理科", "工科"};
    public static final int pageSize=10;
    public static final int ADD=0,DELETE=1,UPDATE=2;
    public static final String citySelect="请输入要查询的城市名称";
    public static final String loginURL="/PracticeMISV10";
    public static final int dayAfterDisabled=15;
}
