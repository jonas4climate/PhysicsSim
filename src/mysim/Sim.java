package mysim;

import java.util.ArrayList;

/**
 * Main class running the simulation and updates
 * note that reducing DT reduces truncation error.
 */
public class Sim extends Constants {

   /**
    *  Modelled time of Sim in s
    */
   public static final int SIM_T_S = 3600*24*365; // 1 year

   /**
    * Delta Time (Timestep) in ms
    */
   public static final long DT_MS = 1000*60*60;

   /**
    * Delta Time (Timestep) in s
    */
   public static final double DT_S = ((double)DT_MS / 1000);

   /**
    * Iterations of modelStep
    */
   public static final double N = (SIM_T_S * (1 / DT_S));

   /**
    * List containing all currently existing objects in the Sim
    */
   private static ArrayList<PhysicsObject3D> physicsObjects = new ArrayList<>();

   /**
    * Deepcopy of initial physicsObject before simulation starts to 
    * compare final results using realtime value comparison with equational truths
    */
   private static ArrayList<PhysicsObject3D> initPhysicsObjects = new ArrayList<>();

   /**
    * Slight drift due to threading inaccuracy
    */
   public static final boolean REALTIME_ENABLED = false;

   /**
    * TBD TODO to be implemented
    */
   public static final boolean STATUS_UPDATE_ENABLED = false;

   public static long timerStart;
   public static long timerEnd;

   public static void main(String[] args) throws InterruptedException {
      setup();
      for (int i = 1; i <= N ; i++) {
         double currentTimeInSim = i * DT_S;
         System.out.println(String.format("Result for %.2fs:", currentTimeInSim));
         modelStep(REALTIME_ENABLED);
         System.out.println();
      }
      compareSimAndEquations();
   }

   /**
    * Setup before entering simulation environment
    */
   private static void setup() {
      // Add objects
      physicsObjects.add(EARTH);
      physicsObjects.add(new PhysicsObject3D("Earth-like", Math.pow(10,24), new double[] {0,0,0}, new double[] {0,0,0}, new double[] {0,0,0}));

      // State at begin of simulation (t = 0s)
      System.out.println("Initial setup at 0.00s:");
      physicsObjects.forEach((obj) -> System.out.println(obj));

      // Copy initial objects
      physicsObjects.forEach((obj) -> initPhysicsObjects.add(obj.clone()));
   }

   private static void compareSimAndEquations() {
      // take and use init and final values of objects and use newtonian laws of motion to compare drift
      System.out.println("These should be initial values.");
      initPhysicsObjects.forEach((obj) -> System.out.println(obj));
   }

   /**
    * Performs a single iteration simulating DT time
    * @throws InterruptedException
    */
   private static void modelStep(boolean realtime) throws InterruptedException {
      physicsObjects.forEach((obj) -> {
         // Reset forces
         obj.a.vector = new double[3];

         // apply gravitational forces to the object if obj not massless
         if (obj.m != 0d)
            gravity(obj);

         for (int i = 0; i < 3; i++) {
            obj.s.vector[i] += obj.v.vector[i] * DT_S;
            obj.v.vector[i] += obj.a.vector[i] * DT_S;
         }
         System.out.println(obj);
      });
      // Stop measuring time
      long timerEnd = System.nanoTime();
      if (realtime) {
         try {
            int passedTimeInMs = 0;
            // First iteration ignores this
            if (timerStart != 0)
               passedTimeInMs = (int) Math.abs(((timerEnd - timerStart) / 1000000)); // Nanotime not necessarily positive
            Thread.sleep(DT_MS - passedTimeInMs); // simulate realtime
         } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return;
         }
      }
      // Start measuring time
      timerStart = System.nanoTime();
   }

   /**
    * 
    * @param obj
    * @return
    */
   private static void gravity(PhysicsObject3D obj) {
      // Gravity Super Position Vector = total gravitational acceleration
      double[] gspV = new double[3];
      physicsObjects.forEach((obj2) -> {
         // If not same object and obj2 not massless
         if (obj != obj2 && obj2.m != 0d) {
            double ED = Vector3D.distance(obj.s, obj2.s);
            double[] dirV = Vector3D.normDirV(obj.s, obj2.s).vector.clone();
            // Force in Newtons
            double forceInN = (G_CONST * obj.m * obj2.m) / Math.pow(ED, 2);
            // F = m * a <=> a = F / m
            double acc = forceInN / obj.m;
            for (int i = 0; i < 3; i++) {
               gspV[i] += dirV[i] * acc;
            }
         }
      });
      for (int i = 0; i < 3; i++)
         obj.a.vector[i] += gspV[i];
   }
}