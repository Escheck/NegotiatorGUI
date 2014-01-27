package negotiator.utility;

import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import negotiator.Bid;
import negotiator.Domain;
import negotiator.Timeline;
import negotiator.xml.SimpleDOMParser;
import negotiator.xml.SimpleElement;



public class NonlinearUtilitySpace extends UtilitySpace
{
	private double maxUtilityValue;
	private UtilityFunction nonlinearFunction;
	private ArrayList<InclusiveHyperRectangle> allinclusiveConstraints; // we keep all constraints for negotiating agent's strategy
	private ArrayList<ExclusiveHyperRectangle> allexclusiveConstraints; // we keep all constraints for negotiating agent's strategy
	
	// add some parameters for discount factor
	/**
     * Creates an empty nonlinear utility space.
     */
    public NonlinearUtilitySpace(){
    	this.domain = new Domain();
    	this.nonlinearFunction=new UtilityFunction();
    	this.allinclusiveConstraints=new ArrayList<InclusiveHyperRectangle>();
    	this.allexclusiveConstraints=new ArrayList<ExclusiveHyperRectangle>();
    	spaceType=UTILITYSPACETYPE.NONLINEAR;
    	
    }
    
    public NonlinearUtilitySpace(Domain domain) {
    	this.domain=domain;
    	this.nonlinearFunction=new UtilityFunction();
    	this.allinclusiveConstraints=new ArrayList<InclusiveHyperRectangle>();
    	this.allexclusiveConstraints=new ArrayList<ExclusiveHyperRectangle>();
    	spaceType=UTILITYSPACETYPE.NONLINEAR;
    }

    public NonlinearUtilitySpace(Domain domain, String fileName) throws Exception{
    	 this.domain = domain;
    	 this.nonlinearFunction=new UtilityFunction();
    	 this.fileName = fileName;
    	 this.allinclusiveConstraints=new ArrayList<InclusiveHyperRectangle>();
     	 this.allexclusiveConstraints=new ArrayList<ExclusiveHyperRectangle>();
         spaceType=UTILITYSPACETYPE.NONLINEAR;         
         
         if(!fileName.equals("")) {
        	 SimpleDOMParser parser = new SimpleDOMParser();
             BufferedReader file = new BufferedReader(new FileReader(new File(fileName)));                  
             SimpleElement root = parser.parse(file);
             fXMLRoot = root;             
        	 loadNonlinearSpace(root);
        	 
         }else throw new IOException();        	  
    }
    
  
    public ArrayList<InclusiveHyperRectangle> getAllInclusiveConstraints(){
    	return this.allinclusiveConstraints;
    }
  
    private ArrayList<ExclusiveHyperRectangle> getAllExclusiveConstraints(){
    	return this.allexclusiveConstraints;
    }
    
    
    /** @return a clone of another utility space */
    public NonlinearUtilitySpace(UtilitySpace us)    {
    	domain=us.getDomain();
    	fileName = us.getFileName();
    	spaceType=UTILITYSPACETYPE.NONLINEAR;
    	fXMLRoot = us.getXMLRoot();
    	maxUtilityValue = ((NonlinearUtilitySpace)us).getMaxUtilityValue();
    	nonlinearFunction=((NonlinearUtilitySpace)us).getNonlinearFunction();     	
    	allinclusiveConstraints=((NonlinearUtilitySpace)us).getAllInclusiveConstraints();
    	allexclusiveConstraints=((NonlinearUtilitySpace)us).getAllExclusiveConstraints();
    	this.setDiscount(((NonlinearUtilitySpace)us).getDiscountFactor());
    	this.setReservationValue(((NonlinearUtilitySpace)us).getReservationValue());
    }
    
    
    
    //This method parse xml file and load nonlinear utility space 
    
     private ArrayList<Constraint> loadHyperRectangles(Object[] rectangeElements) {
    	
    	ArrayList<Constraint> hyperRectangleConstraints=new ArrayList<Constraint>();
    	
    	
    	for (int j=0; j<rectangeElements.length; j++) {
			
    		HyperRectangle rectangle=null; 
    		ArrayList<Bound> boundlist=new ArrayList<Bound>();
    		Object[] bounds=null;
    		
    		if (((SimpleElement)rectangeElements[j]).getChildByTagName("INCLUDES").length!=0){   
    			rectangle= new InclusiveHyperRectangle();
    			allinclusiveConstraints.add((InclusiveHyperRectangle)rectangle);
    			bounds=((SimpleElement)rectangeElements[j]).getChildByTagName("INCLUDES");
    		}
    		
    		if (((SimpleElement)rectangeElements[j]).getChildByTagName("EXCLUDES").length!=0) {
    			rectangle= new ExclusiveHyperRectangle();	
    			allexclusiveConstraints.add((ExclusiveHyperRectangle)rectangle);
    		    bounds=((SimpleElement)rectangeElements[j]).getChildByTagName("EXCLUDES");
    		}
			
    		if ((((SimpleElement)rectangeElements[j]).getChildByTagName("INCLUDES").length==0) && (((SimpleElement)rectangeElements[j]).getChildByTagName("EXCLUDES").length==0) )
    		{
    			rectangle=new InclusiveHyperRectangle(true);
    		}else {
					for (int k=0; k<bounds.length; k++) {
					    Bound b= new Bound(((SimpleElement)bounds[k]).getAttribute("index"), ((SimpleElement)bounds[k]).getAttribute("min"),((SimpleElement) bounds[k]).getAttribute("max"));
					    boundlist.add(b);	
					}  			
				   	rectangle.setBoundList(boundlist);	
    		}
	   		
	   	rectangle.setUtilityValue(Double.parseDouble(((SimpleElement)rectangeElements[j]).getAttribute("utility")));
	   	if (((SimpleElement)rectangeElements[j]).getAttribute("weight")!=null)
	   		rectangle.setWeight(Double.parseDouble(((SimpleElement)rectangeElements[j]).getAttribute("weight")));
	
	        hyperRectangleConstraints.add(rectangle);	    	        
		}
    	 	return hyperRectangleConstraints;
    	
    }
    
    
    private UtilityFunction loadUtilityFunction(SimpleElement utility){
    	
    	UtilityFunction currentFunction=new UtilityFunction();
    	// set the aggregation type 	    
	    currentFunction.setAggregationType(AGGREGATIONTYPE.getAggregationType(utility.getAttribute("aggregation")));
	    //set the weight if it is specified
	    
	    if (utility.getAttribute("weight")!=null)
	    	currentFunction.setWeight(Double.parseDouble(utility.getAttribute("weight")));
	    
	 // similarly other constraint can be parsed and add to the constraints by adding a addConstraints method
	    currentFunction.setConstraints(loadHyperRectangles(utility.getChildByTagName("hyperRectangle"))); 
	    
	  //here load inner utility functions !
    	
	    Object[] innerFunctions=((SimpleElement)utility).getChildByTagName("ufun");
	    
	    for (int k=0; k<innerFunctions.length; k++) {
	    	currentFunction.addUtilityFunction(loadUtilityFunction(((SimpleElement)innerFunctions[k])));	 
		} 
	    return currentFunction;
    }
    
	
    private void loadNonlinearSpace(SimpleElement root) {		
    	 	
    	//load reservation value
    			try {
    				if((root.getChildByTagName("reservation")!=null)&&(root.getChildByTagName("reservation").length>0)){
    					SimpleElement xml_reservation = (SimpleElement)(root.getChildByTagName("reservation")[0]);
    					this.setReservationValue(Double.valueOf(xml_reservation.getAttribute("value")));
    					System.out.println("Reservation value: "+this.getReservationValue());
    				}
    			} catch (Exception e) {
    				System.out.println("Utility space has no reservation value");
    			}
         //load discount factor
    			try {
    				if((root.getChildByTagName("discount_factor")!=null)&&(root.getChildByTagName("discount_factor").length>0)){
    					SimpleElement xml_reservation = (SimpleElement)(root.getChildByTagName("discount_factor")[0]);
    					double df = Double.valueOf(xml_reservation.getAttribute("value"));
    					this.setDiscount(validateDiscount(df));
    					System.out.println("Discount value: "+this.getDiscountFactor());
    				}
    			} catch (Exception e) {
    				System.out.println("Utility space has no discount factor;");
    			}
    			
    	//load utility 
    	Object utility= ((SimpleElement)root.getChildElements()[0]).getChildByTagName("utility")[0];
		this.setMaxUtilityValue(Double.parseDouble(((SimpleElement)utility).getAttribute("maxutility")));
	    this.nonlinearFunction=loadUtilityFunction((SimpleElement)((SimpleElement)utility).getChildByTagName("ufun")[0]);	
	}

	public double getMaxUtilityValue() {
		return maxUtilityValue;
	}

	private void setMaxUtilityValue(double maxUtilityValue) {
		this.maxUtilityValue = maxUtilityValue;
	}

	
	
	@Override
	 public double getUtility(Bid bid) throws Exception {
		 return (double)nonlinearFunction.getUtility(bid)/this.maxUtilityValue;
	 }
	  
	    
	@Override 
	public void checkReadyForNegotiation(Domain dom) throws Exception
    {
             
    }

	
	//RA We make it private for ANAC competition
	private UtilityFunction getNonlinearFunction() { 
		return nonlinearFunction;
	}

}
