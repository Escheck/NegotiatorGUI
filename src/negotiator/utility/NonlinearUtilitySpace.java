package negotiator.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import negotiator.Bid;
import negotiator.Domain;
import negotiator.Timeline;
import negotiator.xml.SimpleDOMParser;
import negotiator.xml.SimpleElement;
import negotiator.utility.AGGREGATIONTYPE;



public class NonlinearUtilitySpace extends UtilitySpace {
	
	private double maxUtilityValue;
	private UtilityFunction nonlinearFunction;
	
	// add some parameters for discount factor
	/**
     * Creates an empty nonlinear utility space.
     */
    public NonlinearUtilitySpace(){
    	this.domain = new Domain();
    	this.nonlinearFunction=new UtilityFunction();
    	spaceType=UTILITYSPACETYPE.NONLINEAR;
    }
    
    public NonlinearUtilitySpace(Domain domain) {
    	this.domain=domain;
    	this.nonlinearFunction=new UtilityFunction();
    	spaceType=UTILITYSPACETYPE.NONLINEAR;
    }

    public NonlinearUtilitySpace(Domain domain, String fileName) throws Exception{
    	 this.domain = domain;
    	 this.nonlinearFunction=new UtilityFunction();
    	 this.fileName = fileName;
         spaceType=UTILITYSPACETYPE.NONLINEAR;         
         
         if(!fileName.equals("")) {
        	 SimpleDOMParser parser = new SimpleDOMParser();
             BufferedReader file = new BufferedReader(new FileReader(new File(fileName)));                  
             SimpleElement root = parser.parse(file);
             fXMLRoot = root;             
        	 loadNonlinearSpace(root);
        	 
         }else throw new IOException();        	  
    }
    
    /** @return a clone of another utility space */
    public NonlinearUtilitySpace(UtilitySpace us)    {
    	domain=us.getDomain();
    	fileName = us.getFileName();
    	spaceType=UTILITYSPACETYPE.NONLINEAR;
    	fXMLRoot = us.getXMLRoot();
    	maxUtilityValue = ((NonlinearUtilitySpace)us).getMaxUtilityValue();
    	nonlinearFunction=((NonlinearUtilitySpace)us).getNonlinearFunction();     	
    	
    }
    //This method parse xml file and load nonlinear utility space 
    
    private ArrayList<Constraint> loadHyperRectangles(Object[] rectangeElements) {
    	
    	ArrayList<Constraint> hyperRectangleConstraints=new ArrayList<Constraint>();
    	
    	for (int j=0; j<rectangeElements.length; j++) {
			
			HyperRectangle rectangle=new HyperRectangle();
			ArrayList<Bound> boundlist=new ArrayList<Bound>();
			Object[] bounds=((SimpleElement)rectangeElements[j]).getChildByTagName("bound");
			    			    			
			for (int k=0; k<bounds.length; k++) {
			    Bound b= new Bound(((SimpleElement)bounds[k]).getAttribute("index"), ((SimpleElement)bounds[k]).getAttribute("min"),((SimpleElement) bounds[k]).getAttribute("max"));
			    boundlist.add(b);	
			}  			
				    			   		
		   	rectangle.setUtilityValue(Double.parseDouble(((SimpleElement)rectangeElements[j]).getAttribute("utility")));
		   	if (((SimpleElement)rectangeElements[j]).getAttribute("weight")!=null)
		   		rectangle.setWeight(Double.parseDouble(((SimpleElement)rectangeElements[j]).getAttribute("weight")));
		   	rectangle.setBoundlist(boundlist);
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
    	 	
    	//load utility 
		Object utility= ((SimpleElement)root.getChildElements()[0]).getChildByTagName("utility")[0];
		this.setMaxUtilityValue(Double.parseDouble(((SimpleElement)utility).getAttribute("maxutility")));
	    this.nonlinearFunction=loadUtilityFunction((SimpleElement)((SimpleElement)utility).getChildByTagName("ufun")[0]);	
	}

	public double getMaxUtilityValue() {
		return maxUtilityValue;
	}

	public void setMaxUtilityValue(double maxUtilityValue) {
		this.maxUtilityValue = maxUtilityValue;
	}

	
	
	@Override
	 public double getUtility(Bid bid) throws Exception {
	//	System.out.println("utility:"+nonlinearFunction.getUtility(bid));
		 return (double)nonlinearFunction.getUtility(bid)/this.maxUtilityValue;
	 }
	  
	    
	@Override 
	public void checkReadyForNegotiation(Domain dom) throws Exception
    {
             
    }
	
	@Override
	public double getUtilityWithDiscount(Bid bid, Timeline timeline) {
		//This parts need to be implemented. For now, we will use utility without discount
		try {
			return getUtility(bid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0.0;		
	}
    
	@Override
	public double getUtilityWithDiscount(Bid bid, double time) {
		//This parts need to be implemented. For now, we will use utility without discount
		try {
			return getUtility(bid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0.0;	
	}

	public UtilityFunction getNonlinearFunction() {
		return nonlinearFunction;
	}

	public void setNonlinearFunction(UtilityFunction nonlinearFunction) {
		this.nonlinearFunction = nonlinearFunction;
	}
	
}