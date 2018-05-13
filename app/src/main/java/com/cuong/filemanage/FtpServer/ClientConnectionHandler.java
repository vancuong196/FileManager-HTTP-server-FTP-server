package com.cuong.filemanage.FtpServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

public class ClientConnectionHandler implements Runnable, OnDataTranferCompletedListener
{
	private Socket channel;
	private Thread thread = null;
	private BufferedReader reader = null;
        PrintStream out;
	private String username;
	private boolean isAuth = false;
	private boolean isAlive = true;
	@SuppressWarnings("unused")
	private boolean isBinaryStream = true;
	private boolean isUTF8Enable = true;
	private long restart = 0L;
	private DataConnection data = null;
	@SuppressWarnings("unused")
	private InetSocketAddress port = null;

	private File userRoot = null;
	private File userCurrentWokingDirectory = null;

	private SimpleDateFormat fmtDate = new SimpleDateFormat("MMM dd HH:mm", Locale.ENGLISH);
	private SimpleDateFormat fmtPast = new SimpleDateFormat("MMM dd  yyyy", Locale.ENGLISH);
	private SimpleDateFormat fmtStamp = new SimpleDateFormat("yyyyMMddHHmmss");

	public ClientConnectionHandler( Socket channel ) throws IOException
	{
		this.channel = channel;
		//this.reader = new Scanner(channel, "UTF-8");
                reader = new BufferedReader(new InputStreamReader(channel.getInputStream()),1);
                out = new PrintStream(channel.getOutputStream());
		System.out.println( "* FTPChannel was created. (" + channel.getPort() + ")" );
		onConnect();
	}

	public void start()
	{
		if( thread==null )
		{
			thread = new Thread(this);
			thread.start();
		}
	}

	public void println( String msg ) throws IOException
	{
			out.println(msg);
                        System.out.println("<= "+msg);
	}

	// send welcom messsage to establish the conection between client and server
	protected void onConnect() throws IOException
	{
		println( "220 Welcome to FPT server" );
	}
        // Listen for client command as long as it still connect to server 
	public void run()
	{
		try
		{
			while(isAlive)
			{
				String line = reader.readLine();
				if( line==null )
					break;

				System.out.println( "=> " + line );

				String cmd = null;
				String param = null;

				int i0 = line.indexOf(' ');
				if( i0!=-1 )
				{
					cmd = line.substring(0, i0);
					param = line.substring(i0).trim();
				}
				else
				{
					cmd = line;
				}
				
				processCommand( cmd, param );
			}
		}
		catch( NoSuchElementException e )
		{
			System.out.println( "* FTPChannel was closed. (" + 
				channel.getInetAddress() + ")" );
		}
		catch( Exception e )
		{
			System.err.println( "* FTP Channel error occured" );
			e.printStackTrace();
		}
		finally
		{
			stop();
		}
	}
// Handle the command by command name and its parameter
	protected void processCommand( String cmd, String param )
		throws Exception
	{
		cmd = cmd.toUpperCase();
		if( cmd.equals("USER") )
		{
			processUser(param);
		}
		else
		if( cmd.equals("PASS") )
		{
			processPassword(param);
		}
		else
		if( cmd.equals("AUTH") )
		{
			processSecurityExtension(param);
		}
		else
		if( cmd.equals("SYST") )
		{
			processSystem();
		}
		else
		if( cmd.equals("EPRT") && isLogin() ) 
		{
			processPortExtensionCommand(param);
		}
		else
		if( cmd.equals("PORT") && isLogin() )
		{
			processPortCommand(param);
		}
		else
		if( cmd.equals("PASV") && isLogin() )
		{
			processPassive();
		}
		else
		if( (cmd.equals("PWD") || cmd.equals("XPWD")) && isLogin() )
		{
			processPrintWorkingDirectory();
		}
		else
		if( cmd.equals("CWD") && isLogin() )
		{
			processChangeWorkingDirectory(param);
		}
		else
		if( cmd.equals("TYPE") )
		{
			processType(param);
		}
		else
		if( cmd.equals("OPTS") && isLogin() )
		{
			processOptions(param);
		}
		else
		if( cmd.equals("LIST") && isLogin() )
		{
			processList();
		}
		else
		if( cmd.equals("NLST") && isLogin() ) 
		{
			processNameList();
		}
		else
		if( cmd.equals("RETR") && isLogin() )
		{
			processRetrieve(param);
		}
		else
		if( cmd.equals("STOR") && isLogin() )
		{
			processStore(param);
		}
		else
		if( cmd.equals("SIZE") && isLogin() )
		{
			processFileSize(param);
		}
		else
		if( cmd.equals("MDTM") && isLogin() ) 
		{
			processFileModifiedTime(param);
		}
		else
		if( cmd.equals("REST") && isLogin() ) 
		{
			processFileRestart(param);
		}
		else
		if( cmd.equals("NOOP") )
		{
			println( "200 NOOP command successful." );
		}
		else
		if( (cmd.equals("MKD") || cmd.equals("XMKD")) && isLogin() )
		{
			makeDirectoryRespond(param);
		}
		else
		if( (cmd.equals("RMD") || cmd.equals("XRMD")) && isLogin() )
		{
			deleteDirectoryRespond(cmd, param);
		}
		else
		if( cmd.equals("DELE") && isLogin() )
		{
			deleteFileRespond(cmd, param);
		}
		else
		if( cmd.equals("CDUP") && isLogin() )
		{
			processChangeDirectoryToParent();
		}
		else
		if( cmd.equals("QUIT") )
		{
			processQuit();
		}
		else
		{
			println( "500 " + cmd + " not understood." );
		}
	}

	protected void processSecurityExtension( String param ) throws Exception
	{

		{
			println( "502 " + param + " did not implemented." );
		}
	}
// check if the user is auth().
	private boolean isLogin() throws IOException
	{
		if( !isAuth )
		{
			println( "530 Not logged in." );
			return false;
		}
		return true;
	}
// 
	protected void processUser( String username ) throws Exception
	{

		this.username = username;
                if (username.equals("anonymous")) {
                    println( "331 Guest login ok. Send indent as password");
                    return;
                }
		println( "331 Password required for " + username );
	}

	protected void processPassword( String password ) throws Exception
	{
		if( username==null )
		{
			println( "503 Bad sequence of commands. Send USER first." );
			return;
		}
                if (username=="anonymous")

                this.isAuth = true;
                

		{

			String privateRoot = null;
			if( privateRoot==null )
				userRoot = new File("/storage/emulated/0");

			if( !userRoot.exists() )
				userRoot.mkdirs();
			userCurrentWokingDirectory = userRoot;
			println( "230 User " + username + " logged in." );
		}
                if (username !="anonymous"){
                    isAuth = true;
                }
	}

	protected void processSystem() throws Exception
	{
		println( "215 UNIX Type: L8" );
	}

	protected void processOptions( String param ) throws Exception
	{
		String[] params = param.split(" ");
		if( params.length > 1 && params[0].equalsIgnoreCase("UTF8") )
		{
			String flag = params[1].toUpperCase();
			if( flag.equals("YES") || flag.equals("TRUE") || flag.equals("ON") )
				isUTF8Enable = true;
			else
				isUTF8Enable = false;

			println( "200 OPTS UTF8 command successful." );
		}
		else
		{
			println( "501 Syntax error in parameters or arguments." );
		}
	}

	protected void processPortExtensionCommand( String param ) throws Exception 
	{

		String[] params = param.split("\\|");
		if( data!=null ) 
		{
			data.stop();
			data = null;
		}

		this.restart = 0L;
		InetAddress addr = null;
		int port = 0;

		if( params[1].equals("1") ) // IPv4
			addr = InetAddress.getAllByName(params[2])[0];
		if( params[1].equals("2") ) // IPv6
			addr = InetAddress.getAllByName(params[2])[0];
		port = Integer.parseInt(params[3]);

		InetSocketAddress sock = null;
		try 
		{
			sock = new InetSocketAddress(addr, port);
			println( "200 EPRT command successful." );
			this.data = DataConnection.createActive(sock);
			this.data.setFileOffset(restart);
			this.data.addDataConnectionListener(this);
			this.data.start();
		}
		catch( Exception e )
		{
			e.printStackTrace();
			println( "500 Invalid port format." );
		}

	}

	protected void processPortCommand( String param ) throws Exception
	{
		String[] ports = param.split(",");
		if( data!=null )
		{
			data.stop();
			data = null;
		}

		this.restart = 0L;

		InetSocketAddress addr = null;
		try
		{
			addr = new InetSocketAddress(
			ports[0] + "." + ports[1] + "." + ports[2] + "." + ports[3],
			Integer.parseInt(ports[4]) * 256 + 
			Integer.parseInt(ports[5]) );

			println( "200 PORT command successful." );

			this.data = DataConnection.createActive(addr);
			this.data.setFileOffset(restart);
			this.data.addDataConnectionListener(this);
			this.data.start();
		}
		catch( Exception e )
		{
			e.printStackTrace();
			println( "500 Invalid port format." );
		}
	}
	
	protected void processPassive() throws Exception

	{
		//println( "502 did not implemented." );

		if( data!=null )
		{
			data.stop();
			data = null;
		}

		this.restart = 0L;

		data = DataConnection.createPassive();
		data.setFileOffset(restart);
		data.addDataConnectionListener(this);
		data.start();
		println( "227 Entering Passive Mode (" + data.getAddressAsString() + ")" );

	}

	protected void processPrintWorkingDirectory() throws Exception
	{
		String root = userRoot.getAbsolutePath();
		String curr = userCurrentWokingDirectory.getAbsolutePath();

		curr = curr.substring(root.length());
		if( curr.length()==0 ) {
			curr = "/";
		}
		println( "257 \"" + curr + "\" is current directory." );
	}

	protected void processChangeWorkingDirectory( String param )
		throws Exception
	{
		File toChange = null;
		if( param.length() > 0 && param.charAt(0)=='/' )
		{
			toChange = new File(userRoot, param.substring(1));
		}
		else
		{
			toChange = new File(userCurrentWokingDirectory, param);
		}

		if( !toChange.exists() || !toChange.isDirectory() )
		{
			println( "550 " + param + ": No such file or directory" );
			return;
		}

		String root = userRoot.getAbsolutePath();
		String willChange = toChange.getCanonicalPath();
		if( !willChange.startsWith(root) )
		{
			println( "553 Requested action not taken." );
			return;
		}

		this.userCurrentWokingDirectory = new File(willChange);
		println( "250 CWD command successful" );
	}

	protected void processNameList() throws Exception 
	{
		File[] files = userCurrentWokingDirectory.listFiles();
		StringBuilder sb = new StringBuilder();
		for(File f : files) 
			sb.append(f.getName()).append("\r\n");

		if( data!=null )
		{
			println( "150 Opening ASCII mode data connection for file list" );
			data.send(sb.toString(), isUTF8Enable);
		}
		else
		{
			println( "552 Requested file list action aborted." );
		}

	}

	protected void processList() throws Exception
	{
		File[] files = userCurrentWokingDirectory.listFiles();
		StringBuilder sb = new StringBuilder();

		Calendar cal = Calendar.getInstance();
		int currentYear = cal.get(Calendar.YEAR);

		List<File> list = new ArrayList<File>(files.length);	
		for(File f : files)
			list.add(f);

		Collections.sort(list, new Comparator<File>() {
			public int compare( File f0, File f1 )
			{
				return f0.getName().compareTo(f1.getName());	
			}
		});

		for(File f : list)
		{
			if( f.isDirectory() )
			{
				sb.append( "drwxr-xr-x" );
			}
			else
			if( f.isFile() )
			{
				sb.append( "-rw-r--r--" );
			}
			else
				continue;
			sb.append( ' ' );
			sb.append( String.format("%3d", 1) );
			sb.append( ' ' );
			sb.append( String.format("%-8s", this.username) );
			sb.append( ' ' );
			sb.append( String.format("%-8s", this.username) );
			sb.append( ' ' );
			long len = f.length();
			if( f.isDirectory() )
				len = 4096;
			sb.append( String.format("%8d", len) );
			sb.append( ' ' );

			cal.setTimeInMillis(f.lastModified());
			if( cal.get(Calendar.YEAR)==currentYear )
			{
				sb.append( fmtDate.format(cal.getTime()) );
			}
			else
			{
				sb.append( fmtPast.format(cal.getTime()) );
			}
			sb.append( ' ' );
			sb.append( f.getName() );
			sb.append( "\r\n" );
		}


		if( data!=null )
		{
			println( "150 Opening ASCII mode data connection for file list" );
			data.send(sb.toString(), isUTF8Enable);
		}
		else
		{
			println( "552 Requested file list action aborted." );
		}
	}

	protected void processRetrieve( String param ) throws Exception
	{
		File f = null;
		if( param.charAt(0)=='/' )
			f = new File(userRoot, param);
		else
			f = new File(userCurrentWokingDirectory, param);

		if( !f.exists() )
		{
			println( "550 " + param + ": No such file or directory" );	
			if( data!=null )
				data.stop();
			return;
		}

		if( data!=null )
		{
			println( "150 Opening BINARY mode data connection for " +
				param + " (" + f.length() + " bytes)" );
			data.sendFile(f);	
		}
		else
		{
			println( "552 Requested file action aborted." );
		}
	}

	protected void processStore( String param ) throws Exception
	{
		File f = new File(userCurrentWokingDirectory, param);

		if( data!=null )
		{
			println( "150 Opening BINARY mode data connection for " + param );
			data.storeFile(f);	
		}
		else
		{
			println( "552 Requested file action aborted." );
		}
	}

	protected void processFileSize( String param ) throws Exception
	{
		File f = null;
		if( param.charAt(0)=='/' )
			f = new File(userRoot, param);
		else
			f = new File(userCurrentWokingDirectory, param);

		if( f.exists() )
		{
			println( "213 " + f.length() );
		}
		else
		{
			println( "550 " + param + ": No such file or directory" );
		}
	}

	protected void processFileModifiedTime( String param ) throws Exception
	{
		File f = new File(userCurrentWokingDirectory, param);
		if( f.exists() )
		{
			println( "213 " + fmtStamp.format(f.lastModified()) );
		}
		else
		{
			println( "550 " + param + ": No such file or directory" );
		}
	}

	protected void processType( String param ) throws Exception
	{
		param = param.toUpperCase();
		if( param.equals("I") )
		{
			isBinaryStream = true;
		}
		else
		if( param.equals("A") )
		{
			isBinaryStream = false;
		}
		else
		{
			println( "504 Command not implemented for that parameter." );
			return;
		}

		println( "200 Type set to " + param );
	}

	protected void processFileRestart( String strOffset ) throws Exception
	{
		long offset = Long.parseLong(strOffset);
		this.restart = offset;
		if( data!=null )
		{
			data.setFileOffset(offset);
		}

		println( "350 Restarting at " + offset + ". Send STORE or RETRIEVE to initiate transfer");
	}

	private String getUserPath( File f ) throws IOException
	{

		String path = f.getPath();
		if( path.charAt(0)!='/' )
			path = '/' + path;
		return path;
	}

	protected void makeDirectoryRespond( String param ) throws Exception
	{
		File f = null;
		if( param.charAt(0)=='/' )
			f = new File(userRoot, param);
		else
			f = new File(userCurrentWokingDirectory, param);

		if( f.exists() )
		{
			println( "521 Directory already exists." );
			return;
		}

		if( f.mkdir() )
		{
			println( "257 \"" + getUserPath(f) + "\" - Directory successfully created.");	
		}
		else
		{
			println( "521 Making directory was failed." );
		}
	}

	protected void deleteDirectoryRespond( String cmd, String param ) 
		throws Exception
	{
		File f = null;
		if( param.charAt(0)=='/' )
			f = new File(userRoot, param);
		else
			f = new File(userCurrentWokingDirectory, param);

		if( !f.exists() )
		{
			println( "521 " + param + ": No such directory." );
			return;
		}

		if( f.isDirectory() && f.delete() )
		{
			println( "250 " + cmd + " command successful." );
		}
		else
		{
			println( "521 Removing directory was failed." );
		}
	}

	protected void deleteFileRespond( String cmd, String param )
		throws IOException
	{
		File f = null;
		if( param.charAt(0)=='/' )
			f = new File(userRoot, param);
		else
			f = new File(userCurrentWokingDirectory, param);

		if( !f.exists() )
		{
			println( "521 " + param + ": No such directory." );
			return;
		}

		if( f.isFile() && f.delete() )
		{
			println( "250 " + cmd + " command successful." );
		}
		else
		{
			println( "521 Error while removing file." );
		}
	}

	protected void processChangeDirectoryToParent() throws Exception
	{
		processChangeWorkingDirectory("..");
	}

	protected void processQuit() throws Exception
	{
		println( "221 Goodbye." );
		isAlive = false;
	}

	public void actionNegoatiated( boolean isOk )
	{
		System.out.println( "* Event: actionNegotiated: " + isOk );
	}


	public void transferStarted()
	{
		System.out.println( "* Event: transferStarted" );
	}

	
	public void transferCompleted( boolean hasError )
	{
		System.out.println("* Event: transferCompleted: hasError=" + hasError);

		try
		{
			if( !hasError )
				println( "226 Transfer complete." );
		}
		catch( IOException e )
		{
			e.printStackTrace();	
		}
	}

	public void stop()
	{
		if( thread!=null )
		{
			thread.interrupt();
			thread = null;
		}

		if( channel!=null )
		{
			try
			{
				channel.close();
			}
			catch( IOException e ) {}
			channel = null;
		}
                ClientConnectionManager.getInstance().remove(this);
	}
}
