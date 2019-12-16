# Physics Simulator

In this private project I am creating a Physics Simulator in Java from scratch.

The simulator is currently used to simulate **gravity in 3D space** and is capable of simulating [n-body-simulations](https://en.wikipedia.org/wiki/N-body_problem) such as the Sun-Earth-Moon system over years with astounding precision within seconds of runtime.

The simulator comes with a variety of features including 
- extensive output/logging
- collision detection and handling (physically accurate merging)
- easy and centralized customizability in the [Setup class](src/mysim/Setup.java)
- real-time mode

All documentation can be generated running `make javadoc`.

Have a look into the `example.log` and modify the `Sim.java` class as needed. Run `make run` to generate your own simulation with output to terminal or `make log` to move output into log file. 

Example log file:

![log](example-log.png)

The current version is fully capable to simulate the collision and gravity segments of the Physics engine but no GUI has been implemented yet (looking into that).
