import javax.xml.crypto.Data;
import java.io.*;
import java.net.SocketException;

//extends the Thread class, allowing it to act as a thread
class Connector extends Thread{

    public Connector(){}

//    this overrides the run method from Thread to a custom one
    @Override
    public void run() {
        while(true){
            try {
                System.out.println("ready for connection");

//                tries to accept a connection from the socket, skipping all other processes when
//                a socket exception is encountered
                try{
                    Server.socket = Server.serverSocket.accept();
                } catch (SocketException e){
                    continue;
                }

//                retrieve the IP address of the incoming connection
                String address = String.valueOf(Server.socket.getInetAddress());
                System.out.println("request in" + address);

//                sets the Object Input and Output streams to be used by the user
                ObjectInputStream in = new ObjectInputStream(Server.socket.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(Server.socket.getOutputStream());

                System.out.println("established connection");

//                gets the first message from the user, which is the username
                DataContainer firstMessage =  (DataContainer)in.readObject();

                System.out.println("name got");

//                checks if the name or ip are in the ban list
                if(!Server.banlist.contains(firstMessage.getMessage()) && !Server.ipbanlist.contains(address)){

//                    create a new user thread
                    User newus = new User(in, out, Server.users, address,firstMessage.getMessage());

//                    checks if the user's name is unique, if not, disconnects the user
                    if(unique(firstMessage.getMessage())) {

//                        starts the newus thread and placing it into the users ArrayList
                        newus.sendToUser("Connected");
                        newus.start();
                        Server.users.add(newus);
                        System.out.println(Server.users.size());
                        System.out.println("Client connected");
//                        updates the list of connected users on the GUI
                        Server.frame.updateGUI();
                    } else{
                        newus.sendToUser("Username not unique");
                        out.close();
                        in.close();
                    }
                } else{
                    out.close();
                    in.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
//                handles error from in.readObject()
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

//    checks if the username is unique
    private boolean unique(String username){
        for(User x: Server.users){
            if(x.getUsername().equals(username)) return false;
        }
        return true;
    }

}