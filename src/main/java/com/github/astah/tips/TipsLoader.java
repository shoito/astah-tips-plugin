package com.github.astah.tips;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TipsLoader {
	private HttpClient client;
	
	public static void main(String[] args) {
		TipsLoader tipsLoader = new TipsLoader();
		List<String[]> tipsUrls = tipsLoader.getTipsLabelAndUrls();
		for (String[] tipsUrl : tipsUrls) {
			String[] titleAndContents = tipsLoader.getTitleAndContents(tipsUrl[1]);
			System.out.println("url:" + tipsUrl[1]);
			System.out.println("title:" + titleAndContents[0]);
			System.out.println("contents:" + titleAndContents[1]);
			System.out.println();
		}
	}
	
	public TipsLoader() {
		client = new DefaultHttpClient();
		HttpParams params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 3000);
		HttpConnectionParams.setSoTimeout(params, 3000);
	}
	
	private String doGet(String url) throws IOException {
		HttpUriRequest httpUriRequest = new HttpGet(url);
		HttpResponse response = client.execute(httpUriRequest);
		
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		response.getEntity().writeTo(byteArrayOutputStream);
		
		String responseText = "";
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			responseText = byteArrayOutputStream.toString();
		}
		
		byteArrayOutputStream.close();
		
		return responseText;
	}
	
	public List<String[]> getTipsLabelAndUrls() {
		String url = "http://pipes.yahoo.com/pipes/pipe.run?_id=f97cf0c1646bcc419baeab3e800285ef&_render=json";
		List<String[]> links = new ArrayList<String[]>();
		try {
			JSONObject result = new JSONObject(doGet(url));
			JSONArray items = result.getJSONObject("value").getJSONArray("items");
			for (int i = 0; i < items.length(); i++) {
				JSONObject link = items.getJSONObject(i);
				String[] labelAndUrl = new String[2];
				labelAndUrl[0] = link.getString("content");
				labelAndUrl[1] = link.getString("href");
				links.add(labelAndUrl);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return links;
	}
	
	public String[] getTitleAndContents(String tipsUrl) {
		String url = "http://pipes.yahoo.com/pipes/pipe.run?_id=731395b702185b0da0f101465e5f98d7&_render=json&url=" + tipsUrl;
		String[] titleAndContents = new String[2];
		try {
			JSONObject result = new JSONObject(doGet(url));
			JSONArray items = result.getJSONObject("value").getJSONArray("items");
			titleAndContents[0] = items.getJSONObject(0).getString("content");
			titleAndContents[1] = items.getJSONObject(1).getString("content");
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return titleAndContents;
	}
}
