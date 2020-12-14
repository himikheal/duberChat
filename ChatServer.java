/* [ChatServer.java]
 * You will need to modify this so that received messages are broadcast to all clients
 * @author Mangat
 * @ version 1.0a
 */

//imports for network communication
import java.io.*;
import java.net.*;
import java.util.*;

class ChatServer {

  ServerSocket serverSock;// server socket for connection
  //ServerSocket updateSocket;
  
  static Boolean running = true; // controls if the server is accepting clients
  ArrayList<SuperChat> users = new ArrayList<SuperChat>();
  HashMap<User, ObjectOutputStream[]> outputMap = new HashMap<>(); //[0] is client, [1] is update
  Set<User> outputSet = outputMap.keySet();
  //HashMap<User, ObjectInputStream[]> inputMap = new HashMap<>();
  //Set<User> inputSet = inputMap.keySet();
  /**
   * Main
   * 
   * @param args parameters from command line
   */
  public static void main(String[] args) {
    new ChatServer().go(); // start the server
  }

  /**
   * Go Starts the server
   */
  public void go() {
    System.out.println("Waiting for a client connection..");

    Socket client = null;// hold the client connection
    Socket clientUpdater = null;

    try {
      serverSock = new ServerSocket(5000); // assigns an port to the server
      //updateSocket = new ServerSocket(5001);
      //serverSock.setSoTimeout(15000); //15 second timeout
      while (running) { // this loops to accept multiple clients
        client = serverSock.accept(); // wait for connectio
        clientUpdater = serverSock.accept();
        
        System.out.println("Client connected");
        // Note: you might want to keep references to all clients if you plan to
        // broadcast messages
        // Also: Queues are good tools to buffer incoming/outgoing messages
        //Thread t = new Thread(new ConnectionHandler(client)); // create a thread for the new client and pass in the socket
        //t.start(); // start the new thread
        Thread t = new Thread(new ConnectionHandler(client, clientUpdater)); // create a thread for the new client and pass in the socket
        t.start(); // start the new thread
        //Thread t2 = new Thread(new UpdateHandler(clientUpdater, client));
        //t2.start();
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

  // ***** Inner class - thread for client connection
  class ConnectionHandler implements Runnable {
    //private ObjectOutputStream output;
    private ObjectInputStream input;
    //private ObjectOutputStream updateOutput;
    private ObjectInputStream updateInput;
    private Socket client; // keeps track of the client socket
    private Socket updateSocket;
    private boolean running;
    private User user;
    private PrintWriter fileOut;
    private BufferedReader fileIn;

    /*
     * ConnectionHandler Constructor
     * 
     * @param the socket belonging to this client connection
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
      try { // assign all connections to client
        InputStream inputStream = s.getInputStream();
        //OutputStream outputStream = s.getOutputStream();
        //output = new ObjectOutputStream(outputStream);
        input = new ObjectInputStream(inputStream);
        InputStream updateInputStream = updateSocket.getInputStream();
        //OutputStream updateOutputStream = updateSocket.getOutputStream();
        //updateOutput = new ObjectOutputStream(updateOutputStream);
        updateInput = new ObjectInputStream(updateInputStream);
      } catch (IOException e) {
        e.printStackTrace();
      }
      running = true;
    } // end of constructor

    /*
     * run executed on start of thread
     */
    public void run() {
      try {
        this.user = (User) input.readObject();
        System.out.println("WTFTEST");
        System.out.println("IS USER NULL? " + this.user == null);
        System.out.println(this.user);
        System.out.println(this.user.getUsername());
        users.add(this.user);
        outputMap.put(this.user, new ObjectOutputStream[]{
          new ObjectOutputStream(this.client.getOutputStream()), 
            new ObjectOutputStream(this.updateSocket.getOutputStream())});
        if(this.user.getSignIn()){
          //String st;
          //while((st = fileIn.readLine()) != null){
          //  if(this.user.getUsername().equals(st)){
          fileOut.println(user.getUsername() + ":" + user.getPassword());
          fileOut.close();
        }
        
        //outputMap.get(user)[1].writeObject(users);
        //outputMap.get(user)[1].flush();
        
        for(User key : outputSet) {
          //System.out.println(key != this.user);
          //if(key != this.user) {
          System.out.println(users.size());
          outputMap.get(key)[1].writeObject(users);
          outputMap.get(key)[1].reset();
          //}
        }
      } catch (ClassNotFoundException e) {
        System.out.println("Class not found");
        e.printStackTrace();
      } catch (IOException e2) {
        System.out.println("IO NO GOOD");
        e2.printStackTrace();
      }

      // Get a message from the client
      //Message msg = null;

      // Send a message to the client

      // Get a message from the client
      while (running) { // loop unit a message is received
        try {
          Object o = input.readObject();
          //msg = (Message) input.readObject(); // get a message from the client
          
          if(o instanceof Message) {
            Message msg = (Message) o;
            System.out.println(this.user.getUsername() + ": " + msg.getText());
            msg.setText(this.user.getUsername() + ": " + msg.getText());
            if(!msg.isGlobal()) {
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
            System.out.println("asdfMOVIE");
            for(User key : outputSet) {
              for(int i = 0; i < ((GroupChat)o).getGroup().size(); i++) {
                System.out.println("asdfMOVIE2");
                System.out.println(key);
                System.out.println(((GroupChat)o).getGroup().get(i));
                
                if(((GroupChat)o).getGroup().get(i).getUsername().equals(key.getUsername())) {
                  System.out.println("asdfMOVIE3");
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
//                  outputMap.remove(key);
//                  outputSet.remove(key);
//                  users.remove(key);
                }
              }
              running = false;
            }
          }
        } catch (IOException e) {
          System.out.println("Failed to receive msg from the client");
          e.printStackTrace();
          running = false;
        } catch (ClassNotFoundException e2) {
          System.out.println("Class not found");
          e2.printStackTrace();
        }
      }

      // close the socket
      try {
        input.close();
        outputMap.get(user)[0].close();
        outputMap.get(user)[1].close();
        client.close();
        updateSocket.close();
        for(User key : outputSet) {
          if(key.getUsername().equals(this.user.getUsername())) {
            outputMap.remove(key);
            outputSet.remove(key);
            users.remove(key);
          }
        }
      } catch (IOException e) {
        System.out.println("Failed to close socket1");
        e.printStackTrace();
      }
    } // end of run()
  } // end of inner class

} // end of Class