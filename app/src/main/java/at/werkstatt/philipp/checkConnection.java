package at.werkstatt.philipp;


/**
 * Created by Philipp Mödritscher on 02.05.2017.
 */

public class CheckConnection extends Thread{
    private ServerDispatcher mServerDispatcher;
    private static long TIMEOUT = 10000;
    public boolean isAlive= true;
    private ClientInfo mClientInfo;

    public CheckConnection( ClientInfo aClientInfo,ServerDispatcher aServerDispatcher) {
        mClientInfo = aClientInfo;
        mServerDispatcher = aServerDispatcher;



    }

    public void run() {
        try {
            while (!isInterrupted()) {
                mServerDispatcher.dispatchMessage(null,NetworkMessages.SERVER_MESSAGE_Here);
                isAlive = false;
                sleep(TIMEOUT);

                if(!isAlive){
                    System.out.println("Client "+mClientInfo.ID+" is not alive anymore");
                }else{
                    System.out.println("Client "+mClientInfo.ID+" is alive");
                }

            }
        }catch (Exception e){

            System.err.println("CheckConnection error: "+e);
        }


    }
}
