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
  ServerSocket updateSocket;
  
  static Boolean running = true; // controls if the server is accepting clients
  ArrayList<User> users = new ArrayList<User>();
  HashMap<User, Socket> userMap = new HashMap<>();
  Set<User> userSet = userMap.keySet();
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
    Socket clientUpdate = null;

    try {
      serverSock = new ServerSocket(5000); // assigns an port to the server
      updateSocket = new ServerSocket(5001);
      // serverSock.setSoTimeout(15000); //15 second timeout
      while (running) { // this loops to accept multiple clients
        client = serverSock.accept(); // wait for connectio
        clientUpdate = updateSocket.accept();
        
        System.out.println("Client connected");
        // Note: you might want to keep references to all clients if you plan to
        // broadcast messages
        // Also: Queues are good tools to buffer incoming/outgoing messages
        Thread t = new Thread(new ConnectionHandler(client)); // create a thread for the new client and pass in the socket
        Thread t2 = new Thread(new UpdateHandler(clientUpdate));
        t.start(); // start the new thread
        t2.start();
      }
    } catch (Exception e) {
      System.out.println("Error accepting connection");
      e.printStackTrace();
      // close all and quit
      try {
        client.close();
        clientUpdate.close();
      } catch (Exception e1) {
        System.out.println("Failed to close socket");
      }
      System.exit(-1);
    }
  }

  // ***** Inner class - thread for client connection
  class ConnectionHandler implements Runnable {
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Socket client; // keeps track of the client socket
    private boolean running;
    private User user;

    /*
     * ConnectionHandler Constructor
     * 
     * @param the socket belonging to this client connection
     */
    ConnectionHandler(Socket s) {
      this.client = s; // constructor assigns client to this
      try { // assign all connections to client
        InputStream inputStream = s.getInputStream();
        OutputStream outputStream = s.getOutputStream();
        output = new ObjectOutputStream(outputStream);
        input = new ObjectInputStream(inputStream);

        //try {
        //  user = (User) input.readObject();
        //  System.out.println(user);
        //  users.add(user);
        //  userMap.put(user, this.client);
        //} catch (ClassNotFoundException e) {
        //  System.out.println("Class not found");
        //  e.printStackTrace();
        //}

        //output.writeObject(users);
//
        //for(User key : userSet) {
        //  if(userMap.get(key) != this.client) {
        //    ObjectOutputStream tempOutput = new ObjectOutputStream(userMap.get(key).getOutputStream());
        //    tempOutput.writeObject(users);
        //    tempOutput.close();
        //  }
        //}

      } catch (IOException e) {
        e.printStackTrace();
      }
      running = true;
    } // end of constructor

    /*
     * run executed on start of thread
     */
    public void run() {

      // Get a message from the client
      Message msg = null;

      // Send a message to the client

      // Get a message from the client
      while (running) { // loop unit a message is received
        try {
         //if (input.ready()) { // check for an incoming messge
            //if(username == null) {
            //  try {
            //    username = (String) input.readObject();
            //  } catch (ClassNotFoundException e) {
            //    System.out.println("Class not found");
            //    e.printStackTrace();
            //  }
            //  output.flush();
            //}
            //else {
              try {
                msg = (Message) input.readObject(); // get a message from the client
              } catch (ClassNotFoundException e) {
                System.out.println("Class not found");
                e.printStackTrace();
              }
              System.out.println(this.user.getUsername() + ": " + msg.getText());
              output.writeObject(msg); // echo the message back to the client ** This needs changing for multiple clients
              output.flush();
              for(User key : userSet) {
                if(msg.getTargetUser().equals(key)) {
                  ObjectOutputStream targetOutput = new ObjectOutputStream(userMap.get(key).getOutputStream());
                  targetOutput.writeObject(this.user.getUsername() + ": " + msg.getText());
                  targetOutput.flush();
                  targetOutput.close();
                }
              }
            //}
          //}
        } catch (IOException e) {
          System.out.println("Failed to receive msg from the client");
          e.printStackTrace();
        }
      }

      // close the socket
      try {
        input.close();
        output.close();
        client.close();
        updateSocket.close();
      } catch (Exception e) {
        System.out.println("Failed to close socket1");
      }
    } // end of run()
  } // end of inner class

  class UpdateHandler implements Runnable {
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Socket updateSocket;
    private User user;
    
    UpdateHandler(Socket s) {
      updateSocket = s;
      try { // assign all connections to client
        InputStream inputStream = s.getInputStream();
        OutputStream outputStream = s.getOutputStream();
        output = new ObjectOutputStream(outputStream);
        input = new ObjectInputStream(inputStream);

      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    public void run() {
      try {
        try {
          user = (User) input.readObject();
          System.out.println(user);
          users.add(user);
          userMap.put(user, this.updateSocket);
        } catch (ClassNotFoundException e) {
          System.out.println("Class not found");
          e.printStackTrace();
        }

        //output.writeObject(users);

        for(User key : userSet) {
          if(userMap.get(key) != this.updateSocket) {
            ObjectOutputStream tempOutput = new ObjectOutputStream(userMap.get(key).getOutputStream());
            tempOutput.writeObject(users);
            tempOutput.close();
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      try {
        input.close();
        output.close();
        //updateSocket.close();
      } catch (Exception e) {
        System.out.println("Failed to close socket2");
      }
    }
  }
} // end of Class