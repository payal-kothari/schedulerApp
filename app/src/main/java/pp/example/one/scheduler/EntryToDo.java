package pp.example.one.scheduler;

/**
 * Created by payalkothari on 12/30/16.
 */
public class EntryToDo {

    String task;
    String date;
    int ID;
    int StatusID;
    String status;

    public void setTask(String task){
        this.task = task;
    }

    public void setStatusID(int statusID){
        this.StatusID = statusID;
    }

    public void setID(int id){
        this.ID = id;
    }

    public void setStatus(String status){
        this.status = status;
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

    public int getStatusID(){
        return this.StatusID;
    }

    public String getTask(){
        return this.task;
    }

    public String getStatus(){
        return this.status;
    }

}
