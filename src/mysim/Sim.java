package mysim;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Stack;

/**
 * Main class running the simulation and updates.
 * Note that reducing DT reduces truncation error.
 */
public class Sim extends Util {
 

   /******************** TIME ********************/

   /**
    *  Modelled time of Sim in s
    */
   public static final int SIM_T_S = ORBITAL_PERIOD_EARTH/12;

   /**
    * Delta Time (Timestep) in ms
    */
   public static final int DT_MS = 100;

   /**
    * Delta Time (Timestep for modelling) in s
    */
   public static final double DT_S = ((double)DT_MS / 1000);

   /**
    * number of iterations of modelStep to complete simulation
    */
   public static final int N = (int) (SIM_T_S / DT_S);

   /**
    * Keeps track of current time of simulation
    */
   private static double currentTimeInSim = 0d;

   

   /******************** SIMULATION OBJECTS ********************/

   /**
    * List containing all currently existing objects in the Sim
    */
   private static ArrayList<PhysicsObject3D> physicsObjects = new ArrayList<>();

   /**
    * Deepcopy of initial physicsObject before start of simulation
    */
   private static ArrayList<PhysicsObject3D> initPhysicsObjects = new ArrayList<>();



   /******************** MODES ********************/

   /**
    * Slows simulation down to realtime
    */
   private static final boolean REALTIME_ENABLED = false;

   /**
    * Set to true to enable state updates during the simulation process.
    */
   private static final boolean PRINT_VERBOSE = true;

   /**
    * Determines after how much passed time (in s) it prints the current state of the simulation.
    * Increasing this or setting PRINT_VERBOSE to false greatly increases simulation speed.
    */
   private static final double PRINT_DT = SIM_T_S/30;



   /******************** UTIL ********************/

   /**
    * Used for timekeeping for Thread.sleep in REALTIME mode
    */
   private static long timerStart;

   /**
    * Used for timekeeping for Thread.sleep in TREALTIME mode
    */
   private static long timerEnd;

   /**
    * Contains all objects that are supposed to be removed after lambda-looping over all objects. (Currently all objects colliding)
    */
   private static Stack<PhysicsObject3D> objToRemove = new Stack<PhysicsObject3D>();

   /**
    * Contains all objects that are supposed to be added after lambda-looping over all objects. (Currently all objects created after collision)
    */
   private static Stack<PhysicsObject3D> objToAdd = new Stack<PhysicsObject3D>();



   public static void main(String[] args) throws InterruptedException {
      // Current model simulates solar system but with one huge unusual object to display and test new collision feature

      // create space with objects
      setup();

      for (int i = 1; i <= N ; i++) {
         currentTimeInSim = i * DT_S;

         if (PRINT_VERBOSE && currentTimeInSim % PRINT_DT == 0) {
            System.out.println(String.format("\nProgress %.0f%% - Result for %.2fs:", (double) (((long)100*i)/N), currentTimeInSim));
            System.out.println("-------------------------------------");
         }
            
         modelStep();

         if (PRINT_VERBOSE && currentTimeInSim % PRINT_DT == 0)
            System.out.println();
      }
      printFinalState();
      //compareSimAndEquations();
   }

   /**
    * Setup before entering simulation environment. Add all simulation objects here and print initial setup of simulation. 
    * Also keep deepcopy of initial elements if you want to compare final values with it later
    */
   private static void setup() {
      // Add objects
      physicsObjects.add(EARTH);
      physicsObjects.add(MOON);
      physicsObjects.add(SUN);
      physicsObjects.add(new PhysicsObject3D("Huge mass", R_SUN * 10, M_SUN * 100, new double[]{AU,0,AU}));

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
            if (obj != obj2 && primitiveCollisionCheck(obj, obj2) && !(objToRemove.contains(obj) || objToRemove.contains(obj2)))
               handleCollisions(obj, obj2);
         });

         // update position and velocity in space for DT
         for (int i = 0; i < 3; i++) {
            obj.s.vector[i] += obj.v.vector[i] * DT_S;
            obj.v.vector[i] += obj.a.vector[i] * DT_S;
         }

         // print distance to other objects in the simulation
         if (PRINT_VERBOSE && currentTimeInSim % PRINT_DT == 0) {
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
      timerEnd = System.nanoTime();

      // Adjust timing for REALTIME mode
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
         System.out.println(String.format("Progress %.0f%% - Event occured at %.2fs:", (double) (((long)100*(currentTimeInSim/DT_S))/N), currentTimeInSim));
         System.out.println(String.format("\nCollision of %s and %s occured. Objects merged into new object %s\n\n%s", obj.name, obj2.name, collisionObj.name, collisionObj));
         System.out.println("-----------------------------------------\n");
      }
   }

   private static void printInitialState() {
      System.out.println("\nSimulator settings for simulation:");
      System.out.println("----------------------------------");
      System.out.println("Simulation performed at: " + Calendar.getInstance().getTime().toString());
      System.out.println(String.format("Simulated time = %dd %dh %dm %ds \nSimulation steps (precision) = %.3fs \nREALTIME_ENABLED = %b \nPRINT_VERBOSE = %b",
       SIM_T_S / 86400, SIM_T_S % 86400 / 3600, SIM_T_S % 3600 / 60, SIM_T_S % 60, // time of sim in d h m s
       DT_S, REALTIME_ENABLED, PRINT_VERBOSE));
      System.out.println("\n\nObjects in the simulation:");
      System.out.println("--------------------------");
      physicsObjects.forEach((obj) -> {
         System.out.println(obj);
         physicsObjects.forEach((obj2) -> {
            if (obj != obj2)
               System.out.println(String.format("            %6.2em away from %s.", Vector3D.distance(obj.s, obj2.s), obj2.name));
         });
         System.out.println();
      });
      System.out.println("\n");
   }

   /**
    * Prints state at the end of the simulation
    */
   private static void printFinalState() {
      System.out.println("SIMULATION COMPLETED.\n");
      //System.out.println(String.format("Runtime = %ds"),runtime); //TODO add runtime
      System.out.println(String.format("Progress 100%% - Final state at %ds:", SIM_T_S));
      System.out.println("-------------------------------------");
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