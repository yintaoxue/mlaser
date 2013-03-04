package org.fossd.mlaser.api;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.buffer.DynamicChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders.Names;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.util.CharsetUtil;

public class LaserUpstreamHandler extends SimpleChannelUpstreamHandler {

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		HttpRequest request = (HttpRequest) e.getMessage();

		HttpMethod method = request.getMethod();
		System.out.println("method:" + method.getName());

		// Get
		if (method == HttpMethod.GET) {
			System.out.println("method:GET");
		}

		String uri = request.getUri();
		System.out.println("uri:" + uri);

		QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());

		String path = queryStringDecoder.getPath();
		System.out.println("path:" + path);

		Map<String, List<String>> params = queryStringDecoder.getParameters();
		if (!params.isEmpty()) {
			for (Entry<String, List<String>> p : params.entrySet()) {
				String key = p.getKey();
				List<String> vals = p.getValue();
				for (String val : vals) {
					System.out.println("get:" + key + "=" + val);
				}
			}
		}

		ChannelBuffer content = request.getContent();
		if (content.readable()) {
			String param = content.toString("UTF-8");
			QueryStringDecoder queryStringDecoder2 = new QueryStringDecoder("/?" + param);
			Map<String, List<String>> params2 = queryStringDecoder2.getParameters();
			if (!params2.isEmpty()) {
				for (Entry<String, List<String>> p : params2.entrySet()) {
					String key = p.getKey();
					List<String> vals = p.getValue();
					for (String val : vals) {
						System.out.println("post:" + key + "=" + val);
					}
				}
			}
		}

		HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		ChannelBuffer buffer = new DynamicChannelBuffer(2048);
		buffer.writeBytes("Hello,World!".getBytes("UTF8"));

		response.setContent(buffer);
		response.setHeader(Names.CONTENT_TYPE, "text/html; charset=UTF8");
		response.setHeader(Names.CONTENT_LENGTH, response.getContent().writerIndex());

		Channel ch = e.getChannel();
		// Write the initials line and the header
		ChannelFuture future = ch.write(response);
		future.addListener(ChannelFutureListener.CLOSE);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		Channel ch = e.getChannel();
		Throwable cause = e.getCause();
		if (cause instanceof TooLongFrameException) {
			sendError(ctx, HttpResponseStatus.BAD_REQUEST);
			return;
		}
		cause.printStackTrace();
		if (ch.isConnected()) {
			sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
		HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, status);
		response.setHeader(Names.CONTENT_TYPE, "text/plain; charset=UTF8");
		response.setContent(ChannelBuffers.copiedBuffer("Failure: " + status.toString() + "\r\n", CharsetUtil.UTF_8));

		// Close the connection as soon as the error message is sent.
		ctx.getChannel().write(response).addListener(ChannelFutureListener.CLOSE);
	}
}
