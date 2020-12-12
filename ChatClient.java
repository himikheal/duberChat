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
  
  private JButton sendButton, clearButton, loginButton, friendButton, backButton;
  private JTextField typeField;
  private JTextArea msgArea, globalMsgArea;
  private JPanel southPanel;
  private JPanel loginPanel;
  private JPanel friendPanel;
  private JPanel globalPanel;
  //private JLabel errorLabel = new JLabel("");
  private Socket mySocket; // socket for connection
  private Socket updateSocket;
  //private ObjectInputStream input;
  //private ObjectOutputStream output;
  //private ObjectInputStream updateInput;
  //private ObjectOutputStream updateOutput;
  private boolean running = true; // thread status via boolean
  private User user = new User(null); //
  private User targetUser;
  private JFrame window;
  private JFrame login;
  private JFrame friends;
  private Message message;
  private boolean logged = false;
  private JScrollPane scroller;
  private int size;
  ArrayList<User> users = new ArrayList<User>();
  
  public static void main(String[] args) {
    new ChatClient().go();
  }
  
  public void go() {
    window = new JFrame("Chat Client");
    login = new JFrame("Login Window");
    friends = new JFrame("Friends");
    southPanel = new JPanel();
    loginPanel = new JPanel();
    friendPanel = new JPanel();
    globalPanel = new JPanel();
    southPanel.setLayout(new GridLayout(2, 0));
    loginPanel.setLayout(new GridLayout(1, 0));
    friendPanel.setLayout(new BoxLayout(friendPanel, BoxLayout.Y_AXIS));
    globalPanel.setLayout(new BorderLayout());
    friends.getContentPane().setLayout(new BoxLayout(friends.getContentPane(), BoxLayout.X_AXIS));
                          
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
    
    typeField = new JTextField(10);
    
    msgArea = new JTextArea();
    globalMsgArea = new JTextArea();
    
    loginPanel.add(typeField);
    loginPanel.add(loginButton);
//    southPanel.add(typeField);
//    southPanel.add(sendButton);
//    southPanel.add(errorLabel);
//    southPanel.add(clearButton);
    
    login.add(BorderLayout.CENTER, loginPanel);
    
//    window.add(BorderLayout.CENTER, msgArea);
//    window.add(BorderLayout.SOUTH, southPanel);
    
    login.setSize(400, 400);
    login.setVisible(true);
    
    // call a method that connects to the server
    connect("127.0.0.1", 5000);
    // after connecting loop and keep appending[.append()] to the JTextArea
    //readMessagesFromServer();
          
    
  }
  
  // Attempts to connect to the server and creates the socket and streams
  public Socket connect(String ip, int port) {
    System.out.println("Attempting to make a connection..");
    
    try {
      mySocket = new Socket("127.0.0.1", 5000); // attempt socket connection (local address). This will wait until a connection is made
      updateSocket = new Socket("127.0.0.1", 5001);
      //InputStream inputStream = mySocket.getInputStream();
      //input = new ObjectInputStream(inputStream);
      //OutputStream outputStream = mySocket.getOutputStream();
      //output = new ObjectOutputStream(outputStream);
      //InputStream updateInputStream = updateSocket.getInputStream();
      //updateInput = new ObjectInputStream(updateInputStream);
      //OutputStream updateOutputStream = updateSocket.getOutputStream();
      //updateOutput = new ObjectOutputStream(updateOutputStream);
      Thread t = new Thread(new messageReader(mySocket));
      
      t.start(); // start the new thread
      
      
    } catch (IOException e) { // connection error occured
      System.out.println("Connection to Server Failed");
      e.printStackTrace();
    }
    
    System.out.println("Connection made.");
    return mySocket;
  }
  
  // Starts a loop waiting for server input and then displays it on the textArea
  //public void readMessagesFromServer() {
  //  
  //  while (running) { // loop unit a message is received
  //    try {
  //      //Object o = input.readObject();
  //      //if(o instanceof String) {
  //      System.out.println("RESCHED");
  //      String msg = "";
  //      //try {
  //      msg = (String) input.readObject(); // read the message
  //      //} catch (ClassNotFoundException e) {
  //      //  System.out.println("Class not found");
  //      // e.printStackTrace();
  //      //}
  //      System.out.println("received: " + msg);
  //      msgArea.append(msg + "\n");
  //      //}
  //      //else if(o instanceof ArrayList) {
  //        //users = ((ArrayList<User>) o);
  //      //if(updateInput.available() != 0) {
  //        users = ((ArrayList<User>) updateInput.readObject());
  //        System.out.println(users);
  //        updateFriends();
  //      //}
  //      //}
  //    } catch (IOException e) {
  //      System.out.println("Failed to receive msg from the server");
  //      e.printStackTrace();
  //    } catch (ClassNotFoundException e) {
  //      System.out.println("Class not found");
  //      e.printStackTrace();
  //    }
  //  }
  //  try { // after leaving the main loop we need to close all the sockets
  //    input.close();
  //    output.close();
  //    updateInput.close();
  //    updateOutput.close();
  //    mySocket.close();
  //  } catch (Exception e) {
  //    System.out.println("Failed to close socket");
  //  }
  //  
  //}

  public void updateFriends(){
    System.out.println("USERSIZE " + users.size());
    for(int i = 0; i < users.size(); i++){
      friendButton = new JButton(users.get(i).getUsername());
      friendButton.addActionListener(new FriendButtonListener());
      friendButton.setPreferredSize(new Dimension(250,50));
      friendButton.setMaximumSize(new Dimension(250,50));
      friendButton.setAlignmentX(Component.CENTER_ALIGNMENT);
      friendPanel.add(friendButton);
      if(users.size() > 8){
        int diff = (users.size()*50)-400;
        friendPanel.setPreferredSize(new Dimension(250, 400+diff));
      }
    }
    friendPanel.revalidate();
  }
  // ****** Inner Classes for Action Listeners ****
  
  // send - send msg to server (also flush), then clear the JTextField

  class messageReader implements Runnable {
    private Socket socket;
    private boolean running;
    private ObjectInputStream input;
    private ObjectOutputStream output;

    messageReader(Socket s) {
      this.socket = s;
      try {
        InputStream inputStream = socket.getInputStream();
        this.input = new ObjectInputStream(inputStream);
        OutputStream outputStream = socket.getOutputStream();
        this.output = new ObjectOutputStream(outputStream);
      } catch(IOException e) {
        e.printStackTrace();
      }
      this.running = true;
    }

    public void run() {
      while(running) {
        try {
          System.out.println("AHAHTEST");
          String msg = "";
          msg = (String) this.input.readObject(); // read the message
          System.out.println("received: " + msg);
          msgArea.append(msg + "\n");
        } catch (IOException e) {
          System.out.println("Failed to receive msg from the server");
          e.printStackTrace();
        } catch (ClassNotFoundException e) {
          System.out.println("Class not found");
          e.printStackTrace();
        }
      }
    }
  }

  class clientUpdater implements Runnable {
    private Socket socket;
    private boolean running;
    private User user;
    private ObjectInputStream input;
    private ObjectOutputStream output;

    clientUpdater(Socket s, User user) {
      this.socket = s;
      this.user = user;
      try {
        InputStream inputStream = socket.getInputStream();
        this.input = new ObjectInputStream(inputStream);
        OutputStream outputStream = socket.getOutputStream();
        this.output = new ObjectOutputStream(outputStream);
      } catch(IOException e) {
        System.out.println("TESTING3");
        e.printStackTrace();
      }
      this.running = true;
    }

    public void run() {
      try {
        this.output.writeObject(user);
      }catch(IOException e) {
        System.out.println("NO SEND USER");
        e.printStackTrace();
      }

      //while(running) {
        try {
          System.out.println("HI1");
          users = ((ArrayList<User>) this.input.readObject());
          users.add(new User("bob"));
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
      //}
    }
  }
  
  class SendButtonListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      
      //try {
        message = new Message(targetUser, typeField.getText());
        //output.writeObject(message);
        //output.flush();
      //} catch(IOException e) { // Catches IO error
      //  e.printStackTrace();
      //}
      typeField.setText("");
      //}
    }
  }
  
  // QuitButtonListener - Quit the program
  class QuitButtonListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      running = false;
      window.dispose();
      friends.dispose();
      System.exit(-1);
    }
  }
  
  class BackButtonListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      window.setVisible(false);
      friends.setVisible(true);
    }
  }
  
  class FriendButtonListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      
      Object source = event.getSource();
      JButton btn = (JButton)source;
      String friendName = btn.getText();
      targetUser = new User(friendName);
      System.out.println(friendName);
      
      friends.setVisible(false);
      window.setSize(400,400);
      window.setVisible(true);
      
    }
  }
  
  class LoginButtonListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      user = new User(typeField.getText());
      typeField.setText("");

      //try{
        System.out.println(user == null);
        Thread t2 = new Thread(new clientUpdater(updateSocket, user));
        t2.start();
      //}catch(IOException e){
      //  System.out.println("Error sending user info");
      //}

        System.out.println(size = users.size());
        
        System.out.println(users);
      
      loginPanel.remove(typeField);
      login.dispose();
      
      southPanel.add(typeField);
      southPanel.add(sendButton);
      southPanel.add(backButton);
      southPanel.add(clearButton);
      
      window.add(BorderLayout.CENTER, msgArea);
      window.add(BorderLayout.SOUTH, southPanel);
      
      southPanel = new JPanel();
      typeField = new JTextField(10);
      sendButton = new JButton("SEND");
      sendButton.addActionListener(new SendButtonListener());
      clearButton = new JButton("QUIT");
      clearButton.addActionListener(new QuitButtonListener());
      
      southPanel.add(typeField);
      southPanel.add(sendButton);
      southPanel.add(clearButton);
      
      globalPanel.add(globalMsgArea, BorderLayout.CENTER);
      globalPanel.add(southPanel, BorderLayout.SOUTH);
      
      
      //updateFriends();
      friends.add(scroller);
      friends.add(globalPanel);
      friends.pack();
      friends.setVisible(true);
      friendPanel.revalidate();
      friendPanel.repaint();
      
      
    }
  }
}