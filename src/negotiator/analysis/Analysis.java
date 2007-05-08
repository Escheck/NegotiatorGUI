package negotiator.analysis;

import java.util.ArrayList;
import java.util.Iterator;

import negotiator.Bid;
import negotiator.BidIterator;
import negotiator.Domain;
import negotiator.Main;
import negotiator.NegotiationTemplate;
import negotiator.UtilitySpace;
import negotiator.xml.SimpleElement;

/**
 * This class calculates the following characteristics of the negotiation space:
 * - Pareto-efficient frontier;
 * - Nash product optimal outcome;
 * - Kalai-Smorodinsky optimal outcome.
 * 
 * 
 * @author Dmytro Tykhonov
 *
 */
public class Analysis {
    private Bid fNashProduct;
    private ArrayList<Bid> fPareto;
    private Bid fKalaiSmorodinsky;
    private SimpleElement fRoot;  
    private NegotiationTemplate fNegotiationTemplate;
	public Analysis(NegotiationTemplate pTemplate, boolean pPerformCalculations) {
		fNegotiationTemplate = pTemplate;
    	fPareto = new ArrayList<Bid>();		
    	if(pPerformCalculations) {
    		buildParetoFrontier();
    	}
	}
	public Analysis(NegotiationTemplate pTemplate, SimpleElement pRoot) {
		fNegotiationTemplate = pTemplate;
		fRoot = pRoot;
    	fPareto = new ArrayList<Bid>();
    	loadFromXML(fRoot);
	}
	protected void loadFromXML(SimpleElement pXMLAnalysis) {
		SimpleElement lXMLAnalysis = pXMLAnalysis;
		//read Paretto
		if(lXMLAnalysis.getChildByTagName("pareto").length>0) {
			SimpleElement lXMLPareto = (SimpleElement)(lXMLAnalysis.getChildByTagName("pareto")[0]);
			Object[] lXMLParetoBids = (lXMLPareto.getChildByTagName("bid"));            	
			for(int i=0;i<lXMLParetoBids.length;i++) {
				//TODO fix the loading bid from XML in Analysis 
/*				Bid lBid = new Bid(getDomain(), (SimpleElement)(lXMLParetoBids[i]));            	
				fPareto.add(lBid);*/
			}
		}
		//	read Nash
		//read Kalai-Smorodinsky
	}
	
	protected Domain getDomain() {
		return fNegotiationTemplate.getDomain();
	}
	protected UtilitySpace getAgentAUtilitySpace() {
		return fNegotiationTemplate.getAgentAUtilitySpace();
	}
	protected UtilitySpace getAgentBUtilitySpace() {
		return fNegotiationTemplate.getAgentBUtilitySpace();
	}
	
    private boolean checkSolutionVSParetoFrontier(Bid pBid) {
    	boolean lIsStillASolution = true;
    	for (Iterator<Bid> lBidIter = fPareto.iterator(); lBidIter.hasNext();) {
    	      Bid lBid = lBidIter.next();
//      		 System.out.println("checking bid "+lBid.indexesToString() +" vs " + pBid.indexesToString());    	      
    	      if((getAgentAUtilitySpace().getUtility(pBid)<getAgentAUtilitySpace().getUtility(lBid))&&
    	         (getAgentBUtilitySpace().getUtility(pBid)<getAgentAUtilitySpace().getUtility(lBid)))
    	    	  return false;
    	}
    	return lIsStillASolution;
    }
    private boolean checkSolutionVSOtherBids(Bid pBid) {
    	boolean lIsStillASolution = true;
    	BidIterator lBidIter = new BidIterator(getDomain());
    	while(lBidIter.hasNext()) {
    		Bid lBid = lBidIter.next();
//    		System.out.println("checking bid "+lBid.indexesToString() +" vs " + pBid.indexesToString());
    		if((getAgentAUtilitySpace().getUtility(pBid)<getAgentAUtilitySpace().getUtility(lBid))&&
    		   (getAgentBUtilitySpace().getUtility(pBid)<getAgentAUtilitySpace().getUtility(lBid)))
    			return false;
    	}
    	return lIsStillASolution;
    }
    public SimpleElement getXMLRoot() {
    	return fRoot;
    }
    public void buildParetoFrontier() {
        Main.logger.add("Building Pareto Frontier...");
    	//loadAgentsUtilitySpaces();
    	BidIterator lBidIter = new BidIterator(getDomain());
    	while(lBidIter.hasNext()) {
    		Bid lBid = lBidIter.next();    		
    		if(!checkSolutionVSParetoFrontier(lBid)) continue;
    		if(checkSolutionVSOtherBids(lBid)) 
    			fPareto.add(lBid);
    	}
    	Main.logger.add("Finished building Pareto Frontier.");
    	//make an XML representation of the analysis

    	fRoot = new SimpleElement("analysis");
    	SimpleElement lXMLPareto = new SimpleElement("pareto");
    	fRoot.addChildElement(lXMLPareto);
    	for(int i=0;i<fPareto.size();i++) {
    		//TODO fix saving bids to XML in Analysis (Pareto)
//    		SimpleElement lXMLBid = fPareto.get(i).toXML();
///    		lXMLPareto.addChildElement(lXMLBid);    		
    	}
 
    	return;
    }
    /**
     *  Calculate Nash product. Assumes that Pareto frontier is already built.
     * 
     * @throws AnalysisException
     */
    public void calculateNash() throws AnalysisException {
    	if(fPareto.size()<1) 
    		throw new AnalysisException("Nash product: Pareto frontier is unavailable.");
    	else {
    		Bid fMaxBid = fPareto.get(0);
    		double fMaxUtility = fNegotiationTemplate.getAgentAUtilitySpace().getUtility(fMaxBid)*
			fNegotiationTemplate.getAgentBUtilitySpace().getUtility(fMaxBid);    			
 
    		for(int i =1;i<fPareto.size();i++) {
    			Bid fTempBid = fPareto.get(i);
    			double fTempUtility = fNegotiationTemplate.getAgentAUtilitySpace().getUtility(fTempBid)*
    			fNegotiationTemplate.getAgentBUtilitySpace().getUtility(fTempBid);
    			if(fTempUtility>fMaxUtility) {
    				fMaxBid = fTempBid;
    				fMaxUtility = fTempUtility;
    			}
    		}
    		fNashProduct = fMaxBid;
    	}
    	return;
    }
    /**
     * Calculates Kalai-Smorodinsky optimal outcome. Assumes that Pareto frontier is already built.
     * 
     * @throws AnalysisException
     */
    public void calculateKalaiSmorodinsky() throws AnalysisException {
    	if(fPareto.size()<1) 
    		throw new AnalysisException("Nash product: Pareto frontier is unavailable.");
    	else {
    		Bid lMinAssymetryBid = fPareto.get(0);
    		double lMinAssymetryUtility = Math.abs(fNegotiationTemplate.getAgentAUtilitySpace().getUtility(lMinAssymetryBid)-
			fNegotiationTemplate.getAgentBUtilitySpace().getUtility(lMinAssymetryBid));    			
 
    		for(int i =1;i<fPareto.size();i++) {
    			Bid lTempBid = fPareto.get(i);
    			double lTempUtility = fNegotiationTemplate.getAgentAUtilitySpace().getUtility(lTempBid)-
    			fNegotiationTemplate.getAgentBUtilitySpace().getUtility(lTempBid);
    			if(lTempUtility>lMinAssymetryUtility) {
    				lMinAssymetryBid = lTempBid;
    				lMinAssymetryUtility = lTempUtility;
    			}
    		}
    		fKalaiSmorodinsky= lMinAssymetryBid;
    	}
    	return;
    }
	/**
	 * @return the fKalaiSmorodinsky
	 */
	public Bid getKalaiSmorodinsky() {
		return fKalaiSmorodinsky;
	}
	/**
	 * @return the fNashProduct
	 */
	public Bid getNashProduct() {
		return fNashProduct;
	}
    public int getParetoCount() {
    	return fPareto.size();
    }
    public Bid getParetoBid(int pIndex) {
    	return fPareto.get(pIndex);    	
    }
}
