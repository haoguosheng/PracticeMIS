/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entitiesBeans;

import tools.SQLTool;

/**
 *
 * @author Idea
 * @param <T>
 */
public class AbstractEntityBean<T> {
 private final SQLTool<T> myDao = new SQLTool<>();
    public int executUpdate(String sql) {
        return myDao.executUpdate(sql);
    }
}
