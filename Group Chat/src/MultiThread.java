import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

public class MultiThread {
	private static ServerSocket serverSocket = null;
	private static Socket clientSocket = null;
	private static final int MxClientCnt = 10;
	private static final ClientThread[] T = new ClientThread[MxClientCnt];

	public static void main(String args[]) {
		int portNumber = 2222;
		try {
			serverSocket = new ServerSocket(portNumber);
		} catch (IOException e) {
			System.out.println(e);
		}
		while (true) {
			try {
				clientSocket = serverSocket.accept();
				int i;
				for (i = 0; i < MxClientCnt; i++) {
					if (T[i] == null) {
						T[i] = new ClientThread(clientSocket, T);
						T[i].start();
						break;
					}
				}
				if (i == MxClientCnt) {
					PrintStream os = new PrintStream( clientSocket.getOutputStream() );
					os.println("Server too busy. Try later.");
					os.close();
					clientSocket.close();
				}
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}
}