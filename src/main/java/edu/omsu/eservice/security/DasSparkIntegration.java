package edu.omsu.eservice.security;

import edu.omsu.eservice.security.DasOAuth2Client.InsufficientScopeException;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.pac4j.core.config.Config;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.sparkjava.CallbackRoute;
import org.pac4j.sparkjava.DefaultHttpActionAdapter;
import org.pac4j.sparkjava.SecurityFilter;
import org.pac4j.sparkjava.SparkWebContext;
import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.embeddedserver.EmbeddedServer;
import spark.embeddedserver.EmbeddedServerFactory;
import spark.embeddedserver.EmbeddedServers;
import spark.embeddedserver.jetty.EmbeddedJettyServer;
import spark.embeddedserver.jetty.JettyHandler;
import spark.embeddedserver.jetty.JettyServerFactory;
import spark.http.matching.MatcherFilter;
import spark.route.Routes;
import spark.staticfiles.StaticFilesConfiguration;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static spark.Spark.*;

public class DasSparkIntegration {
    static class LogoutRoute implements Route {
        final String logoutRedirectUrl;

        public LogoutRoute(String logoutRedirectUrl) {
            this.logoutRedirectUrl = logoutRedirectUrl;
        }

        @Override
        public Object handle(Request req, Response resp) throws Exception {
            SparkWebContext context = new SparkWebContext(req, resp);
            ProfileManager manager = new ProfileManager(context);
            manager.logout();

            if (req.headers("X-Remote-Logout") == null) {
                /* Local logout initiated by user via
                 in-app logout button click */
                resp.redirect(logoutRedirectUrl);
                return "";
            }

            /* Remote logout via crossdomain HTTP
            initiated by DAS */
            return "";
        }
    }

    static class ProbeAuthRoute implements Route {
        @Override
        public String handle(Request request, Response response) throws Exception {
            final SparkWebContext context = new SparkWebContext(request, response);

            if (new ProfileManager(context).isAuthenticated() == false)
                halt(SC_FORBIDDEN);

            return "";
        }
    }

    static class CustomJettyServerFactory implements JettyServerFactory {
        @Override
        public Server create(int maxThreads, int minThreads, int threadTimeoutMillis) {
            if (maxThreads > 0) {
                int max = maxThreads;
                int min = (minThreads > 0) ? minThreads : 8;
                int idleTimeout = (threadTimeoutMillis > 0) ? threadTimeoutMillis : 60000;
                return new Server(new QueuedThreadPool(max, min, idleTimeout));
            } else
                return new Server();
        }

        @Override
        public Server create(ThreadPool threadPool) {
            if (threadPool != null)
                return new Server(threadPool);
            else
                return new Server();
        }
    }

    static class CustomJettyFactory implements EmbeddedServerFactory {
        JettyServerFactory serverFactory;
        String appRoot;

        CustomJettyFactory(String appRoot) {
            this.serverFactory = new CustomJettyServerFactory();
            this.appRoot = appRoot;
        }

        @Override
        public EmbeddedServer create(Routes routeMatcher, StaticFilesConfiguration staticFilesConfiguration, boolean hasMultipleHandler) {
            MatcherFilter matcherFilter = new MatcherFilter(routeMatcher, staticFilesConfiguration, false, hasMultipleHandler);
            matcherFilter.init(null);
            JettyHandler handler = new JettyHandler(matcherFilter);
            handler.getSessionCookieConfig().setPath(appRoot);
            return new EmbeddedJettyServer(serverFactory, handler);
        }

    }


    static final String AUTHORIZER_NAME = "student";
    static private SecurityFilter securityFilter = null;

    static public void enableSecurityFilter(String fullPath) {
        if (securityFilter != null) {
            before(fullPath, securityFilter);
        }
    }

    static public void init(String appRoot, String appUrl, String dasHost, String dasServerUri, DasOAuth2Client client) {
        EmbeddedServers.add(EmbeddedServers.defaultIdentifier(), new CustomJettyFactory(appRoot));

        Config config = new Config(appRoot + "/j_oauth_check", client);
        config.setHttpActionAdapter(new DefaultHttpActionAdapter()); // ???
        config.addAuthorizer(AUTHORIZER_NAME, client.profileAuthorizer);
        securityFilter = new SecurityFilter(config, "DasOAuth2Client", AUTHORIZER_NAME);
        before(appRoot + "/", securityFilter);
        get(appRoot + "/", (Request req, Response res) -> {
            if (!res.raw().isCommitted())
                res.redirect(appUrl);
            return null;
        });

        Filter corsFilter = (req, resp) -> {
            resp.header("Access-Control-Allow-Origin", dasHost);
            resp.header("Access-Control-Allow-Credentials", "true");
            if ("OPTIONS".equals(req.requestMethod())) {
                resp.header("Access-Control-Max-Age", "3600");
                resp.header("Access-Control-Allow-Methods", "GET");
            }
        };

        before(appRoot + "/auth/probe_auth", corsFilter);
        get(appRoot + "/auth/probe_auth", new ProbeAuthRoute());

        before(appRoot + "/j_xdomain_logout", corsFilter);
        get(appRoot + "/j_xdomain_logout", new LogoutRoute(dasServerUri + "/logout.do"));

        CallbackRoute callBackRoute = new CallbackRoute(config);
        get(appRoot + "/j_oauth_check", callBackRoute);
        post(appRoot + "/j_oauth_check", callBackRoute);

        exception(InsufficientScopeException.class, (Exception ex, Request request, Response response) -> {
            if (!response.raw().isCommitted())
                response.redirect(((InsufficientScopeException) ex).forwardUri);
        });
    }
}