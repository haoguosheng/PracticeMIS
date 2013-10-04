/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.util.Calendar;
import java.util.LinkedHashMap;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author Idea
 */
@ManagedBean
@ApplicationScoped
public class PublicFields {

    private String tag = "左边没有就在这里添加";
    private Calendar c = Calendar.getInstance();
    private int year = c.get(Calendar.YEAR), month = c.get(Calendar.MONTH),  currentMonth = month;
    private LinkedHashMap<Integer, Integer> yearMap;
    private LinkedHashMap<Integer, Integer> monthMap;
    private LinkedHashMap<Integer, Integer> dayMap = new LinkedHashMap<Integer, Integer>();


    /**
     * @return the yearMap
     */
    public LinkedHashMap<Integer, Integer> getYearMap() {
        if (null ==yearMap) {
            yearMap = new LinkedHashMap<Integer, Integer>();
            yearMap.put(c.get(Calendar.YEAR), c.get(Calendar.YEAR));
            yearMap.put(c.get(Calendar.YEAR) - 1, c.get(Calendar.YEAR) - 1);
        }
        return yearMap;
    }

    public LinkedHashMap<Integer, Integer> getMonthMap() {
        if (null == monthMap) {
            monthMap = new LinkedHashMap<Integer, Integer>();
            for (int i = 0; i < 12; i++) {
                monthMap.put(i + 1, i);
            }
        }
        return monthMap;
    }

    public LinkedHashMap<Integer, Integer> getDayMap() {
        dayMap.clear();
        Calendar c1 = Calendar.getInstance();
        c1.add(Calendar.YEAR, year - c.get(Calendar.YEAR));
        c1.add(Calendar.MONTH, month - currentMonth + 1);
        for (int i = 0; i < c1.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            dayMap.put(i + 1, i + 1);
        }
        return dayMap;
    }

    /**
     * @return the tag
     */
    public String getTag() {
        return tag;
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
}
