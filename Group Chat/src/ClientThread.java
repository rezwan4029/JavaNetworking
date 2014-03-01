import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class ClientThread extends Thread {
	private String clientName;
	private DataInputStream in;
	private PrintStream out;
	private Socket clientSocket;
	private ClientThread[] T;
	private int MxClientCnt;
	private String zZz = "---------------------------------------------------------------------------";

	public ClientThread(Socket clientSocket, ClientThread[] T ) {
		this.clientSocket = clientSocket;
		this.T = T;
		MxClientCnt = T.length;
	}

	@SuppressWarnings("deprecation")
	public void run() {
		String Name ;
		int MxClientCnt = this.MxClientCnt;
		ClientThread[] T = this.T;
		try {
			in = new DataInputStream(clientSocket.getInputStream());
			out = new PrintStream(clientSocket.getOutputStream());
			out.printf("Enter your name : ");
			Name = in.readLine();
			out.println("Welcome " + Name + " to the chat room !!!\n");
			synchronized (this) {
				for (int i = 0 ; i < MxClientCnt; i++ ) 
					if (T[i] != null && T[i] == this) {
						clientName = Name;
						break;
					}
				for (int i = 0; i < MxClientCnt; i++) {
					if (T[i] != null && T[i] != this) {
						T[i].out.println(zZz + "\n" + Name + " has entered the chat room\n" + zZz);
					}
				}
			}
			while (true) {
				String Txt = in.readLine();
				if ( Txt.equals("QUIT") ) break;
				if ( Txt.charAt(0) == '@' ) {
					int len = Txt.length();
					String msg = "" , CName = "";
					boolean flag = false ;
					for (int pos = 1 ; pos < len; pos++) {
						if (Txt.charAt(pos) == ' ') {
							flag = true ;
							continue;
						}
						if( flag ) msg += Txt.charAt(pos);
						else CName += Txt.charAt(pos);
					}
					msg = msg.trim();
					boolean found = false ;
					synchronized (this) {
						for (int i = 0; i < MxClientCnt; i++) 
							if (T[i] != null && T[i] != this && T[i].clientName.equals(CName) ) {
								T[i].out.println("[ " + Name + " ] : "	+ msg);
								this.out.println("Me" + " >> " + msg);
								found = true ;
								break;
							}
						if( found == false ) {
							this.out.println("There is no such user named " + Name + " !!!");
						}
					}

					// System.out.println( CName + "\n" + msg );

				} else {
					synchronized (this) {
						for (int i = 0; i < MxClientCnt; i++) 
							if (T[i] != null && T[i].clientName != null && T[i] != this) 
								T[i].out.println("[ " + Name + " ] : " + Txt );
						
						for (int i = 0; i < MxClientCnt; i++) 
							if (T[i] != null && T[i] == this) 
								T[i].out.println("Me" + " >> " + Txt);
					}
				}
			}
			synchronized (this) {
				
				for (int i = 0; i < MxClientCnt; i++) 
					if (T[i] != null && T[i] != this) 
						T[i].out.println(zZz + "\n" + Name + " has left from the chat room\n" + zZz);
			
			}
			out.println("You Left the room ");

			// clear the current thread
			synchronized (this) {
				for (int i = 0; i < MxClientCnt; i++)
					if (T[i] == this) {
						T[i] = null;
						break;
					}
			}
			in.close();
			out.close();
			clientSocket.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}
}