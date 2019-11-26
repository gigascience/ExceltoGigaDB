import java.net.HttpURLConnection;
import java.net.URL;


public class registerdoi {
	
	public static void main(String[] args) {

		URL url = null;
	    HttpURLConnection httpurlconnection = null;
	    try
	    {
	      url = new URL("http://mds.datacite.org/doi");

	      httpurlconnection = (HttpURLConnection) url.openConnection();
	      httpurlconnection.setDoOutput(true);
	      httpurlconnection.setRequestMethod("POST");
	      httpurlconnection.setRequestProperty("Content-type","text/plain");
	      httpurlconnection.setRequestProperty("Charset",   "UTF-8");
	      String username="username=02000001";
	      httpurlconnection.getOutputStream().write(username.getBytes());
	      httpurlconnection.getOutputStream().flush();
	      httpurlconnection.getOutputStream().close();
	      int code = httpurlconnection.getResponseCode();
	      System.out.println("code   " + code);

	    }
	    catch(Exception e)
	    {
	      e.printStackTrace();
	    }
	    finally
	    {
	      if(httpurlconnection!=null)
	        httpurlconnection.disconnect();
	    }


	       
	       
		
	}
}
