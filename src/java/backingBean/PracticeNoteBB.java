/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Practicenote;
import entities.Stuentrel;
import entities.User;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import tools.SQLTool;
import tools.StaticFields;

/**
 *
 * @author myPC
 */
@Named
@SessionScoped
public class PracticeNoteBB implements Serializable {

    @Inject
    private CheckLogin checkLogin;
    private SQLTool<Practicenote> practDao;
    private SQLTool<Stuentrel> seDao;
    private SQLTool<User> userDao;
    private int positionId;
    private String detail;
    private List<Practicenote> submittedNoteList;
    private User studentUser;
    private String studentUserno;
    private final Calendar c = Calendar.getInstance();
    private int year = c.get(Calendar.YEAR), month = c.get(Calendar.MONTH), day = c.get(Calendar.DAY_OF_MONTH);
    private final int currentMonth = month;
    private LinkedHashMap<Integer, Integer> dayMap = new LinkedHashMap<>();
    private List<Stuentrel> stuEnRelList;
    private String deleteRepDate, alterDate;
    private boolean readflag;

    @PostConstruct
    public void init() {
        practDao = new SQLTool<>();
        seDao = new SQLTool<>();
        userDao = new SQLTool<>();
    }

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
                practDao.executUpdate("insert into practicenote" + StaticFields.currentGradeNum + checkLogin.getUser().getSchoolId() + "(detail, submitdate, studentEntId,stuno) values('" + getDetail() + "', '" + s + "'," + this.getStudentUser().getStuentrelList().get(0).getId() + ",'" + this.getStudentUserno() + "')");
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
            User user = this.checkLogin.getUser();
            this.studentUserno = user.getUno();
            submittedNoteList = new ArrayList<>();
            List<Stuentrel> stuEntRelList = this.getStudentUser().getStuentrelList();
            if (stuEntRelList.size() > 0) {
                String tem = "";
                for (Stuentrel s : stuEntRelList) {
                    tem += s.getId() + ",";
                }
                submittedNoteList = practDao.getBeanListHandlerRunner("select * from practicenote" + StaticFields.currentGradeNum + user.getSchoolId() + " where studentEntId in(" + tem.substring(0, tem.length() - 1) + ")", new Practicenote());
            }
        }
        return submittedNoteList;
    }
    private Practicenote temPraNote;//存放临时的周记报告，用于只读显示

    public String directToNote() {
        readflag = true;
        FacesContext context = FacesContext.getCurrentInstance();
        if (null != (context.getExternalContext().getRequestParameterMap().get("studentNo"))) {
            User user = checkLogin.getUser();
            //主要用于教师查看学生的周记
            studentUserno = context.getExternalContext().getRequestParameterMap().get("studentNo");
            this.studentUser = userDao.getBeanListHandlerRunner("select * from student" + StaticFields.currentGradeNum + checkLogin.getUser().getSchoolId() + " where uno='" + studentUserno + "'", checkLogin.getUser()).get(0);
            this.submittedNoteList = practDao.getBeanListHandlerRunner("select * from practicenote" + StaticFields.currentGradeNum + user.getSchoolId() + " where studententid=" + this.studentUser.getStuentrelList().get(0).getId(), new Practicenote());
        }
        String strToFormat = context.getExternalContext().getRequestParameterMap().get("submitDate");
        deleteRepDate = strToFormat;
        alterDate = strToFormat;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date requestDate = null;
        try {
            requestDate = sdf.parse(strToFormat);
        } catch (ParseException ex) {
            Logger.getLogger(PracticeNoteBB.class.getName()).log(Level.SEVERE, null, ex);
        }
        Iterator<Practicenote> it = submittedNoteList.iterator();
        while (it.hasNext()) {
            Practicenote tem = it.next();
            if (requestDate.getDate() == tem.getSubmitdate().getDate()) {
                this.temPraNote = tem;
                this.temPraNote.setStuUno(studentUserno);
                break;
            }
        }
        return "showStudentReport.xhtml";
    }

    public String deleteSelectReport() {
        String s = deleteRepDate;
        practDao.executUpdate("delete from practicenote" + StaticFields.currentGradeNum + checkLogin.getUser().getSchoolId() + " where stuno='" + checkLogin.getUser().getUno() + "' and submitdate='" + s + "'");
        submittedNoteList = practDao.getBeanListHandlerRunner("select * from practicenote" + StaticFields.currentGradeNum + checkLogin.getUser().getSchoolId() + " where stuno='" + checkLogin.getUser().getUno() + "'", new Practicenote());
        readflag = true;
        return "viewReports.xhtml";
    }

    public String editSelectReport() {
        readflag = false;
        return "showStudentReport.xhtml";
    }

    public String alterSelectReport() {
        String s = alterDate;
        practDao.executUpdate("update practicenote" + StaticFields.currentGradeNum + checkLogin.getUser().getSchoolId() + " set detail='" + temPraNote.getDetail() + "' where stuno='" + checkLogin.getUser().getUno() + "' and submitdate='" + s + "'");
        submittedNoteList = practDao.getBeanListHandlerRunner("select * from practicenote" + StaticFields.currentGradeNum + checkLogin.getUser().getSchoolId() + " where stuno='" + checkLogin.getUser().getUno() + "'", new Practicenote());
        return "submitReports.xhtml";
    }

    /*
     *获得学生选择的企业；
     *如果学生还没有选择企业，则转向选择企业页面
     */
    public List<Stuentrel> getStuEnRelList() {
        if (null == stuEnRelList) {
            List<Stuentrel> stuEntList = seDao.getBeanListHandlerRunner("select * from Stuentrel" + StaticFields.currentGradeNum + getStudentUser().getSchoolId() + " where id=" + this.getStudentUser().getStuentrelList().get(0).getId(), new Stuentrel());
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

    public User getStudentUser() {
        studentUser = checkLogin.getUser();
        return studentUser;
    }

    public String getStudentUserno() {
        return studentUserno;
    }

    public void setStudentUserno(String studentUserno) {
        this.studentUserno = studentUserno;
    }

    public boolean isReadflag() {
        return readflag;
    }

    public void setReadflag(boolean readflag) {
        this.readflag = readflag;
    }

    public CheckLogin getCheckLogin() {
        return checkLogin;
    }

    public void setCheckLogin(CheckLogin checkLogin) {
        this.checkLogin = checkLogin;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    /**
     * @param stuEnRelList the stuEnRelList to set
     */
    public void setStuEnRelList(List<Stuentrel> stuEnRelList) {
        this.stuEnRelList = stuEnRelList;
    }
}
