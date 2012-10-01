package com.axiomalaska.xml.validator;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import org.junit.Test;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


public class XMLValidatorTest {
    private static final File WMS_130_GET_CAP = new File( "src/test/resources/wms130GetCapabilities.xml" );
    private static final File WMS_130_GET_CAP_ERROR = new File( "src/test/resources/wms130GetCapabilities-error.xml" );
    
    @Test
    public void testWms() throws FileNotFoundException, SAXException, IOException{
        XMLValidator xmlValidator = new XMLValidator();
        xmlValidator.validate( WMS_130_GET_CAP );
        assertEquals( 0, xmlValidator.getErrors() );
        assertEquals( 0, xmlValidator.getWarnings() );
    }
    
    @Test
    public void testWmsError() throws FileNotFoundException, SAXException, IOException{
        XMLValidator xmlValidator = new XMLValidator();
        xmlValidator.validate( WMS_130_GET_CAP_ERROR );
        assertEquals( 1, xmlValidator.getErrors() );
        assertEquals( 0, xmlValidator.getWarnings() );
    }

    @Test
    public void testWmsString() throws FileNotFoundException, SAXException, IOException{
        XMLValidator xmlValidator = new XMLValidator();
        String xmlStr = readFile( WMS_130_GET_CAP );
        xmlValidator.validate( xmlStr );
        assertEquals( 0, xmlValidator.getErrors() );
        assertEquals( 0, xmlValidator.getWarnings() );
    }

    @Test
    public void testWmsStringError() throws FileNotFoundException, SAXException, IOException{
        XMLValidator xmlValidator = new XMLValidator();
        String xmlStr = readFile( WMS_130_GET_CAP_ERROR );
        xmlValidator.validate( xmlStr );
        assertEquals( 1, xmlValidator.getErrors() );
        assertEquals( 0, xmlValidator.getWarnings() );
    }

    private static String readFile( File file ) throws IOException {
        FileInputStream stream = new FileInputStream( file );
        try {
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            /* Instead of using default, pass in a decoder. */
            return Charset.defaultCharset().decode(bb).toString();
        } finally {
            stream.close();
        }
      }    
}
