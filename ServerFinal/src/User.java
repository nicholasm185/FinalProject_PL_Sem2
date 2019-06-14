import javax.xml.crypto.Data;
import java.io.*;
import java.util.ArrayList;

// extends from the class Thread, which allows this class to act as a thread and have start() function
public class User extends Thread{
    private volatile boolean exit = false;

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ArrayList<User> user;
    private String username;
    private String address;

// stores the last message sent by the user
    private String lastMsg;

    private DataContainer message;

    public User(ObjectInputStream in, ObjectOutputStream out, ArrayList<User> user, String address, String username){
        this.out = out;
        this.in = in;
        this.user = user;
        this.username = username;
        this.address = address;
    }

//    this overrides the run method from Thread to a custom one
    @Override
    public void run() {

//        sends to all user that this user has joined the chat
        mta(this.username + " has joined the chat");

        while(!exit) {
            try {
//                attempts to read for DataContainer object in the ObjectInputStream
                message = (DataContainer)in.readObject();

//                if the message is an exit message, disconnects the user
                if(message.getMessage().equals("/exit")){
//                    notify everyone that this user has disconnected
                    mta(username+ " has disconnected");
                    disconnect();
                }
//                if the message starts with the /pic command, sends the DataContainer to all other users
                else if(message.getMessage().startsWith("/pic")){
                    sendClass(message);
//                    notify users who sent the message
                    mta(username+ " sent a picture");
                }
                else {
//                    set last message and send the message to all other users
                    lastMsg = message.getMessage();
                    mta(username + ": " + message.getMessage());
                }
            } catch(IOException | ClassNotFoundException e){
                this.out = null;
                this.in = null;
            }
        }
    }

    //  Send message to everyone in the user array list
    private void mta(String msg){
        message = new DataContainer(msg);
        for (User u: user) {
            try {
                u.out.writeObject(message);
                u.out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    sends the DataContainer with picture inside to all the users
    private void sendClass(DataContainer x){
        for (User u: user){
            try{
                u.out.writeObject(x);
                u.out.flush();
                System.out.println("picture sent");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //  disconnect this user from the server by deleting it from the arraylist and closing in and out
    public void disconnect() throws IOException {
        for(int i = 0; i < Server.users.size(); i++){
            if(Server.users.get(i).username.equals(this.username)){
                Server.users.remove(i);
                Server.frame.updateGUI();
                break;
            }
        }
        out.close();
        in.close();
        this.exit = true;
    }

//    getters

    public String getUsername(){
        return username;
    }

    public String getLastMsg(){
        return lastMsg;
    }

    public String getAddress() {
        return address;
    }

//    send to this particular user, not others
    public void sendToUser(String msg){
        message = new DataContainer(msg);
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
