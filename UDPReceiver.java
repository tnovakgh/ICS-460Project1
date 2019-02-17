import java.io.*;

import java.net.DatagramPacket;

import java.net.DatagramSocket;



public class UDPReceiver extends Thread {



    private DatagramSocket socket;

    private boolean running;

    private static final int BUFFER_SIZE = 16;

    private byte[] buffer = new byte[BUFFER_SIZE];



    public UDPReceiver() {

        try {

            socket = new DatagramSocket(4445);

        }catch(IOException ex){

            ex.printStackTrace();

        }

    }



    public void run() {

        int packets = 0;

        running = true;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        while (running) {

            try {

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                socket.receive(packet);



                if (new String(packet.getData(), 0, packet.getLength()).trim().equals("end")) {

                    running = false;

                    continue;

                }



                byteArrayOutputStream.write(packet.getData());



                packets++;

                System.out.println("Packets received: " + packets);



                buffer = new byte[BUFFER_SIZE];



            }catch(IOException ex){

                ex.printStackTrace();

            }

        }

        try {

            byte[] result = byteArrayOutputStream.toByteArray();

            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream("duck.jpg"));

            outputStream.write(result);

        }catch(IOException ex){

            ex.printStackTrace();

        }



        socket.close();

    }



    public static void main(String[] args) {

        new UDPReceiver().start();

    }

}