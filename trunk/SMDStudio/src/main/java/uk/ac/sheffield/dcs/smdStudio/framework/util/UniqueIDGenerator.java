package uk.ac.sheffield.dcs.smdStudio.framework.util;

import java.util.UUID;

public class UniqueIDGenerator
{
    public static synchronized String getNewId() {
        return UUID.randomUUID().toString();
    }

}
