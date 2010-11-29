package kkckkc.jsourcepad.http;

import kkckkc.jsourcepad.util.Config;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.springframework.beans.factory.FactoryBean;

public class HttpServerFactoryBean implements FactoryBean<Context> {

	@Override
    public Context getObject() throws Exception {
        Server server = new Server(Config.getHttpPort());
        Context root = new Context(server, "/", Context.SESSIONS);
        server.start();

        return root;
/*
		InetSocketAddress addr = new InetSocketAddress(InetAddress.getByName(Config.getLocalhost()), Config.getHttpPort());

        System.out.println("addr = " + addr);

		HttpServer server = HttpServer.create(addr, 0);
		server.setExecutor(Executors.newCachedThreadPool());
		server.start();

        System.out.println("HttpServerFactoryBean.getObject");

		return server;*/
    }

	@Override
    public Class<?> getObjectType() {
	    return Context.class;
    }

	@Override
    public boolean isSingleton() {
	    return true;
    }

}
