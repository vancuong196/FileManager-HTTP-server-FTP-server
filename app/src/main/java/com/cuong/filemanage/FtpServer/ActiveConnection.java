package com.cuong.filemanage.FtpServer;

import java.io.IOException;
import java.nio.channels.SocketChannel;

class ActiveConnection extends DataConnection
{
	ActiveConnection() throws IOException
	{

	}
	
	protected void makeDataTranferConnection() throws IOException
	{
		channel = SocketChannel.open();
		channel.configureBlocking(true);
		channel.connect( this.addr );
                
	}
}
