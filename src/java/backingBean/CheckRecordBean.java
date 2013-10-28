/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Checkrecords;
import entities.Enterstudent;
import entities.Practicenote;
import entities.Stuentrel;
import entities.User;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import tools.SQLTool;
import tools.StaticFields;
import tools.UserAnalysis;

/**
 *
 * @author Administrator
 */
@Named
@SessionScoped
public class CheckRecordBean implements Serializable {

    @Inject
    private CheckLogin checkLogin;
    private Checkrecords checkrecords = new Checkrecords();
    private Checkrecords detailCheckrecords = new Checkrecords();
    private final Calendar c = Calendar.getInstance();
    private int year = c.get(Calendar.YEAR), month = c.get(Calendar.MONTH), day = c.get(Calendar.DAY_OF_MONTH);
    private final int currentMonth = month;
    private LinkedHashMap<String, Integer> rankMap;
    private String studentNo;
    private User StudentUser;
    private String teacherNo;
    private LinkedHashMap<Integer, Integer> dayMap;
    private List<Practicenote> practiceList;
    private ArrayList<Checkrecords> checkList;
    private SQLTool<User> userDao;
    private SQLTool<Checkrecords> checkDao;
    private SQLTool<Practicenote> pDao;
    private SQLTool<Stuentrel> stuentrelDao;
    private Integer cityid;
    private Integer enterid;
    private List<Stuentrel> stuInSameRel = new LinkedList<>();

    @PostConstruct
    public void init() {
        checkrecords = new Checkrecords();
        userDao = new SQLTool<>();
        checkDao = new SQLTool<>();
        pDao = new SQLTool<>();
        stuentrelDao = new SQLTool<>();
        dayMap = new LinkedHashMap<>();
        practiceList = new LinkedList<>();
    }

    public String addCheckRecord() {
        User myUser = getCheckLogin().getUser();
        checkrecords.setSchoolId(myUser.getSchoolId());
        Calendar tempc = Calendar.getInstance();
        tempc.add(Calendar.YEAR, year - c.get(Calendar.YEAR));
        tempc.add(Calendar.MONTH, month - c.get(Calendar.MONTH));
        tempc.add(Calendar.DAY_OF_MONTH, day - c.get(Calendar.DAY_OF_MONTH));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String s = sdf.format(tempc.getTime());
        String sql = "select * from checkRecords" + StaticFields.currentGradeNum + checkrecords.getSchoolId() + " where stuno='" + this.studentNo + "' and checkdate='" + s + "'";
        List<Checkrecords> checkListTme = checkDao.getBeanListHandlerRunner(sql, checkrecords);
        if (checkListTme.size() > 0) {//该生检查记录已经存在了
            this.checkrecords = checkListTme.get(0);
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage(UserAnalysis.getRoleName(this.studentNo) + "的检查记录已经存在，不能再添加了"));
        } else {
            checkrecords.setStuno(this.studentNo);
            checkrecords.setTeachno(myUser.getUno());
            checkrecords.setCheckdate(tempc.getTime());
            String insert = "insert into checkrecords" + StaticFields.currentGradeNum + checkrecords.getSchoolId() + "(stuno, teachno, checkdate, checkcontent, recommendation, rank, remark) values('"
                    + this.studentNo + "', '" + myUser.getUno() + "', '" + s + "', '" + checkrecords.getCheckcontent() + "', '"
                    + checkrecords.getRecommendation() + "', '" + checkrecords.getRank() + "', '" + checkrecords.getRemark() + "')";
            checkDao.executUpdate(insert);
            //          FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("添加成功，您可以继续添加"));
            this.checkrecords = new Checkrecords();
            this.setYear(c.get(Calendar.YEAR));
            this.setMonth(c.get(Calendar.MONTH));
            this.setDay(c.get(Calendar.DAY_OF_MONTH));
        }
        return null;
    }

    public ArrayList<Checkrecords> getCheckList() {
        User user = this.checkLogin.getUser();
        checkList = new ArrayList<>();
        if (user.getRoleid() == StaticFields.studentRole) {//学生
            List<Stuentrel> listStuEnt = stuentrelDao.getBeanListHandlerRunner("select * from stuentrel" + StaticFields.currentGradeNum + user.getSchoolId() + " where  stuno ='" + user.getUno() + "'", new Stuentrel());
            if (listStuEnt.size() > 0) {
                Stuentrel stutem = listStuEnt.get(0);
                checkList = (ArrayList) checkDao.getBeanListHandlerRunner("select * from HGS.CHECKRECORDS" + UserAnalysis.getSchoolId(stutem.getStuno()) + " where stuno='" + stutem.getStuno() + "'", checkrecords);
            }
        } else {//非学生
            if (null != getTeacherNo()) {
                switch (getTeacherNo()) {
                    case "unRecord": {//查询未检查的学生
                        List<Stuentrel> stuList = stuentrelDao.getBeanListHandlerRunner("select * from stuentrel" + StaticFields.currentGradeNum + this.checkLogin.getUser().getSchoolId() + " where  stuno not in ( select stuno from checkrecords" + StaticFields.currentGradeNum + this.checkLogin.getUser().getSchoolId() + ")", new Stuentrel());
                        for (Stuentrel tempuser : stuList) {
                            Checkrecords checkTme = new Checkrecords();
                            checkTme.setStuno(tempuser.getStuno());
                            checkList.add(checkTme);
                        }
                        for (Checkrecords s : checkList) {
                            s.setSchoolId(this.checkLogin.getUser().getSchoolId());
                        }
                    }
                    break;
                    case "all": {
                        checkList = new ArrayList<>();
                        if (user.getRoleinfo().getCanseeall() == StaticFields.CanSeeOnlySchool) {
                            checkList = new ArrayList<>();
                            List<Checkrecords> tempList = checkDao.getBeanListHandlerRunner("select * from checkrecords" + StaticFields.currentGradeNum + this.checkLogin.getUser().getSchoolId(), new Checkrecords());
                            for (Checkrecords Checkrecord : tempList) {
                                checkList.add(Checkrecord);
                            }
                            for (Checkrecords s : checkList) {
                                s.setSchoolId(this.checkLogin.getUser().getSchoolId());
                            }
                        }
                    }
                    break;
                    case "null": {
                    }
                    break;
                    default: {//某一教师在查询
                        checkList = new ArrayList<>();
                        List<Checkrecords> tempList = checkDao.getBeanListHandlerRunner("select * from checkrecords" + StaticFields.currentGradeNum + user.getSchoolId() + " where teachNo = '" + getTeacherNo() + "'", new Checkrecords());
                        for (Checkrecords Checkrecord : tempList) {
                            checkList.add(Checkrecord);
                        }
                        for (Checkrecords s : checkList) {
                            s.setSchoolId(this.checkLogin.getUser().getSchoolId());
                        }
                    }
                }

            }
        }
        return checkList;
    }

    public String seeDetail(int id) {
        setDetailCheckrecords(checkDao.getBeanListHandlerRunner("select * from checkrecords" + StaticFields.currentGradeNum + getCheckLogin().getUser().getSchoolId() + " where id = " + id, new Checkrecords()).get(0));
        return "stuSeeCheckRecord.xhtml";
    }

    public LinkedHashMap<String, Integer> getRankMap() {
        if (null == rankMap) {
            rankMap = new LinkedHashMap<>();
            rankMap.put(StaticFields.rankString[0], 0);
            rankMap.put(StaticFields.rankString[1], 1);
            rankMap.put(StaticFields.rankString[2], 2);
            rankMap.put(StaticFields.rankString[3], 3);
        }
        return rankMap;
    }

    public LinkedHashMap<Integer, Integer> getDayMap() {
        dayMap.clear();
        Calendar c1 = Calendar.getInstance();
        c1.add(Calendar.YEAR, year - c.get(Calendar.YEAR));
        c1.add(Calendar.MONTH, month - currentMonth);
        for (int i = 0; i < c1.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            dayMap.put(i + 1, i + 1);
        }
        return dayMap;
    }

    public void setYear(int year) {
        this.year = year;
    }

    /**
     * @param month the month to set
     */
    public void setMonth(int month) {
        this.month = month;
    }

    /**
     * @param day the day to set
     */
    public void setDay(int day) {
        this.day = day;
    }

    /**
     * @return the year
     */
    public int getYear() {
        return year;
    }

    /**
     * @return the month
     */
    public int getMonth() {
        return month;
    }

    /**
     * @return the day
     */
    public int getDay() {
        return day;
    }

    /**
     * @return the studentNo
     */
    public String getStudentNo() {
        return studentNo;
    }

    /**
     * @param studentNo the studentNo to set
     */
    public void setStudentNo(String studentNo) {
        User myUser = getCheckLogin().getUser();
        if (!"0".equals(studentNo)) {
            this.studentNo = studentNo;
            String sql1 = "select * from student" + StaticFields.currentGradeNum + myUser.getSchoolId() + " where uno='" + studentNo + "'";
            this.StudentUser = userDao.getBeanListHandlerRunner(sql1, new User()).get(0);
            this.StudentUser.setSchoolId(myUser.getSchoolId());
            String sql2 = "select * from practicenote" + StaticFields.currentGradeNum + myUser.getSchoolId() + " where stuno='" + studentNo + "'";
            this.practiceList = pDao.getBeanListHandlerRunner(sql2, new Practicenote());
            for (Practicenote p : practiceList) {
                p.setSchoolId(myUser.getSchoolId());
            }
        } else {
            this.studentNo = "0";
            this.StudentUser = null;
            this.practiceList = new LinkedList<>();
        }
    }

    /**
     * @return the checkrecords
     */
    public Checkrecords getCheckrecords() {
        return checkrecords;
    }

    /**
     * @param checkrecords the checkrecords to set
     */
    public void setCheckrecords(Checkrecords checkrecords) {
        this.checkrecords = checkrecords;
    }

    /**
     * @return the StudentUser
     */
    public User getStudentUser() {
        return StudentUser;
    }

    /**
     * @return the practiceList
     */
    public List<Practicenote> getPracticeList() {
        return practiceList;
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

    /**
     * @return the teacherNo
     */
    public String getTeacherNo() {
        return teacherNo;
    }

    /**
     * @param teacherNo the teacherNo to set
     */
    public void setTeacherNo(String teacherNo) {
        this.teacherNo = teacherNo;
    }

    /**
     * @return the detailCheckrecords
     */
    public Checkrecords getDetailCheckrecords() {
        return detailCheckrecords;
    }

    /**
     * @param detailCheckrecords the detailCheckrecords to set
     */
    public void setDetailCheckrecords(Checkrecords detailCheckrecords) {
        this.detailCheckrecords = detailCheckrecords;
    }

    /**
     * @return the cityid
     */
    public Integer getCityid() {
        return cityid;
    }

    /**
     * @param cityid the cityid to set
     */
    public void setCityid(Integer cityid) {
        this.cityid = cityid;
    }

    /**
     * @return the enterid
     */
    public Integer getEnterid() {
        return enterid;
    }

    /**
     * @param enterid the enterid to set
     */
    public void setEnterid(Integer enterid) {
        this.enterid = enterid;
    }

    /**
     * @param checkList the checkList to set
     */
    public void setCheckList(ArrayList<Checkrecords> checkList) {
        this.checkList = checkList;
    }

    /**
     * @return the stuInSameRel
     */
    public List<Stuentrel> getStuInSameRel() {
        stuInSameRel = stuentrelDao.getBeanListHandlerRunner("select * from stuentrel" + StaticFields.currentGradeNum + checkLogin.getUser().getSchoolId() + " where entstuid in (select id from enterstudent" + StaticFields.currentGradeNum + " where enterid in(select id from enterprise where cityid=" + cityid + "))", new Stuentrel());
        return stuInSameRel;
    }
}
