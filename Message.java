/**
  * Message
  * An object sent to the server with a string and target
  * To be rerouted to target user
  */
class Message implements java.io.Serializable{
  private User targetUser;
  private String text;
  private boolean global;
  
  /**
   * Message
   * The constructor for the Message object
   * Sets variables values with constructor values
   * @param targetUser
   * @param text
   * @param global
   */
  Message(User targetUser, String text, boolean global){
    this.targetUser = targetUser;
    this.text = text;
    this.global = global;
  }

  /**
   * getText
   * Returns the text of the message
   * @return The text of the message
   */
  public String getText(){
    return this.text;
  }

  /**
   * setText
   * Sets the text of the message
   * @param text The new String
   */
  public void setText(String text){
    this.text = text;
  }
  
  /**
   * getTargetUser
   * Returns the targeted user object
   * @return The target user
   */
  public User getTargetUser(){
    return this.targetUser;
  }
  
  /**
   * isGlobal
   * Returns the value of boolean global
   * @return The value of global
   */
  public boolean isGlobal(){
    return this.global;
  }
}