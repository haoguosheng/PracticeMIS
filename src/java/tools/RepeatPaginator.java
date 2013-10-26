/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.util.List;

/**
 *
 * @author Idea
 */
public class RepeatPaginator implements java.io.Serializable{
  //  private static final int DEFAULT_RECORDS_NUMBER = 2;
    private static final int DEFAULT_PAGE_INDEX = 1;

    private int records;
    private int recordsTotal;
    private int pageIndex;
    private int pages;
    private List<?> origModel;
    private List<?> model;

    public RepeatPaginator(List<?> model,int num) {
        this.origModel = model;
        this.records = num;
        this.pageIndex = DEFAULT_PAGE_INDEX;        
        this.recordsTotal = model.size();
    }
    public void init(){
        if (records > 0) {
            pages =recordsTotal / records;
            if (recordsTotal % records > 0) {
                pages++;
            }
            if (pages == 0) {
                pages = 1;
            }
        } else {
            records = 1;
            pages = 1;
        }
        updateModel();
    }

    private void updateModel() {
        int fromIndex = getFirst();
        int toIndex = getFirst() + records;
        if(toIndex > this.recordsTotal) {
            toIndex = this.recordsTotal;
        }
        this.model = origModel.subList(fromIndex, toIndex);
    }

    public void next() {
        if(this.pageIndex < pages) {
            this.pageIndex++;
        }
        updateModel();
    }

    public void prev() {
        if(this.pageIndex > 1) {
            this.pageIndex--;
        }
        updateModel();
    }   

 
    public int getPageIndex() {
        return pageIndex;
    }

    public int getPages() {
        return pages;
    }

    private int getFirst() {
        return (pageIndex * records) - records;
    }

    public List<?> getModel() {
        return model;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }
}
