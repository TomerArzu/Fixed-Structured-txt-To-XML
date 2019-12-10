# Fixed-Structured-txt-To-XML

This project done as an job interview assignment.

Text To XML (TTX) Converter is a software that receive as an input text file (.txt), which is contain a data about bank customer details and his transactions, and create an XML file, by no more than few clicks.

Learning to use the software is very easy. We assume that you familiar with XML files and working with them. Anther assumption is that the input file, the data to feed the application, is on-hand and ready to conversion by the rules we will introduce later on the user manual.

As part of the assignment I required to do a user manual, you can download it also. In the user Manual you will find:
  - General info about the sofware.
  - System requirements.
  - Step-By-Step Instructions.
  - Errors Handling.

All the data in the manual followed by pictures and explanations.

## How I Did It?
  
  1. The input txt file go through IODataManager class, in the class I used the function public ArrayList<String> ReadLines()
  that parses the file and delimite unnecessary data that the txt file include like the titles. the function return ArrayList<string> that conatin all the importent lines in the text (such as user data and his transactions)
  
  2. Createing two entities: Client and Transaction.
  The Transaction - consist of and id, type, amount, currency, balance, balanceCurrency. Where amount is the current amount action with it's currency and balance is the total balance with it's currency.
  The Client- consist of firstName, LastName, id, <b>ArrayList<Transaction> clientTrans<b/>, <b>SortedMap<String, Double> clientBalance<b/>.
  - clientTrans - holds all the clients transaction
  - clientBalance - holds the balance for each currency. For example: {"USD" : 1200, "INS" : 500}
  
  3. Creating an temporary XML file <b>without<b/> tab indentation between the XML nodes.
  
  4. extends SAX DefaultHandler - SAX DefaultHandler reads the XML file and each time it meets event the callback done.
  For Example: when its read the XML we meet open element like: <element> and it call the function public void startElement(String uri, String localName, String qualifiedName,Attributes attributes) that any one can implement differently.
  In my implementation I've read all the XML file and each new like correspont to the child node indent I've added \\t* [tab] intentation.
  
  
  ### New things I've learned
  
 1. Use regEx in java to validate and delimite data.
 2. Use org.w3c.dom lib to create XML file from scratch.
 3. Use SAX handler to catch events from XML file.
 
 <strong> If you find any problem or any idea to optimize the program please PM me <strong/>
