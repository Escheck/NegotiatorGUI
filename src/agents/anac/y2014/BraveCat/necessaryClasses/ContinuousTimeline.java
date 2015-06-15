 package agents.anac.y2014.BraveCat.necessaryClasses;

 import negotiator.Timeline;
 public class ContinuousTimeline extends Timeline
 {
   protected long pauseTime = 0;
   private final int totalSeconds;
   protected long startTime;
 
   public ContinuousTimeline(int totalSecs)
   {
     this.totalSeconds = totalSecs;
     this.startTime = System.nanoTime();
     this.hasDeadline = true;
     System.out.println("Started time line of " + totalSecs + " seconds.");
   }
 
   public double getElapsedSeconds()
   {
     long t2 = System.nanoTime();
     return (t2 - this.startTime) / 1000000000.0D;
   }
 
   public double getElapsedMilliSeconds()
   {
     long t2 = System.nanoTime();
     return (t2 - this.startTime) / 1000000.0D;
   }
 
   public long getTotalMiliseconds()
   {
     return 1000 * this.totalSeconds;
   }
 
   public long getTotalSeconds()
   {
     return this.totalSeconds;
   }
 
   public void printElapsedSeconds()
   {
     System.out.println("Elapsed: " + getElapsedSeconds() + " seconds");
   }
 
   @Override
   public void printTime()
   {
     System.out.println("t = " + getTime());
   }
 
   @Override
   public double getTime()
   {
     double t = getElapsedSeconds() / this.totalSeconds;
     if (t > 1.0D)
       t = 1.0D;
     return t;
   }
 
   @Override
   public double getTotalTime()
   {
     return getTotalSeconds();
   }
 
   @Override
   public double getCurrentTime()
   {
     return getElapsedSeconds();
   }
  @Override
   public void pause()
   {
       this.paused = true;
       this.pauseTime = System.nanoTime();
   }
   @Override
   public void resume()
   {
       this.paused = false;
       this.startTime += (System.nanoTime() - this.pauseTime);
   }
 }