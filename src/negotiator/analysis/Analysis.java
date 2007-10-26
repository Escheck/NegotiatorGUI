package negotiator.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import negotiator.Bid;
import negotiator.BidIterator;
import negotiator.Domain;
import negotiator.Main;
import negotiator.NegotiationTemplate;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.IssueInteger;
import negotiator.issue.IssueReal;
import negotiator.utility.UtilitySpace;
import negotiator.xml.SimpleElement;

/**
 * This class calculates the following characteristics of the negotiation space:
 * - Pareto-efficient frontier;
 * - Nash product optimal outcome;
 * - Kalai-Smorodinsky optimal outcome.
 * 
 * 
 * @author Dmytro Tykhonov
 *   FIXME Write analysis to a separete file!!!
 */
public class Analysis {
	private Bid fNashProduct;
	private ArrayList<Bid> fPareto;
	private Bid fKalaiSmorodinsky;
	private SimpleElement fRoot;  
	private NegotiationTemplate fNegotiationTemplate;
	private ArrayList<Bid> fCompleteSpace=null;
	public Analysis(NegotiationTemplate pTemplate, boolean pPerformCalculations) {
		fNegotiationTemplate = pTemplate;
		fPareto = new ArrayList<Bid>();		
		if(pPerformCalculations) {
			buildParetoFrontier();
			try {
				calculateKalaiSmorodinsky();
				calculateNash();
			} catch (AnalysisException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			if(getTotalNumberOfBids()<100000) buildCompleteOutcomeSpace();
		}
	}
	public Analysis(NegotiationTemplate pTemplate, SimpleElement pRoot) {
		fNegotiationTemplate = pTemplate;
		fRoot = pRoot;
		fPareto = new ArrayList<Bid>();
		loadFromXML(fRoot);
	}
	public int  getTotalNumberOfBids() {
		int lTotalNumberofBids=1;
		for(int i=0;i<fNegotiationTemplate.getDomain().getNumberOfIssues();i++) {
			switch(fNegotiationTemplate.getDomain().getIssue(i).getType()) {
			case DISCRETE:
				lTotalNumberofBids = lTotalNumberofBids*((IssueDiscrete)(fNegotiationTemplate.getDomain().getIssue(i))).getNumberOfValues();
				break;
			case INTEGER:
				int lTmp = ((IssueInteger)(fNegotiationTemplate.getDomain().getIssue(i))).getUpperBound()-
				((IssueInteger)(fNegotiationTemplate.getDomain().getIssue(i))).getLowerBound()+1;
				lTotalNumberofBids = lTotalNumberofBids*lTmp;
				break;
			case REAL:
				lTotalNumberofBids = lTotalNumberofBids*((IssueReal)(fNegotiationTemplate.getDomain().getIssue(i))).getNumberOfDiscretizationSteps();
				break;
				/* Removed by DT because KH removed PRICE
				 * 
				
			case PRICE:
				lTotalNumberofBids = lTotalNumberofBids*((IssuePrice)(fNegotiationTemplate.getDomain().getIssue(i))).getNumberOfDiscretizationSteps();
				break;*/				
			}//swith
		}//for
		return lTotalNumberofBids;
	}
	public void buildCompleteOutcomeSpace() {
		//calculate total number of bids
		fCompleteSpace = new ArrayList<Bid>();
		BidIterator lBidIter = new BidIterator(getDomain());
		while(lBidIter.hasNext()) {
			Bid lBid = lBidIter.next();
//			System.out.println("checking bid "+lBid.indexesToString() +" vs " + pBid.indexesToString());    	      
			fCompleteSpace.add(lBid);
		}//for

	}
	protected void loadFromXML(SimpleElement pXMLAnalysis) {
		SimpleElement lXMLAnalysis = pXMLAnalysis;
		//read Paretto
		if(lXMLAnalysis.getChildByTagName("pareto").length>0) {
			SimpleElement lXMLPareto = (SimpleElement)(lXMLAnalysis.getChildByTagName("pareto")[0]);
			Object[] lXMLParetoBids = (lXMLPareto.getChildByTagName("bid"));            	
			for(int i=0;i<lXMLParetoBids.length;i++) {
				//TODO: COMPLETED DT fix the loading bid from XML in Analysis 
				try {
					Bid lBid = new Bid(getDomain(), (SimpleElement)(lXMLParetoBids[i]));            	
					fPareto.add(lBid);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}
		sortParetoFrontier();		
		//	read Nash
		if(lXMLAnalysis.getChildByTagName("nash").length>0) {
			SimpleElement lXMLPareto = (SimpleElement)(lXMLAnalysis.getChildByTagName("nash")[0]);
			Object[] lXMLParetoBids = (lXMLPareto.getChildByTagName("bid"));
			try {
				Bid lBid = new Bid(getDomain(), (SimpleElement)(lXMLParetoBids[0]));            	
				fNashProduct = lBid;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}		
		//read Kalai-Smorodinsky
		if(lXMLAnalysis.getChildByTagName("kalai_smorodinsky").length>0) {										   
			SimpleElement lXMLPareto = (SimpleElement)(lXMLAnalysis.getChildByTagName("kalai_smorodinsky")[0]);
			Object[] lXMLParetoBids = (lXMLPareto.getChildByTagName("bid"));  
			try {
				Bid lBid = new Bid(getDomain(), (SimpleElement)(lXMLParetoBids[0]));            	
				fKalaiSmorodinsky = lBid;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}		


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
//			System.out.println("checking bid "+lBid.indexesToString() +" vs " + pBid.indexesToString());    	      
			if((getAgentAUtilitySpace().getUtility(pBid)<getAgentAUtilitySpace().getUtility(lBid))&&
					(getAgentBUtilitySpace().getUtility(pBid)<getAgentBUtilitySpace().getUtility(lBid)))
				return false;
		}
		return lIsStillASolution;
	}
	private boolean checkSolutionVSOtherBids(Bid pBid) {
		boolean lIsStillASolution = true;
		BidIterator lBidIter = new BidIterator(getDomain());
		while(lBidIter.hasNext()) {
			Bid lBid = lBidIter.next();
//			System.out.println("checking bid "+lBid.indexesToString() +" vs " + pBid.indexesToString());
			if((getAgentAUtilitySpace().getUtility(pBid)<getAgentAUtilitySpace().getUtility(lBid))&&
					(getAgentBUtilitySpace().getUtility(pBid)<getAgentBUtilitySpace().getUtility(lBid)))
				return false;
		}
		return lIsStillASolution;
	}
	public void buildParetoFrontier() {
		Main.logger.add("Building Pareto Frontier...");
		//loadAgentsUtilitySpaces();
		BidIterator lBidIter = new BidIterator(getDomain());
		while(lBidIter.hasNext()) {
			Bid lBid = lBidIter.next();
			System.out.println("checking bid "+lBid.toString());			
			if(!checkSolutionVSParetoFrontier(lBid)) continue;
			if(checkSolutionVSOtherBids(lBid)) 
				fPareto.add(lBid);
		}
		sortParetoFrontier();    	
		Main.logger.add("Finished building Pareto Frontier.");
		//make an XML representation of the analysis

		fRoot = new SimpleElement("analysis");
		SimpleElement lXMLPareto = new SimpleElement("pareto");
		fRoot.addChildElement(lXMLPareto);
		for(int i=0;i<fPareto.size();i++) {
			//TODO: COMPLETED DT fix saving bids to XML in Analysis (Pareto)
			SimpleElement lXMLBid = fPareto.get(i).toXML();
			lXMLPareto.addChildElement(lXMLBid);    		
		}

		return;
	}
	/**
	 *  Calculate Nash product. Assumes that Pareto frontier is already built.
	 * 
	 * @throws AnalysisException
	 */
	public void calculateNash() throws AnalysisException {
		//FIXME Nash for the car example seems to be wrong.
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
		SimpleElement lXMLNash= new SimpleElement("nash");
		fRoot.addChildElement(lXMLNash);
		SimpleElement lXMLBid = fNashProduct.toXML();
		lXMLNash.addChildElement(lXMLBid);    		
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
				double lTempUtility = Math.abs(fNegotiationTemplate.getAgentAUtilitySpace().getUtility(lTempBid)-
				fNegotiationTemplate.getAgentBUtilitySpace().getUtility(lTempBid));
				if(lTempUtility<lMinAssymetryUtility) {
					lMinAssymetryBid = lTempBid;
					lMinAssymetryUtility = lTempUtility;
				}
			}
			fKalaiSmorodinsky= lMinAssymetryBid;
		}
		SimpleElement lXMLKalai = new SimpleElement("kalai_smorodinsky");
		fRoot.addChildElement(lXMLKalai);
		SimpleElement lXMLBid = fKalaiSmorodinsky.toXML();
		lXMLKalai.addChildElement(lXMLBid);    		

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
	public boolean isCompleteSpaceBuilt() {
		if(fCompleteSpace!=null) return true;
		else return false;
	}
	public Bid getBidFromCompleteSpace(int pIndex) {
		return fCompleteSpace.get(pIndex);
	}
	public SimpleElement getXMLRoot() {
		return fRoot;
	}
	private void sortParetoFrontier() {
		Collections.sort(fPareto, new BidComparator());
	}
	protected  class BidComparator implements java.util.Comparator
	{
		public int compare(Object o1,Object o2) throws ClassCastException
		{
			if(!(o1 instanceof Bid)) {
				throw new ClassCastException();
			}
			if(!(o2 instanceof Bid)) {
				throw new ClassCastException();
			}
			double d1 = fNegotiationTemplate.getAgentAUtilitySpace().getUtility((Bid)o1);
			double d2 = fNegotiationTemplate.getAgentAUtilitySpace().getUtility((Bid)o2);

			if (d1 < d2) {
				return -1; 
			}
			else if (d1 > d2) {
				return 1; 
			}
			else {
				return 0;
			}
		}
	}     
}
