/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Practicenote;
import entities.Stuentrel;
import entities.User;
import entitiesBeans.PracticenoteLocal;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import tools.StaticFields;
import tools.UserAnalysis;

/**
 *
 * @author myPC
 */
@Named
@ConversationScoped
public class PracticeNoteBB implements Serializable {

    private @Inject
    Practicenote practiceNote;
    private @Inject
    User user;
    private User studentUser;
    private final PracticenoteLocal practDao = new PracticenoteLocal();
    private int positionId;
    private List<Practicenote> submittedNoteList;
    private final Calendar c = Calendar.getInstance();
    private int year = c.get(Calendar.YEAR), month = c.get(Calendar.MONTH), day = c.get(Calendar.DAY_OF_MONTH);
    private final int currentMonth = month;
    private LinkedHashMap<Integer, Integer> dayMap = new LinkedHashMap<>();
    private List<Stuentrel> stuEnRelList;
    private boolean readflag;
    private Practicenote temPraNote;//存放临时的周记报告，用于只读显示

    public void submitPracNote() {
        Calendar tempc = Calendar.getInstance();
        tempc.add(Calendar.YEAR, year - c.get(Calendar.YEAR));
        tempc.add(Calendar.MONTH, month - c.get(Calendar.MONTH));
        tempc.add(Calendar.DAY_OF_MONTH, day - c.get(Calendar.DAY_OF_MONTH));
        int week = tempc.get(Calendar.WEEK_OF_YEAR);
        boolean hasSubmitted = false;
        if (tempc.after(Calendar.getInstance())) {
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("实习日期不能超过今天"));
        } else {
            Iterator<Practicenote> it = this.getSubmittedNoteList().iterator();
            while (it.hasNext()) {
                Date submitDate = it.next().getSubmitdate();
                Calendar myTem = (Calendar) tempc.clone();
                myTem.setTime(submitDate);
                if (week == myTem.get(Calendar.WEEK_OF_YEAR)) {
                    hasSubmitted = true;
                    break;
                }
            }
            if (hasSubmitted) {
                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("请确认您输入了正确的日期，因为当前日期的周记已经提交过了"));
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String s = sdf.format(tempc.getTime());
                //注意：java.sql.Date只能读取日期("yyyy-MM-dd")
                //java.sql.Date date = Date.Valuseof(s);
                List<Stuentrel> temList = this.getUser().getStuentrelList();
                this.practiceNote.setStuno(user.getUno());
                this.practiceNote.setStudententid(temList.get(temList.size() - 1).getId());
                practDao.create(this.practiceNote, this.getUser().getSchoolId(), s);
                submittedNoteList = null;//重新生成该列表
            }
        }
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
    /*
     **功能：获得已经提交的实习周记
     */

    public List<Practicenote> getSubmittedNoteList() {
        if (null == submittedNoteList || submittedNoteList.isEmpty()) {
            submittedNoteList = new ArrayList<>();
            List<Stuentrel> stuEntRelList = this.getStudentUser().getStuentrelList();
            if (stuEntRelList.size() > 0) {
                String tem = "";
                for (Stuentrel s : stuEntRelList) {
                    tem += s.getId() + ",";
                }
                try {
                    submittedNoteList = practDao.getList("select * from practicenote" + StaticFields.currentGradeNum + this.getStudentUser().getSchoolId() + " where studentEntId in(" + tem.substring(0, tem.length() - 1) + ") and stuno='" + user.getUno() + "'");
                } catch (Exception e) {
                    submittedNoteList = new ArrayList<>();
                }
            } else {//stuEntRelList.size()==0
                try {
                    FacesContext context = FacesContext.getCurrentInstance();
                    HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
                    response.sendRedirect("selectMyEnterprise.xhtml");
                } catch (IOException ex) {
                    Logger.getLogger(UserinfoBean.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        return submittedNoteList;
    }

    public String directToNote(Practicenote practicenotePara) {
        readflag = true;
        temPraNote = practicenotePara;
        return "showStudentReport.xhtml";
    }

    public String deleteSelectReport() {
        practDao.remove(temPraNote, this.getUser().getSchoolId());
        submittedNoteList = null;
        temPraNote = null;
        readflag = true;
        return "submitReports.xhtml";
    }

    public String editSelectReport() {
        readflag = false;
        return null;
    }

    public boolean outDate() {
        boolean result;
        Calendar temC = Calendar.getInstance();
        temC.clear();
        temC.set(temPraNote.getSubmitdate().getYear() + 1900, temPraNote.getSubmitdate().getMonth(), temPraNote.getSubmitdate().getDay());
        temC.add(Calendar.DATE, StaticFields.dayAfterDisabled);
        if (temC.after(c)) {//超过指定的天数就不允许再修改了
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    public String alterSelectReport() {
        practDao.edit(temPraNote, this.getUser().getSchoolId());
        //       practDao.executUpdate("update practicenote" + StaticFields.currentGradeNum + this.getUser().getSchoolId() + " set detail='" + temPraNote.getDetail() + "' where STUDENTENTID=" + temPraNote.getStudententid() + " and submitdate='" + s + "'");
        //       submittedNoteList = practDao.getList("select * from practicenote" + StaticFields.currentGradeNum + this.getUser().getSchoolId() + " where stuno='" + this.getUser().getUno() + "'");
        submittedNoteList = null;
        return "showStudentReport.xhtml";
    }

    /*
     *获得学生选择的企业；
     *如果学生还没有选择企业，则转向选择企业页面
     */
    public List<Stuentrel> getStuEnRelList() {
        if (null == stuEnRelList) {
            List<Stuentrel> stuEntList = this.getUser().getStuentrelList();
            if (stuEntList.isEmpty()) {
                FacesContext context = FacesContext.getCurrentInstance();
                HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
                try {
                    response.sendRedirect("selectMyEnterprise.xhtml");
                } catch (IOException ex) {
                    Logger.getLogger(PracticeNoteBB.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return stuEnRelList;
    }
/*
    *处理来自teacherCheck.xhmtl的请求，并跳转
    */
    public String teachSeePracticeNote(Practicenote practice) {
        this.practiceNote = practice;
        return "teachSeeReport.xhtml";
    }

    public Practicenote getTemPraNote() {
        return temPraNote;
    }

    public void setPositionId(int positionId) {
        this.positionId = positionId;
    }

    public int getPositionId() {
        return this.positionId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public boolean isReadflag() {
        return readflag;
    }

    public void setReadflag(boolean readflag) {
        this.readflag = readflag;
    }

    public User getUser() {
//                if (null == user) {
//            FacesContext context = FacesContext.getCurrentInstance();
//            HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
//            user = (User) session.getAttribute("myUser");
//        }
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setStuEnRelList(List<Stuentrel> stuEnRelList) {
        this.stuEnRelList = stuEnRelList;
    }

    public boolean couldShowPracticeNoteList() {
        return this.getSubmittedNoteList().size() > 0;
    }

    public Practicenote getPracticeNote() {
        if (null == practiceNote) {
            practiceNote = new Practicenote();
        }
        return practiceNote;
    }

    public void setPracticeNote(Practicenote practiceNote) {
        this.practiceNote = practiceNote;
    }

    public User getStudentUser() {
        if (UserAnalysis.getTableName(user.getUno()).contains("stu")) {
            this.studentUser = user;
        }
        return studentUser;
    }

    public void setStudentUser(User studentUser) {
        this.studentUser = studentUser;
    }
}
