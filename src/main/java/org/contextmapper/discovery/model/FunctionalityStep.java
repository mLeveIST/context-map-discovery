package org.contextmapper.discovery.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof FunctionalityStep))
            return false;

        FunctionalityStep functionalityStep = (FunctionalityStep) object;

        return new EqualsBuilder()
                .append(stepBoundedContext, functionalityStep.stepBoundedContext)
                .append(stepService, functionalityStep.stepService)
                .append(stepOperation, functionalityStep.stepOperation)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(stepBoundedContext)
                .append(stepService)
                .append(stepOperation)
                .hashCode();
    }
}
