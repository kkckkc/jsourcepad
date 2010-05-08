package kkckkc.jsourcepad.http;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.FactoryBean;
import com.sun.net.httpserver.*;

public class HttpServerFactoryBean implements FactoryBean<HttpServer> {

	@Override
    public HttpServer getObject() throws Exception {
		InetSocketAddress addr = new InetSocketAddress(8080);
		HttpServer server = HttpServer.create(addr, 0);
		server.setExecutor(Executors.newCachedThreadPool());
		server.start();
		
		return server;
    }

	@Override
    public Class<?> getObjectType() {
	    return HttpServer.class;
    }

	@Override
    public boolean isSingleton() {
	    return true;
    }

}
