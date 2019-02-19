import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Receiver for a passed binary file split up in chunks.
 *   
 * ICS460 - Group Project #1
 * 
 * @author Michael, Andrew, Troy (Team 5)
 * 
 */

public class UDPReceiver extends Thread {
	
    private DatagramSocket socket;
    private boolean running;
    private static final int BUFFER_SIZE = 16;
    private byte[] buffer = new byte[BUFFER_SIZE];

    /**
	 * Public constructor that creates an open socket on port 4445
	 */
    public UDPReceiver() { 
        try {
            socket = new DatagramSocket(4445);
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
    
    /**
	 * The thread that will constantly loop to read in new packets.
	 */
    public void run() {
        int packets = 0;
        running = true;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        while (running) { //loop until the end of the data is received
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                if (new String(packet.getData(), 0, packet.getLength()).trim().equals("end")) {
                    running = false;
                    continue;
                }
                byteArrayOutputStream.write(packet.getData()); //Print out the packet information that will match the senders information
                System.out.println(String.format("[%d][%d][%d]", packets + 1, packets * BUFFER_SIZE, packets * BUFFER_SIZE + buffer.length));
                packets++;
                buffer = new byte[BUFFER_SIZE];
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        }
        try {
            byte[] result = byteArrayOutputStream.toByteArray();
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream("image.jpg"));
            outputStream.write(result);
            outputStream.close();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        socket.close();
    }

    /**
	 * Starts our thread.
	 */
    public static void main(String[] args) {
        new UDPReceiver().start();
    }
}