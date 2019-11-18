package mysim;

import java.util.ArrayList;

/**
 * Main class running the simulation and updates.
 * Note that reducing DT reduces truncation error.
 */
public class Sim extends Constants {
 
   /**
    *  Modelled time of Sim in s
    */
   public static final int SIM_T_S = (int) ORBITAL_PERIOD_EARTH;

   /**
    * Delta Time (Timestep) in ms
    */
   public static final int DT_MS = 1000;

   /**
    * Delta Time (Timestep for modelling) in s
    */
   public static final double DT_S = ((double)DT_MS / 1000);

   /**
    * number of iterations of modelStep to complete simulation
    */
   public static final int N = (int) (SIM_T_S * (1 / DT_S));

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
    * Slows simulation down to realtime
    */
   private static final boolean REALTIME_ENABLED = false;

   /**
    * Set to true to enable state updates during the simulation process
    */
   private static final boolean PRINT_ENABLED = true;

   /**
    * Determines after how much passed time (in s) it prints the current state of the simulation.
    * Reducing this or setting PRINT_ENABLED = false greatly increases simulation speed.
    */
   private static final double PRINT_DT = SIM_T_S/(12);

   /**
    * Used for timekeeping for Thread.sleep in REALTIME mode
    */
   private static long timerStart;

   /**
    * Used for timekeeping for Thread.sleep in TREALTIME mode
    */
   private static long timerEnd;

   public static void main(String[] args) throws InterruptedException {
      setup();
      for (int i = 1; i <= N ; i++) {
         currentTimeInSim = i * DT_S;
         if (PRINT_ENABLED && currentTimeInSim % PRINT_DT == 0)
            System.out.println(String.format("Progress %.0f%% - Result for %.2fs:", (double) (((long)100*i)/N), currentTimeInSim));
         modelStep();
         if (PRINT_ENABLED && currentTimeInSim % PRINT_DT == 0)
            System.out.println();
      }
      // State at end of simulation
      printFinalState();
      //compareSimAndEquations();
   }

   /**
    * Setup before entering simulation environment. Add all simulation objects here and print initial setup of simulation. 
    * Also keep deepcopy of initial elements to compare with formula later
    */
   private static void setup() {
      // Add objects
      physicsObjects.add(EARTH);
      physicsObjects.add(MOON);
      physicsObjects.add(SUN);

      // State at begin of simulation (t = 0s)
      printInitialState();

      // Copy initial objects
      physicsObjects.forEach((obj) -> initPhysicsObjects.add(obj.clone()));
   }

   /**
    * Performs a single iteration of the simulation simulating DT time
    * @throws InterruptedException
    */
   private static void modelStep() throws InterruptedException {
      physicsObjects.forEach((obj) -> {
         // Reset forces
         obj.a.vector = new double[3];

         // apply gravitational forces to the object if obj not massless
         if (obj.m != 0d)
            gravity(obj);

         // Update position and velocity in space for DT
         for (int i = 0; i < 3; i++) {
            obj.s.vector[i] += obj.v.vector[i] * DT_S;
            obj.v.vector[i] += obj.a.vector[i] * DT_S;
         }

         // Print distance to other objects in the simulation
         if (PRINT_ENABLED && currentTimeInSim % PRINT_DT == 0) {
            System.out.println(obj);
            physicsObjects.forEach((obj2) -> {
               if (obj != obj2)
                  System.out.println(String.format("            %6.2em away from %s.", Vector3D.distance(obj.s, obj2.s), obj2.name));
            });
         }
      });

      // Stop measuring time
      timerEnd = System.nanoTime();

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
    * Adds gravitational forces acting on this object to its a
    * @param obj
    * @return
    */
   private static void gravity(PhysicsObject3D obj) {
      // Gravity Super Position Vector = total gravitational acceleration for this object
      if (obj.m >= 0) {
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

   /**
    * Use init and final values of objects of the simulation and use laws of motion to determine accuracy. Only possible in certain cases
    */
   private static void compareSimAndEquations() {
      System.out.println("\n\n\nSimulation accuracy analysis:");
      System.out.println("Initial objects:");
      initPhysicsObjects.forEach((obj) -> System.out.println(obj));
   }

   private static void printInitialState() {
      System.out.println("Progress 0% - Initial setup at 0.00s:");
      physicsObjects.forEach((obj) -> System.out.println(obj));
      System.out.println("\n");
   }

   /**
    * Prints state at the end of the simulation
    */
   private static void printFinalState() {
      System.out.println(String.format("Progress 100%% - Final state at %ds:", SIM_T_S));
      physicsObjects.forEach((obj) -> {
         System.out.println(obj);
         physicsObjects.forEach((obj2) -> {
            if (obj != obj2)
               System.out.println(String.format("            %6.2em away from %s.", Vector3D.distance(obj.s, obj2.s), obj2.name));
         });
      });
   }
}