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
