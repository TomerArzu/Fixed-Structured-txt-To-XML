import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class AppController {
	@FXML
	private Button upldBtn;
	@FXML
	private Button cnvrtBtn;
	@FXML
	private TextField pathviewtxtfld;
	Client sophie=null;
	IODataManager f;
	Transaction trns;
	SaxXmlModifier sax;
	ArrayList<String> dataFromFiles;
	public static boolean createXML=true; //global var that notify if the process done well
	String inputPath;
	String outputPath;
	String fileName;
	File selectedFile;

	public void uploadFile(ActionEvent event)
	{
		try 
		{
			FileChooser fc=new FileChooser();
			selectedFile=fc.showOpenDialog(null);
			if(selectedFile.getName().endsWith(".txt"))
			{
				pathviewtxtfld.setVisible(true);
				pathviewtxtfld.setText(selectedFile.getAbsolutePath());
				inputPath=selectedFile.getAbsolutePath(); // input path
				outputPath=selectedFile.getAbsolutePath().replace(".txt", "_output_XML.xml");
				fileName=selectedFile.getName();

				f=new IODataManager(inputPath);
				dataFromFiles = f.ReadLines();
				sophie=f.createClient(dataFromFiles.get(0));
				if(sophie==null) 
				{
					showErrorMsg("Wrong File structure","Somthing wrong in Client Details section");
					createXML=false;
				}
				else 
				{ //creating Transactions objects
					dataFromFiles.remove(0);
					for(String s: dataFromFiles)
					{
						trns=f.createTransaction(s);
						if(trns!=null) // if there are no transactions --> must we transactions --> wrong file format
						{
							if(!(sophie.addTransaction(trns))) //if transaction didn't added
							{
								showErrorMsg("Balance Error","balance of transaction: "+ trns.getId() +" does not match to the other specified balances");
								break;
							}
						} //
						else {
							showErrorMsg("Wrong File structure","Somthing Wrong in the Transactions section");
							createXML=false;
						}
						createXML=true; // all transactions are added
					}
				}
				if(createXML)
				{
					cnvrtBtn.setVisible(true);
					cnvrtBtn.setDisable(false);
				}
			} //if(selectedFile.getName().endsWith(".txt"))
			else 
			{
				showErrorMsg("Upload Wrong Format","Please upload only \".txt\" files (Text Files Only)");
			}
		}// try

		catch (FileNotReadableException e) 
		{
			showErrorMsg("The File Is Not Readable","Try to open the file the read permission");
			createXML=false;
		}
		catch(NumberFormatException e)
		{
			showErrorMsg("Wrong File Format","Somthing Wrong in the Transactions section");
			createXML=false;
		}
		catch (NullPointerException e) 
		{
			if(selectedFile==null) // if no file selected
			{
				return;
			}
			showErrorMsg("Wrong File Format","Somthing Wrong in the Transactions section");
			createXML=false;
		}
		catch (ArrayIndexOutOfBoundsException e) 
		{
			showErrorMsg("Wrong File Format","Somthing Wrong in the Transactions section");
			createXML=false;
		}
	}

	public void ConvertToXML(ActionEvent event)
	{

		try
		{
			f.writeToXML(outputPath,sophie);
			SaxXmlModifier sax=new SaxXmlModifier();
			sax.start(outputPath);
			DoneMsg();
		} //try
		catch (ParserConfigurationException e) 
		{
			System.out.println("ParserConfigurationException- if a DocumentBuilder cannot be created which satisfies the configuration requested.");
			e.printStackTrace();
			showErrorMsg("ParserConfigurationException","Ops...Something Wrong, Please contact us");

		} catch (TransformerException e) {
			System.out.println("TransformerException - If an unrecoverable error occursduring the course of the transformation.");
			e.printStackTrace();
			showErrorMsg("TransformerException","Ops...Something Wrong, Please contact us");

		}
	}

	public void beforeNewFile() // works after each convert and when error occurred
	{
		pathviewtxtfld.clear();
		cnvrtBtn.setDisable(true);
		sophie=null;
		f=null;
		trns=null;
		dataFromFiles.clear();
		createXML=false;
		sax=null;
	}

	public void showErrorMsg(String errorType, String msg) // show warning when error occurred
	{
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Pay Attention - "+ errorType);
		alert.setHeaderText("Sorry, The File \""+ fileName +"\" Was Not Converted\nCheck The Error Details And Try To Upload The File Again");
		alert.setContentText("Error Details:\n"+msg);
		beforeNewFile();
		alert.showAndWait();
	}

	public void DoneMsg()
	{
		try {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Your XML Is Ready");
			alert.setHeaderText("Congratulations! Your XML Is Ready.");
			alert.setContentText("The XML file location: "+outputPath);

			ButtonType openFileBtn = new ButtonType("Open file location");
			ButtonType  cancelBtn = new ButtonType("Done", ButtonData.CANCEL_CLOSE);

			alert.getButtonTypes().setAll(openFileBtn, cancelBtn);
			beforeNewFile(); // init before new file when we are done
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == openFileBtn)
			{
				Runtime.getRuntime().exec("explorer.exe /select," + outputPath);//open file explorer in the output path (Works only on windows)
			}
		} catch (IOException e) 
		{
			showErrorMsg("Cant Open The File","Ops...Somthing wrong so we can not open the file location!\n But Dont worry THE FILE WAS CREATED");
		} 
	}

	@FXML
	public void initialize() {
		pathviewtxtfld.setVisible(false);
		cnvrtBtn.setVisible(false);
	}
}
