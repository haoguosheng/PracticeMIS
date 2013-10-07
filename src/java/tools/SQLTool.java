/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

/**
 *
 * @author Administrator
 */
@ManagedBean
@SessionScoped
public class SQLTool<T> implements java.io.Serializable {

    public List<T> getBeanListHandlerRunner(String sql, T t) {
        List<T> paperClassificationList = null;
        ResultSetHandler<List<T>> rp = new BeanListHandler(t.getClass());
        QueryRunner run = new QueryRunner(ConnectionManager.getDataSource());
        try {
            paperClassificationList = run.query(sql, rp);
        } catch (SQLException ex) {
            FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("查找不成功或数据库未启动！"));
        }
        return paperClassificationList;
    }

    public int executUpdate(String sql) {
        int result = 0;
        QueryRunner run = new QueryRunner(ConnectionManager.getDataSource());
        try {
            result = run.update(sql);
        } catch (SQLException ex) {
            FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("更新失败！"));
        }
        if (result <= 0) {
            FacesContext.getCurrentInstance().addMessage("globalMessages", new FacesMessage("被影响的记录数为0，表明更改部分或全部失败！请查找原因"));
        } else {
            FacesContext.getCurrentInstance().addMessage("globalMessages", new FacesMessage("被影响的记录数为" + result + "，表明更改部分或全部成功！"));
        }
        return result;
    }

    public List<String> getIdListHandlerRunner(String sql) {
        List<String> ls = new LinkedList<String>();
        ResultSetHandler<Object[]> h = new ResultSetHandler<Object[]>() {
            @Override
            public Object[] handle(ResultSet rs) throws SQLException {
                Object[] result;
                List<String> tem = new ArrayList<String>();
                while (rs.next()) {
                    tem.add(rs.getString(1));
                }
                result = tem.toArray();
                return result;
            }
        };
        QueryRunner run = new QueryRunner(ConnectionManager.getDataSource());
        try {
            Object[] myTem = run.query(sql, h);
            for (int i = 0; i < myTem.length; i++) {
                ls.add(myTem[i].toString());
            }
        } catch (SQLException ex) {
            FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("查找不成功！"));
        }
        return ls;
    }
}
