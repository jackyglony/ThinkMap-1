/*
 * Copyright 2014 Matthew Collins
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.thinkofdeath.thinkcraft.bukkit.web;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.CharsetUtil;
import uk.co.thinkofdeath.thinkcraft.bukkit.ThinkMapPlugin;

import java.net.URI;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaders.setContentLength;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HTTPHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final ThinkMapPlugin plugin;

    public HTTPHandler(ThinkMapPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest msg)
            throws Exception {
        httpRequest(ctx, msg);
    }

    public void httpRequest(ChannelHandlerContext context, FullHttpRequest request) throws Exception {
        if (!request.getDecoderResult().isSuccess()) {
            sendHttpResponse(context, request, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
            return;
        }

        URI uri = new URI(request.getUri());
        uri = uri.normalize();
        if (uri.getPath().contains("..")) {
            sendHttpResponse(context, request, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
            return;
        }

        plugin.getWebHandler().getEndPointManager().handle(context, uri, request);
    }

    public static void sendHttpResponse(ChannelHandlerContext context, FullHttpRequest request, FullHttpResponse response) {
        if (response.getStatus().code() != 200) {
            response.content().writeBytes(response.getStatus().toString().getBytes(CharsetUtil.UTF_8));
        }
        setContentLength(response, response.content().readableBytes());

        if (isKeepAlive(request)) {
            response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }

        ChannelFuture future = context.writeAndFlush(response);
        if (!isKeepAlive(request) || response.getStatus().code() != 200) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }
}
