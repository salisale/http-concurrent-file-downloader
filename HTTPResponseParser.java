
public class HTTPResponseParser {
	private String response;
	private boolean fatalError = false;
	private boolean redirect = false;
	
	public HTTPResponseParser(String response) {
		this.response = response;
		String respCode = getResponseCode();
		
		switch (respCode.charAt(0)) {
			case '2': // okay
				break;
			case '4':	// not found
				fatalError = true;
				break;
			case '5':
				fatalError = true;
				break;
			case '3': // redirected
				redirect = true;
				break;
			default: // what about 1?
				break;
		}
		
	}
	public String getResponseCode() {
		return response.substring(9,12);
	}
	/**
	 * @return 0 if the message does not contain content-length
	 */
	public int getContentLength() {
		if (!response.contains("Content-Length")) {
			return 0;
		}
		String len = response.split("Content-Length: ")[1].split("\n")[0];
		return Integer.valueOf(len.trim());
	}
	public boolean getResumePermission() {
		return checkAcceptRanges();
	}
	public boolean getMultiDownloadServerPermission() {
		return checkAcceptRanges();
	}
	private boolean checkAcceptRanges() {
		return response.split("Accept-Ranges: ")[1].startsWith("bytes");
	}
	public String getRedirectedLocation() {
		return response.split("Location: ")[1].split("\n")[0];
	}
	public String getDateModified() {
		return response.split("Last-Modified: ")[1].split("\n")[0];
	}
	public boolean webPageError() {
		return fatalError;
	}
	public boolean webPageRedirect() {
		return redirect;
	}
}
