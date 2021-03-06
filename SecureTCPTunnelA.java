import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.*;

public class SecureTCPTunnelA {
        public static String RemoteHostAddress = "";
        public static int RemotePortNumber = 0;

	public static void main(String[] args) throws IOException {
		int port = Integer.parseInt(args[0]);
                RemoteHostAddress = args[1];
                RemotePortNumber = Integer.parseInt(args[2]);
		System.out.println("Server waiting for connection on port "+port);
		ServerSocket ss = new ServerSocket(port);
                
		while(true)
                {
                    System.out.println("Waiting for client socket");
                    Socket clientSocket = ss.accept();
                
                   System.out.println("Recieved Client Socket");
                    new HandleConnectionFromClientToRemote(clientSocket);
                }
	}
}
class HandleConnectionFromClientToRemote
{
        Socket clientSocket=null;
        Socket remoteSocket=null;
        public HandleConnectionFromClientToRemote(Socket cs)
        {
             //cs.socket().setTcpNoDelay(true);

             DataOutputStream clientout = null;
             DataInputStream clientin = null;
             DataOutputStream remoteout = null;
             DataInputStream remotein = null;
    
             clientSocket=cs;
             try
             {
            System.out.println("Recieved connection from "+clientSocket.getInetAddress()+" on port "+clientSocket.getPort());
	    System.out.println("Connecting to "+SecureTCPTunnelA.RemoteHostAddress+" on port "+SecureTCPTunnelA.RemotePortNumber);
	
             remoteSocket = new Socket(SecureTCPTunnelA.RemoteHostAddress,SecureTCPTunnelA.RemotePortNumber);
             clientout = new DataOutputStream(clientSocket.getOutputStream());
             clientin = new DataInputStream(clientSocket.getInputStream());
             remoteout = new DataOutputStream(remoteSocket.getOutputStream());
             remotein = new DataInputStream(remoteSocket.getInputStream());
	
   		    //create two threads to send and recieve from client
		    RecieveFromClientSendToRemoteThread recievefromclient = new RecieveFromClientSendToRemoteThread (clientin,remoteout);
		    recievefromclient.start();
		    RecieveFromRemoteSendToClientThread recievefromremote = new RecieveFromRemoteSendToClientThread (remotein,clientout);
		    recievefromremote.start();
             }
       	     catch(Exception ex){System.out.println(ex.getMessage());}
        }
}

class RecieveFromClientSendToRemoteThread extends Thread
{
        DataInputStream input = null;
        DataOutputStream output = null;
	
	public RecieveFromClientSendToRemoteThread(DataInputStream in, DataOutputStream out)
	{
                input=in;
                output=out;
System.out.println("Recieve Thread started "+SecureTCPTunnelA.RemoteHostAddress+" on port "+SecureTCPTunnelA.RemotePortNumber);
	
	}//end constructor

	public void run() {
		try{
		byte[] buffer = new byte[10485760];
                int readlength;
		while(true){
               System.out.println("waiting to read from client");
		while ((readlength = input.read(buffer, 0, buffer.length)) > 0) {
            byte[] dst = Arrays.copyOf(buffer, readlength);
            output.write(dst);
            System.out.println("client says " + readlength  + " " +dst.length);
            }
		
		System.out.println("Connection Closed");
            		return;

           }
		
	}
	catch(Exception ex){System.out.println(ex.getMessage());}
	}
}//end class RecieveFromClientThread

class RecieveFromRemoteSendToClientThread extends Thread
{
        DataInputStream input = null;
        DataOutputStream output = null;
	
	public RecieveFromRemoteSendToClientThread(DataInputStream in, DataOutputStream out)
	{
                input=in;
		output=out;
System.out.println("Send Thread started "+SecureTCPTunnelA.RemoteHostAddress+" on port "+SecureTCPTunnelA.RemotePortNumber);

	}//end constructor

	public void run() {
                try{
		byte[] buffer = new byte[10485760];
                int readlength;
		while(true){
               System.out.println("waiting to read from client");
		while ((readlength = input.read(buffer, 0, buffer.length)) > 0) {
            byte[] dst = Arrays.copyOf(buffer, readlength);
            output.write(dst);
            System.out.println("remote says " + readlength  + " " +dst.length);
            }
		
		System.out.println("Connection Closed");
            		return;

           }
		
	}
	catch(Exception ex){System.out.println(ex.getMessage());}
	}
}//end class RecieveFromClientThread
