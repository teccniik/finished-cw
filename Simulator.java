import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing zebras, deers and lions, bears, tigers.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probability that a lion, tiger, bear will be created in any given grid position.
    private static final double LION_CREATION_PROBABILITY = 0.07;
    private static final double BEAR_CREATION_PROBABILITY = 0.08;
    private static final double TIGER_CREATION_PROBABILITY = 0.11;

    // The probability that a zebra and deer will be created in any given grid position.
    private static final double DEER_CREATION_PROBABILITY = 0.16;   
    private static final double ZEBRA_CREATION_PROBABILITY = 0.12;
    
    private static final double PLANT_CREATION_PROBABILITY = 0.6;

    //
    protected static boolean isRunning;
    // List of animals in the field.
    private List<Actor> animals;
    private List<Actor> plants;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    // current time of day of the simulation
    public static int time;
    // number to track current season of the simulation
    private int seasonTrack;
    // current season of simulation
    public static int season = 0; 
    // number to track weather
    public int weather = 0; 
    // number for rain
    public static Double rain = 0.0;
    // value to check if it has rained during current season
    public static boolean rainedThisSeason;
    
    // Random number generator
    private static final Random rand = Randomizer.getRandom();

    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }

    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        animals = new ArrayList<>();
        plants = new ArrayList<>();
        field = new Field(depth, width);

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width, this);
        view.setColor(Deer.class, new Color(221, 150, 0));
        view.setColor(Zebra.class, Color.BLACK);        
        view.setColor(Lion.class, Color.BLUE);
        view.setColor(Bear.class, new Color(95, 64, 1));
        view.setColor(Tiger.class, Color.RED);
        view.setColor(Grass.class, Color.GREEN);
        
        // Set time to 0.
        time = 0;
        // Setup a valid starting point.
        reset();
    }

    /**
     * Run the simulation from its current state for a reasonably long period,
     * (1000 steps).
     */
    public void runLongSimulation()
    {
        simulate(1000);
    }

    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        if (!isRunning) { // Sets isRunning to true at start of simulation
            isRunning = true;
        }
        for(int step = 1; step <= numSteps && view.isViable(field) && isRunning == true; step++) {
            simulateOneStep();

            delay(40);   // uncomment this to run more slowly
        }
        isRunning = false;
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * Tiger, Lion, Bear and Zebra and Deer.
     * This method also tracks the time and season, and runs processing for weather.
     */
    public void simulateOneStep()
    {
        step++;
        time = step % 24;
        seasonTrack = step % 30;
        
        
        if (seasonTrack == 0) { // Tracking length of seasons
            rainedThisSeason = false;
            season = (season) % 4;
            switch (season) {
                case 0: //System.out.println("Spring"); break;
                case 1: //System.out.println("Summer"); break;
                case 2: //System.out.println("Autumn"); break;
                case 3: //System.out.println("Winter"); break;
            }
            season++;
        }
        
        if ((time % 8) == 0) { // Every 8 hours, it could start raining.
            setRain();
            if (getRain()) {
                rainedThisSeason = true;
            }
        }
        
        // Provide space for newborn animals.
        List<Actor> newAnimals = new ArrayList<>();        
        // Let all zebras and deers act.
        for(Iterator<Actor> it = animals.iterator(); it.hasNext(); ) {
            Actor animal = it.next();
            animal.act(newAnimals);
            if(! animal.isAlive()) {
                it.remove();
            }
        }
        
        // Provide space for newborn plants.
        List<Actor> newPlants = new ArrayList<>();
        // Let all plants act.
        for (Iterator<Actor> it = plants.iterator(); it.hasNext(); ) {
            Actor plant = it.next();
            plant.act(newPlants);
            if (!plant.isAlive()) {
                it.remove();
            }
        }
        
        Location location = new Location(1,1);
        Grass grass = new Grass(true, field, location);
        if (plants.size() < 30) {
            populateGrass();
        }

        // Add the newly born lions, tigers, bears and zebras and deers to the main lists.
        animals.addAll(newAnimals);
        plants.addAll(newPlants);

        view.showStatus(step, field);
    }
    
    /**
     * Pauses the Simulation.
     */
    public void pause() {
        isRunning = false;
    }
    
    /**
     * Quits the simulation.
     */
    public void quit() {
        System.exit(0);
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        animals.clear();
        plants.clear();
        populate();

        // Show the starting state in the view.
        view.showStatus(step, field);
    }
    
    /**
     * Method allowing grass to repopulate when its population goes below a certain level.
     * Code is similar to that in populate() method, but breaks once there are more than 100 plants.
     */
    private void populateGrass() {
        System.out.println("Grass has repopulated!");
        Random rand = Randomizer.getRandom();
        while (plants.size() <= 60) {
            for (int row = 0; row < field.getDepth(); row++) {
                for (int col = 0; col < field.getWidth(); col++) {
                    Location location = new Location(row, col); 
                    if (field.getObjectAt(location) != null) { 
                        if (rand.nextDouble() <= PLANT_CREATION_PROBABILITY) {
                            if (location != null) {
                                Grass grass = new Grass(true, field, location);
                                plants.add(grass);
                            }
                        }
                    }    
                }
                if (plants.size() > 100) {
                    break;
                }
            }
        }
    }

    /**
     * Randomly populate the field with Tigers, Lions, Bears, and Zebras and Deers. Also populates Grass
     */
    private void populate()
    {
        rainedThisSeason = true;
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= TIGER_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Tiger tiger = new Tiger(true, field, location, 15, 150, 0.4, 2);
                    animals.add(tiger);
                    //System.out.println(tiger.isFemale());
                }
                else if(rand.nextDouble() <= BEAR_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Bear bear = new Bear(true, field, location, 15, 150, 0.4, 2);
                    animals.add(bear);
                }
                else if(rand.nextDouble() <= LION_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Lion lion = new Lion(true, field, location, 15, 150, 0.5, 2);
                    animals.add(lion);
                }
                else if(rand.nextDouble() <= ZEBRA_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Zebra zebra = new Zebra(true, field, location, 15, 150, 0.5, 2);
                    animals.add(zebra);
                }
                else if(rand.nextDouble() <= DEER_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Deer deer = new Deer(true, field, location, 15, 150, 0.8, 2);
                    animals.add(deer);
                }
                else if (rand.nextDouble() <= PLANT_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    if (location != null) {
                        Grass grass = new Grass(true, field, location);
                        plants.add(grass);
                    }
                }
                // else leave the location empty.
            }
        }
    }
    
    /**
     * Get current time of day.
     * @return int time  The time of day for the simulation
     *      This is a number between 0 and 23.
     */
    public static int getTime() {
        return time;
    }
    
    /**
     * Get current season of Simulator.
     * @return int season  The number representing each season of the year.
     *  This is a number between 0 and 3. 0 is Spring, 1 is Summer, 2 is Autumn & 3 is Winter.
     */
    public static int getSeason() {
        return season;
    }
    
    /**
     * Sets value for rain.
     */
    public static void setRain() {
        rain = rand.nextDouble();
    }
    
    /**
     * Returns whether or not it's raining.
     * @return boolean  True if rain value is greater than or equal to 0.75.
     */
    public static boolean getRain() {
        return (rain >= 0.75);
    }
    
    /**
     * Returns true if it has rained during the current season.
     * @return boolean  True if it has recently rained.
     */
    public static boolean rainedThisSeason() {
        return rainedThisSeason;
    }
    
    /**
     * Pause for a given time.
     * @param millisec  The time to pause for, in milliseconds
     */
    private void delay(int millisec)
    {
        try {
            Thread.sleep(millisec);
        }
        catch (InterruptedException ie) {
            // wake up
        }
    }
}
