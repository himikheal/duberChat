class Message implements java.io.Serializable{
  private User targetUser;
  private String text;
  private boolean global;
  
  Message(User targetUser, String text, boolean global){
    this.targetUser = targetUser;
    this.text = text;
    this.global = global;
  }
  public String getText(){
    return this.text;
  }
  
  public void setText(String text){
    this.text = text;
  }
  
  public User getTargetUser(){
    return this.targetUser;
  }
  
  public boolean isGlobal(){
    return this.global;
  }
  
  public void setGlobal(boolean global){
    this.global = global;
  }
}