package io.electrum.pputestserver;

import io.electrum.pputestserver.backend.MockResponseTemplates;
import io.electrum.pputestserver.utils.Utils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

public class PpuTestServer extends ResourceConfig {

   private static final Logger logger = LoggerFactory.getLogger(PpuTestServer.class);

   public PpuTestServer()
         throws IOException, ClassNotFoundException, NoSuchMethodException, InstantiationException,
         IllegalAccessException, InvocationTargetException {
      JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
      provider.setMapper(Utils.getObjectMapper());

      register(provider);

      logger.info("Loading packages...");
      packages(PpuTestServer.class.getPackage().getName());

      logger.info("initializing backend...");
      MockResponseTemplates.init();
   }
}
