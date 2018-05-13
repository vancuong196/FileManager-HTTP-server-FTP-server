package com.cuong.filemanage.FtpServer;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;

public abstract class DataConnection implements Runnable
{
	protected SocketChannel channel;
	protected InetSocketAddress addr;
	private Thread thread = null;

	private boolean isMakeConnectionCompleted = false;
	private OnDataTranferCompletedListener listeners;
	private Object lock = new Object();
	private boolean notified = false;
	private ByteBuffer characterWriter = null;
	private File fileSend = null;
	private File fileReceive = null;
	private long offset = 0L;

	public static DataConnection createPassive() throws IOException
	{
		return new PassiveConnection();
	}

	public static DataConnection createActive( InetSocketAddress dest )
		throws IOException
	{
		ActiveConnection ac = new ActiveConnection();
		ac.addr = dest;
		return ac;
	}

	protected DataConnection()
	{

	}

	public void setFileOffset( long offset )
	{
		this.offset = offset;
	}

	public long getFileOffset()
	{
		return this.offset;
	}

	public InetSocketAddress getAddress()
	{
		return this.addr;
	}


	public String getAddressAsString()
	{
		int port = addr.getPort();

		String[] ips = addr.getAddress().getHostName().split("\\.");
		System.out.println(addr.getAddress().getHostAddress());
		System.out.println(addr.getAddress().getHostName());
		return ips[0] + "," + ips[1] + "," + ips[2] + "," + ips[3] +
			"," + (port/256) + "," + (port%256);
	}

	public void addDataConnectionListener( OnDataTranferCompletedListener l )
	{
                listeners = l;
	}

	public void removeDataConnectionListener( OnDataTranferCompletedListener l )
	{
		listeners = null;
	}

	protected abstract void makeDataTranferConnection() throws IOException;

	public void start()
	{
		if( thread==null )
		{
			thread = new Thread(this);
			thread.start();
		}
	}

	public void stop()
	{
		if( channel!=null )
		{
			try 
			{
				channel.close();
			}
			catch( IOException e ) {}
			channel = null;
		}

		if( thread!=null )
		{
			thread.interrupt();
			thread = null;
		}

	}

	/**
	 *
	 */
	public void run()
	{
		FileChannel file = null;
		try
		{
			isMakeConnectionCompleted = false;
			makeDataTranferConnection();
			isMakeConnectionCompleted = true;
			listeners.actionNegoatiated(true);

			synchronized(lock)
			{
				if( !notified )
				{
//					System.out.println( "DEBUG: lock.wait()" );
					lock.wait(1000*8);
				}
			}

			listeners.transferStarted();

			if( characterWriter!=null )
			{
				while( characterWriter.hasRemaining() )
					channel.write(characterWriter);
			}

			if( fileSend!=null )
			{
				ByteBuffer buf = ByteBuffer.allocateDirect(16384);
				file = new FileInputStream(fileSend).getChannel();
				file.position( offset );
				while(true)
				{
					buf.clear();
					int readlen = file.read(buf);
					if( readlen < 1 )
						break;
					buf.flip();
					while( buf.hasRemaining() )
						channel.write(buf);
				}
			}

			if( fileReceive!=null )
			{
				ByteBuffer buf = ByteBuffer.allocateDirect(16384);	
				file = new FileOutputStream(fileReceive).getChannel();
				while(true)
				{
					buf.clear();
					int readlen = channel.read(buf);
					if( readlen < 1 )
						break;
					buf.flip();
					while( buf.hasRemaining() )
						file.write(buf);
				}
			}

			listeners.transferCompleted(false);
		}
		catch( InterruptedException e )
		{
			if( !isMakeConnectionCompleted)
			{
				listeners.actionNegoatiated(false);
			}
			else
			{
				listeners.transferCompleted(true);
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();

			if( !isMakeConnectionCompleted)
			{
				listeners.actionNegoatiated(false);
			}
			else
			{
				listeners.transferCompleted(true);
			}
		}
		finally
		{
			if( file!=null )
			{
				try { file.close(); } catch( IOException e ) {}
				file = null;
			}
			stop();
		}
	}

	public boolean isNegotiated()
	{
		return this.isMakeConnectionCompleted;
	}

	public void send( String msg, boolean isUTF8 ) throws IOException
	{
		this.characterWriter = ByteBuffer.wrap(msg.getBytes(
			isUTF8 ? "UTF-8" : "CP949"));
		synchronized(lock)
		{
			lock.notify();
		}
		this.notified = true;
// System.out.println( "DEBUG: lock.notify()" );
	}

	public void sendFile( File f )
	{
		this.characterWriter = null;
		this.fileSend = f;
		synchronized(lock)
		{
			lock.notify();
		}
		this.notified = true;
//		System.out.println( "DEBUG: lock.notify()" );
	}

	public void storeFile( File f )
	{
		this.characterWriter = null;
		this.fileReceive = f;
		synchronized(lock)
		{
			lock.notify();
		}
		this.notified = true;
//		System.out.println( "DEBUG: lock.notify()" );
	}
}
