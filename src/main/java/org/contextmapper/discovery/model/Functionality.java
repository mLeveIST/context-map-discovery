package org.contextmapper.discovery.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

public class Functionality {

    private String name;
    private List<FunctionalityStep> functionalitySteps;
    private boolean isSaga;

    public Functionality(String name) {
        setName(name);
        setSaga(false);
        this.functionalitySteps = new ArrayList<>();
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
    public void addFunctionalitySteps(List<FunctionalityStep> functionalitySteps) {
        for (FunctionalityStep functionalityStep : functionalitySteps) {
            this.addFunctionalityStep(functionalityStep);
        }
    }

    /**
     * Gets the set of Functionality steps within the Functionality.
     *
     * @return the set of Functionality steps which are part of the Functionality
     */
    public List<FunctionalityStep> getFunctionalitySteps() {
        return new ArrayList<>(functionalitySteps);
    }

    public boolean isSaga() {
        return isSaga;
    }

    public void setSaga(boolean saga) {
        isSaga = saga;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Functionality))
            return false;

        Functionality functionality = (Functionality) object;

        return new EqualsBuilder()
                .append(name, functionality.name)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(name)
                .hashCode();
    }
}
