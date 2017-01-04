package listviewfromsqlitedb.example.com.scheduler;

/**
 * Created by payalkothari on 12/30/16.
 */
public class Entry {

    String startTime;
    String endTime;
    String taskName;
    String date;
    int ID;
    String actualStartTime;
    String actualEndTime;
    String total;

    public void setStart(String start){
        this.startTime = start;
    }

    public void setActualStart(String actualStart){
        this.actualStartTime = actualStart;
    }

    public void setActualEnd(String actualEnd){
        this.actualEndTime = actualEnd;
    }

    public void setEnd(String end){
        this.endTime = end;
    }

    public void setTask(String task){
        this.taskName = task;
    }

    public void setTotal(String t){
        this.total = t;
    }

    public void setID(int id){
        this.ID = id;
    }

    public void setDate(String d){
        this.date = d;
    }

    public int getID(){
        return this.ID;
    }

    public String getDate(){
        return this.date;
    }

    public String getStartTime(){
        return this.startTime;
    }

    public String getEndTime(){
        return this.endTime;
    }

    public String getTask(){
        return this.taskName;
    }

    public String getTotal(){
        return this.total;
    }

}
