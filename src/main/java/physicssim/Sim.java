package physicssim;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Stack;

/**
 * Main class running the simulation and updates.
 */
public abstract class Sim extends Setup {
 

   /******************** TIME ********************/

   /**
    * Delta Time (Timestep for modelling) in s
    */
   private static final double DT_S = ((double)DT_MS / 1000);

   /**
    * number of iterations of modelStep to complete simulation
    */
   private static final long N = (long) (SIM_T_S / DT_S);

   /**
    * Keeps track of current time of simulation in seconds
    */
   private static double timeInSim;

   

   /******************** SIMULATION OBJECTS ********************/

   /**
    * Deepcopy of initial physicsObject before start of simulation
    */
   private static ArrayList<PhysicsObject3D> initPhysicsObjects = new ArrayList<>();



   /******************** OTHERS ********************/

   /**
    * Flag whether to print this iteration or not
    */
   private static boolean printThisIter;

   /**
    * Used for timekeeping for Thread.sleep in REALTIME mode
    */
   private static long REALTIME_timerStart;

   /**
    * Used for timekeeping for Thread.sleep in TREALTIME mode
    */
   private static long REALTIME_timerEnd;

   /**
    * Time at start of simulation
    */
   private static long totalTimerStart;

   /**
    * Time at end of simulation
    */
   private static long totalTimerEnd;

   /**
    * Counting time and modified at runtime to manage when to print
    */
   private static double timeForPrint;

   /**
    * Contains all objects that are supposed to be removed (due to collision) after lambda-looping over all objects.
    */
   private static Stack<PhysicsObject3D> objToRemove = new Stack<PhysicsObject3D>();

   /**
    * Contains all objects that are supposed to be added (due to collision) after lambda-looping over all objects.
    */
   private static Stack<PhysicsObject3D> objToAdd = new Stack<PhysicsObject3D>();



   public static void main(String[] args) throws InterruptedException {
      setup();

      totalTimerStart = System.nanoTime();

      while (timeInSim < SIM_T_S) {
         timeInSim += DT_S;

         // Manage priting at correct iterations based on PRINT_DT
         timeForPrint += DT_S;
         if (timeForPrint > PRINT_DT_S) {
            timeForPrint -= PRINT_DT_S;
            printThisIter = true;
         } else {
            printThisIter = false;
         }


         if (PRINT_VERBOSE && printThisIter) {
            System.out.println(String.format("\nProgress %.2f%% - Result for %dd %dh %dm %ds:",
            (100*timeInSim/DT_S)/N, (int) (timeInSim / 86400), (int) (timeInSim % 86400 / 3600), 
            (int) (timeInSim % 3600 / 60), (int) (timeInSim % 60)));
            System.out.println("-------------------------------------");
         }
            
         modelStep();

         if (PRINT_VERBOSE && printThisIter)
            System.out.println();
      }

      totalTimerEnd = System.nanoTime();

      printFinalState();
   }

   /**
    * Setup before entering simulation environment. Creates deepcopy of initial elements in case you would want to compare final values with it later
    */
   private static void setup() {
      Setup.addModelledObjects();

      // State at begin of simulation (t = 0s)
      printInitialState();

      // Copy initial objects
      physicsObjects.forEach((obj) -> initPhysicsObjects.add(obj.clone()));
   }

   /**
    * Performs a single iteration of the simulation simulating DT time. 
    * Applies gravity and moves objects, checks and handles collisions
    * @throws InterruptedException exception in case the Thread for realtime mode gets interrupted
    */
   private static void modelStep() throws InterruptedException {
      physicsObjects.forEach((obj) -> {
         // reset forces
         obj.a.vector = new double[3];

         // apply gravitational forces to the object if obj not massless
         if (obj.m > 0d)
            gravity(obj);

         // collision detection
         physicsObjects.forEach((obj2) -> {
            // Make sure that objects that are checked for collision have not already collided before
            if (obj != obj2 && primitiveCollisionCheck(obj, obj2) && !(objToRemove.contains(obj) || objToRemove.contains(obj2)))
               handleCollisions(obj, obj2);
         });

         // update position and velocity in space for DT
         for (int i = 0; i < 3; i++) {
            obj.s.vector[i] += obj.v.vector[i] * DT_S;
            obj.v.vector[i] += obj.a.vector[i] * DT_S;
         }

         // print distance to other objects in the simulation
         if (PRINT_VERBOSE && printThisIter) {
            System.out.println(obj);
            physicsObjects.forEach((obj2) -> {
               if (obj != obj2)
                  System.out.println(String.format("            %6.2em away from %s.", Vector3D.distance(obj.s, obj2.s), obj2.name));
            });
            System.out.println();
         }
      });

      // Remove collided objects
      while (objToRemove.size() > 0) {
         if (physicsObjects.contains(objToRemove.peek())) {
            PhysicsObject3D collidedObj = objToRemove.pop();
            physicsObjects.remove(collidedObj);
         } else
            throw new IllegalStateException("Tried to remove an object that didn't exist but should have.");
      }

      // Add new objects created by collisions (merged from two colliding objects)
      while (objToAdd.size() > 0) {
         PhysicsObject3D collisionObj = objToAdd.pop();
         physicsObjects.add(collisionObj);
      }


      // Stop measuring time
      REALTIME_timerEnd = System.nanoTime();

      // Adjust timing for REALTIME mode
      if (REALTIME_ENABLED) {
         try {
            int passedTimeInMs = 0;
            // First iteration ignores this
            if (REALTIME_timerStart != 0)
               passedTimeInMs = (int) Math.abs(((REALTIME_timerEnd - REALTIME_timerStart) / 1000000)); // Nanotime not necessarily positive
            Thread.sleep(DT_MS - passedTimeInMs); // simulate realtime
         } catch (IllegalArgumentException e) {
            System.out.println("IllegalArgumentException: Tried to make Thread sleep < 0ms. Potentially caused by simulation running too slowly to allow REALTIME_MODE.");
            e.printStackTrace();
            System.exit(1);
         }
      }

      // Start measuring time
      REALTIME_timerStart = System.nanoTime();
   }

   /**
    * Adds gravitational forces acting on this object to its a
    * @param obj object that is being gravitationally pulled by other object's mass
    */
   private static void gravity(PhysicsObject3D obj) {
      // Gravity Super Position Vector = total gravitational acceleration for this object
      if (obj.m > 0) {
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
      } else {

      }
   }


   /**
    * Simple first way of checking if two objects collide. Assumes all objects are spheres and checks if any two objects are closer than their added radii
    * @param obj potentially colliding object
    * @param obj2 potentially colliding object
    * @return true if they collide
    */
   private static boolean primitiveCollisionCheck(PhysicsObject3D obj, PhysicsObject3D obj2) {
      double collisionDistance = obj.r + obj2.r;
      double distance = Vector3D.distance(obj.s, obj2.s);
      if (distance < collisionDistance) {
         return true;
      }
      return false;
   }

   /**
    * Handles collision occurences of given objects by removing them and adding an object that represents the objects in a merged state. 
    * Its new properties are physically accurately calculated based of collision information and object information of the colliding objects.
    * @param obj colliding object
    * @param obj2 colliding object
    */
   private static void handleCollisions(PhysicsObject3D obj, PhysicsObject3D obj2) {
      // For new m
      double new_m = obj.m + obj2.m;


      // For new r
      double totalVol = obj.getVolume() + obj2.getVolume();
      double new_r = Math.sqrt(totalVol/((4/3)*Math.PI));


      // For new s
      Vector3D helper_distance_vector = Vector3D.substract(obj2.s, obj.s);
      helper_distance_vector.scale(0.5);

      Vector3D new_s = Vector3D.add(obj.s, helper_distance_vector);


      // For new v
      double obj_Ekin = obj.getKineticEnergy();
      double obj2_Ekin = obj2.getKineticEnergy();
      double totalKin = obj_Ekin + obj2_Ekin;
      // Scale vectors relative to the their total kinetic energy
      Vector3D obj_v = obj.v.clone();
      obj_v.scale(obj_Ekin/totalKin);
      Vector3D obj2_v = obj2.v.clone();
      obj2_v.scale(obj2_Ekin/totalKin);
   
      Vector3D new_v = Vector3D.add(obj_v, obj2_v);


      PhysicsObject3D collisionObj = new PhysicsObject3D(new_r, new_m, new_s.vector, new_v.vector);

      objToAdd.add(collisionObj);
      objToRemove.add(obj);
      objToRemove.add(obj2);

      if (PRINT_VERBOSE) {
         System.out.println("\n-----------------------------------------");
         System.out.println(String.format("Progress %.0f%% - Event occured at %dd %dh %dm %ds:", (100*timeInSim/DT_S)/N, (int) (timeInSim / 86400), (int) (timeInSim % 86400 / 3600), 
         (int) (timeInSim % 3600 / 60), (int) (timeInSim % 60)));
         System.out.println(String.format("\nCollision of %s and %s occured. Objects merged into new object %s\n\n%s", obj.name, obj2.name, collisionObj.name, collisionObj));
         System.out.println("-----------------------------------------\n");
      }
   }

   private static void printInitialState() {
      // Disabling timer currently not allowed so user verify their input at simulation begin
      System.out.println("\nTo view this simulation, please set Terminal width > 141\n");
      if (PRINT_INITIALIZATION_SLOW) {
         try {
            Thread.sleep(3000);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
      System.out.println("\nSimulator settings for simulation:");
      System.out.println("----------------------------------");
      System.out.println(String.format("Simulation performed at: %s\nSimulated time = %dd %dh %dm %ds \nSimulation steps (precision) = %.3fs \nREALTIME_ENABLED = %b \nPRINT_VERBOSE = %b", 
      Calendar.getInstance().getTime().toString(), 
      SIM_T_S / 86400, SIM_T_S % 86400 / 3600, SIM_T_S % 3600 / 60, SIM_T_S % 60, // time of sim in d h m s
      DT_S, 
      REALTIME_ENABLED, 
      PRINT_VERBOSE));

      if (PRINT_INITIALIZATION_SLOW) {
         try {
            Thread.sleep(5000);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
      System.out.println("\n\nObjects in the simulation:");
      System.out.println("--------------------------");
      physicsObjects.forEach((obj) -> {
         System.out.println(obj);
         /*physicsObjects.forEach((obj2) -> {
            if (obj != obj2)
               System.out.println(String.format("            %6.2em away from %s.", Vector3D.distance(obj.s, obj2.s), obj2.name));
         });*/
         System.out.println();
      });
      if (PRINT_INITIALIZATION_SLOW) {
         try {
            System.out.println("\n-Simulation Start Timer-");
            Thread.sleep(1000);
            for (int i = 5; i > 0; i--) {
               System.out.print(i);
               Thread.sleep(250);
               for (int j = 0; j < 3; j++) {
                  System.out.print(".");
                  Thread.sleep(250);
               }
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
      System.out.println("\n");
   }

   /**
    * Prints state at the end of the simulation
    */
   private static void printFinalState() {
      long runtime = totalTimerEnd - totalTimerStart; // in ns
      System.out.println("SIMULATION COMPLETED!");
      System.out.println("---------------------\n");
      System.out.println(String.format("Runtime = %ds %dms %dμs %dηs\n", (runtime/1000000000) % 1000, (runtime/1000000) % 1000, (runtime/1000) % 1000, runtime % 1000));
      System.out.println(String.format("Final state at %dd %dh %dm %ds:", 
      (int) (SIM_T_S / 86400), (int) (SIM_T_S % 86400 / 3600), (int) (SIM_T_S % 3600 / 60), (int) (SIM_T_S % 60)));
      System.out.println("-------------------------------");
      physicsObjects.forEach((obj) -> {
         System.out.println(obj);
         physicsObjects.forEach((obj2) -> {
            if (obj != obj2)
               System.out.println(String.format("            %6.2em away from %s.", Vector3D.distance(obj.s, obj2.s), obj2.name));
         });
         System.out.println();
      });
   }
}