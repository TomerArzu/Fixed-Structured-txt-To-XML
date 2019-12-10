//D:\\Users\\tomer\\Documents\\XMLFiles\\MyXMLOutput.xml
//D:\\Users\\tomer\\Documents\\XMLFiles\\newfile.xml

import java.io.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import org.xml.sax.helpers.DefaultHandler;

public class SaxXmlModifier extends DefaultHandler {
	// class members
	static String displayText[];
	static int numberLines;
	static String indentation;
	static boolean inTrans;
	static boolean openerLineDown;

	public void start(String path)
	{
		try {
			//
			displayText = new String[1000];
			numberLines = 0;
			indentation = "";
			inTrans=false;
			openerLineDown=false;
			
			//
			
			File inputFile = new File(path);
			//SAXParserFactory factory = SAXParserFactory.newInstance();
			SaxXmlModifier obj = new SaxXmlModifier();
			obj.childLoop(inputFile);
			FileWriter filewriter = new FileWriter(path);
			for(int loopIndex = 0; loopIndex < numberLines; loopIndex++) 
			{
				filewriter.write(displayText[loopIndex].toCharArray());
			}
			filewriter.close();
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	public void childLoop(File input) {
		DefaultHandler handler = this;
		SAXParserFactory factory = SAXParserFactory.newInstance();

		try {
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(input, handler);
		} catch (Throwable t) {}
	}

	public void startDocument() {
		displayText[numberLines] = indentation;
		displayText[numberLines] += "<?xml version=\"1.0\" encoding=\"windows-1252\"?>\n";
		numberLines++;
	}

	public void startElement(String uri, String localName, String qualifiedName,Attributes attributes) 
	{
		if(qualifiedName.compareTo("PersonalDetails")==0||qualifiedName.compareTo("Transactions")==0)
		{
			indentation="\t";
			inTrans=qualifiedName.compareTo("Transactions")==0;
			openerLineDown=true;
		}
		else if((qualifiedName.compareTo("FirstName")==0||qualifiedName.compareTo("LastName")==0||qualifiedName.compareTo("ID")==0) && !(inTrans))
		{
			indentation = "\t\t";
		}
		else if(qualifiedName.compareTo("Transaction")==0)
		{
			indentation = "\t\t";
			openerLineDown=true;
		}
		else if((qualifiedName.compareTo("ID")==0||qualifiedName.compareTo("Type")==0||qualifiedName.compareTo("amount")==0||qualifiedName.compareTo("currency")==0||qualifiedName.compareTo("Balance")==0)&& (inTrans))
		{
			indentation = "\t\t\t";
		}
		else indentation="";
		displayText[numberLines] = indentation;
		indentation="";
		displayText[numberLines] += '<';
		displayText[numberLines] += qualifiedName;

		displayText[numberLines] += '>';
		if(openerLineDown || qualifiedName.compareTo("ClientInfo")==0) 
		{
			displayText[numberLines] +='\n';
			numberLines++;
			openerLineDown=false;
		}

	}

	public void characters(char characters[], int start, int length) { // <tag>characters</tag>
		String characterData = (new String(characters, start, length)).trim();
		if(characterData.indexOf("\n") < 0 && characterData.length() > 0) // if there is any characters between tags
		{
			displayText[numberLines] += "";
			System.out.println(displayText[numberLines]);
			displayText[numberLines] += characterData;
			numberLines++;
			System.out.println("characters() characterData:"+ characterData);
		}
	}

	public void endElement(String uri, String localName, String qualifiedName) {
		if(qualifiedName.compareTo("PersonalDetails")==0||qualifiedName.compareTo("Transactions")==0)
		{
			indentation="\t";
		}
		else if(qualifiedName.compareTo("Transaction")==0)
		{
			indentation="\t\t";
		}
		else indentation="";
		displayText[numberLines] = indentation;
		indentation="";
		displayText[numberLines] += "</";
		displayText[numberLines] += qualifiedName;
		displayText[numberLines] += ">\n";
		numberLines++;
	}
}