package listviewfromsqlitedb.example.com.scheduler;

/**
 * Created by payalkothari on 12/30/16.
 */
public class Entry {

    String startTime;
    String endTime;
    String taskName;
    int ID;

    public void setStart(String start){
        this.startTime = start;
    }

    public void setEnd(String end){
        this.endTime = end;
    }

    public void setTask(String task){
        this.taskName = task;
    }

    public void setID(int id){
        this.ID = id;
    }

    public int getID(){
        return this.ID;
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

}
