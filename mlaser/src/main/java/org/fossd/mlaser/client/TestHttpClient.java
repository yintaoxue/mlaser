package org.fossd.mlaser.client;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

public class TestHttpClient {

	public static void main(String[] args) {
		// testGet();
		testPost();
	}

	public static void testGet() {
		HttpClient client = new HttpClient();
		// 设置代理服务器地址和端口
		// client.getHostConfiguration().setProxy("proxy_host_addr",proxy_port);
		// 使用GET方法，如果服务器需要通过HTTPS连接，那只需要将下面URL中的http换成https
		HttpMethod method = new GetMethod("http://localhost:8080/test?id=123&say=hello");
		// 使用POST方法
		// HttpMethod method = new PostMethod("http://java.sun.com";);
		try {
			client.executeMethod(method);
			// 打印服务器返回的状态
			System.out.println(method.getStatusLine());
			// 打印返回的信息
			System.out.println(method.getResponseBodyAsString());
			// 释放连接
			method.releaseConnection();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void testPost() {
		HttpClient client = new HttpClient();
		client.getHostConfiguration().setHost("localhost", 8080, "http");
		// 使用POST方式提交数据
		HttpMethod method = getPostMethod();
		try {
			client.executeMethod(method);
			// 打印服务器返回的状态
			System.out.println(method.getStatusLine());
			// 打印结果页面
			String response = new String(method.getResponseBodyAsString().getBytes("UTF-8"));
			// 打印返回的信息
			System.out.println(response);
			method.releaseConnection();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 使用POST方式提交数据
	 * 
	 * @return
	 */
	private static HttpMethod getPostMethod() {
		PostMethod post = new PostMethod("/create/feed");
		NameValuePair nameValue1 = new NameValuePair("title", "My Feed");
		NameValuePair nameValue2 = new NameValuePair("content", "My Feed Content.");
		post.setRequestBody(new NameValuePair[] { nameValue1, nameValue2 });
		return post;
	}

}
