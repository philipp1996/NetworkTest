package at.werkstatt.philipp.networktest;

import android.content.Context;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class MyServer {

   private ServerSocket m_server;

   private boolean isStopped = false;
   private boolean error=false;
    private  Thread m_objThread;
    private ServerDispatcher serverDispatcher;

    Context c;

   MyServer(Context c)
   {
       try {
           m_server = new ServerSocket(2002);
           this.c=c;
       } catch (Exception e) {
           error=true;
           System.err.println(e);
       }
   }

   void startListening()
   {
        m_objThread = new Thread(new Runnable() {
           public void run() {
               // Start ServerDispatcher thread
               serverDispatcher = new ServerDispatcher(c);
               serverDispatcher.start();

               while (!isStopped()) {
                   try {
                       Socket socket = m_server.accept();
                       ClientInfo clientInfo = new ClientInfo();
                       clientInfo.mSocket = socket;
                       ClientListener clientListener =
                               new ClientListener(clientInfo, serverDispatcher);
                       ClientSender clientSender =
                               new ClientSender(clientInfo, serverDispatcher);
                       clientInfo.mClientListener = clientListener;
                       clientInfo.mClientSender = clientSender;
                       clientListener.start();
                       clientSender.start();
                       serverDispatcher.addClient(clientInfo);



                   } catch (Exception ioe) {
                       ioe.printStackTrace();
                   }
               }

           }});

           m_objThread.start();
   }


   private synchronized boolean isStopped() {
       return this.isStopped;
   }

   public synchronized void stop(){
       this.isStopped = true;
      // System.out.println("Try to close Server:");

       try {
           serverDispatcher.stopAll();
           serverDispatcher.interrupt();
           this.m_server.close();
           this.m_objThread.interrupt();




       } catch (Exception e) {
           System.out.println("SERVER: Exception in stop():");
           e.printStackTrace();
       }
   }
}
