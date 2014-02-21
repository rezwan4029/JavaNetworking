import javax.swing.*;
public class ClientServer {
	public static void main(String[] args) {
		String[] X = { "Client", "Server" };
		String cur = (String) JOptionPane.showInputDialog(null, "Login as : ", "Group Chat", JOptionPane.QUESTION_MESSAGE, null, X, X[0]);
		if (cur.equals("Server")) {
			String[] arg = new String[] {};
			new MultiThread();
			MultiThread.main(arg);
		} else {
			String[] Args = new String[] { "localhost" };
			new ChatClient();
			ChatClient.main( Args );
		}
	}
}