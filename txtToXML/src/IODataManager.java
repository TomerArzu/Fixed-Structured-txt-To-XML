import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class IODataManager {
	
	private String path;

	public IODataManager(String path)
	{
		this.path=path;
	}

	public ArrayList<String> ReadLines() throws FileNotReadableException{
		String line="";
		ArrayList<String> dataLines=new ArrayList<String>(); //reads the line that wasn't delimited 
		try {	
			Pattern toDelimite1=Pattern.compile("Client\\sDetails:|Transactions:|[\\n]+|[\\r]+"); //with this regExp I can verify all fields are ok
			//Pattern toDelimite2=Pattern.compile("[a-zA-Z]+\\s[a-zA-Z]+:\\s|,\\s|[a-zA-Z]+:\\s|\\n|\\r"); //with this regExp I get only the fields value but its harder to verify
			File f1=new File(this.path);
			if(!(f1.canRead()))
				throw new FileNotReadableException(this.path);
			Scanner fr=new Scanner(f1);
			fr.useDelimiter(toDelimite1);
			while(fr.hasNext()) //as long we have something in the string
			{
				line=fr.next();
				if(!(line.isEmpty()))
					dataLines.add(line);
			}
			fr.close();
		}
		catch(FileNotFoundException e)
		{
			System.out.println("There is now such a file");
			AppController.createXML=false;
		}
		return dataLines;
	}

	public Client createClient(String personData){
		Client c=null;
		String fn = null,ln = null,id = null,det;
		String cdc="[\\w]+[\\s[\\w]]*"; //regExp to check detail content(if not empty)
		Pattern p=Pattern.compile("(\\w+\\s*\\w*:\\s)([A-Za-z0-9]+[\\sA-Za-z]*)");
		Matcher m=p.matcher(personData);
		while(m.find())
		{
			det=m.group(1).replaceAll(":\\s(?!\\w)", ""); //replace the :\s at the beginning of each detail
			if(det.compareToIgnoreCase("First Name")==0)	
				fn=m.group(2);
			else if(det.compareToIgnoreCase("Last Name")==0)	
				ln=m.group(2);
			else if(det.compareToIgnoreCase("ID")==0)	
				id=m.group(2);
			else return null; /*Wrong format of the file*/
		}
		try {
			if(fn.matches(cdc)&&ln.matches(cdc)&&id.matches(cdc)) //check if content of client details is not empty 
				c=new Client(fn,ln,id);
		}
		catch(NullPointerException e){
			c=null;
			AppController.createXML=false;
		}
		return c;
	}

	public Transaction createTransaction(String trans) throws NullPointerException, ArrayIndexOutOfBoundsException,NumberFormatException{
		Transaction t=null;
		String id = null,ty = null,am = null,cu = null,bl = null,det;
		String ctd=".[\\w]+[\\s[\\w]]*"; //regExp to check transaction data(if not empty)
		Pattern p=Pattern.compile("(\\w+\\s*\\w*:\\s)(.[A-Za-z0-9]+[\\sA-Za-z]*)");
		Matcher m=p.matcher(trans);
		while(m.find())
		{
			det=m.group(1).replaceAll(":\\s(?!\\w)", ""); //replace the :\s at the beginning for detail
			if(det.compareToIgnoreCase("ID")==0)	
				id=m.group(2);
			else if(det.compareToIgnoreCase("Type")==0)	
				ty=m.group(2);
			else if(det.compareToIgnoreCase("Amount")==0)	
				am=m.group(2);
			else if(det.compareToIgnoreCase("Currency")==0)	
				cu=m.group(2);
			else if(det.compareToIgnoreCase("Balance")==0)	
				bl=m.group(2);
			else return null; /*Wrong format of the file*/
		}
			if(id.matches(ctd) && ty.matches(ctd) && am.matches(ctd) && cu.matches(ctd) && bl.matches(ctd)) //check if content of client details is not empty 
				t=new Transaction(id, ty, am, cu, bl);
		return t;
	}

	public void writeToXML(String targetPath,Client c) throws ParserConfigurationException, TransformerException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); //create new factory (the factory produce API to create XML [DOM object trees] docs) to create builder
        DocumentBuilder builder = factory.newDocumentBuilder(); //creating builder to create 
        Document doc = builder.newDocument(); //with the builder we create a new doc
        //creating the root node "ClientInfo"
        Element root = doc.createElement("ClientInfo");
        doc.appendChild(root); //append the root node to the XML as tree root node
        root.appendChild(add_2nd_DegChild(doc,c,"PersonalDetails"));
        root.appendChild(add_2nd_DegChild(doc,c,"Transactions"));
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transf = transformerFactory.newTransformer();
        
        doc.setXmlStandalone(true); //remove "stand alone"
        transf.setOutputProperty(OutputKeys.ENCODING, "windows-1252");
        transf.setOutputProperty(OutputKeys.INDENT, "yes");
        transf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transf.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC,""); // add new line after prolog
        
        
        DOMSource source = new DOMSource(doc);

        File myFile = new File(targetPath);
        
        StreamResult file = new StreamResult(myFile);

        transf.transform(source, file);
	}
	
	private Node add_2nd_DegChild(Document doc,Client c, String childName)
	{
		Element rootChild=doc.createElement(childName);//create a child for the root (one indention)
		if(childName.compareTo("PersonalDetails")==0) /*if we create the personal details section*/
		{
			rootChild.appendChild(add_nth_DegChild(doc,"FirstName",c.getFirstName()));
			rootChild.appendChild(add_nth_DegChild(doc,"LastName",c.getLastName()));
			rootChild.appendChild(add_nth_DegChild(doc,"ID",c.getId()));
		}
		else /*the transaction details*/
		{
			for(Transaction t:c.getClientTrans())
			{
				rootChild.appendChild(add_3rd_DegChildTrans(doc,t,"Transaction"));
			}
		}
		return rootChild;
	}
	
	private Node add_3rd_DegChildTrans(Document doc, Transaction t, String childName) 
	{
		Element transChild = doc.createElement(childName);
		String b=t.getBalance().toString();
		transChild=doc.createElement(childName);
		transChild.appendChild(add_nth_DegChild(doc,"ID",t.getId()));
		transChild.appendChild(add_nth_DegChild(doc,"Type",t.getType()));
		transChild.appendChild(add_nth_DegChild(doc,"amount",t.getAmount().toString()));
		transChild.appendChild(add_nth_DegChild(doc,"currency",t.getCurrency()));
		if(t.getBalance()>0) b="+"+t.getBalance().toString();
		transChild.appendChild(add_nth_DegChild(doc,"Balance",b+" "+t.getBalanceCurrency()));
		return transChild;
	}

	private Node add_nth_DegChild(Document doc, String elmtName,String elmntText)
	{
		Element nodeToAdd=doc.createElement(elmtName);
		Text nodeText=doc.createTextNode(elmntText);
		nodeToAdd.appendChild(nodeText);
		return nodeToAdd;
	}


}
