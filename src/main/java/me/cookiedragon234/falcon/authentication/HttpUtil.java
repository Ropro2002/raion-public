package me.cookiedragon234.falcon.authentication;

import com.google.gson.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URI;

public class HttpUtil {
	@Nullable
	public static String makePostRequest(URI url, JsonObject payload) throws IOException, ParseException {
		HttpClient httpClient = HttpClientBuilder.create().build();
		
		try {
			HttpPost request = new HttpPost(url);
			request.addHeader("content-type", "application/x-www-form-urlencoded");
			
			StringEntity params = new StringEntity("content=" + payload.toString());
			request.setEntity(params);
			
			HttpResponse response = httpClient.execute(request);
			
			String output = EntityUtils.toString(response.getEntity());
			
			EntityUtils.consume(response.getEntity());
			httpClient.getConnectionManager().shutdown();
			
			return output;
		}
		catch (Exception e) {
			httpClient.getConnectionManager().shutdown();
			throw e;
		}
	}
}
