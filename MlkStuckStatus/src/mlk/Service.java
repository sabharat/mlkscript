package mlk;

public class Service {
private String serviceType;
private Integer msisdn;

public Service(String serviceType, Integer msisdn) {
	super();
	this.serviceType = serviceType;
	this.msisdn = msisdn;
}
public Service() {
	super();
}
public String getServiceType() {
	return serviceType;
}
public void setServiceType(String serviceType) {
	this.serviceType = serviceType;
}
public Integer getMsisdn() {
	return msisdn;
}
public void setMsisdn(Integer msisdn) {
	this.msisdn = msisdn;
}
}
