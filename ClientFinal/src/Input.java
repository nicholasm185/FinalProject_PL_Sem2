import java.io.IOException;
import java.io.ObjectInputStream;

// extends from the class Thread, which allows this class to act as a thread and have start() function
public class Input extends Thread {

//    variables used in the class
    private boolean exit = false;
    private ObjectInputStream in;
    private AppWindow frame;
    private DataContainer message;

//    constructor accepts the ObjectInputStream from Client and the AppWindow gui frame
    public Input(ObjectInputStream in, AppWindow frame){
        this.frame = frame;
        this.in = in;
    }

//    this overrides the run method from Thread to a custom one
    @Override
    public void run(){
        while(!exit){
            try{
//                retreives DataContainer from the ObjectInputStream
                message = (DataContainer) in.readObject();

//                if the DataContainer contains an image, displays it to the frame else, it displays the message only
                if(message.getMessage().equals("/pic")){
                    frame.imageConsole(message);
                } else{
                    frame.console(message.getMessage());
                }

            } catch (IOException e) {
//                when the thread encounters an IOException, it sets its own in variable to null and stops its own process
                this.in = null;
                this.exit = true;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NullPointerException e){
//                this handles the error caused when a DataContainer with incomplete image is received
                System.out.println("incomplete data received");
                frame.console("Incomplete data received");
            }
        }
    }

}
