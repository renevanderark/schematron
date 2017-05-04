package nl.kb.schematron.web;

import nl.kb.schematron.SchematronResult;
import nl.kb.schematron.SchematronValidator;
import org.xml.sax.SAXException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Path("/schema")
public class SchemaResource {

  private static final Map<UUID, SchematronValidator> validators = new HashMap<>();

  @POST
  @Produces("text/plain")
  @Consumes("text/xml")
  @Path("/upload")
  public String upload(InputStream sch) throws TransformerException, IOException {
    final SchematronValidator schematronValidator = new SchematronValidator(new StreamSource(sch));
    final UUID uuid = UUID.randomUUID();
    validators.put(uuid, schematronValidator);
    return uuid.toString();
  }

  @POST
  @Produces("text/xml")
  @Consumes("text/xml")
  @Path("/validate/{id}")
  public String validate(@PathParam("id") String sUuid,  InputStream xml)
    throws TransformerException, IOException, ParserConfigurationException, XPathExpressionException, SAXException {
    final UUID uuid = UUID.fromString(sUuid);
    if (validators.containsKey(uuid)) {
      final SchematronResult validate = validators.get(uuid).validate(new StreamSource(xml));
      return validate.toString();
    } else {
      throw new IOException("Schema with uuid " + sUuid + " not found");
    }

  }
}
