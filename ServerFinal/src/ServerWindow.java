import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ServerWindow extends JFrame {
    private JPanel ServerApp;
    private JTable UserList;
    private JTextField Bannmtxt;
    private JButton banButton;
    private JButton banButton1;
    private JTextField Baniptxt;

//    headers for the UserLists table
    private String header[] = {"Username", "IP address"};

//    template of the default table model
    public DefaultTableModel dtm;

    public ServerWindow(){
        setContentPane(ServerApp);
        pack();
        setVisible(true);
//        allows termination of the program when the window exit button is pressed
        setDefaultCloseOperation(EXIT_ON_CLOSE);

//        set the table model and apply it to the UserLists
        dtm = new DefaultTableModel(header, 0);
        UserList.setModel(dtm);

//        listener for when the ban by name button is pressed
        banButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                add the name into the banlist
                String ban = Bannmtxt.getText();
                Bannmtxt.setText("");
                Server.banlist.add(ban);

//                remove the user with the same username from the server
                for(User x : Server.users){
                    if(x.getUsername().equals(ban)){
                        try {
                            x.sendToUser("you have been banned");
                            x.disconnect();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        updateGUI();
                        break;
                    }
                }
            }
        });

//        listener for when the ban by ip button is pressed
        banButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                add the ip into the ipbanlist
                String ipban = Baniptxt.getText();
                Baniptxt.setText("");
                Server.ipbanlist.add(ipban);

//                disconnect the first person with the banned ip
                for(User x : Server.users){
                    if(x.getAddress().equals(ipban)){
                        try {
                            x.sendToUser("your ip has been banned");
                            x.disconnect();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        updateGUI();
                        break;
                    }
                }
            }
        });
    }

//    updates the UserLists table
    public void updateGUI(){
        Server.frame.dtm.setRowCount(0);

        for(int i = 0; i < Server.users.size(); i++){
            Object[] objs = {Server.users.get(i).getUsername(),Server.users.get(i).getAddress()};
            Server.frame.dtm.addRow(objs);
        }

    }


}
