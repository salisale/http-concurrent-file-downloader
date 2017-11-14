import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

public class HTTPRequest {
	private String mURL;
	private static String mHost;
	private static String mPath;
	private static String mIP;
	private static int mPort;
	private static BufferedWriter bw;
	private static BufferedReader br;
	private Socket socket;
	
	public HTTPRequest(String mURL) {
		this.mURL = mURL;
		initVar();
	}
	
	private void initVar() {
		URI uri = null;
		try {
			uri = new URI(mURL);
			mHost = uri.getHost(); 
			mPath = uri.getRawPath(); 
			mPort = uri.getPort()==-1? 80 : uri.getPort();
			System.out.println(mHost + " " + mPath + " " + mPort);
			if (mPath == null || mPath.length() == 0) {
			    mPath = "/";
			} 
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} 
		InetAddress addr = null;
		try {
			addr = InetAddress.getByName(mHost);
		} catch (UnknownHostException e1) {
			System.out.println(e1.getMessage());
		}		
		// addr returns www.example.com/192.155.48.108
		mIP = addr.toString().split("/")[1];
		
	}	
	/**
	 * @return Header Response
	 */
	public String sendRequest() {
		
		// make connection
		try {
			socket = new Socket(mIP, mPort);
		} catch (UnknownHostException e1) {
			System.out.println(e1.getMessage());
		} catch (IOException e1) {
			System.out.println(e1.getMessage());
		}
		// send request
		StringBuilder sb = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
			bw.write(getRequestMessage());
			bw.flush();
			
			// get response header
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			sb = new StringBuilder();
		    String line;
		    while((line=br.readLine())!=null && line.length()!=0) { // not empty line
		    	sb.append(line).append("\n");
		    }
		    
		    br.close();
		    socket.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	    return sb.toString();	
	}
	public String getIP() {
		return mIP;
	}
	public int getPort() {
		return mPort;
	}
	public String getRequestMessage() {
		return "GET " + mPath + " HTTP/1.1\r\n" +
				"Host: " + mHost + "\r\nConnection: close" + "\r\n\r\n";
	}

}
