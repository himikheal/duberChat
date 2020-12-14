import java.io.Serializable;


class User extends SuperChat implements Serializable {
  private String username;
  private String password;
  private boolean signIn;

  User(String username) {
    this.username = username;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
  
  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
  
  public boolean getSignIn(){
    return this.signIn;
  }
  
  public void setSignIn(boolean signIn){
    this.signIn = signIn;
  }
}