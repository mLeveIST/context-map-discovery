package org.contextmapper.discovery.model;

public class FunctionalityStep {

    private BoundedContext stepBoundedContext;
    private Service stepService;
    private Method stepOperation;

    public FunctionalityStep(BoundedContext stepBoundedContext, Service stepService, Method stepOperation) {
        this.stepBoundedContext = stepBoundedContext;
        this.stepService = stepService;
        this.stepOperation = stepOperation;
    }

    public BoundedContext getStepBoundedContext() {
        return stepBoundedContext;
    }

    public void setStepBoundedContext(BoundedContext stepBoundedContext) {
        this.stepBoundedContext = stepBoundedContext;
    }

    public Service getStepService() {
        return stepService;
    }

    public void setStepService(Service stepService) {
        this.stepService = stepService;
    }

    public Method getStepOperation() {
        return stepOperation;
    }

    public void setStepOperation(Method stepOperation) {
        this.stepOperation = stepOperation;
    }
}
