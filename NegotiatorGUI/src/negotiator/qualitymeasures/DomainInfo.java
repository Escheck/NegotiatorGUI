package negotiator.qualitymeasures;

public class DomainInfo {
	String domain;
	String prefProfA;
	String prefProfB;
	
	public DomainInfo(String domain) {
		this.domain = domain;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getPrefProfA() {
		return prefProfA;
	}

	public void setPrefProfA(String prefProfA) {
		this.prefProfA = prefProfA;
	}

	public String getPrefProfB() {
		return prefProfB;
	}

	public void setPrefProfB(String prefProfB) {
		this.prefProfB = prefProfB;
	}
}
