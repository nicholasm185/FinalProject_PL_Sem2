import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;

// inherits JFrame, enabling Login to access JFrame class functions
public class Login extends JFrame{
//    components of the GUI
    private JPanel LoginForm;
    private JTextField namefld;
    private JTextField hostfld;
    private JTextField portfld;
    private JButton ConnectBtn;

//    variables used in the Login GUI class
    private String name;
    private String host;
    private int port;

//    Login class constructor
    public Login(){
//        copy the system's look and feel to make the window matching with the system
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
//            handles the variety of exceptions thrown by UIManager.setLookandFeel.
//            Exception is used due to the numerous number of exeptions to be handled.
            e.printStackTrace();
        }

//        sets the contents of the window, setting its position, turning on visibility, and enabling program termination
//        on window close.
        setContentPane(LoginForm);
        setLocationRelativeTo(null);
        pack();
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

//        puts focus on the name field for quick access
        namefld.requestFocus();

//        event listener for when the "connect" button is pressed"
        ConnectBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connect();
            }
        });

//        event listener for when enter is pressed on the name text field
        namefld.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    hostfld.requestFocus();
                }
            }
        });

//        event listener for when enter is pressed on the host text field
        hostfld.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    portfld.requestFocus();
                }
            }
        });

//        event listener for when enter is pressed on the port text field
        portfld.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    connect();
                }
            }
        });

    }

//    function to connect the client to the server
    private void connect(){
//        boolean later used to check if name and host are acceptable
        boolean ok = false;

//        gets the text from name and host text fields
        name = namefld.getText();
        host = hostfld.getText();

//        gets the port number and handles the exception of when the data inputted is not a number
        try{
            port = Integer.parseInt(portfld.getText());
        } catch(NumberFormatException e){
            System.out.println("Bad port");
        }

//        turns ok to true when name and host are not empty
        if(!name.equals("") && !host.equals("")){
            ok = true;
        }


        if(ok){
            System.out.println("connecting");

//            this try catch handles when the server is not found or when the port number is out of range
            try {
//                sets the static variables of Client to the data inputted
                Client.socket = new Socket(host,port);
                Client.out = new ObjectOutputStream(Client.socket.getOutputStream());

//                send out the first message, this is important to establish connection for the ObjectOutputStream as
//                the server will be waiting a package
                DataContainer firstMessage = new DataContainer(name);
                Client.out.writeObject(firstMessage);
                Client.out.flush();
                System.out.println("name sent");

//                establish connection of the ObjectInputStream
                Client.in = new ObjectInputStream(Client.socket.getInputStream());

//                starts the AppWindow window (chat window)
                AppWindow frame = new AppWindow();

//                start the Input thread, which accepts data sent from the server
                Input usrinpt = new Input(Client.in, frame);
                usrinpt.start();

//                disposes this window
                dispose();

            } catch (IOException e) {
                System.out.println("server not found");
                JOptionPane.showMessageDialog(null, "server not found");
                hostfld.setText("");
                portfld.setText("");
            } catch (IllegalArgumentException e2){
                JOptionPane.showMessageDialog(null, "port out of range");
                hostfld.setText("");
                portfld.setText("");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please input appropriate data");
            hostfld.setText("");
            portfld.setText("");
        }

    }
}
