package org.contextmapper.discovery.model;

import java.util.HashSet;
import java.util.Set;

public class Application {

    private String name;
    private Set<Service> services;
    private Set<Functionality> functionalities;

    public Application(String name) {
        setName(name);
        this.services = new HashSet<>();
        this.functionalities = new HashSet<>();
    }

    /**
     * Sets the name of this Application.
     *
     * @param name the name of the Application.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the name of this Application.
     *
     * @return the name of the Application as String
     */
    public String getName() {
        return name;
    }

    /**
     * Adds a service to the Application.
     *
     * @param service the service to be added to the Application
     */
    public void addService(Service service) {
        this.services.add(service);
    }

    /**
     * Adds all services in the given set to the Application.
     *
     * @param services the set of services to be added to the Application
     */
    public void addServices(Set<Service> services) {
        for (Service service : services) {
            this.addService(service);
        }
    }

    /**
     * Gets the set of services within the Application.
     *
     * @return the set of services which are part of the Application
     */
    public Set<Service> getServices() {
        return new HashSet<>(services);
    }

    /**
     * Adds a functionality to the Application.
     *
     * @param functionality the functionality to be added to the Application
     */
    public void addFunctionality(Functionality functionality) {
        this.functionalities.add(functionality);
    }

    /**
     * Adds all functionalities in the given set to the Application.
     *
     * @param functionalities the set of functionalities to be added to the Application
     */
    public void addFunctionalities(Set<Functionality> functionalities) {
        for (Functionality functionality : functionalities) {
            this.addFunctionality(functionality);
        }
    }

    /**
     * Gets the set of functionalities within the Application.
     *
     * @return the set of functionalities which are part of the Application
     */
    public Set<Functionality> getFunctionalities() {
        return new HashSet<>(functionalities);
    }
}
