import java.util.ArrayList;
/**
 * GroupMessage
 * Messages meant to be sent to group chats
 * Has text as a string and an arraylist of users
 */
class GroupMessage implements java.io.Serializable{
  private ArrayList<User> targetUsers = new ArrayList<User>();
  private String text;
  
  /**
   * GroupMessage
   * Constructor for GroupMessage
   * Sets values from constructors to variables
   * @param targetUsers The targeted users of the message
   * @param text
   */
  GroupMessage(ArrayList<User> targetUsers, String text){
    this.targetUsers = targetUsers;
    this.text = text;
  }
  
  /**
   * getText
   * Returns the string stored in text
   * @return The string stored in text
   */
  public String getText(){
    return this.text;
  }
  
  /**
   * setText
   * Sets the string stored in text
   * @param text The new message
   */
  public void setText(String text){
    this.text = text;
  }
  
  /**
   * getTargetUsers
   * Returns the targets of the message
   * @return An arraylist with the targets
   */
  public ArrayList<User> getTargetUsers(){
    return this.targetUsers;
  }
}