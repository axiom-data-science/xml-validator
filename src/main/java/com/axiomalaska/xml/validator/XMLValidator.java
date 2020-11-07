package com.axiomalaska.xml.validator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

public class XMLValidator implements ErrorHandler {
    private static Logger LOG = Logger.getLogger( XMLValidator.class );

    private int errors;
    private int warnings;

    public class ResolvedNamespace {
        private String namespaceURI;
        private String systemId;
        private String baseURI;

        public ResolvedNamespace(String namespaceURI, String systemId, String baseURI) {
            this.namespaceURI = namespaceURI;
            this.systemId = systemId;
            this.baseURI = baseURI;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(namespaceURI);
            sb.append(" ");
            sb.append(systemId);
            if (baseURI != null) {
                sb.append(" ");
                sb.append(baseURI);
            }

            return sb.toString();
        }
    }

    private List<ResolvedNamespace> resolvedNamespaces = new ArrayList<ResolvedNamespace>();

    public int getErrors() {
        return errors;
    }

    public int getWarnings() {
        return warnings;
    }

    public List<ResolvedNamespace> getResolvedNamespaces() {
        return resolvedNamespaces;
    }

    public String getResolvedNamespacesLog() {
        return resolvedNamespaces.stream().map(ResolvedNamespace::toString).collect(Collectors.joining("\n"));
    }

    public void fatalError( SAXParseException e ) throws SAXException {
        LOG.fatal( e.toString() );
        throw( e );
    }

    public void error( SAXParseException e ) throws SAXException {
        errors++;
        LOG.error( e.toString() );
    }

    public void warning( SAXParseException e ) throws SAXException {
        warnings++;
        LOG.warn( e.toString() );
    }

    public void validate( File xmlFile ) throws SAXException, IOException{
        validate( new StreamSource( new FileReader( xmlFile ) ) );
    }

    public void validate( String xmlString ) throws SAXException, IOException{
        validate( new StreamSource( new StringReader( xmlString ) ) );
    }

    public void validate( StreamSource source ) throws SAXException, IOException{
        Validator validator = SchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI ).newSchema().newValidator();
        validator.setErrorHandler( this );
        validator.setResourceResolver(new LSResourceResolver() {
            @Override
            public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
                //track resolved namespace
                resolvedNamespaces.add(new ResolvedNamespace(namespaceURI, systemId, baseURI));

                //returning null causes the schema to be resolved normally
                return null;
            }
        });
        validator.validate( source );
    }

    public static void main( String[] args ) throws FileNotFoundException, SAXException, IOException{
        if( args.length != 1 ){
            throw new RuntimeException("Must take a single argument (XML file or XML string)");
        }

        XMLValidator xmlValidator = new XMLValidator();

        File file = new File( args[0] );

        if( file.exists() ){
            //it's a file
            LOG.info( "Validating XML file: " + file.getAbsolutePath() );
            xmlValidator.validate( file );
        } else {
            //it's an XML string
            LOG.info( "Validating XML string");
            xmlValidator.validate( args[0] );
        }

        LOG.info( "Total XML errors: " + xmlValidator.getErrors() );
        LOG.info( "Total XML warnings: " + xmlValidator.getWarnings() );
        LOG.info( "Resolved XMLnamespaces: \n" + xmlValidator.getResolvedNamespacesLog() );
    }
}