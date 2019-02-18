import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPSender {

    private DatagramSocket socket;
    private InetAddress address;
    private static final int BUFFER_SIZE = 16;
    private byte[] buffer;

    public UDPSender() {
        try {
            socket = new DatagramSocket();
            address = InetAddress.getByName("localhost");
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public String sendEcho(String msg) {
        String received = "";
        
        try {
            buffer = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 4445);
            socket.send(packet);
            packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            received = new String(packet.getData(), 0, packet.getLength());
        }catch(IOException ex){
            ex.printStackTrace();
        }
        return received;
    }

    public void sendFile(String fileName) {
        int packets = 0;
        
        try {
            InputStream inputStream = new BufferedInputStream(new FileInputStream(fileName));
            buffer = new byte[BUFFER_SIZE];
            
            while (inputStream.read(buffer) != -1) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 4445);
                socket.send(packet);
                System.out.println(String.format("[%d][%d][%d]", packets, packets * BUFFER_SIZE, packets * BUFFER_SIZE + buffer.length));
                packets++;
            }
            buffer = "end".getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 4445);
            socket.send(packet);
            socket.close();
            inputStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if(args.length < 1){
            System.out.println("Please provide an input file.\n");
            System.exit(0);
        }
        UDPSender client = new UDPSender();
        client.sendFile(args[0]);
    }
}