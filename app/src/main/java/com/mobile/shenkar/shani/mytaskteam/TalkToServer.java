package com.mobile.shenkar.shani.mytaskteam;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class TalkToServer {

//	private static ResponseHandler<String> GetResponseHandler()
//	{
//		 ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
//				@Override
//	        	public String handleResponse(final HttpResponse response)
//	                throws HttpResponseException, IOException {
//	                StatusLine statusLine = response.getStatusLine();
//	                if (statusLine.getStatusCode() >= 300) {
//	                    throw new HttpResponseException(statusLine.getStatusCode(),
//	                            statusLine.getReasonPhrase());
//	                }
//
//	                HttpEntity entity = response.getEntity();
//	                return entity == null ? null : EntityUtils.toString(entity, "UTF-8");
//	            }
//	        };
//	        return responseHandler;
//	}
//
	public static String PostToUrl(String p_url,String pHttpBody)
	{

		String Response = "";

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("json",pHttpBody));

			Response = makePOSTRequest(p_url, nameValuePairs);
		}
		catch (Exception ex)
		{
			Log.e("Server Error: ", ex.getMessage());
		}




       return Response;
	}



	public static String makePOSTRequest(String url, List<NameValuePair> nameValuePairs) {
		String response = "";

		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			response = EntityUtils.toString(httpEntity);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		Log.d(LOGTAG, "POST Response >>> " + response);
		return response;

	}
}
