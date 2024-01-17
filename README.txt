# Parallel Programming with Java:
# Parallelizing Monte Carlo Function Optimisation

## Description
Monte Carlo algorithm for
finding the minimum (the lowest point) of a two-dimensional mathematical function
f(x,y) within a specified range.

A Terrain area is initialized at the beginning of the program using a 2d array. A terrain area made up of x and y-coordinates is initialized and heights at each point are initialized to Integer.MAX_VALUE, the terrain is divided up into rows and columns.

Each search object, lands on a particular block and the respective x-coordinate, y-coordinate and height is calculated as follows in the get_height function: 

double x_coord = xmin + ( (xmax - xmin) / rows ) * x;
double y_coord = ymin + ( (ymax - ymin) / columns ) * y;
/* Compute function value */
double value = -2 * Math.sin(x_coord) * Math.cos(y_coord/2.0) + Math.log( Math.abs(y_coord - Math.PI*2) );

## Usage
1. Navigate to the directory of the folder containing folders bin and src
2. Compile the program with make
3. When running the program input make run ARGS="arg1 arg2 arg3 arg4 arg5 arg6 arg7"
   Where args={rows, columns, xmin, xmax, ymin, ymax, Search Density}
4. The program will run the parallel version of the algorithm and output the results of the search.

