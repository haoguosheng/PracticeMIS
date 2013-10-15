/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 *
 * @author Administrator
 */
public class MySessionListener implements HttpSessionListener {

    private static List<HttpSession> allSession = new LinkedList<>();

    /**
     * @return the allSession
     */
    public static List<HttpSession> getAllSession() {
        return allSession;
    }

    /**
     * @param aAllSession the allSession to set
     */
    public static void setAllSession(List<HttpSession> aAllSession) {
        allSession = aAllSession;
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        getAllSession().add(se.getSession());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        getAllSession().remove(se.getSession());
    }
}
