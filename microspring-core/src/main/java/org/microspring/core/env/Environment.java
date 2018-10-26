package org.microspring.core.env;

public interface Environment {

    String[] getActiveProfiles();

    String[] getDefaultProfiles();

    boolean acceptsProfiles(Profiles profiles);
}
