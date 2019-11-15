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
   public static final int SIM_T_S = 60*60*24*365;

   /**
    * Delta Time (Timestep) in ms
    */
   public static final int DT_MS = 1000;

   /**
    * Delta Time (Timestep) in s
    */
   public static final double DT_S = ((double)DT_MS / 1000);

   /**
    * Iterations of modelStep
    */
   public static final double N = (SIM_T_S * (1 / DT_S));

   /**
    * Keeps track of current time of simulation
    */
   private static double currentTimeInSim = 0d;

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
   private static final boolean REALTIME_ENABLED = false;

   /**
    * Determines whether to print current simulator state or just run quietly
    */
   private static final boolean PRINT_ENABLED = true;

   /**
    * Determines after how much passed time (in s) it prints the current status of the simulation
    */
   private static final double PRINT_DT = SIM_T_S/48;



   public static long timerStart;
   public static long timerEnd;

   public static void main(String[] args) throws InterruptedException {
      setup();
      for (int i = 1; i <= N ; i++) {
         currentTimeInSim = i * DT_S;
         if (PRINT_ENABLED && currentTimeInSim % PRINT_DT == 0)
            System.out.println(String.format("Result for %.2fs:", currentTimeInSim));
         modelStep();
         if (PRINT_ENABLED && currentTimeInSim % PRINT_DT == 0)
            System.out.println();
      }
      // State at end of simulation
      System.out.println(String.format("Final state at %ds:", SIM_T_S));
      physicsObjects.forEach((obj) -> {
         System.out.println(obj);
         // TODO remove when nicer way implemented
         physicsObjects.forEach((obj2) -> {
            if (obj != obj2)
               System.out.println(String.format("ED: %s is %.2fm away from %s.",obj.name, Vector3D.distance(obj.s, obj2.s), obj2.name));
         });
      });
      //compareSimAndEquations();
   }

   /**
    * Setup before entering simulation environment
    */
   private static void setup() {
      // Add objects
      physicsObjects.add(EARTH);
      physicsObjects.add(MOON);
      physicsObjects.add(SUN);
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
   private static void modelStep() throws InterruptedException {
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
         if (PRINT_ENABLED && currentTimeInSim % PRINT_DT == 0) {
            System.out.println(obj);
            // TODO remove when nicer way implemented
            physicsObjects.forEach((obj2) -> {
               if (obj != obj2)
                  System.out.println(String.format("ED: %s is %.2fm away from %s.",obj.name, Vector3D.distance(obj.s, obj2.s), obj2.name));
            });
         }
      });
      // Stop measuring time
      long timerEnd = System.nanoTime();
      if (REALTIME_ENABLED) {
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