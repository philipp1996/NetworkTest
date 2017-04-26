package at.werkstatt.philipp.networktest;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

/**
 * Created by philipp on 06.04.2017.
 */

public class Client {
    public static  String SERVER_HOSTNAME = "localhost";
    public static final int SERVER_PORT = 2002;
    static PrintWriter out = null;
    static Socket socket = null;

    private static boolean connected = false;

    public static void startCLient(Context c,String ip)
    {
        //System.out.println("startCLient:");
        SERVER_HOSTNAME=ip;
        BufferedReader in = null;



        try {

            //System.out.println("Try to Connect:");
            socket = new Socket(SERVER_HOSTNAME, SERVER_PORT);
            in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));




            display("Connected to server " +
                    SERVER_HOSTNAME + ":" + SERVER_PORT,c);
            connected = true;

        } catch (Exception e) {
            System.err.println("Can not establish connection to " +
                    SERVER_HOSTNAME + ":" + SERVER_PORT+" Exception: "+e);
            connected = false;


        }


        System.out.println("Check Message start:");
        try {
            // Read messages from the server and print them
            String message;
            while ((message=in.readLine()) != null) {
                System.out.println("+++++++++++++++++++++++++++++++++");
                System.out.println(""+message);
                display(message,c);
                System.out.println("+++++++++++++++++++++++++++++++++");
            }
        } catch (Exception e) {
            System.err.println("Connection to server broken.");
            connected = false;
            e.printStackTrace();
        }

    }
    public static void sendMessage(String message)
    {
        Sender sender = new Sender(message,socket);
        sender.setDaemon(true);
        sender.start();

    }

    public boolean isConnected(){
        return this.connected;
    }


    public static void display(final String message, final Context c)
    {

        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {

            @Override
            public void run() {
                //Your UI code here
                Toast.makeText(c,"Client get: "+message,Toast.LENGTH_SHORT).show();
            }
        });


    }
}

class Sender extends Thread
{
    private PrintWriter mOut;
    private String mMessage;
    private Socket mSocket;


    public Sender(String message, Socket socket)
    {

        mMessage = message;
        mSocket =socket;
    }

    /**
     * Until interrupted reads messages from the standard input (keyboard)
     * and sends them to the chat server through the socket.
     */
    public void run()
    {
        try {

            System.out.println(""+mSocket.isClosed());
            System.out.println(""+mSocket.isConnected());



                System.out.println("Try to send messsage to Server get Stream: ");
                mOut = new PrintWriter(
                    new OutputStreamWriter(mSocket.getOutputStream()));



                mOut.println(mMessage);
                mOut.flush();


        } catch (Exception io) {
            // Communication is broken
            System.err.println("Cannot send message to Server Exception");
        }
    }
}
