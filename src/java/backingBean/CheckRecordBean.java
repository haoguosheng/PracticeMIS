/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Checkrecords;
import entities.Practicenote;
import entities.Stuentrel;
import entities.User;
import entitiesBeans.CheckrecordsLocal;
import entitiesBeans.PracticenoteLocal;
import entitiesBeans.StuentrelLocal;
import entitiesBeans.UserLocal;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import tools.StaticFields;
import tools.UserAnalysis;

/**
 *
 * @author Administrator
 */
@Named
@SessionScoped
public class CheckRecordBean implements Serializable {

    private @Inject
    User user;
    Checkrecords checkrecords;
    private String studentNo;
    private User student;
    private Integer cityid;
    private Integer enterid;
    private final Calendar c = Calendar.getInstance();
    private int year = c.get(Calendar.YEAR), month = c.get(Calendar.MONTH), day = c.get(Calendar.DAY_OF_MONTH);
    private final int currentMonth = month;
    private LinkedHashMap<String, Integer> rankMap;
    private LinkedHashMap<Integer, Integer> dayMap = new LinkedHashMap<>();
    private List<Practicenote> practiceList = new LinkedList<>();
    private List<Checkrecords> checkList;
    private final CheckrecordsLocal checkDao = new CheckrecordsLocal();
    private final PracticenoteLocal pDao = new PracticenoteLocal();
    private final StuentrelLocal stuentrelDao = new StuentrelLocal();
    private final UserLocal userDao = new UserLocal();
    private boolean readflag;
    private List<Stuentrel> stuInSameRel = new LinkedList<>();
    private String deleteRepDate, checkDate;

    public String addCheckRecord() {
        //       checkrecords.setSchoolId(getUser().getSchoolId());
        if (null != this.studentNo) {
            Calendar tempc = Calendar.getInstance();
            tempc.add(Calendar.YEAR, year - c.get(Calendar.YEAR));
            tempc.add(Calendar.MONTH, month - c.get(Calendar.MONTH));
            tempc.add(Calendar.DAY_OF_MONTH, day - c.get(Calendar.DAY_OF_MONTH));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String s = sdf.format(tempc.getTime());
            String sql = "select * from checkRecords" + StaticFields.currentGradeNum + user.getSchoolId() + " where stuno='" + this.getStudentNo() + "' and checkdate='" + s + "'";
            List<Checkrecords> checkListTme = checkDao.getList(sql);
            if (checkListTme.size() > 0) {//该生检查记录已经存在了
                this.checkrecords = checkListTme.get(0);
                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage(UserAnalysis.getRoleName(this.checkrecords.getStuno()) + "的检查记录已经存在，不能再添加了"));
            } else {
                checkrecords.setStuno(this.getStudentNo());
                checkrecords.setTeachno(getUser().getUno());
                checkrecords.setCheckdate(tempc.getTime());
                String insert = "insert into checkrecords" + StaticFields.currentGradeNum + user.getSchoolId() + "(stuno, teachno, checkdate, checkcontent, recommendation, rank, remark) values('"
                        + this.checkrecords.getStuno() + "', '" + getUser().getUno() + "', '" + s + "', '" + checkrecords.getCheckcontent() + "', '"
                        + checkrecords.getRecommendation() + "', '" + checkrecords.getRank() + "', '" + checkrecords.getRemark() + "')";
                checkDao.executUpdate(insert);
                //          FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("添加成功，您可以继续添加"));
                this.checkrecords = new Checkrecords();
                this.setYear(c.get(Calendar.YEAR));
                this.setMonth(c.get(Calendar.MONTH));
                this.setDay(c.get(Calendar.DAY_OF_MONTH));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("请选择城市与学生！"));
        }
        return null;
    }

    public List<Checkrecords> getCheckList() {
        if (null == checkList) {
            checkList = new ArrayList<>();
            if (user.getRoleid() == StaticFields.studentRole) {//学生
                List<Stuentrel> listStuEnt = stuentrelDao.getList("select * from stuentrel" + StaticFields.currentGradeNum + user.getSchoolId() + " where  stuno ='" + user.getUno() + "'");
                if (listStuEnt.size() > 0) {
                    Stuentrel stutem = listStuEnt.get(0);
                    checkList = (ArrayList) checkDao.getList("select * from CHECKRECORDS" + StaticFields.currentGradeNum + user.getSchoolId() + " where stuno='" + stutem.getStuno() + "'");
                }
            } else {//非学生
                if (null != this.checkrecords && null != this.checkrecords.getTeachno()) {
                    switch (this.checkrecords.getTeachno()) {
//                    case "unRecord": {//查询未检查的学生
//                        List<Stuentrel> stuList = stuentrelDao.getList("select * from stuentrel" + StaticFields.currentGradeNum + this.getUser().getSchoolId() + " where  stuno not in ( select stuno from checkrecords" + StaticFields.currentGradeNum + this.getUser().getSchoolId() + ")");
//                        for (Stuentrel tempuser : stuList) {
//                            Checkrecords checkTme = new Checkrecords();
//                            checkTme.setStuno(tempuser.getStuno());
//                            checkList.add(checkTme);
//                        }
//                        for (Checkrecords s : checkList) {
//                            s.setSchoolId(this.getUser().getSchoolId());
//                        }
//                    }
//                    break;
//                    case "all": {
//                        checkList = new ArrayList<>();
//                        if (user.getRoleinfo().getCanseeall() == StaticFields.CanSeeOnlySchool) {
//                            checkList = new ArrayList<>();
//                            List<Checkrecords> tempList = checkDao.getList("select * from checkrecords" + StaticFields.currentGradeNum + this.getUser().getSchoolId());
//                            for (Checkrecords Checkrecord : tempList) {
//                                checkList.add(Checkrecord);
//                            }
//                            for (Checkrecords s : checkList) {
//                                s.setSchoolId(this.getUser().getSchoolId());
//                            }
//                        }
//                    }
//                    break;
                        case "null": {
                        }
                        break;
                        default: {//查询某一教师
                            checkList = new ArrayList<>();
                            List<Checkrecords> tempList = checkDao.getList("select * from checkrecords" + StaticFields.currentGradeNum + user.getSchoolId() + " where teachNo = '" + this.checkrecords.getTeachno() + "'");
                            for (Checkrecords Checkrecord : tempList) {
                                checkList.add(Checkrecord);
                            }
                            for (Checkrecords s : checkList) {
                                s.setSchoolId(this.getUser().getSchoolId());
                            }
                        }
                    }

                } else {//教师在查询自己检查的信息
                    checkList = checkDao.getList("select * from checkrecords" + StaticFields.currentGradeNum + getUser().getSchoolId() + " where teachno='" + getUser().getUno() + "' order by stuno");
                }
            }
        }
        return checkList;
    }

    public String seeDetail(Checkrecords checkPara) {
        this.checkrecords = checkPara;
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

//    public String directToNote() {
//        FacesContext context = FacesContext.getCurrentInstance();
//        String checkDate1 = context.getExternalContext().getRequestParameterMap().get("checkDate");
//        String stuno = context.getExternalContext().getRequestParameterMap().get("studentNo");
//        String teachno = context.getExternalContext().getRequestParameterMap().get("teacherNo");
//        String schoolId = UserAnalysis.getSchoolId(stuno);
//        checkrecords = checkDao.getList("select * from checkrecords" + StaticFields.currentGradeNum + schoolId + " where stuno='" + stuno + "' and checkdate='" + checkDate1 + "' and teachno='" + teachno + "'").get(0);
////        checkrecords.setSchoolId(schoolId);
//        return "stuSeeCheckRecord.xhtml";
//    }
    public String directToNoteshowCheckRecord(Checkrecords checkrecordsPara) {
        setReadflag(true);
        this.checkrecords = checkrecordsPara;
        return "showCheckRecord.xhtml";
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public Checkrecords getCheckrecords() {
        return checkrecords;
    }

    public void setCheckrecords(Checkrecords checkrecords) {
        this.checkrecords = checkrecords;
    }
    private String oldStuno = "";

    public List<Practicenote> getPracticeList() {
        if (null != this.getStudentNo()) {
            if (!oldStuno.equals(this.getStudentNo())) {
                this.oldStuno = this.getStudentNo();
//            String sql1 = "select * from student" + StaticFields.currentGradeNum + myUser.getSchoolId() + " where uno='" + this.checkrecords.getStuno()+ "'";
                String sql2 = "select * from practicenote" + StaticFields.currentGradeNum + getUser().getSchoolId() + " where stuno='" + this.getStudentNo() + "'";
                this.practiceList = pDao.getList(sql2);
            }
        }
        return practiceList;
    }

    public String deleteSelectRecord() {
        checkDao.executUpdate("delete from checkrecords" + StaticFields.currentGradeNum + getUser().getSchoolId() + " where id=" + checkrecords.getId());
        this.checkrecords = null;
        setReadflag(true);
        return "teacherCheck.xhtml";
    }

    public String editSelectRecord() {
        setReadflag(false);
        return "showCheckRecord.xhtml";
    }

    public String alterSelectRecord() {
        if (!this.readflag) {
            checkDao.executUpdate("update checkrecords" + StaticFields.currentGradeNum + getUser().getSchoolId() + " set checkcontent='" + checkrecords.getCheckcontent() + "', recommendation='" + checkrecords.getRecommendation() + "', rank='" + checkrecords.getRank() + "', remark='" + checkrecords.getRemark() + "' where id=" + checkrecords.getId());
            setReadflag(true);
        }
        return null;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getCityid() {
        return cityid;
    }

    public void setCityid(Integer cityid) {
        this.cityid = cityid;
    }

    public Integer getEnterid() {
        return enterid;
    }

    public void setEnterid(Integer enterid) {
        this.enterid = enterid;
    }

    public void setCheckList(ArrayList<Checkrecords> checkList) {
        this.checkList = checkList;
    }

    public List<Stuentrel> getStuInSameRel() {
        stuInSameRel = stuentrelDao.getList("select * from stuentrel" + StaticFields.currentGradeNum + getUser().getSchoolId() + " where entstuid in (select id from enterstudent" + StaticFields.currentGradeNum + " where enterid in(select id from enterprise where cityid=" + cityid + "))");
        return stuInSameRel;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(String checkDate) {
        this.checkDate = checkDate;
    }

    public boolean isReadflag() {
        return readflag;
    }

    public void setReadflag(boolean readflag) {
        this.readflag = readflag;
    }
}
