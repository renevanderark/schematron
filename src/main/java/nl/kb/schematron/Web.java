package nl.kb.schematron;


import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import nl.kb.schematron.web.SchemaResource;
import nl.kb.schematron.web.WebConf;

public class Web extends Application<WebConf> {

  public static void main(String[] args) throws Exception {
    new Web().run(args);
  }

  @Override
  public void run(WebConf webConf, Environment environment) throws Exception {
    environment.jersey().register(new SchemaResource());
  }
}
