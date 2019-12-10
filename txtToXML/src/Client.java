import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

public class Client {
	private String firstName;
	private String LastName;
	private String id;
	private ArrayList<Transaction> clientTrans;
	private SortedMap<String, Double> clientBalance;

	public Client(String firstName,String LastName,String id)
	{
		this.firstName=firstName;
		this.LastName=LastName;
		this.id=id;
		clientTrans=new ArrayList<Transaction>();
		this.clientBalance=new TreeMap<String, Double>();
	}
	
	public boolean addTransaction(Transaction t) // add transaction to array list and maintains balance check
	{
		boolean added=false, amountNeg=false;
		if(clientTrans.isEmpty()) // if there are no transactions yet
		{
			clientTrans.add(t);
			// Maintains of the balance by the client
			if(t.getBalanceCurrency().compareToIgnoreCase(t.getCurrency())==0) //if the current transaction and the current balance have the same currency 
			{
				clientBalance.put(t.getCurrency(), t.getBalance());
			}
			else { //if the currency of the transaction and the balance is different
				clientBalance.put(t.getBalanceCurrency(), t.getBalance());
				clientBalance.put(t.getCurrency(), t.getAmount());
			}
			added=true;
		}
		else { // if we have transactions already
			for(Transaction trans:clientTrans) // check if there is the same transaction id
			{
				if(t.getId().compareTo(trans.getId())==0) {
					return added;
				}
			}
			if(clientBalance.containsKey(t.getCurrency())) // if there is such a currency in the client balance
			{
				if(t.getType().compareToIgnoreCase("withdraw")==0 || t.getType().compareToIgnoreCase("credit card")==0) //if we need to subtract the total balance
				{
					t.setAmount(t.getAmount()*(-1));
					amountNeg=true;
				}
				if(clientBalance.get(t.getCurrency())+t.getAmount()==t.getBalance()) //check if the balance from text stand with the balance that should to be
				{
					clientBalance.put(t.getCurrency(), clientBalance.get(t.getCurrency())+t.getAmount());
					added=clientTrans.add(t);
				}
				else // if there is problem with the balance
				{
					System.out.println("Balance Problem! the Balance of transaction: "+t.getId()+" doesnt stands with the previous balance.");
				}
				if(amountNeg) 
					t.setAmount(t.getAmount()*(-1));
			}
			else // if there is new currency
			{
				clientBalance.put(t.getCurrency(), t.getAmount());
				added=this.clientTrans.add(t);
			}
		}
		return added;
	}
	
	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return LastName;
	}

	public String getId() {
		return id;
	}

	public ArrayList<Transaction> getClientTrans() {
		return clientTrans;
	}

}
