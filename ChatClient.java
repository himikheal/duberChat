/* [ChatClient.java]
 * A not-so-pretty implementation of a basic chat client
 * @author Mangat
 * @ version 1.0a
 */

import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.awt.event.*;
import java.util.*;
import static javax.swing.ScrollPaneConstants.*;

class ChatClient {
  
  private JButton sendButton, clearButton, loginButton, friendButton, backButton, groupButton, groupToggle, nameButton;
  private JTextField typeField, globalTypeField, loginField, signUpField, portField, nameField;
  private JTextArea msgArea, globalMsgArea, groupMsgArea;
  private JPasswordField loginPassword, signUpPassword;
  private JPanel southPanel;
  private JPanel loginPanel;
  private JPanel signUpPanel;
  private JPanel friendPanel;
  private JPanel globalPanel;
  private JPanel portPanel;
  private JPanel startingPanelContainer;
  private JPanel loginPanelHelper;
  private JPanel groupNameHelper;
  private JTabbedPane startingPanel = new JTabbedPane();
  private JLabel label;
  private Socket mySocket; // socket for connection
  private Socket updateSocket;
  private ObjectInputStream input;
  private ObjectOutputStream output;
  private boolean running = true; // thread status via boolean
  private User user = new User(null); //
  private User targetUser;
  private JFrame window, login, friends, groupName;
  private Message message;
  private GroupMessage groupMessage;
  private boolean global = true;
  private boolean toggled = false;
  private boolean group = false;
  private boolean connected = false;
  private boolean threadCall = true;
  private JScrollPane scroller;
  private int size;
  ArrayList<User> users = new ArrayList<User>();
  ArrayList<User> targetGroup = new ArrayList<User>();
  ArrayList<SuperChat> menu = new ArrayList<SuperChat>();
  
  public static void main(String[] args) {
    new ChatClient().go();
  }
  
  public void go() {
    window = new JFrame("Chat Client");
    login = new JFrame("Login Window");
    friends = new JFrame("Global Room / Online Users");
   // groupName = new JFrame("Name your Group");
    southPanel = new JPanel();
    loginPanel = new JPanel();
    loginPanelHelper = new JPanel();
    signUpPanel = new JPanel();
    friendPanel = new JPanel();
    globalPanel = new JPanel();
    portPanel = new JPanel();
    groupNameHelper = new JPanel();
    startingPanelContainer = new JPanel();
    southPanel.setLayout(new GridLayout(2, 0));
    loginPanelHelper.setLayout(new GridLayout(2, 0));
    friendPanel.setLayout(new BoxLayout(friendPanel, BoxLayout.Y_AXIS));
    globalPanel.setLayout(new BorderLayout());
    friends.getContentPane().setLayout(new BoxLayout(friends.getContentPane(), BoxLayout.X_AXIS));
    
    window.setSize(400,400);
                          
    scroller = new JScrollPane(friendPanel);
    scroller.setPreferredSize(new Dimension(250,400));
    scroller.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
    
    sendButton = new JButton("SEND");
    sendButton.addActionListener(new SendButtonListener());
    clearButton = new JButton("QUIT");
    clearButton.addActionListener(new QuitButtonListener());
    loginButton = new JButton("LOGIN");
    loginButton.addActionListener(new LoginButtonListener());
    backButton = new JButton("BACK");
    backButton.addActionListener(new BackButtonListener());
    
    msgArea = new JTextArea();
    globalMsgArea = new JTextArea();
    groupMsgArea = new JTextArea();
    
    typeField = new JTextField(10);
    globalTypeField = new JTextField(10);
    loginField = new JTextField(10);
    signUpField = new JTextField(10);
    portField = new JTextField("127.0.0.1:5000",20);
    nameField = new JTextField(10);
    loginPassword = new JPasswordField(10);
    signUpPassword = new JPasswordField(10);
   
    
    label = new JLabel("Username", SwingConstants.CENTER);
    loginPanelHelper.add(label);
    loginPanelHelper.add(loginField);
    label = new JLabel("Password", SwingConstants.CENTER);
    loginPanelHelper.add(label);
    loginPanelHelper.add(loginPassword);
    
    loginPanel.add(loginPanelHelper);
    loginButton.setPreferredSize(new Dimension(150,30));
    loginPanel.add(loginButton);
    
    loginPanelHelper = new JPanel();
    loginPanelHelper.setLayout(new GridLayout(2, 0));
    loginButton = new JButton("SIGN UP");
    loginButton.addActionListener(new SignUpButtonListener());
    
    label = new JLabel("Username", SwingConstants.CENTER);
    loginPanelHelper.add(label);
    loginPanelHelper.add(signUpField);
    label = new JLabel("Password", SwingConstants.CENTER);
    loginPanelHelper.add(label);
    loginPanelHelper.add(signUpPassword);
    
    signUpPanel.add(loginPanelHelper);
    loginButton.setPreferredSize(new Dimension(150,30));
    signUpPanel.add(loginButton);
    
    loginPanelHelper = new JPanel();
    loginPanelHelper.setPreferredSize(new Dimension(400,400));
    loginButton = new JButton("CHOOSE SERVER");
    loginButton.addActionListener(new ServerButtonListener());
    
    label = new JLabel("<HTML>Enter server address and port appended with a colon<P>Will time out after ~20 seconds", SwingConstants.CENTER);
    label.setPreferredSize(new Dimension(300,30));
    loginPanelHelper.add(label);
    loginPanelHelper.add(portField);
    loginPanelHelper.add(loginButton);
    
    portPanel.add(loginPanelHelper);
    
    
    startingPanel.addTab("Server Info", null, portPanel, "Enter an ip address and port of the server you wish to connect to");
    startingPanel.addTab("Log in", null, loginPanel, "Use this if you made an account already");
    startingPanel.addTab("Sign up", null, signUpPanel, "Create an account here");
    

    startingPanel.setPreferredSize(new Dimension(400,400));
    
    startingPanelContainer.add(startingPanel);
    login.add(startingPanelContainer);
    
    login.pack();
    login.setVisible(true);
         
    
  }
  
  // Attempts to connect to the server and creates the socket and streams
  public Socket connect(String ip, int port) {
    System.out.println("Attempting to make a connection..");
    
    try {
      this.mySocket = new Socket(ip, port); // attempt socket connection (local address). This will wait until a connection is made
      this.updateSocket = new Socket(ip, port);

      connected = true;
 
      label.setText("Connection Made");
      System.out.println("Connection Made");
      
    } catch (IOException e) { // connection error occured
      connected = false;
      label.setText("Server not Found, Check Address");
      System.out.println("Connection to Server Failed");
      e.printStackTrace();
    }


    return mySocket;
  }
  
  public void updateFriends(){
    friendPanel.removeAll();
    
    for(int i = menu.size()-1; i >= 0; i--){
      if(menu.get(i) instanceof User){
        menu.remove(i);
      }
    }
    
    for(int i = 0; i < users.size(); i++){
      menu.add(users.get(i));
    }
    
    for(int i = 0; i < menu.size(); i++){
      if(menu.get(i) instanceof GroupChat){
        groupButton = new JButton(((GroupChat)menu.get(i)).getName());
        groupButton.addActionListener(new GroupButtonListener());
        groupButton.setPreferredSize(new Dimension(250,50));
        groupButton.setMaximumSize(new Dimension(250,50));
        groupButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        friendPanel.add(groupButton);
      }else if(menu.get(i) instanceof User){
        
        System.out.println("MENUSIZE " + menu.size());
        users.remove(user);
        friendButton = new JButton(((User)menu.get(i)).getUsername());
        friendButton.addActionListener(new FriendButtonListener());
        friendButton.setPreferredSize(new Dimension(250,50));
        friendButton.setMaximumSize(new Dimension(250,50));
        friendButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        friendPanel.add(friendButton);
      }
      if(menu.size() > 8){
        int diff = (menu.size()*50)-400;
        friendPanel.setPreferredSize(new Dimension(250, 400+diff));
      }
      friendPanel.revalidate();
    }
  }
  // ****** Inner Classes for Action Listeners ****
  
  // send - send msg to server (also flush), then clear the JTextField

  class messageReader implements Runnable {
    private Socket socket;
    private boolean running;
    private User user;

    messageReader(Socket s, User user) {
      this.user = user;
      this.socket = s;
      try {
        OutputStream outputStream = socket.getOutputStream();
        output = new ObjectOutputStream(outputStream);
        
      } catch(IOException e) {
       e.printStackTrace();
      }
      this.running = true;
    }

    public void run() {
      try {
        output.writeObject(this.user);
        InputStream inputStream = socket.getInputStream();
        System.out.println("AAAAA4");
        input = new ObjectInputStream(inputStream);
        System.out.println("AAAAA5");
      }catch(IOException e) {
        System.out.println("NO SEND USER");
        e.printStackTrace();
      }
      

      while(running) {
        try {
          System.out.println("AHAHTEST");
          Object o = input.readObject();
          System.out.println("asdasdasdasasdasd" + o == null);
          if(o instanceof Message){
            Message msg = (Message) o; // read the message
            if(msg.isGlobal()){
              System.out.println("GlReceived: " + msg.getText());
              globalMsgArea.append(msg.getText() + "\n");
            }else{
              System.out.println("DmReceived: " + msg.getText());
              msgArea.append(msg.getText() + "\n");
            }
          }else if(o instanceof GroupMessage){
            GroupMessage gMsg = (GroupMessage) o;
            System.out.println("GrReceived: " + gMsg.getText());
            groupMsgArea.append(gMsg.getText() + "\n"); 
          }
        } catch (IOException e) {
          System.out.println("Failed to receive msg from the server");
          e.printStackTrace();
          running = false;
        } catch (ClassNotFoundException e) {
          System.out.println("Class not found");
          e.printStackTrace();
          running = false;
        }
      }
    }
  }

  class clientUpdater implements Runnable {
    private Socket socket;
    private boolean running;
    private ObjectInputStream updateInput;
    private ObjectOutputStream updateOutput;

    clientUpdater(Socket s) {
      this.socket = s;
      try {
        OutputStream outputStream = socket.getOutputStream();
        this.updateOutput = new ObjectOutputStream(outputStream);
        InputStream inputStream = socket.getInputStream();
        this.updateInput = new ObjectInputStream(inputStream);
        
      } catch(IOException e) {
        System.out.println("TESTING3");
        e.printStackTrace();
      }
      this.running = true;
    }

    public void run() {
      while(running) {
        try {
          System.out.println("HI1");
          Object o = this.updateInput.readObject();
          if(o instanceof ArrayList){
            users = ((ArrayList<User>)o);
            for(int i = 0; i < users.size(); i++) {
              if(users.get(i).getUsername().equals(user.getUsername())) {
                users.remove(i);
              }
            }
          }else if(o instanceof GroupChat){
            menu.add((GroupChat)o);
          }else if(o instanceof String){
            if(((String)o).equals("/DISCONNECT!")){
              running = false;
            }
          }
          System.out.println(users);
          System.out.println("HI2");
          updateFriends();
          System.out.println("HI3");
        }catch(IOException e) {
          System.out.println("TESTING1");
          e.printStackTrace();
        }catch(ClassNotFoundException e2) {
          System.out.println("TESTING2");
          e2.printStackTrace();
        }
      }
      try{
        updateInput.close();
        updateOutput.close();
      }catch(IOException e){
        System.out.println("Error closing stuff");
        e.printStackTrace();
      }
    }
  }
  
  class SendButtonListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      if(global){
        try {
          message = new Message(targetUser, globalTypeField.getText(), global);
          output.writeObject(message);
          output.flush();
        } catch(IOException e) { // Catches IO error
          e.printStackTrace();
        }
        globalTypeField.setText("");
      }else if(group){
        try {
          groupMessage = new GroupMessage(targetGroup, typeField.getText());
          output.writeObject(groupMessage);
          output.flush();
        } catch(IOException e) { // Catches IO error
          e.printStackTrace();
        }
        typeField.setText("");
      }else{
        try {
          message = new Message(targetUser, typeField.getText(), global);
          output.writeObject(message);
          output.flush();
        } catch(IOException e) { // Catches IO error
          e.printStackTrace();
        }
        typeField.setText("");
      }
    }
  }
  
  // QuitButtonListener - Quit the program
  class QuitButtonListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      try{
        output.writeObject("/DISCONNECT!");
        output.close();
        input.close();
        mySocket.close();
        updateSocket.close();
      }catch(IOException e){
        System.out.println("Error closing stuff");
        e.printStackTrace();
      }
      running = false;
      window.dispose();
      friends.dispose();
      System.exit(-1);
    }
  }
  
  class BackButtonListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      window.setVisible(false);
      BorderLayout b = (BorderLayout)window.getLayout();
      if(!group){
        window.remove(msgArea);
        msgArea = new JTextArea();
      }else{
        window.remove(groupMsgArea);
        groupMsgArea = new JTextArea();
      }
      global = true;
      group = false;
      window.add(BorderLayout.CENTER, msgArea);
      friends.setVisible(true);
    }
  }
  
  class ToggleButtonListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      toggled = !toggled;
      if(!toggled){
        groupName = new JFrame("Name your Group");
        nameButton = new JButton("SUBMIT NAME");
        nameButton.addActionListener(new NameButtonListener());
        nameButton.setPreferredSize(new Dimension(150, 30));
        groupNameHelper.add(nameField);
        groupNameHelper.add(nameButton);
        groupName.add(groupNameHelper);
        groupName.pack();
        groupName.setVisible(true);
      }
    }
  }
  
  class SignUpButtonListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      user = new User(signUpField.getText());
      user.setSignIn(true);
      String password = "";
      for(int i = 0; i < signUpPassword.getPassword().length; i++){
        password += signUpPassword.getPassword()[i];
      }
      user.setPassword(password);
      signUpField.setText("");
      signUpPassword.setText("");
      
      if(threadCall){
        threadCall = !threadCall;
        System.out.println("??? "+user.getUsername());
        Thread t = new Thread(new messageReader(mySocket, user));
        System.out.println("WHY1");
        t.start(); // start the new thread
        System.out.println("WHY2");
        Thread t2 = new Thread(new clientUpdater(updateSocket));
        System.out.println("WHY3");
        t2.start();
        System.out.println("WHY4");
      }
      
//      login.dispose();
//      
//      southPanel.add(typeField);
//      southPanel.add(sendButton);
//      southPanel.add(backButton);
//      southPanel.add(clearButton);
//      
//      window.add(BorderLayout.CENTER, msgArea);
//      window.add(BorderLayout.SOUTH, southPanel);
//      
//      southPanel = new JPanel();
//      sendButton = new JButton("SEND");
//      sendButton.addActionListener(new SendButtonListener());
//      clearButton = new JButton("QUIT");
//      clearButton.addActionListener(new QuitButtonListener());
//      
//      southPanel.add(globalTypeField);
//      southPanel.add(sendButton);
//      southPanel.add(clearButton);
//      
//      globalPanel.add(globalMsgArea, BorderLayout.CENTER);
//      globalPanel.add(southPanel, BorderLayout.SOUTH);
//      
//      groupToggle = new JButton("<HTML>TOGGLE GROUP<P>CHAT CREATION");
//      groupToggle.addActionListener(new ToggleButtonListener());
//      groupToggle.setMaximumSize(new Dimension(150,60));
//      friends.add(groupToggle);
//      
//      friends.add(scroller);
//      friends.add(globalPanel);
//      friends.pack();
//      friends.setVisible(true);
//      friendPanel.revalidate();
//      friendPanel.repaint();
    }
  }
  
  class ServerButtonListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      String server = portField.getText();
      String address = server.substring(0, server.indexOf(":"));
      String portString = server.substring(server.indexOf(":")+1, server.length());
      int port = Integer.parseInt(portString);
      connect(address,port);
    }
  }
  
   class NameButtonListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      try{
        targetGroup.add(user);
        GroupChat g = new GroupChat(targetGroup, nameField.getText());
        System.out.println(g.getGroup() == null);
        System.out.println(g.getGroup().size());
        for(int i = 0; i < g.getGroup().size(); i++){
          System.out.println("text" + g.getGroup().get(i));
        }
        output.writeObject(g);
      }catch(IOException e){
        System.out.println("Failed to send groupchat");
        e.printStackTrace();
      }
      targetGroup.clear();
      nameField.setText("");
      groupName.removeAll();
      groupName.dispose();
    }
  }
  
  class FriendButtonListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      global = false;
      Object source = event.getSource();
      JButton btn = (JButton)source;
      String friendName = btn.getText();
      System.out.println(friendName);
      
      for(int i = 0; i < menu.size(); i++){
        if(menu.get(i) instanceof User){
          System.out.println(((User)menu.get(i)).getUsername());
          if(((User)menu.get(i)).getUsername().equals(friendName)){
            targetUser = (User)menu.get(i);
          }
        }
      }
      System.out.println(friendName);
      
      if(toggled){
        targetGroup.add(targetUser);
      }else{
        friends.setVisible(false);
        window.setVisible(true);
      }
    }
  }
  
  class GroupButtonListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      global = false;
      group = true;
      Object source = event.getSource();
      JButton btn = (JButton)source;
      String gName = btn.getText();
      for(int i = 0; i < menu.size(); i++){
        if(menu.get(i) instanceof GroupChat){
          if(((GroupChat)menu.get(i)).getName().equals(gName)){
            targetGroup = ((GroupChat)menu.get(i)).getGroup();
          }
        }
      }
      friends.setVisible(false);
      window.remove(msgArea);
      window.add(BorderLayout.CENTER, groupMsgArea);
      window.setVisible(true);
    }
  }
  
  class LoginButtonListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      user = new User(loginField.getText());
      System.out.println(user);
      String password = "";
      for(int i = 0; i < loginPassword.getPassword().length; i++){
        password += loginPassword.getPassword()[i];
      }
      user.setPassword(password);
      loginField.setText("");
      loginPassword.setText("");

      if(threadCall){
        threadCall = !threadCall;
        System.out.println("??? "+user.getUsername());
        Thread t = new Thread(new messageReader(mySocket, user));
        System.out.println("WHY1");
        t.start(); // start the new thread
        System.out.println("WHY2");
        Thread t2 = new Thread(new clientUpdater(updateSocket));
        System.out.println("WHY3");
        t2.start();
        System.out.println("WHY4");
      }
      
      login.dispose();
      
      southPanel.add(typeField);
      southPanel.add(sendButton);
      southPanel.add(backButton);
      southPanel.add(clearButton);
      
      window.add(BorderLayout.CENTER, msgArea);
      window.add(BorderLayout.SOUTH, southPanel);
      
      southPanel = new JPanel();
      sendButton = new JButton("SEND");
      sendButton.addActionListener(new SendButtonListener());
      clearButton = new JButton("QUIT");
      clearButton.addActionListener(new QuitButtonListener());
      
      southPanel.add(globalTypeField);
      southPanel.add(sendButton);
      southPanel.add(clearButton);
      
      globalPanel.add(globalMsgArea, BorderLayout.CENTER);
      globalPanel.add(southPanel, BorderLayout.SOUTH);
      
      groupToggle = new JButton("<HTML>TOGGLE GROUP<P>CHAT CREATION");
      groupToggle.addActionListener(new ToggleButtonListener());
      groupToggle.setMaximumSize(new Dimension(150,60));
      friends.add(groupToggle);
      
      friends.add(scroller);
      friends.add(globalPanel);
      friends.pack();
      friends.setVisible(true);
      friendPanel.revalidate();
      friendPanel.repaint();
      
      
    }
  }
}