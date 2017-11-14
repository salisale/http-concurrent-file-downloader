
public class WebPageError {
	private static String[] errors = {"400=Bad Request", "401=Unauthorized",
			"402=Payment Required", "403=Forbidden",
			"404=Not Found", "405=Method Not Allowed",
			"406=Not Acceptable", "407=Proxy Authentication Required",
			"408=Request Time-out", "409=Conflict",
			"410=Gone", "415=Unsupported Media Type",
			"500=Internal Server Error", "501=Not Implemented",
			"502=Bad Gateway", "503=Service Unavailable",
			"504=Gatewat Time-out", "505=HTTP Version not supported"};
	public static String getErrorMessage(String respCode) {
		String errMessage = "ERROR: ";
		// concat error message
		for (String str: errors) {
			if (str.substring(0,3).equals(respCode)) {
				errMessage+=str.substring(4);
			}
		}
		return errMessage;
	}

}
