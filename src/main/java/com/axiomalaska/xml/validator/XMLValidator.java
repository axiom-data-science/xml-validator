package com.axiomalaska.xml.validator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLValidator implements ErrorHandler {
    private static Logger LOG = Logger.getLogger( XMLValidator.class );
        
    private int errors;
    private int warnings;
    
    public int getErrors() {
        return errors;
    }

    public int getWarnings() {
        return warnings;
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
    }
}