import java.util.ArrayList;

class GroupChat extends SuperChat implements java.io.Serializable{
  ArrayList<User> group = new ArrayList<User>();
  String name;
  
  GroupChat(ArrayList<User> group, String name){
    this.group = group;
    this.name = name;
  }
  
  public void setGroup(ArrayList<User> group){
    this.group = group;
  }
  
  public ArrayList<User> getGroup(){
    return this.group;
  }
  
  public void setName(String name){
    this.name = name;
  }
  
  public String getName(){
    return this.name;
  }
}