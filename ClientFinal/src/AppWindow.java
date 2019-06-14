import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

// inherits JFrame, enabling Login to access JFrame class functions
public class AppWindow extends JFrame{
    private JPanel ChatWindow;
    private JTextPane chatHistory;
    private JTextField txtInput;
    private JButton sendBtn;
    private JScrollPane scrollPane;

//    this is used to modify the chat history box
    private StyledDocument doc = chatHistory.getStyledDocument();

    private DataContainer message;

//    make a packet to disconnect the user from the server safely
    private DataContainer exitMessage = new DataContainer("/exit");

//    this function is called when the user presses window close button
    private WindowListener exitListener = new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            try {
//                sends the exit message
                Client.out.writeObject(exitMessage);
                Client.out.flush();

//                closing the Socket, Object Input and Output stream while automatically ending the Input thread in the process
                Client.out.close();
                Client.in.close();
                Client.socket.close();

//                reopens the login page
                Login login = new Login();

//                handles IOException from writeObject (for when the server is no longer present and window is closed)
            }catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    };

    public AppWindow(){
//        copy the system's look and feel to make the window matching with the system
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentPane(ChatWindow);
        setLocationRelativeTo(null);
        pack();
        setVisible(true);

//        enables exitLisenter function to be called when window is closed
        this.addWindowListener(exitListener);

//        puts focus on the input text box
        txtInput.requestFocus();

//        listener for when the send button is pressed
        sendBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                send();
                txtInput.requestFocus();
            }
        });

//        listener for when enter key is pressed in the input text box
        txtInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    send();
                    txtInput.requestFocus();
                }
            }
        });
    }

//    sends message based on the text in the input text box
    private void send(){

//        gets the text from the input text box and set it back to blank
        String msg = txtInput.getText();
        txtInput.setText("");

//        checks if the message is empty and is not /exit, which would cause the user to be disconnected when not suppressed
        if(!msg.equals("") && !msg.equals("/exit")){
            try {
//                if msg starts with /pic command followed by file path, create a DataContainer which contains the picture
                if(msg.startsWith("/pic")){

//                    split the string based on space
                    String prmsg[] = msg.split("\\s");

//                    put the file path and /pic command into the DataContainer constructor
                    message = new DataContainer(prmsg[1], prmsg[0]);
                } else{

//                    create a DataContainer based on msg
                    message = new DataContainer(msg);
                }

//                sends the message through ObjectOutputStream from the Client class
                Client.out.writeObject(message);
                Client.out.flush();

//                handles for when the server is no longer present
            } catch (IOException e) {
                console("Error, connection to server lost");
                System.out.println("Error, connection to server lost");
                System.exit(0);

//                handles when the /pic command is not used properly
            } catch (ArrayIndexOutOfBoundsException e){
                console("invalid input");
            }
        }
    }

//    displays a message into the chat history box
    public void console(String message){
        try {
            doc.insertString(doc.getLength(), message + "\n", null);
            chatHistory.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

//    displays image into the chat history box
    public void imageConsole(DataContainer x){
        try{

//            scale the size of the image
//            based on StackOverflow answer by Harry Joy https://stackoverflow.com/questions/5895829/resizing-image-in-java
            ImageIcon imageIcon = new ImageIcon(x.getPicdata());
            Image image = imageIcon.getImage();
            Image newImg = image.getScaledInstance(100,100, Image.SCALE_SMOOTH);
            ImageIcon scaledimg = new ImageIcon(newImg);

//            prints "picture received" in the text history
            doc.insertString(doc.getLength(), "picture received\n", null);
            chatHistory.setCaretPosition(doc.getLength());
            chatHistory.insertIcon(scaledimg);
            doc.insertString(doc.getLength(), "\n", null);
            chatHistory.setCaretPosition(doc.getLength());

//            handles BadLocationException from insertIcon
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}
