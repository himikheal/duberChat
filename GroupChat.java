import java.util.ArrayList;
/**
 * GroupChat
 * Chat object that is used for group chats in the server
 * Stores an arraylist and a name
 */
class GroupChat extends SuperChat implements java.io.Serializable{
  ArrayList<User> group = new ArrayList<User>();
  String name;
  
  /**
   * GroupChat
   * The constructor for GroupChat, sets values from constructor
   * @param group An arraylist of users that are in the group
   * @param name  The name of the group chat
   */
  GroupChat(ArrayList<User> group, String name){
    this.group = group;
    this.name = name;
  }
  
  /**
   * getGroup
   * Returns the arraylist group
   * @return The arraylist group
   */
  public ArrayList<User> getGroup(){
    return this.group;
  }
  
  /**
   * getName
   * Returns the name of the group chat
   * @return The name of the group chat
   */  
  public String getName(){
    return this.name;
  }
  
}