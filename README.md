xml-validator
=============

Simple Java utility to validate that XML files are well formed and valid against their internally declared xsds and dtds.

Uses javax.xml.validation. The main method accepts a path to an XML file or an XML string as the single argument.

Example:
    java -jar xml-validator.jar my-xml-file.xml
    
Creating a bash alias for this program, for example:
    mvn clean insatll
    cp target/xml-validator-shaded.jar /usr/local/bin/xml-validator.jar
    echo alias validate-xml=\'java -jar /usr/local/bin/xml-validator.jar\' >> ~/.bashrc
    . ~/.bashrc   
and then creating a vim command for the alias:
    echo command VXML !validate-xml %
will allow you to quickly validate XML files in vim as you edit them:
    :VXML