# duberChat
 
A Java-based multi-client chat application built as a networking project. duberChat supports user authentication, direct messaging, global chat, and group chat rooms over a TCP socket connection.
 
---
 
## Features
 
- **User Authentication** — Sign up for a new account or log in with an existing one. Credentials are persisted server-side in `UserInfo.txt`.
- **Direct Messaging** — Send private messages to any online user.
- **Global Chat** — Broadcast messages to all connected users simultaneously.
- **Group Chat** — Create named group chats and invite multiple users to a shared room.
- **Live User List** — The online users panel updates in real time as users connect or disconnect.
- **Server Console Commands** — The server supports runtime commands to send messages or shut down gracefully.
 
---
 
## Project Structure
 
| File | Description |
|---|---|
| `ChatServer.java` | Server application — accepts connections, routes messages, manages users |
| `ChatClient.java` | Client application — Swing GUI for login, messaging, and chat rooms |
| `User.java` | Model representing a connected user (username, password, login state) |
| `SuperChat.java` | Abstract base class shared by `User` and `GroupChat` |
| `Message.java` | Model for direct and global messages |
| `GroupMessage.java` | Model for group chat messages |
| `GroupChat.java` | Model representing a named group chat and its member list |
| `UserInfo.txt` | Flat-file database storing registered usernames and passwords |
