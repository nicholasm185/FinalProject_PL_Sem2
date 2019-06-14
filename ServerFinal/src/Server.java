import jdk.dynalink.beans.StaticClass;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

//    initialize static variables for use throughout the program
    static ServerSocket serverSocket;
    static Socket socket;
    static ArrayList<User> users = new ArrayList<>();
    static ArrayList<String> banlist = new ArrayList<>();
    static ArrayList<String> ipbanlist = new ArrayList<>();

//    starts the server window
//    this is static to allow other classes to access and edit the ServerWindow
    static ServerWindow frame = new ServerWindow();


    public static void main(String[] args) throws IOException {

//        starts the server with a new ServerSocket at port 7777, an unused port in most machines
        System.out.println("Starting server");
        serverSocket = new ServerSocket(7777);
        System.out.println("server running");

//        starts the connector thread
        Connector connector = new Connector();
        connector.start();

    }
}
