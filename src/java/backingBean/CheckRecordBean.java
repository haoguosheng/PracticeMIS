/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Checkrecords;
import entities.Practicenote;
import entities.User;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import tools.ForCallBean;
import tools.SQLTool;
import tools.UserAnalysis;

/**
 *
 * @author Administrator
 */
@ManagedBean
@SessionScoped
public class CheckRecordBean implements Serializable {

    private Checkrecords checkrecords = new Checkrecords();
    private Calendar c = Calendar.getInstance();
    private int year = c.get(Calendar.YEAR), month = c.get(Calendar.MONTH), day = c.get(Calendar.DAY_OF_MONTH), currentMonth = month;
    private LinkedHashMap<String, Integer> rankMap;
    private String studentNo;
    private User StudentUser;
    private LinkedHashMap<Integer, Integer> dayMap = new LinkedHashMap<Integer, Integer>();
    private List<Practicenote> practiceList = new LinkedList<Practicenote>();
    private SQLTool<User> userDao = new SQLTool<User>();
    private SQLTool<Checkrecords> cDao = new SQLTool<Checkrecords>();
    private SQLTool<Practicenote> pDao = new SQLTool<Practicenote>();

    public String addCheckRecord() {
        User myUser= new ForCallBean().getUser();
        checkrecords.setSchoolId(myUser.getSchoolId());
        Calendar tempc = Calendar.getInstance();
        tempc.add(Calendar.YEAR, year - c.get(Calendar.YEAR));
        tempc.add(Calendar.MONTH, month - c.get(Calendar.MONTH));
        tempc.add(Calendar.DAY_OF_MONTH, day - c.get(Calendar.DAY_OF_MONTH));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String s = sdf.format(tempc.getTime());
        String sql = "select * from checkRecords" + checkrecords.getSchoolId() + " where stuno='" + this.studentNo + "' and checkdate='" + s + "'";
        List<Checkrecords> checkList = cDao.getBeanListHandlerRunner(sql, checkrecords);
        if (checkList.size() > 0) {//该生检查记录已经存在了
            this.checkrecords = checkList.get(0);
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage(UserAnalysis.getRoleName(this.studentNo) + "的检查记录已经存在，不能再添加了"));
        } else {
            checkrecords.setStuno(this.studentNo);
            checkrecords.setTeachno(myUser.getUno());
            checkrecords.setCheckdate(tempc.getTime());
            String insert = "insert into checkrecords" + checkrecords.getSchoolId() + "(stuno, teachno, checkdate, checkcontent, recommendation, rank, remark) values('"
                    + this.studentNo + "', '" + myUser.getUno() + "', " + tempc.getTime() + ", '" + checkrecords.getCheckcontent() + "', '"
                    + checkrecords.getRecommendation() + "', '" + checkrecords.getRank() + "', '" + checkrecords.getRemark() + "')";
            cDao.executUpdate(insert);
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("添加成功，您可以继续添加"));
            this.checkrecords = new Checkrecords();
            this.setYear(c.get(Calendar.YEAR));
            this.setMonth(c.get(Calendar.MONTH));
            this.setDay(c.get(Calendar.DAY_OF_MONTH));
        }
        return null;
    }

    public LinkedHashMap<String, Integer> getRankMap() {
        if (null == rankMap) {
            rankMap = new LinkedHashMap<String, Integer>();
            rankMap.put("优秀", 0);
            rankMap.put("良好", 1);
            rankMap.put("及格", 2);
            rankMap.put("不及格", 3);
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

    /**
     * @param year the year to set
     */
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
        User myUser =new ForCallBean().getUser();
        if (!"0".equals(studentNo)) {
            this.studentNo = studentNo;
            String sql1 = "seleclt * from student" + myUser.getSchoolId() + " where uno='" + studentNo + "'";
            this.StudentUser = userDao.getBeanListHandlerRunner(sql1, new User()).get(0);
            String sql2 = "seleclt * from pracricenote" + myUser.getSchoolId() + " where uno='" + studentNo + "'";
            this.practiceList = pDao.getBeanListHandlerRunner(sql2, new Practicenote());
        } else {
            this.studentNo = "0";
            this.StudentUser = null;
            this.practiceList = new LinkedList<Practicenote>();
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
}
