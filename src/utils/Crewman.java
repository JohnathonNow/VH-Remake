/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utils;

/**
 *
 * @author John
 */
public class Crewman {
    private final String firstName, lastName, role, description;
    private final int maxHealth;
    private int health;
    private final float skill;
    public Crewman(String firstName, String lastName, String role, String description, int maxHealth, float skill) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.description = String.format(description, firstName, lastName, role);
        this.health = this.maxHealth = maxHealth;
        this.skill = skill;
    }
    /**
     * Hurts this crewman by pain, and returns whether or not he's dead.
     * 
     * @param pain - how much to hurt the crewman
     * @return - true if dead, false if otherwise.
     */
    public final boolean hurt(int pain)
    {
        return (health-=pain)<=0;
    }
    /**
     * Heals this crewman by medicine, and returns whether or not he's at full health.
     * 
     * @param medicine - how much to hurt the crewman
     * @return - true if fully healed, false if otherwise.
     */
    public final boolean heal(int medicine)
    {
        return Math.min(health+=medicine,maxHealth)==maxHealth;
    }
    
    @Override
    public String toString()
    {
        return (role.concat(" ").concat(firstName).concat(" ").concat(lastName).concat(": ")+skill*100).concat("% Skilled:\n\t").concat(description);
    }
}
