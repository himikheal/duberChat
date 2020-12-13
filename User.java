import java.io.Serializable;


class User extends SuperChat implements Serializable {
  private String username;
  private String password;

  User(String username) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
  
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
  
  @Override
  public boolean equals(Object obj){
    if(obj == null){
      return false;
    }
    if(!(obj instanceof User)){
      return false;
    }
    User other = (User)obj;
    return this.username == other.getUsername() && this.password == other.getPassword();
  }
}