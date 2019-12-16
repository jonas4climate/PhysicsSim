package mysim;

import java.util.ArrayList;

/**
 * Class used to set up the simulator with all necessary information. Look into Util for constants or pre-defined objects.
 */
public abstract class Setup extends Util {

   /******************** TIME ********************/

   /**
    *  Time the simulation will model
    */
   protected static final long SIM_T_S = (long) 40 * ORBITAL_PERIOD_EARTH;

   /**
    * Delta Time (Timestep) in ms. This is the time that is modelled every time modelStep executes.
    * The lower this value the smaller the modelled increments and therefore the more precise the simulation
    */
   protected static final int DT_MS = 10000;



   /******************** SIMULATION OBJECTS ********************/

   /**
    * List containing all currently existing objects in the Sim.
    */
   protected static ArrayList<PhysicsObject3D> physicsObjects = new ArrayList<>();



  /******************** MODES ********************/

   /**
    * Slows simulation down to realtime
   */
   protected static final boolean REALTIME_ENABLED = false;

   /**
    * Receive state updates during the simulation process according to PRINT_DT.
   */
   protected static final boolean PRINT_VERBOSE = true;
 
   /**
    * Allow user to review simulation information and have countdown before simulation start (not recommended for logging or when performance is priority)
    */
   protected static final boolean PRINT_INITIALIZATION_SLOW = false;
 
   /**
    * Determines after how much passed time (in s) a status update of the current state of the simulation is printed.
    * Increasing this or setting PRINT_VERBOSE = false increases simulation speed as it reduces I/O operations.
    */
   protected static final double PRINT_DT = ORBITAL_PERIOD_EARTH;

   /**
    * Adds all objects that should be modelled in the simulation
    */
   protected static void addModelledObjects() {
      physicsObjects.add(EARTH);
      physicsObjects.add(MOON);
      physicsObjects.add(SUN);
      //physicsObjects.add(new PhysicsObject3D("Huge mass", R_SUN * 10, M_SUN * 100, new double[]{AU,0,AU}));
   }
}