 package agents.anac.y2014.BraveCat.OpponentModels;

 import negotiator.Bid;
 
 public class NoModel extends OpponentModel
 {
     @Override
      public void updateModel(Bid opponentBid, double time)
       {
       }
     
     @Override
      public String getName()
     {
       return "No Model";
     }
 }