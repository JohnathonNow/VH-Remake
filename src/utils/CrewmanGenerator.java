/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

/**
 *
 * @author John
 */
public class CrewmanGenerator {
    private BufferedReader brNames, brDescription;
    private int firstNameCount = 0, lastNameCount = 0, descriptionCount;
    private final Random r = new Random();
    private final String[] roles = {
        "Captain","Engineer","Navigator","Scientist","Weapon Specialist","First Officer","Security Chief",
    };
    /**
     * Create a name generator that reads names from a file.
     */
    public CrewmanGenerator() {
        try {
            brNames = new BufferedReader(new FileReader(getClass().getClassLoader().getResource("utils/names.csv").getPath()));
            lastNameCount = (int)brNames.lines().count();
            brNames.close();
            brNames = new BufferedReader(new FileReader(getClass().getClassLoader().getResource("utils/names.csv").getPath()));
            firstNameCount = (int)brNames.lines().filter(s->!s.startsWith(",")).count();
            brNames.close();
            brDescription = new BufferedReader(new FileReader(getClass().getClassLoader().getResource("utils/descriptions.csv").getPath()));
            descriptionCount = (int)brDescription.lines().count();
            brDescription.close();
        } catch (IOException ex) {
            firstNameCount = 0; lastNameCount = 0;
            System.out.println(ex);
        }
    }
    /** 
     * Generates a random first name.
     * 
     * @return a string containing a first name.
     */
    public String getFirst()
    {
        try
        {
            brNames = new BufferedReader(new FileReader(getClass().getClassLoader().getResource("utils/names.csv").getPath()));
            String firstName = ""+brNames.lines().skip(r.nextInt(firstNameCount)).findFirst().get();
            firstName = firstName.substring(0, firstName.indexOf(','));
            brNames.close();
            return firstName;
        }
        catch (IOException e)
        {
            return "";
        }
    }
    
    /** 
     * Generates a random crewman.
     * 
     * @return a Crewman object.
     */
    public Crewman getCrewman()
    {
        return new Crewman(getFirst(),getLast(),getRole(),getDescription(1),10+r.nextInt(11),r.nextFloat());
    }
    /** 
     * Generates a random last name.
     * 
     * @return a string containing a surname.
     */
    public String getLast()
    {
        try
        {
            brNames = new BufferedReader(new FileReader(getClass().getClassLoader().getResource("utils/names.csv").getPath()));
            String lastName = ""+brNames.lines().skip(r.nextInt(lastNameCount)).findFirst().get();
            lastName = lastName.substring(lastName.indexOf(',')+1,lastName.length()-1);
            brNames.close();
            return lastName;
        }
        catch (IOException e)
        {
            return "";
        }
    }
    /** 
     * Generates a random role.
     * 
     * @return a string containing a role.
     */
    public String getRole()
    {
        return roles[r.nextInt(roles.length)];
    }
    /** 
     * Generates a phrase description with n sentences.
     * 
     * @param n - the number of sentences in the description
     * @return a string containing a description.
     */
    public String getDescription(int n)
    {
        try
        {
            String phrase = "";
            for (int i = 0; i < n; i++)
            {
            brDescription = new BufferedReader(new FileReader(getClass().getClassLoader().getResource("utils/descriptions.csv").getPath()));
            String toAdd = brDescription.lines().skip(r.nextInt(descriptionCount)).findFirst().get();
                phrase += toAdd.substring(0, !toAdd.contains(";")?toAdd.length()-1:toAdd.indexOf(","));
            brDescription.close();
            }
            return phrase;
        }
        catch (IOException e)
        {
            return "";
        }
    }
    /**
     * Mad-libs a description with the firstName, lastName, and role.
     * 
     * @param description - the description to fill
     * @param firstName - the first name to use
     * @param lastName - the last name to use
     * @param role - the role to use
     * @return a formatted string of the description and crewman characteristics.
     */
    public String populateDescription(String description, String firstName, String lastName, String role)
    {
        return String.format(description, firstName, lastName, role);
    }
    public static void main(String[] args)
    {
        CrewmanGenerator cg = new CrewmanGenerator();
        for (int i = 0; i < 100; i++)
        {
            System.out.println(cg.getCrewman());
        }
        //System.out.println(cg.populateDescription(cg.getDescription(1), cg.getFirst(), cg.getLast(), cg.getRole()));

    }
}
