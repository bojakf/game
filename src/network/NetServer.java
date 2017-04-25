package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import main.Game;

public class NetServer {
	
	private Thread acceptThread;
	private ServerSocket serverSocket;
	private boolean stop = false;
	private ArrayList<NetHandle> handles;
	
	protected NetServer(int port) {
		
		handles = new ArrayList<>();
		
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		acceptThread = new Thread(acceptRunnable);
		acceptThread.start();
		
	}
	
	public void close() {
		stop = true;
		for(int i = 0; i < handles.size(); i++) {
			handles.get(i).close();
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		serverSocket.close();
		super.finalize();
	}
	
	private Runnable acceptRunnable = new Runnable() {
		
		@Override
		public void run() {
			
			while(!stop) {
				
				try {
				
					Socket client = serverSocket.accept();
					
					ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
					ObjectInputStream in = new ObjectInputStream(client.getInputStream());
					
					out.writeInt(Game.net.genPlayerID());
					
					for(int i = 0; i < Game.net.netObjects.size(); i++) {
						if(Game.net.netObjects.get(i) instanceof NetPlayer) 
							out.writeObject(NetCommands.ADD_PLAYER);
						else
							out.writeByte(NetCommands.ADD_OBJECT);
						out.writeObject(Game.net.netObjects.get(i));
					}
					
					handles.add(new NetHandle(client, out, in));
					
				
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			
		}
	};
	
}
