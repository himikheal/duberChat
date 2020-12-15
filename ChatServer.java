/* [ChatServer.java]
 * Server code for duberChat
 * Controls and relays messages
 * Creates new clients and group chats
 * @author Mr.Mangat, Michael Zhang
 * @ version 2.0a
 */

//imports for network communication
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

class ChatServer {

  ServerSocket serverSock;// server socket for connection
  static Boolean running = true; // controls if the server is accepting clients
  ArrayList<SuperChat> users = new ArrayList<SuperChat>();
  HashMap<User, ObjectOutputStream[]> outputMap = new HashMap<>(); //[0] is client, [1] is update
  Set<User> outputSet = outputMap.keySet();
  Boolean serverRunning = true;

  /**
   * Main
   * @param args parameters from command line
   */
  public static void main(String[] args) {
    new ChatServer().go(); // start the server
  }

  /**
   * go 
   * Starts the server
   */
  public void go() {
    System.out.println("Waiting for a client connection..");
    Socket client = null;// hold the client connection
    Socket clientUpdater = null;
    try {
      serverSock = new ServerSocket(5000); // assigns an port to the server
      Thread console = new Thread(new ConsoleReader());
      console.start();
      while (running && serverRunning) {  // this loops to accept multiple clients
        client = serverSock.accept(); // wait for connection
        clientUpdater = serverSock.accept();
        System.out.println("Client connected");
        Thread t = new Thread(new ConnectionHandler(client, clientUpdater)); // create a thread for the new client and pass in the socket
        t.start(); // start the new thread
      }
    } catch (Exception e) {
      System.out.println("Error accepting connection");
      e.printStackTrace();
      // close all and quit
      try {
        client.close();
        clientUpdater.close();
      } catch (Exception e1) {
        System.out.println("Failed to close socket");
      }
      System.exit(-1);
    }
  }

  /**
   * ConnectionHandler
   * class made as custom thread to handle connections and updates to and from clientside
   */
  class ConnectionHandler implements Runnable {
    private ObjectInputStream input;
    private ObjectInputStream updateInput;
    private Socket client; // keeps track of the client socket
    private Socket updateSocket;
    private boolean running;
    private User user;
    private PrintWriter fileOut;
    private BufferedReader fileIn;

    /*
     * ConnectionHandler Constructor
     * constructor for thread
     * @param s the messagehandler socket belonging to this client connection
     * @param updateSocket the clientupdater socket belonging to this client connection
     */
    ConnectionHandler(Socket s, Socket updateSocket) {
      try{
        fileOut = new PrintWriter(new FileWriter("UserInfo.txt", true));
        fileIn = new BufferedReader(new FileReader("UserInfo.txt"));
      }catch(IOException e){
        System.out.println("UserInfo.txt not found");
      }
      this.client = s; // constructor assigns client to this
      this.updateSocket = updateSocket;
      try { // Creates streams
        InputStream inputStream = s.getInputStream();
        input = new ObjectInputStream(inputStream);
        InputStream updateInputStream = updateSocket.getInputStream();
        updateInput = new ObjectInputStream(updateInputStream);
      } catch (IOException e) {
        e.printStackTrace();
      }
      running = true;
    } // end of constructor

    /*
     * run
     * executed on start of thread
     */
    public void run() {
      try {
        this.user = (User) input.readObject();
        users.add(this.user);
        outputMap.put(this.user, new ObjectOutputStream[]{ //Assigns map with outputs
          new ObjectOutputStream(this.client.getOutputStream()), 
            new ObjectOutputStream(this.updateSocket.getOutputStream())});
        fileIn = new BufferedReader(new FileReader("UserInfo.txt")); //Checks userinfo
        if(this.user.getSignIn()){ //Checks for validity of logins/signins
          String st;
          while((st = fileIn.readLine()) != null){
            if(this.user.getUsername().equals(st.substring(0, st.indexOf(":")))){
              this.user.setSignIn(false);
            }
          }
          if(!this.user.getSignIn()){
            System.out.println(this.user.getUsername() + " had Unsuccessful Signup");
            outputMap.get(this.user)[1].writeObject(this.user);
            outputMap.remove(this.user);
            outputSet.remove(this.user);
            users.remove(this.user);
          }else{
            System.out.println(this.user.getUsername() + " had Successful Signup and Login");
            outputMap.get(this.user)[1].writeObject(this.user);
            fileOut.append(user.getUsername() + ":" + user.getPassword() + "\n");
            fileOut.close();
          }
        }else if(this.user.getLogIn()){
          String st;
          String tempName = this.user.getUsername() + ":" + this.user.getPassword();
          this.user.setLogIn(false);
          while((st = fileIn.readLine()) != null){
            if(tempName.equals(st)){
              this.user.setLogIn(true);
            }
          }
          if(!this.user.getLogIn()){
            System.out.println(this.user.getUsername() + " had Unsuccessful Login");
            outputMap.get(this.user)[1].writeObject(this.user);
            outputMap.remove(this.user);
            outputSet.remove(this.user);
            users.remove(this.user);
          }else{
            System.out.println(this.user.getUsername() + " had Successful Login");
            outputMap.get(this.user)[1].writeObject(this.user);
          }
        }
        fileIn.close();
        
        for(User key : outputSet) { // Sends user list
          outputMap.get(key)[1].writeObject(users);
          outputMap.get(key)[1].reset();
        }
      } catch (ClassNotFoundException e) {
        System.out.println("Class not found");
        e.printStackTrace();
      } catch (IOException e2) {
        System.out.println("IO Error occurred");
        e2.printStackTrace();
      }

      // Get a message from the client
      while (running && serverRunning) { // loop unit a message is received
        try {
          Object o = input.readObject();
          // Checks for the type of object sent, and reacts by doing required steps to either reroute or send a command
          if(o instanceof Message) {
            Message msg = (Message) o;
            System.out.println(this.user.getUsername() + ": " + msg.getText());
            msg.setText(this.user.getUsername() + ": " + msg.getText());
            if(!msg.isGlobal()) { // Checks the type of message sent and routes accordingly
              outputMap.get(user)[0].writeObject(msg); // echo the message back to the client ** This needs changing for multiple clients
              outputMap.get(user)[0].flush();
              for(User key : outputSet) {
                if(msg.getTargetUser().getUsername().equals(key.getUsername())) {
                  outputMap.get(key)[0].writeObject(msg);
                  outputMap.get(key)[0].flush();
                }
              }
            }
            else if(msg.isGlobal()) {
              for(User key : outputSet) {
                outputMap.get(key)[0].writeObject(msg);
                outputMap.get(key)[0].flush();
              }
            }
          }
          else if(o instanceof GroupMessage) {
            GroupMessage msg = (GroupMessage) o;
            msg.setText(this.user.getUsername() + ": " + msg.getText());
            for(User key : outputSet) {
              for(int i = 0; i < msg.getTargetUsers().size(); i++) {
                if(msg.getTargetUsers().get(i).getUsername().equals(key.getUsername())) {
                  outputMap.get(key)[0].writeObject(msg);
                  outputMap.get(key)[0].flush();
                }
              }
            }
          }
          else if(o instanceof GroupChat) {
            System.out.println("GroupChat Created Successfully");
            for(User key : outputSet) {
              for(int i = 0; i < ((GroupChat)o).getGroup().size(); i++) {
                if(((GroupChat)o).getGroup().get(i).getUsername().equals(key.getUsername())) {
                  outputMap.get(key)[1].writeObject(o);
                  outputMap.get(key)[1].flush();
                }
              }
            }
          }
          else if(o instanceof String) {
            if(o.equals("/DISCONNECT!")) {
              for(User key : outputSet) {
                if(key.getUsername().equals(this.user.getUsername())) {
                  outputMap.get(key)[1].writeObject("/DISCONNECT!");
                  outputMap.get(key)[1].flush();
                  outputMap.get(key)[0].writeObject("/DISCONNECT!");
                  outputMap.get(key)[0].flush();
                }
              }
              running = false;
            }else if(o.equals("/BADUSER!")) {
              System.out.println("Bad user info");
              for(User key : outputSet) {
                if(key.getUsername().equals(this.user.getUsername())) {
                  outputMap.get(key)[0].writeObject("/BADUSER!");
                  outputMap.get(key)[0].flush();
                }
              }
            }
          }
        } catch (IOException e) {
          System.out.println("Failed to receive msg from the client");
          running = false;
        } catch (ClassNotFoundException e2) {
          System.out.println("Class not found");
        }
      }

      // close the socket and other items
      try {
        input.close();
        outputMap.get(user)[0].close();
        outputMap.get(user)[1].close();
        client.close();
        updateSocket.close();
        outputMap.remove(this.user);
        outputSet.remove(this.user);
        users.remove(this.user);
        for(User key : outputSet) {
          outputMap.get(key)[1].writeObject(users);
          outputMap.get(key)[1].reset();
        }
      } catch (IOException e) {
        System.out.println("Failed to close socket");
      }
    } // end of run()
  } // end of inner class
  
  /**
   * ConsoleReader
   * class to read the console commands and execute them accordingly
   */
  class ConsoleReader implements Runnable {
    private Scanner input;
    private boolean running;

    /**
     * ConsoleReader
     * constructor for consolereader class
     */
    ConsoleReader() {
      input = new Scanner(System.in);
      this.running = true;
    }

    /**
     * run
     * needed method for classes implementing runnable
     */
    public void run() {
      while(this.running) {
        System.out.println("\nType a command, starting with / and ending with !");
        System.out.println("Command List:");
        System.out.println("QUIT - Closes Server");
        System.out.println("SEND - Sends a message to a user");
        String command = this.input.nextLine();
        // Checks scanner for command inputs
        if(command.equals("/QUIT!")) {
          try {
            for(User key : outputSet) {
              outputMap.get(key)[1].writeObject("/SERVERDC!");
              outputMap.get(key)[1].flush();
              outputMap.get(key)[0].writeObject("/SERVERDC!");
              outputMap.get(key)[0].flush();
            }
          } catch(IOException e) {
            System.out.println("Error stopping server");
          }
          serverRunning = false;
          System.exit(-1);
        }
        else if(command.equals("/SEND!")) {
          System.out.println("Type the message to be sent");
          String message = input.nextLine();
          System.out.println("Type the target user's username");
          String target = input.nextLine();
          try {
            boolean sent = false;
            for(User key : outputSet) {
              if(key.getUsername().equals(target)) {
                outputMap.get(key)[0].writeObject(new Message(new User(null), ("FROM SERVER - " + message), true));
                sent = true;
              }
            }
            if(!sent) {
              System.out.println("No user was found with that name");
            }
          } catch(IOException e) {
            System.out.println("Error sending message");
          }
        }
      }
    }
  }

} // end of Class