package com.cuong.filemanage.FtpServer;

import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.nio.channels.ServerSocketChannel;
import java.util.Enumeration;
import java.util.Random;

class PassiveConnection extends DataConnection
{
	private ServerSocketChannel serverSocket;
	public InetAddress getLocalIpAddress(){
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
				 en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()&& inetAddress instanceof Inet4Address) {
						return inetAddress;
					}
				}
			}
		} catch (Exception ex) {
			Log.e("IP Address", ex.toString());
		}
		return null;
	}
	PassiveConnection() throws IOException
	{

		InetAddress local = getLocalIpAddress();
		System.out.println(local.getHostName());
		if( local == null )
			throw new IOException("Can't take local ip address");

		ServerSocket testSocket = new ServerSocket();
		int okPort = -1;
		int errorCount = 0;
		while(errorCount < 20)
		{
			int port = 4096 + new Random().nextInt(10000);
			try
			{
				testSocket.bind( new InetSocketAddress(local, port) );
				okPort = port;
			}
			catch( IOException e )
			{
				errorCount++;
				continue;
			}
			break;
		}
		testSocket.close();

		this.addr = new InetSocketAddress(local,okPort);
		System.out.println("add  "+addr.getHostName());
		serverSocket = ServerSocketChannel.open();
		serverSocket.configureBlocking(true);
		serverSocket.socket().setSoTimeout( 1000 * 30 );
		serverSocket.socket().bind(this.addr);
	}

	protected void makeDataTranferConnection() throws IOException
	{
			super.channel = serverSocket.accept();

	}

	public void stop()
	{
		super.stop();
		if( serverSocket !=null )
		{
			try 
			{
				serverSocket.close();
			}
			catch( IOException e ) {}
			serverSocket = null;
		}

	}
}
