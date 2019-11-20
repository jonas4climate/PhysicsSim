# Coding journey: Collision detection

After we successfully implemented the simulator with gravitational forces, it works great... but also weird things can happen.   

Since all the objects currently are point-masses and there is nothing checking for collisions, we get effects that would never happen in the real world (as in: gravitational forces of a mass like the sun pulling on an object only a meter next causing truely unrealistic gravitational pull as the objects would have long collided).   
Now mentioning this we should probably also limit the speed of objects to the speed of light $c$ (and maybe with that consider $E = m \cdot c^2$ if $v = c$ was reached) but that is another topic.   

I currently see four ways of implementing collision with varying degrees of accuracy, difficulty and computational needs. For all of them we have to define the physical shape of the objects in some way but this should most likely be similar for them all.
Let's have a look:

At every time step: 
1. Check if two objects have a **common point at their surfaces** i.e. touch   
   $\rightarrow$ very flawed, what if in one step an object moved **inside** the other (due to large $\Delta t$ or high $v$ which are very common in simulations on the scale of e.g. a solar system simulation over years)
2. Check if two objects **share common points** (considers not only surface but the whole 3D           physical shape).   
   $\rightarrow$ but we don't consider, what happens if one object passes through another during a time step (again usually due to large $\Delta t$ or high $v$ )
3. Consider the lines of movement of two objects drawn from their inital $s_s$ to updated $s_u$ position. Now use the geometric defining parameter (e.g. radius) to build a hitbox around this line of all the positions the object could have occupied in this timestep $\Delta t$ . (The geometric object for a sphere stretched along a line would be similar to a cylinder but with half-spheres at each end). If any two of these hitboxes share points, we detect a collision.   
   $\rightarrow$ Now this approach is already really good. It should cover most cases of collisions the way we would want to detect them. But if we wanted to get it _"even more right"_, we could propose:
4. Using approach 3, we have determined that two objects have a path that has the potential to cross. But we don't know if they actually hit each other without accounting for the position over time, the object could have a hitbox but would be hit by another object after it existed this position already. Now what we have left is just to sub-model the collision path in steps and check for those steps if the objects actually DO collide.

Now, this was just my train of thought but we might be able to use some geometry and algebra to assist us in deciding on which solution is computationally reasonable while giving realistic collision detection.