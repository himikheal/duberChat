import java.util.ArrayList;

class GroupMessage implements java.io.Serializable{
  private ArrayList<User> targetUsers = new ArrayList<User>();
  private String text;
  
  GroupMessage(ArrayList<User> targetUsers, String text){
    this.targetUsers = targetUsers;
    this.text = text;
  }
  public String getText(){
    return this.text;
  }
  
  public void setText(String text){
    this.text = text;
  }
  
  public ArrayList<User> getTargetUsers(){
    return this.targetUsers;
  }
}