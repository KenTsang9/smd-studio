package uk.ac.sheffield.dcs.smdStudio.framework.diagram;

import uk.ac.sheffield.dcs.smdStudio.framework.util.UniqueIDGenerator;

/**
 * This class represents an Id. To advantage to transport an id in a specific object rather than in a "standard" object like String
 * or Integer/Long is in the fact that it can be identified by its class. So, it will never be confused with other object attributes
 * (for example, name or age for a person).
 * 
 * But be careful and don't abuse of this programming pattern
 * 
 * @author Alexandre de Pellegrin
 * 
 */
public class Id
{

    /**
     * Default constructor
     */
    public Id()
    {
        this.value = UniqueIDGenerator.getNewId();
    }

    /**
     * @return id value
     */
    public String getValue()
    {
        return value;
    }

    /**
     * Sets id value
     * 
     * @param value v
     */
    public void setValue(String value)
    {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) return false;
        if (value != null) return value.equals(((Id) obj).value);
        return false;
    }

    @Override
    public int hashCode()
    {
        if (value != null)
        {
            return value.hashCode();
        }
        return super.hashCode();
    }

    @Override
    public String toString()
    {
        if (value != null)
        {
            return value;
        }
        return super.toString();
    }

    /**
     * Id value
     */
    private String value;
}
