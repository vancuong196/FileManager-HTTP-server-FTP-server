package com.cuong.filemanage.FtpServer;

public interface OnDataTranferCompletedListener
{
	public void actionNegoatiated(boolean isOk);

	public void transferStarted();

	public void transferCompleted(boolean hasError);
}
