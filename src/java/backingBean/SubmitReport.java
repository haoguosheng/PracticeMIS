/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Enterstudent;
import entities.Position;
import entities.Practicenote;
import entities.Stuentrel;
import entities.User;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import javax.faces.bean.ManagedProperty;
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
public class SubmitReport implements Serializable {
   @Inject
   private  CheckLogin checkLogin;
    private SQLTool<Practicenote> practDao;
    private SQLTool<Position> posDao ;
    private SQLTool<Stuentrel> seDao ;
    private SQLTool<Enterstudent> entersDao;
    private SQLTool<User> userDao ;
    private int positionId;
    private List<Practicenote> submittedNoteList;
    private Practicenote practiceNote = new Practicenote();
//    private boolean inputed; //检查本击是否已经提交
    private User  studentUser;
    private String studentUserno;
    private LinkedHashMap<String, Integer> positionMap;
    private Calendar c = Calendar.getInstance();
    private int year = c.get(Calendar.YEAR), month = c.get(Calendar.MONTH), day = c.get(Calendar.DAY_OF_MONTH), currentMonth = month;
    private LinkedHashMap<Integer, Integer> dayMap = new LinkedHashMap<Integer, Integer>();
    private Position position ;
    private Enterstudent entStu ;
    private Stuentrel stuEnRel ;
    private String deleteRepDate, alterDate;
    private boolean readflag;
@PostConstruct
public void init(){
    practDao = new SQLTool<Practicenote>();
    posDao = new SQLTool<Position>();
    seDao = new SQLTool<Stuentrel>();
     entersDao = new SQLTool<Enterstudent>();
     userDao = new SQLTool<User>();
     position = new Position();
     entStu = new Enterstudent();
     stuEnRel = new Stuentrel();
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
                practiceNote.setSubmitdate(tempc.getTime());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String s = sdf.format(tempc.getTime());
                //注意：java.sql.Date只能读取日期("yyyy-MM-dd")
                //java.sql.Date date = Date.Valuseof(s);
                practiceNote.setStuno(checkLogin.getUser().getUno());
                practiceNote.setEnterid(Integer.parseInt(seDao.getIdListHandlerRunner("select enterId from stuentrel" +StaticFields.currentGradeNum+ checkLogin.getUser().getSchoolId() + " where stuno='" + checkLogin.getUser().getUno() + "'").get(0)));
                practiceNote.setPositionid(positionId);
                practDao.executUpdate("insert into practicenote" +StaticFields.currentGradeNum+ checkLogin.getUser().getSchoolId() + "(detail, submitdate, enterid, positionId, stuno) values('" + practiceNote.getDetail() + "', '" + s + "', " + practiceNote.getEnterid() + ", " + practiceNote.getPositionid() + ", '" + practiceNote.getStuno() + "')");
                submittedNoteList = null;//重新生成该列表
                practiceNote = new Practicenote();
                this.setYear(c.get(Calendar.YEAR));
                this.setMonth(c.get(Calendar.MONTH));
                this.setDay(c.get(Calendar.DAY_OF_MONTH));
               // FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("提交周记成功"));
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

    public List<Practicenote> getSubmittedNoteList() {
        if (null == submittedNoteList||submittedNoteList.isEmpty()) {
            submittedNoteList = practDao.getBeanListHandlerRunner("select * from practicenote" +StaticFields.currentGradeNum+ checkLogin.getUser().getSchoolId() + " where stuno='" + checkLogin.getUser().getUno() + "'", practiceNote);
        }
        for (Practicenote s : submittedNoteList) {
            s.setSchoolId(checkLogin.getUser().getSchoolId());
        }
        return submittedNoteList;
    }
    private Practicenote temPraNote;//存放临时的周记报告，用于只读显示

    public String directToNote() {
        readflag = true;
        FacesContext context = FacesContext.getCurrentInstance();
        if (null != (context.getExternalContext().getRequestParameterMap().get("studentNo"))) {
            //主要用于教师查看学生的周记
            this.studentUser = userDao.getBeanListHandlerRunner("select * from student" +StaticFields.currentGradeNum+ checkLogin.getUser().getSchoolId() + " where uno='" + context.getExternalContext().getRequestParameterMap().get("studentNo") + "'", checkLogin.getUser()).get(0);
            this.submittedNoteList = practDao.getBeanListHandlerRunner("select * from practice" +StaticFields.currentGradeNum+ checkLogin.getUser().getSchoolId() + " where stuno='" + this.studentUser.getUno() + "'", practiceNote);
        }
        String strToFormat = context.getExternalContext().getRequestParameterMap().get("submitDate");
        deleteRepDate = strToFormat;
        alterDate = strToFormat;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date requestDate = null;
        try {
            requestDate = sdf.parse(strToFormat);
        } catch (ParseException ex) {
            Logger.getLogger(SubmitReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        Iterator<Practicenote> it = submittedNoteList.iterator();
        while (it.hasNext()) {
            Practicenote tem = it.next();
            if (requestDate.getDate() == tem.getSubmitdate().getDate()) {
                this.temPraNote = tem;
                break;
            }
        }
        return "showStudentReport.xhtml";
    }

    public LinkedHashMap<String, Integer> getPositionMap() {
        if (null == this.positionMap || this.positionMap.isEmpty()) {
            this.positionMap = new LinkedHashMap();
            List<Stuentrel> ent = seDao.getBeanListHandlerRunner("select * from Stuentrel" +StaticFields.currentGradeNum+ checkLogin.getUser().getSchoolId() + " where stuno='" + checkLogin.getUser().getUno() + "'", stuEnRel);
            if (ent.size() <= 0) {//还没有选择企业呢，跳转选择企业
                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("因为没有选中实习的单位，所以需要在此选择实习的单位，如果这里没您实习的单位，则需要\"添加实习单位\""));
                try {
                    FacesContext context = FacesContext.getCurrentInstance();
                    HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
                    response.sendRedirect("selectMyEnterprise.xhtml");
                } catch (IOException ex) {
                    Logger.getLogger(SubmitReport.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                for (int i = 0; i < ent.size(); i++) {
                    List<Enterstudent> reqStu = entersDao.getBeanListHandlerRunner("select * from enterstudent"+StaticFields.currentGradeNum+" where enterId=" + ent.get(i).getEnterid(), entStu);
                    for (int j = 0; j < reqStu.size(); j++) {
                        Position tempP = posDao.getBeanListHandlerRunner("select * from position"+StaticFields.currentGradeNum+" where id=" + reqStu.get(i).getPositionid(), position).get(0);
                        this.positionMap.put(tempP.getName(), tempP.getId());
                    }
                }
            }
        }
        return positionMap;
    }

    public String deleteSelectReport() {
        String s = deleteRepDate;
        practDao.executUpdate("delete from practicenote" +StaticFields.currentGradeNum+ checkLogin.getUser().getSchoolId() + " where stuno='" + checkLogin.getUser().getUno() + "' and submitdate='" + s + "'");
        submittedNoteList = practDao.getBeanListHandlerRunner("select * from practicenote" +StaticFields.currentGradeNum+ checkLogin.getUser().getSchoolId() + " where stuno='" + checkLogin.getUser().getUno() + "'", practiceNote);
        readflag = true;
        return "viewReports.xhtml";
    }

    public String editSelectReport() {
        readflag = false;
        return "showStudentReport.xhtml";
    }
    
    public String alterSelectReport() {
        String s = alterDate;
        practDao.executUpdate("update practicenote" +StaticFields.currentGradeNum+ checkLogin.getUser().getSchoolId() + " set detail='" + temPraNote.getDetail() + "' where stuno='" + checkLogin.getUser().getUno() + "' and submitdate='" + s + "'");
        submittedNoteList = practDao.getBeanListHandlerRunner("select * from practicenote" +StaticFields.currentGradeNum+ checkLogin.getUser().getSchoolId() + " where stuno='" + checkLogin.getUser().getUno() + "'", practiceNote);
        return "viewReports.xhtml";
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

    public Practicenote getPracticeNote() {
        return practiceNote;
    }

    public void setPracticeNote(Practicenote practiceNote) {
        this.practiceNote = practiceNote;
    }
    /**
     * @return the year
     */
    public int getYear() {
        return year;
    }

    /**
     * @param year the year to set
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * @return the month
     */
    public int getMonth() {
        return month;
    }

    /**
     * @param month the month to set
     */
    public void setMonth(int month) {
        this.month = month;
    }

    /**
     * @return the day
     */
    public int getDay() {
        return day;
    }

    /**
     * @param day the day to set
     */
    public void setDay(int day) {
        this.day = day;
    }

    /**
     * @return the studentUser
     */
    public User getStudentUser() {
        studentUser = userDao.getBeanListHandlerRunner("select * from student" +StaticFields.currentGradeNum+ checkLogin.getUser().getSchoolId() + " where uno='" + studentUserno + "'", checkLogin.getUser()).get(0);
        studentUser.setSchoolId(checkLogin.getUser().getSchoolId());
        return studentUser;
    }

    /**
     * @return the studentUserno
     */
    public String getStudentUserno() {
        return studentUserno;
    }

    /**
     * @param studentUserno the studentUserno to set
     */
    public void setStudentUserno(String studentUserno) {
        this.studentUserno = studentUserno;
    }

    /**
     * @return the readflag
     */
    public boolean isReadflag() {
        return readflag;
    }

    /**
     * @param readflag the readflag to set
     */
    public void setReadflag(boolean readflag) {
        this.readflag = readflag;
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
}
