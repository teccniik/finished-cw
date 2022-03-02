import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a Deer.
 * Deers age, move, eat grass, reproduce and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Deer extends Animal
{
    // Characteristics shared by all Deers (class variables).
    
    // The food value of a single zebra. In effect, this is the
    // number of steps a Deer can go before it has to eat again.
    private static final int G_FOOD_VALUE = 14;
    private static final int GRASS_FOOD_VALUE = 14;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
        // The age at which a Zebra can start to breed.
    protected static int BREEDING_AGE;
    // The age to which a Zebra can live.
    protected static int MAX_AGE;
    // The likelihood of a Zebra breeding.
    protected static double BREEDING_PROBABILITY;
    // The maximum number of births.
    protected static int MAX_LITTER_SIZE;

    // The Zebra's age.
    protected int age;
    // The Zebra's food level, which is increased by eating zebras.
    protected int foodLevel;
    //
    protected int breedingFoodLevel;

    /**
     * Create a Deer. A Deer can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the Deer will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param BREEDING_AGE The age that the Deer must be to breed.
     * @param MAX_AGE The maximum age of the animal
     * @param BREEDING_PROBABILITY The likelihood of the Deer breeding
     * @param MAX_LITTER_SIZE Maximum litter size for the animal when it reproduces.
     */
    public Deer(boolean randomAge, Field field, Location location, int BREEDING_AGE, int MAX_AGE, double BREEDING_PROBABILITY, int MAX_LITTER_SIZE)
    {
        super(field, location, 15, 150, 0.95, 3);
        breedingFoodLevel = 0;

        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(GRASS_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = GRASS_FOOD_VALUE;
        }
    }
    
    /**
     * This is what the Deer does most of the time: it searches for grass. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newDeers A list to return newly born Deers.
     */
    public void act(List<Actor> newDeers)
    {
        incrementAge();
        
        if ((Simulator.getTime() >= 14 && Simulator.getTime() <= 23) || (Simulator.getTime() >= 0 && Simulator.getTime() <= 6)) { // Deer are only active between these times.
            if(isAlive()) {
                if (foodLevel > breedingFoodLevel) {
                    giveBirth(newDeers);
                }
                            
                // Move towards a source of food if found.
                Location newLocation = findFood();
                if(newLocation == null) { 
                    // No food found - try to move to a free location.
                    newLocation = getField().freeAdjacentLocation(getLocation());
                }
                // See if it was possible to move.
                if(newLocation != null) {
                    setLocation(newLocation);
                }
                else {
                    // Overcrowding.
                    setDead();
                }
                incrementHunger();
            }
        }
        else {
            Double randomVal = rand.nextDouble();
        }
    }
    
    /**
     * Look for grass adjacent to the current location.
     * Only the first live grass is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object plant = field.getObjectAt(where);
            if(plant instanceof Grass) {
                Grass grass = (Grass) plant;
                if(grass.isAlive()) { 
                    grass.setDead();
                    foodLevel = GRASS_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this Deer is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newDeers A list to return newly born Deers.
     */
    private void giveBirth(List<Actor> newDeers)
    {
        // New Deers are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Deer young = new Deer(false, field, loc, 15, 150, 0.85, 3);
            newDeers.add(young);
        }
    }
    
    /** 
     * Increment Deer's hunger. This could cause the deer to die
     */
    public void incrementHunger() {
        foodLevel--;
        if (foodLevel <= 0) {
            setDead();
            System.out.println("Deer Starved");
        }
    }
    

    
    // public boolean findMate() {
        // Field field = getField();
        // List<Location> adjacent = field.adjacentLocations(this.getLocation());
        // for (Location where : adjacent) {
            // if (field.getObjectAt(where) != null) {
                // if (!(field.getObjectAt(where) instanceof Grass)) {
                    // Animal speciesInNextCell = (Animal) field.getObjectAt(where);
                    // if (speciesInNextCell != null && this.getClass().equals(speciesInNextCell.getClass()) && speciesInNextCell.isFemale() != this.isFemale() && speciesInNextCell.canBreed()) {
                        // return true;          
                    // }
                // }
            // }  
        // }
        // return false;
    // }

}
