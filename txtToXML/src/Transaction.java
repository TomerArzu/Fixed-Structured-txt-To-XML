
public class Transaction {
	private String id;
	private String type;
	private Double amount;
	private String currency;
	private Double balance;
	private String balanceCurrency;
	
	public Transaction(String id,String type,String amount,String currency,String balance) throws NumberFormatException
	{
		this.id=id;
		this.type=type;
		this.amount=Double.parseDouble(amount);
		this.currency=currency;
		String[] b=balance.split("\\s");
		this.balanceCurrency=b[1]; 
		this.balance=Double.parseDouble(b[0]);
	}

	public String getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public Double getAmount() {
		return amount;
	}
	
	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public Double getBalance() {
		return balance;
	}

	public String getBalanceCurrency() {
		return balanceCurrency;
	}
}
