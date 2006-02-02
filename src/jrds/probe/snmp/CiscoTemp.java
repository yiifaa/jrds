package jrds.probe.snmp;


import jrds.ProbeDesc;
import jrds.RdsHost;
import jrds.snmp.SnmpRequester;

import org.snmp4j.smi.OID;


public class CiscoTemp extends RdsSnmpSimple {
	static final private ProbeDesc pd = new ProbeDesc(2);
	static {
		pd.add("inlet temperature", ProbeDesc.GAUGE, new OID(".1.3.6.1.4.1.9.9.13.1.3.1.3.1"));
		pd.add("outlet temperature", ProbeDesc.GAUGE, new OID(".1.3.6.1.4.1.9.9.13.1.3.1.3.2"));
		pd.setName("ciscotemperature");
		pd.setRequester(SnmpRequester.SIMPLE);
		
		pd.setGraphClasses(new Object[] {"cpuload.xml"});
	}
	
	public CiscoTemp(RdsHost monitoredHost) {
		super(monitoredHost, pd);
	}
	
}