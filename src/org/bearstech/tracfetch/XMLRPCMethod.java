package org.bearstech.tracfetch;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;
import org.xmlrpc.android.XMLRPCFault;

import android.os.Handler;
import android.util.Log;

/**
 * Inspired from android-xmlrpc Test source code.
 */

interface XMLRPCMethodCallback {
	void callFinished(Object result);
	void error(Exception e);
}

public class XMLRPCMethod extends Thread {
	private String method;
	private Object[] params;
	private Handler handler;
	private XMLRPCMethodCallback callBack;
	private XMLRPCClient client;

	public XMLRPCMethod(XMLRPCClient client, String method, XMLRPCMethodCallback callBack) {
		this.method = method;
		this.callBack = callBack;
		this.client = client;
		handler = new Handler();
	}

	public void call() {
		call(null);
	}

	public void call(Object[] params) {
		this.params = params;
		start();
	}

	@Override
	public void run() {
		try {
			//final long t0 = System.currentTimeMillis();
			final Object result = client.callEx(method, params);
			//final long t1 = System.currentTimeMillis();
			
			handler.post(new Runnable() {
				public void run() {
					callBack.callFinished(result);
				}
			});
			
		} catch (final XMLRPCFault e) {
			handler.post(new Runnable() {
				public void run() {
					Log.d("Test", "error", e);
				}
			});
		} catch (final XMLRPCException e) {
			handler.post(new Runnable() {
				public void run() {
					//Throwable cause = e.getCause();
					callBack.error(e);
				}
			});
		}
	}
}