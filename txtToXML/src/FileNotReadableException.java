
public class FileNotReadableException extends Exception {
	
	public FileNotReadableException(String msg)
	{
		super("Error: The file isn't found in the path you have provided\nThe path " + msg+" .");
	}

}
