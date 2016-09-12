package io.electrum.pputestserver;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.LoggerFactory;

public class PpuTestServerRunner {

   private static org.slf4j.Logger logger = LoggerFactory.getLogger(PpuTestServerRunner.class);

   public static void main(String[] args) throws Exception {
      // TODO: add some funky ascii art
      logger.info("--- STARTING PREPAID UTILITY TEST SERVER ---");

      try {
         start(args[0]);
      } catch (Exception e) {
         logger.error("--- STARTUP FAILED ---", e);
         throw e;
      }
   }

   private static void start(String port) throws Exception {

      // === jetty.xml ===
      // Setup Threadpool
      QueuedThreadPool threadPool = new QueuedThreadPool();
      threadPool.setMaxThreads(500);

      // Server
      Server server = new Server(threadPool);

      // Scheduler
      server.addBean(new ScheduledExecutorScheduler());

      // HTTP Configuration
      HttpConfiguration httpConfig = new HttpConfiguration();
      httpConfig.setSecureScheme("https");
      httpConfig.setSecurePort(Integer.parseInt(port));
      httpConfig.setOutputBufferSize(32768);
      httpConfig.setRequestHeaderSize(8192);
      httpConfig.setResponseHeaderSize(8192);
      httpConfig.setSendServerVersion(true);
      httpConfig.setSendDateHeader(false);

      // Handler Structure
      HandlerCollection handlers = new HandlerCollection();
      ContextHandlerCollection contexts = new ContextHandlerCollection();
      handlers.setHandlers(new Handler[] { contexts, new DefaultHandler() });
      server.setHandler(handlers);

      // Extra options
      server.setDumpAfterStart(false);
      server.setDumpBeforeStop(false);
      server.setStopAtShutdown(true);

      // === jetty-http.xml ===
      ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
      http.setPort(Integer.parseInt(port));
      http.setIdleTimeout(30000);
      server.addConnector(http);

      PpuTestServer testServer = new PpuTestServer();
      ServletContainer servletContainer = new ServletContainer(testServer);
      ServletHolder servletHolder = new ServletHolder(servletContainer);

      ServletContextHandler context = new ServletContextHandler();
      context.setContextPath("/");
      context.addServlet(servletHolder, "/*");

      server.setHandler(context);

      // Start the server
      server.start();
      server.join();
   }

}
