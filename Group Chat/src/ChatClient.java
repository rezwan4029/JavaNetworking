import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

public class ChatClient {

    static class ChatAccess extends Observable {
        private Socket socket;
        private OutputStream outputStream;
        @Override
        public void notifyObservers(Object arg) {
            super.setChanged();
            super.notifyObservers(arg);
        }

        /** Create socket, and receiving thread */
        public ChatAccess(String server, int port) throws IOException {
            socket = new Socket(server, port);
            outputStream = socket.getOutputStream();

            Thread receivingThread = new Thread() {
                @Override
                public void run() {
                    try {
                        BufferedReader reader = new BufferedReader( new InputStreamReader(socket.getInputStream()) );
                        String Str ;
                        while (( Str = reader.readLine()) != null ) notifyObservers( Str );
                    } catch (IOException ex) {
                        notifyObservers(ex);
                    }
                }
            };
            receivingThread.start();
        }

        /** Send a line of text */
        public void send(String msg) {
            try {
                outputStream.write(( msg + "\n").getBytes());
                outputStream.flush();
            } catch (IOException ex) {
                notifyObservers(ex);
            }
        }

        /** Close the socket */
        public void close() {
            try {
                socket.close();
            } catch (IOException ex) {
                notifyObservers(ex);
            }
        }
    }

    static class ChatFrame extends JFrame implements Observer {
		private static final long serialVersionUID = 1L;
		private JTextArea TxtArea;
        private JTextField inputTextField;
        private JButton Button;
        private ChatAccess chatAccess;

        public ChatFrame(ChatAccess chatAccess) {
            this.chatAccess = chatAccess;
            chatAccess.addObserver(this);
            GUI();
        }

        private void GUI() {
            TxtArea = new JTextArea(16,27);
            TxtArea.setEditable(false);
            TxtArea.setLineWrap(true);
            TxtArea.setBackground( Color.ORANGE );
            add( new JScrollPane( TxtArea) , BorderLayout.CENTER );

            Box box = Box.createHorizontalBox();
            add(box, BorderLayout.SOUTH);
            inputTextField = new JTextField();
            Button = new JButton("Send");
            box.add(inputTextField);
            box.add(Button);

            ActionListener sendListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String Str = inputTextField.getText();
                    if ( Str != null && Str.trim().length() > 0) chatAccess.send( Str );
                    inputTextField.setText("");
                }
            };
            inputTextField.addActionListener(sendListener);
            Button.addActionListener(sendListener);

            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    chatAccess.close();
                }
            });
        }

        public void update(Observable o, Object arg) {
            final Object finalArg = arg;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    TxtArea.append(finalArg.toString());
                    TxtArea.append("\n");
                }
            });
        }
    }

    public static void main(String[] args) {
        String server = args[0];
        int port = 2222;
        ChatAccess access = null;
        try {
            access = new ChatAccess(server, port);
        } catch (IOException e) {
            System.out.println(e);
        }
        JFrame frame = new ChatFrame(access);
        frame.setTitle("GroupChat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
    }
}