import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

//this class is the data exchanged between users and the server.
//it implements Serializable so that it can be serialized and sent through a network
public class DataContainer implements Serializable {

    //    the variables include the file path of an image, the image's data, and a string message
    private String filename;
    private byte [] picdata;
    private String message;

    //    constructor for when only a message is needed to be sent
    public DataContainer(String message){
        this.message = message;
    }

    //    constructor for when a picture is sent
    public DataContainer(String filename, String message){
        this.filename = filename;
        this.message = message;

        try {
//            gets the picture according to the file path and stores it in the picdata variable
            BufferedImage image = ImageIO.read(new File(filename));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", bos);
            this.picdata = bos.toByteArray();

//            handles when the picture is not found, sets picdata no null
        } catch (IOException e) {
            System.out.println("cant load image");
            this.picdata = null;
        }

    }

    //    getters
    public String getFilename() {
        return filename;
    }

    public byte[] getPicdata() {
        return picdata;
    }

    public String getMessage() {
        return message;
    }
}
