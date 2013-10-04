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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

/**
 *
 * @author Administrator
 */
public class SQLTool<T> {

    public List<T> getBeanListHandlerRunner(String sql, T t) {
        List<T> paperClassificationList = null;
        ResultSetHandler<List<T>> rp = new BeanListHandler(t.getClass());
        QueryRunner run = new QueryRunner(ConnectionManager.getDataSource());
        try {
            paperClassificationList = run.query(sql, rp);
        } catch (SQLException ex) {
            Logger.getLogger(t.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return paperClassificationList;
    }
    
    public int executUpdate(String sql) {
        int result = 0;
        QueryRunner run = new QueryRunner(ConnectionManager.getDataSource());
        try {
            result = run.update(sql);
        } catch (SQLException ex) {
            System.out.println("Fail to update!");
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
        }
        return ls;
    }

}
