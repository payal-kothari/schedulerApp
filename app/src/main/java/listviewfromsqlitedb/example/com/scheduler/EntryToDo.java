package listviewfromsqlitedb.example.com.scheduler;

/**
 * Created by payalkothari on 12/30/16.
 */
public class EntryToDo {

    String task;
    String date;
    int ID;

    public void setTask(String task){
        this.task = task;
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

    public String getTask(){
        return this.task;
    }


}
