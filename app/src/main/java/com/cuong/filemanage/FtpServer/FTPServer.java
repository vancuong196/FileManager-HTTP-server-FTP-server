package com.cuong.filemanage.FtpServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
public class FTPServer implements Runnable
{
        private ServerSocket socket;
        private int port;
        boolean mIsRunning = true;
    public boolean isRunning(){
    	return this.mIsRunning;
	}
	public FTPServer(int port) throws IOException
	{
            this.port = port;
            socket =new ServerSocket(2121);
	}

	public void start() {
		mIsRunning = true;
		new Thread(this).start();
	}

	public void run()
	{
		try
		{
			while(true)
			{
				Socket sc = socket.accept();
				ClientConnectionManager.getInstance().add(new ClientConnectionHandler(sc));
			
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		finally
		{
			destroy();
		}
	}

	public void destroy()
	{
		if( this.socket!=null )
		{
			try
			{
				this.socket.close();
			}
			catch( IOException e ) {}
			this.socket = null;
			this.mIsRunning = false;
		}
	}
}
