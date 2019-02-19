import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Sender for a passed binary file split up in chunks.
 *   
 * ICS460 - Group Project #1
 * 
 * @author Michael, Andrew, Troy (Team 5)
 * 
 */
public class UDPSender {

    private DatagramSocket socket;
    private InetAddress address;
    private static final int BUFFER_SIZE = 16;
    private byte[] buffer;

    /**
	 * Public constructor that creates a new socket on the local network
	 */
    public UDPSender() {
        try {
            socket = new DatagramSocket();
            address = InetAddress.getByName("localhost");
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    /**
	 * Sends the file one packet at a time over the specified socket and port
	 * 
	 * @param fileName
	 * 			the path to the input file that we will be sending to the receiver
	 */
    public void sendFile(String fileName) {
        int packets = 0;
        
        try {
            InputStream inputStream = new BufferedInputStream(new FileInputStream(fileName));
            buffer = new byte[BUFFER_SIZE];
            
            while (inputStream.read(buffer) != -1) { //create a new packet with a specified size and send it through the socket
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 4445);
                socket.send(packet);
                System.out.println(String.format("[%d][%d][%d]", packets, packets * BUFFER_SIZE, packets * BUFFER_SIZE + buffer.length));
                packets++;
            }
            buffer = "end".getBytes(); //we have sent everything, send a signal to the receiver that this is the ending packet
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 4445);
            socket.send(packet);
            socket.close();
            inputStream.close(); //close everything up
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) { //if there was no file provided via commandline provide an error and exit.
            System.out.println("Please provide an input file.\n");
            System.exit(0);
        }
        UDPSender client = new UDPSender();
        client.sendFile(args[0]); //send the passed in command line file
    }
}