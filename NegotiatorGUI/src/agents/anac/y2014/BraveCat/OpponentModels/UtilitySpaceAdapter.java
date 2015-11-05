 package agents.anac.y2014.BraveCat.OpponentModels;

 import negotiator.Bid;
 import negotiator.Domain;
 import negotiator.utility.AdditiveUtilitySpace;
 
 public class UtilitySpaceAdapter extends AdditiveUtilitySpace
 {
   private OpponentModel opponentModel;
 
   public UtilitySpaceAdapter(OpponentModel opponentModel, Domain domain)
   {
     this.opponentModel = opponentModel;
     this.domain = domain;
   }
 
   @Override
   public double getUtility(Bid b)
   {
     double u = 0.0D;
     try {
       u = this.opponentModel.getBidEvaluation(b);
     }
     catch (Exception e) {
       System.err.println("getNormalizedUtility failed. returning 0");
       u = 0.0D;
     }
     return u;
   }
 
   @Override
   public double getWeight(int i)
   {
     System.err.println("The opponent model should overwrite getWeight() when using the UtilitySpaceAdapter");
     return i;
   }
 }