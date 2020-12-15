import java.io.Serializable;

/**
 * User
 * custom object to hold all user information, ie username and password
 */
class User extends SuperChat implements Serializable {
  private String username;
  private String password;
  private boolean signIn;
  private boolean logIn;

  /**
   * User
   * Constructor for user class
   * @param username, the users name
   */
  User(String username) {
    this.username = username;
  }

  /**
   * getUsername
   * gets the username as a string
   * @return this.username the string username
   */
  public String getUsername() {
    return this.username;
  }

   /**
   * setUsername
   * sets the username based on the input
   * @param logIn the wanted username value
   */
  public void setUsername(String username) {
    this.username = username;
  }
  
  /**
   * getPassword
   * gets the password as a string
   * @return this.password the string password
   */
  public String getPassword() {
    return this.password;
  }

  /**
   * setPassword
   * sets the password based on the input
   * @param logIn the wanted password value
   */
  public void setPassword(String password) {
    this.password = password;
  }
  
  /**
   * getSignIn
   * gets the boolean dictating if the user is signing in for the first time/creating an account
   * @return this.signIn the signIn boolean
   */
  public boolean getSignIn(){
    return this.signIn;
  }
  
  /**
   * setSignIn
   * sets the signIn boolean based on the input
   * @param signIn the wanted boolean value
   */
  public void setSignIn(boolean signIn){
    this.signIn = signIn;
  }
  
  /**
   * getlogIn
   * gets the boolean dictating if the user is logging in/has an account
   * @return this.logIn the logIn boolean
   */
  public boolean getLogIn(){
    return this.logIn;
  }
  
  /**
   * setLogIn
   * sets the login boolean based on the input
   * @param logIn the wanted boolean value
   */
  public void setLogIn(boolean logIn){
    this.logIn = logIn;
  }
}