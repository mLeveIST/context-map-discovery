package org.contextmapper.discovery.model;

import java.util.HashSet;
import java.util.Set;

public class Functionality {

    private String name;
    private Set<FunctionalityStep> functionalitySteps;

    public Functionality(String name) {
        setName(name);
        this.functionalitySteps = new HashSet<>();
    }

    /**
     * Sets the name of this Functionality.
     *
     * @param name the name of the Functionality.
     */
    public void setName(String name) {
        if (name == null || "".equals(name))
            throw new IllegalArgumentException("The name of a Functionality must not be null or empty.");
        this.name = name;
    }

    /**
     * Gets the name of this Functionality.
     *
     * @return the name of the Functionality as String
     */
    public String getName() {
        return name;
    }

    /**
     * Adds a Functionality step to the Functionality.
     *
     * @param functionalityStep the Functionality steps to be added to the Functionality
     */
    public void addFunctionalityStep(FunctionalityStep functionalityStep) {
        this.functionalitySteps.add(functionalityStep);
    }

    /**
     * Adds all Functionality steps in the given set to the Functionality.
     *
     * @param functionalitySteps the set of Functionality steps to be added to the Functionality
     */
    public void addSagaSteps(Set<FunctionalityStep> functionalitySteps) {
        for (FunctionalityStep functionalityStep : functionalitySteps) {
            this.addFunctionalityStep(functionalityStep);
        }
    }

    /**
     * Gets the set of Functionality steps within the Functionality.
     *
     * @return the set of Functionality steps which are part of the Functionality
     */
    public Set<FunctionalityStep> getFunctionalitySteps() {
        return new HashSet<>(functionalitySteps);
    }
}
