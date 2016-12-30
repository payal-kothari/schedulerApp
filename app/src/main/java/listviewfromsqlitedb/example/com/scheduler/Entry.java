package listviewfromsqlitedb.example.com.scheduler;

/**
 * Created by payalkothari on 12/30/16.
 */
public class Entry {

    String startTime;
    String endTime;
    String taskName;

    public void setStart(String start){
        this.startTime = start;
    }

    public void setEnd(String end){
        this.endTime = end;
    }

    public void setTask(String task){
        this.taskName = task;
    }
}
