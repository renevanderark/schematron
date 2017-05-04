package nl.kb.schematron.web;


import nl.kb.schematron.SchematronResult;
import nl.kb.schematron.SchematronValidator;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.xml.sax.SAXException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.InputStream;

@Path("/")
public class ValidationEndpoint {

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("validate")
    @Produces(MediaType.TEXT_XML)
    public Response validate(
            @FormDataParam("schema") InputStream schema,
            @FormDataParam("xml") InputStream xml) throws IOException, TransformerException, ParserConfigurationException, XPathExpressionException, SAXException {

        final SchematronValidator schematronValidator = new SchematronValidator(new StreamSource(schema));

        final SchematronResult schematronResult = schematronValidator.validate(new StreamSource(xml));

        return Response.ok(schematronResult.toString()).build();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response form() {

        return Response.ok(
                "<html><body>" +
                        "<h1>Schematron validator service</h1>" +
                        "<h2>Command line usage with curl</h2>" +
                        "<pre>" +
                        " curl -X POST -F \"schema=@src/test/resources/jp2-gvn-schema.sch\" -F \"xml=@src/test/resources/jp2-correct-test.xml\" http://localhost:8080/validate" +
                        "</pre>" +
                        "<h2>Or try the webform</h2>" +
                        "<form action=\"/validate\" method=\"POST\" enctype=\"multipart/form-data\" >" +
                        "<p><input type=\"file\" name=\"schema\" /> Schematron schema</p>" +
                        "<p><input type=\"file\" name=\"xml\" /> Xml file to validate</p>" +
                        "<button type=\"submit\">Validate!</button>" +
                        "</form>" +
                        "</body></html>").build();
    }
}
