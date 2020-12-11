class Message implements java.io.Serializable{
  private User targetUser;
  private String text;
  
  Message(User targetUser, String text){
    this.targetUser = targetUser;
    this.text = text;
  }
  public String getText(){
    return this.text;
  }
  
  public User getTargetUser(){
    return this.targetUser;
  }
}