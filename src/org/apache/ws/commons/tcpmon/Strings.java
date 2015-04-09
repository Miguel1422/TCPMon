package org.apache.ws.commons.tcpmon;

public class Strings {
	private static String remoteHost = "127.0.0.1";
	private static String remotePort = "80";
	private static String localPort = "8080";
	private static boolean sslServerSelected = false;
	
	
	public static boolean isSslServerSelected() {
		return sslServerSelected;
	}
	public static void setSslServerSelected(boolean sslServer) {
		Strings.sslServerSelected = sslServer;
	}
	
	
	public static String getRemoteHost() {
		return remoteHost;
	}
	public static void setRemoteHost(String remoteHost) {
		Strings.remoteHost = remoteHost;
	}
	public static String getRemotePort() {
		return remotePort;
	}
	public static void setRemotePort(String remotePort) {
		Strings.remotePort = remotePort;
	}
	public static String getLocalPort() {
		return localPort;
	}
	public static void setLocalPort(String localPort) {
		Strings.localPort = localPort;
	}
	
	
}
