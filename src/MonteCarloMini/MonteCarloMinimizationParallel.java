package MonteCarloMini;
/* Serial  program to use Monte Carlo method to 
 * locate a minimum in a function
 * This is the reference sequential version (Do not modify this code)
 * Michelle Kuttel 2023, University of Cape Town
 * Adapted from "Hill Climbing with Montecarlo"
 * EduHPC'22 Peachy Assignment" 
 * developed by Arturo Gonzalez Escribano  (Universidad de Valladolid 2021/2022)
 */
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.io.FileWriter;
import java.io.IOException;
//import java.util.concurrent.RecursiveTask;

class MonteCarloMinimizationParallel extends RecursiveAction{

	static final boolean DEBUG=false;
	static int SEQ_CUTOFF;
	static final ForkJoinPool fjPool = new ForkJoinPool(); 

	static long startTime = 0;
	static long endTime = 0;
	int begin;
	int end;
	SearchParallel [] searches;		// Array of searches
	static int min;
	static int local_min;
	static int finder;

	//timers - note milliseconds
	private static void tick(){
		startTime = System.currentTimeMillis();
	}
	private static void tock(){
		endTime=System.currentTimeMillis(); 
	}

	public MonteCarloMinimizationParallel(SearchParallel [] searches, int begin, int end) {

		this.searches = searches;
		this.begin = begin;
		this.end = end;
		//this.SEQ_CUTOFF = SEQ_CUTOFF;
		
	}

	public void compute() {
		//int local_min = Integer.MAX_VALUE;
		//int min = Integer.MAX_VALUE;

		if (end-begin <= SEQ_CUTOFF) {

			//int finder = -1;
			
			for  (int i=begin;i<end;++i) {
    		local_min=searches[i].find_valleys();
    		if((!searches[i].isStopped())&&(local_min<min)) { //don't look at  those who stopped because hit exisiting path
    			min=local_min;
    			finder=i; //keep track of who found it
    		}
    		if(DEBUG) System.out.println("Search "+searches[i].getID()+" finished at  "+local_min + " in " +searches[i].getSteps());
    		}

		}
		else {
			MonteCarloMinimizationParallel monteLeft = new MonteCarloMinimizationParallel(searches, begin, ((begin + end)/2));
			MonteCarloMinimizationParallel monteRight = new MonteCarloMinimizationParallel(searches, ((begin + end)/2), end);
			monteLeft.fork();
			monteRight.compute();
			monteLeft.join();
	
		}

		
	}
	
    public static void main(String[] args)  {

    	int rows, columns; //grid size
    	double xmin, xmax, ymin, ymax; //x and y terrain limits
    	TerrainArea terrain;  //object to store the heights and grid points visited by searches
    	double searches_density;	// Density - number of Monte Carlo  searches per grid position - usually less than 1!

     	int num_searches;		// Number of searches
    	SearchParallel [] searches;		// Array of searches
    	Random rand = new Random();  //the random number generator
		
    	
    	if (args.length!=7) {  
    		System.out.println("Incorrect number of command line arguments provided.");   	
    		System.exit(0);
    	}
    	/* Read argument values */
    	rows =Integer.parseInt( args[0] );
    	columns = Integer.parseInt( args[1] );
    	xmin = Double.parseDouble(args[2] );
    	xmax = Double.parseDouble(args[3] );
    	ymin = Double.parseDouble(args[4] );
    	ymax = Double.parseDouble(args[5] );
    	searches_density = Double.parseDouble(args[6] );
  
    	if(DEBUG) {
    		/* Print arguments */
    		System.out.printf("Arguments, Rows: %d, Columns: %d\n", rows, columns);
    		System.out.printf("Arguments, x_range: ( %f, %f ), y_range( %f, %f )\n", xmin, xmax, ymin, ymax );
    		System.out.printf("Arguments, searches_density: %f\n", searches_density );
    		System.out.printf("\n");
    	}
    	
    	// Initialize 
    	terrain = new TerrainArea(rows, columns, xmin,xmax,ymin,ymax);
    	num_searches = (int)( rows * columns * searches_density );
    	searches= new SearchParallel [num_searches];

    	for (int i=0;i<num_searches;i++) 
    		searches[i]=new SearchParallel(i+1, rand.nextInt(rows),rand.nextInt(columns),terrain);
    	
      	if(DEBUG) {
    		/* Print initial values */
    		System.out.printf("Number searches: %d\n", num_searches);
    		terrain.print_heights();
    	}
    	
    	//start timer
    	tick();
    	
    	//all searches
		min = Integer.MAX_VALUE;
    	local_min=Integer.MAX_VALUE;
    	//SEQ_CUTOFF = (int)(0.015*num_searches);
		SEQ_CUTOFF = 500;

		/* 
    	for  (int i=0;i<num_searches;i++) {
    		local_min=searches[i].get_height();
    		if((!searches[i].isStopped())&&(local_min<min)) { //don't look at  those who stopped because hit exisiting path
    			min=local_min;
    			finder=i; //keep track of who found it
    		}
    		if(DEBUG) System.out.println("Search "+searches[i].getID()+" finished at  "+local_min + " in " +searches[i].getSteps());
    	}
		*/
		ForkJoinPool.commonPool().invoke(new MonteCarloMinimizationParallel(searches,0, searches.length));
   		//end timer
   		tock();
   		
    	if(DEBUG) {
    		/* print final state */
    		terrain.print_heights();
    		terrain.print_visited();
    	}
    	
		System.out.printf("Run parameters\n");
		System.out.printf("\t Rows: %d, Columns: %d\n", rows, columns);
		System.out.printf("\t x: [%f, %f], y: [%f, %f]\n", xmin, xmax, ymin, ymax );
		System.out.printf("\t Search density: %f (%d searches)\n", searches_density,num_searches );

		/*  Total computation time */
		System.out.printf("Time: %d ms\n",endTime - startTime );
		int tmp=terrain.getGrid_points_visited();
		System.out.printf("Grid points visited: %d  (%2.0f%s)\n",tmp,(tmp/(rows*columns*1.0))*100.0, "%");
		tmp=terrain.getGrid_points_evaluated();
		System.out.printf("Grid points evaluated: %d  (%2.0f%s)\n",tmp,(tmp/(rows*columns*1.0))*100.0, "%");
		
		/* Results*/
		System.out.printf("Global minimum: %d at x=%.1f y=%.1f\n\n", min, terrain.getXcoord(searches[finder].getPos_row()), terrain.getYcoord(searches[finder].getPos_col()));
		double[] data = {rows, columns, searches_density, num_searches, endTime - startTime};
		String csvFilePath = "Testing_MyLaptop/output_parallel(2).csv"; // Change this to your desired file path
        
        try (FileWriter writer = new FileWriter(csvFilePath, true)) { // Open in append mode
            for (int i = 0; i < data.length; i++) {
                String strNum = Double.toString(data[i]);
                writer.append(strNum);
                
                if (i < data.length - 1) {
                    writer.append(", "); // Add comma and space if not the last value
                }
            }
            
            writer.append("\n"); // Add a newline at the end
            
            System.out.println("Data has been written to " + csvFilePath);
            
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }
}