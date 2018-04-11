package com.cuong.filemanage.FtpServer;

public interface OnDataTranferCompleteListener
{
	public void actionNegoatiated(boolean isOk);

	public void transferStarted();

	public void transferCompleted(boolean hasError);
}
