from mpl_toolkits import mplot3d
import numpy as np
import matplotlib.pyplot as plt
import csv

fig = plt.figure()
ax = plt.axes(projection='3d')

file = open("data.csv", "r")
reader = csv.reader(file)
for line in reader:
   t = line[0],line[1],line[2]
   print(t)

# Data for a three-dimensional line
zline = np.linspace(0, 10, 30)
xline = np.linspace(0, 10, 30)
yline = np.sin(zline)
ax.plot3D(xline, yline, zline, 'blue')

# x,y,z = [1,2,3],[2,3,4],[3,4,5]
# ax.plot_wireframe(x,y,z)
# Data for three-dimensional scattered points
zdata = 1 * np.random.random(10)
xdata = 1 * np.random.random(10)
ydata = 1 * np.random.random(10)
ax.scatter3D(xdata, ydata, zdata, c=zdata, cmap="Blues");

zdata = 1 * np.random.random(10)
xdata = np.sin(zdata) + 0.1 * np.random.randn(10)
ydata = np.cos(zdata) + 0.1 * np.random.randn(10)
ax.scatter3D(xdata, ydata, zdata, c=zdata);

plt.show()