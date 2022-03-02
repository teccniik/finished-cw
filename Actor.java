import java.util.List;
/**
 * An Interface for things that act.
 *
 * @author (your name
 * @version (a version number or a date)
 */
public interface Actor
{
    /**
     * Method for an Actor's actions in a step.
     */
    abstract void act(List<Actor> newActors);
    
    /**
     * A method that would return whether the Actor is alive.
     */
    abstract boolean isAlive();
    
    /**
     * A method that sets an Actor as dead.
     */
    void setDead();
    
    /**
     * A method that will return the Actor's location.
     */
    Location getLocation();
    
    /**
     * A method that will set the Actor's location.
     */
    void setLocation(Location location);
    
    /**
     * A method that will return the Actor's current field
     */
    Field getField();
    
    /**
     * A method that will increment the Actor's age.
     */
    void incrementAge();
    
    /**
     * A method that will return whether or not an Actor can breed.
     */
    boolean canBreed();
    
    
    
    
    
    
    
}
