import java.io.*;
import java.net.*;
import java.util.Scanner;

public class DaytimeUDPClient {
	
	private static final int BUFFER_SIZE = 16;
    private byte[] buffer;
    private InetAddress address;
	private DatagramSocket socket = null;
	private FileEvent event = null;
	private String sourceFilePath = "C:/Users/novak_000/Desktop/toWorkOnICS460.docx";
	private String destinationPath = "C:/tmp/downloads/udp/";
	private String hostName = "localHost";

	public DaytimeUDPClient() {
		try {
            socket = new DatagramSocket();
            address = InetAddress.getByName("localhost");

        }catch(IOException ex){
            ex.printStackTrace();
        }
	}
	
	public void createConnection() {
		
		int packets = 0;
        
        try {
            InputStream inputStream = new BufferedInputStream(new FileInputStream("C:/Users/novak_000/Desktop/toWorkOnICS460.docx"));
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
            inputStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        
		try {
			byte[] incomingData = new byte[1024];
			event = getFileEvent();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(outputStream);
			os.writeObject(event);
			byte[] data = outputStream.toByteArray();
			DatagramPacket sendPacket = new DatagramPacket(data, data.length, address, 9876);
			socket.send(sendPacket);
			System.out.println("File sent from client");
			DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
			socket.receive(incomingPacket);
			String response = new String(incomingPacket.getData());
			System.out.println("Response from server:" + response);
			socket.close();
			Thread.sleep(2000);
			System.exit(0);

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public FileEvent getFileEvent() {
		FileEvent fileEvent = new FileEvent();
		String fileName = sourceFilePath.substring(sourceFilePath.lastIndexOf("/") + 1, sourceFilePath.length());
		String path = sourceFilePath.substring(0, sourceFilePath.lastIndexOf("/") + 1);
		fileEvent.setDestinationDirectory(destinationPath);
		fileEvent.setFilename(fileName);
		fileEvent.setSourceDirectory(sourceFilePath);
		File file = new File(sourceFilePath);
		if (file.isFile()) {
			try {
				DataInputStream diStream = new DataInputStream(new FileInputStream(file));
				long len = (int) file.length();
				byte[] fileBytes = new byte[(int) len];
				int read = 0;
				int numRead = 0;
				while (read < fileBytes.length && (numRead = diStream.read(fileBytes, read, fileBytes.length - read)) >= 0) {
					read = read + numRead;
				}
				fileEvent.setFileSize(len);
				fileEvent.setFileData(fileBytes);
				fileEvent.setStatus("Success");
			} catch (Exception e) {
				e.printStackTrace();
				fileEvent.setStatus("Error");
			}
		} else {
			System.out.println("path specified is not pointing to a file");
			fileEvent.setStatus("Error");
		}
		return fileEvent;
	}

	public static void main(String[] args) {
		DaytimeUDPClient client = new DaytimeUDPClient();
		client.createConnection();
	}
}