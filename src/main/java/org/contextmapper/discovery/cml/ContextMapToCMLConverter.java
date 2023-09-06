/*
 * Copyright 2019 The Context Mapper Project Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.contextmapper.discovery.cml;

import org.contextmapper.discovery.model.Method;
import org.contextmapper.discovery.model.Relationship;
import org.contextmapper.discovery.model.Type;
import org.contextmapper.discovery.model.TypeKind;
import org.contextmapper.dsl.contextMappingDSL.*;
import org.contextmapper.tactic.dsl.tacticdsl.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.contextmapper.discovery.model.DomainObjectType.ENTITY;

/**
 * Converts a {@link org.contextmapper.discovery.model.ContextMap} to the CML {@link org.contextmapper.dsl.contextMappingDSL.ContextMap}
 *
 * @author Stefan Kapferer
 */
public class ContextMapToCMLConverter {

    private Map<String, BoundedContext> boundedContextMap = new HashMap<>();
    private Map<org.contextmapper.discovery.model.DomainObject, DomainObject> domainObjectLookupMap = new HashMap<>();
    private Map<String, Service> serviceMap = new HashMap<>();
    private Map<org.contextmapper.discovery.model.Functionality, Functionality> functionalityLookupMap = new HashMap<>();

    public ContextMappingModel convert(org.contextmapper.discovery.model.ContextMap inputMap) {
        ContextMappingModel model = ContextMappingDSLFactory.eINSTANCE.createContextMappingModel();
        ContextMap contextMap = ContextMappingDSLFactory.eINSTANCE.createContextMap();
        model.setMap(contextMap);

        for (org.contextmapper.discovery.model.BoundedContext boundedContext : inputMap.getBoundedContexts()) {
            BoundedContext bc = convert(boundedContext);
            model.getBoundedContexts().add(bc);
            contextMap.getBoundedContexts().add(bc);
        }

        for (Relationship relationship : inputMap.getRelationships()) {
            contextMap.getRelationships().add(convert(relationship));
        }

        updateEntityAttributesAndReferences();
        updateFunctionalitySteps();

        return model;
    }

    private BoundedContext convert(org.contextmapper.discovery.model.BoundedContext inputContext) {
        BoundedContext bc = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
        bc.setName(inputContext.getName());
        bc.setImplementationTechnology(inputContext.getTechnology());
        for (org.contextmapper.discovery.model.Aggregate aggregate : inputContext.getAggregates()) {
            bc.getAggregates().add(convert(aggregate));
        }
        if (inputContext.getApplication() != null) {
            bc.setApplication(convert(inputContext.getApplication()));
        }
        this.boundedContextMap.put(inputContext.getName(), bc);
        return bc;
    }

    private Application convert(org.contextmapper.discovery.model.Application inputApplication) {
        Application application = ContextMappingDSLFactory.eINSTANCE.createApplication();
        if (inputApplication.getName() != null && !"".equals(inputApplication.getName())) {
            application.setName(inputApplication.getName());
        }
        for (org.contextmapper.discovery.model.Service service : inputApplication.getServices()) {
            application.getServices().add(convertAndSave(service));
        }
        for (org.contextmapper.discovery.model.Functionality functionality : inputApplication.getFunctionalities()) {
            application.getFunctionalities().add(convert(functionality));
        }
        return application;
    }

    private Service convertAndSave(org.contextmapper.discovery.model.Service inputService) {
        Service service = convert(inputService);
        serviceMap.put(inputService.getName(), service);
        return service;
    }

    private Functionality convert(org.contextmapper.discovery.model.Functionality inputFunctionality) {
        Functionality functionality = ContextMappingDSLFactory.eINSTANCE.createFunctionality();
        if (inputFunctionality.getName() != null && !"".equals(inputFunctionality.getName())) {
            functionality.setName(inputFunctionality.getName());
        }
        functionality.setSagaOrchestrator(inputFunctionality.isSaga());
        this.functionalityLookupMap.put(inputFunctionality, functionality);
        return functionality;
    }

    private Aggregate convert(org.contextmapper.discovery.model.Aggregate inputAggregate) {
        Aggregate aggregate = ContextMappingDSLFactory.eINSTANCE.createAggregate();
        aggregate.setName(inputAggregate.getName());
        if (inputAggregate.getDiscoveryComment() != null && !"".equals(inputAggregate.getDiscoveryComment()))
            aggregate.setComment("/* " + inputAggregate.getDiscoveryComment() + " */");
        for (org.contextmapper.discovery.model.DomainObject domainObject : inputAggregate.getDomainObjects()) {
            aggregate.getDomainObjects().add(convert(domainObject));
        }
        for (org.contextmapper.discovery.model.DomainObject domainObject : inputAggregate.getDomainObjects()) {
            convertDomainObjectMethods(domainObject);
        }
        for (org.contextmapper.discovery.model.Service service : inputAggregate.getServices()) {
            aggregate.getServices().add(convert(service));
        }
        Optional<Entity> rootEntity = aggregate.getDomainObjects().stream().filter(o -> o instanceof Entity).map(o -> (Entity) o)
                .filter(e -> e.getName().endsWith("_RootEntity")).findFirst();
        if (rootEntity.isPresent())
            rootEntity.get().setAggregateRoot(true);
        return aggregate;
    }

    private DomainObject convert(org.contextmapper.discovery.model.DomainObject inputDomainObject) {
        if (ENTITY.equals(inputDomainObject.getType()))
            return convertDomainObjectToEntity(inputDomainObject);
        return convertDomainObjectToValueObject(inputDomainObject);
    }

    private Entity convertDomainObjectToEntity(org.contextmapper.discovery.model.DomainObject inputDomainObject) {
        Entity entity = TacticdslFactory.eINSTANCE.createEntity();
        entity.setName(inputDomainObject.getName());
        if (inputDomainObject.getDiscoveryComment() != null && !"".equals(inputDomainObject.getDiscoveryComment()))
            entity.setComment("/* " + inputDomainObject.getDiscoveryComment() + " */");
        domainObjectLookupMap.put(inputDomainObject, entity);
        return entity;
    }

    private ValueObject convertDomainObjectToValueObject(org.contextmapper.discovery.model.DomainObject inputDomainObject) {
        ValueObject valueObject = TacticdslFactory.eINSTANCE.createValueObject();
        valueObject.setName(inputDomainObject.getName());
        if (inputDomainObject.getDiscoveryComment() != null && !"".equals(inputDomainObject.getDiscoveryComment()))
            valueObject.setComment("/* " + inputDomainObject.getDiscoveryComment() + " */");
        domainObjectLookupMap.put(inputDomainObject, valueObject);
        return valueObject;
    }

    private void convertDomainObjectMethods(org.contextmapper.discovery.model.DomainObject inputDomainObject) {
        DomainObject domainObject = this.domainObjectLookupMap.get(inputDomainObject);
        for (Method inputMethod : inputDomainObject.getMethods()) {
            DomainObjectOperation operation = TacticdslFactory.eINSTANCE.createDomainObjectOperation();
            operation.setName(inputMethod.getName());
            operation.setReturnType(createComplexType(inputMethod.getReturnType()));
            operation.getParameters().addAll(createParameters(inputMethod.getParameters()));
            domainObject.getOperations().add(operation);
        }
    }

    private Set<Parameter> createParameters(Set<org.contextmapper.discovery.model.Parameter> inputParameters) {
        Set<Parameter> parameters = new HashSet<>();
        for (org.contextmapper.discovery.model.Parameter inputParameter : inputParameters) {
            Parameter parameter = TacticdslFactory.eINSTANCE.createParameter();
            parameter.setName(inputParameter.getName());
            parameter.setParameterType(createComplexType(inputParameter.getType()));
            parameters.add(parameter);
        }
        return parameters;
    }

    private ComplexType createComplexType(Type type) {
        if (type == null)
            return null; // "void" case

        ComplexType complexType = TacticdslFactory.eINSTANCE.createComplexType();
        if (type.isDomainObjectType())
            complexType.setDomainObjectType(this.domainObjectLookupMap.get(type.getDomainObjectType()));
        else
            complexType.setType(type.getPrimitiveType());

        if (type.isCollectionType())
            complexType.setCollectionType(CollectionType.get(type.getCollectionType()));
        return complexType;
    }

    private UpstreamDownstreamRelationship convert(Relationship relationship) {
        UpstreamDownstreamRelationship upstreamDownstreamRelationship = ContextMappingDSLFactory.eINSTANCE.createUpstreamDownstreamRelationship();
        upstreamDownstreamRelationship.setUpstream(this.boundedContextMap.get(relationship.getUpstream().getName()));
        upstreamDownstreamRelationship.setDownstream(this.boundedContextMap.get(relationship.getDownstream().getName()));
        for (org.contextmapper.discovery.model.Aggregate aggregate : relationship.getExposedAggregates()) {
            Optional<Aggregate> cmlAggregate = upstreamDownstreamRelationship.getUpstream().getAggregates().stream().filter(a -> a.getName().equals(aggregate.getName())).findFirst();
            if (cmlAggregate.isPresent())
                upstreamDownstreamRelationship.getUpstreamExposedAggregates().add(cmlAggregate.get());
        }
        if (relationship.getExposedAggregatesComment() != null && !"".equals(relationship.getExposedAggregatesComment()))
            upstreamDownstreamRelationship.setExposedAggregatesComment("// " + relationship.getExposedAggregatesComment());
        return upstreamDownstreamRelationship;
    }

    private Service convert(org.contextmapper.discovery.model.Service inputService) {
        Service service = TacticdslFactory.eINSTANCE.createService();
        service.setName(inputService.getName());
        if (inputService.getDiscoveryComment() != null && !"".equals(inputService.getDiscoveryComment())) {
            service.setComment("/* " + inputService.getDiscoveryComment() + " */");
        }
        convertServiceOperations(inputService, service);
        return service;
    }

    private void convertServiceOperations(org.contextmapper.discovery.model.Service inputService, Service service) {
        for (Method inputMethod : inputService.getOperations()) {
            ServiceOperation operation = TacticdslFactory.eINSTANCE.createServiceOperation();
            operation.setName(inputMethod.getName());
            operation.setReturnType(createComplexType(inputMethod.getReturnType()));
            operation.getParameters().addAll(createParameters(inputMethod.getParameters()));
            service.getOperations().add(operation);
        }
    }

    private void updateEntityAttributesAndReferences() {
        for (Map.Entry<org.contextmapper.discovery.model.DomainObject, DomainObject> entry : this.domainObjectLookupMap.entrySet()) {
            updateDomainObject(entry.getKey(), entry.getValue());
        }
    }

    private void updateDomainObject(org.contextmapper.discovery.model.DomainObject inputDomainObject, DomainObject domainObject) {
        Set<org.contextmapper.discovery.model.Attribute> primitiveAttributes = inputDomainObject.getAttributes().stream().filter(a -> a.getType().getKind() == TypeKind.PRIMITIVE).collect(Collectors.toSet());
        Set<org.contextmapper.discovery.model.Attribute> domainObjectAttributes = inputDomainObject.getAttributes().stream().filter(a -> a.getType().getKind() == TypeKind.DOMAIN_OBJECT).collect(Collectors.toSet());
        for (org.contextmapper.discovery.model.Attribute inputAttribute : primitiveAttributes) {
            Attribute attribute = TacticdslFactory.eINSTANCE.createAttribute();
            attribute.setName(inputAttribute.getName());
            attribute.setType(inputAttribute.getType().getPrimitiveType());
            if (inputAttribute.getType().isCollectionType())
                attribute.setCollectionType(CollectionType.get(inputAttribute.getType().getCollectionType()));
            domainObject.getAttributes().add(attribute);
        }
        for (org.contextmapper.discovery.model.Attribute inputAttribute : domainObjectAttributes) {
            Reference reference = TacticdslFactory.eINSTANCE.createReference();
            reference.setName(inputAttribute.getName());
            reference.setDomainObjectType(this.domainObjectLookupMap.get(inputAttribute.getType().getDomainObjectType()));
            if (inputAttribute.getType().isCollectionType())
                reference.setCollectionType(CollectionType.get(inputAttribute.getType().getCollectionType()));
            domainObject.getReferences().add(reference);
        }
    }

    private void updateFunctionalitySteps() {
        for (Map.Entry<org.contextmapper.discovery.model.Functionality, Functionality> entry : this.functionalityLookupMap.entrySet()) {
            updateFunctionality(entry.getKey(), entry.getValue());
        }
    }

    private void updateFunctionality(org.contextmapper.discovery.model.Functionality inputFunctionality, Functionality functionality) {
        for (org.contextmapper.discovery.model.FunctionalityStep inputFunctionalityStep : inputFunctionality.getFunctionalitySteps()) {
            functionality.getFunctionalitySteps().add(convert(inputFunctionalityStep));
        }
    }

    private FunctionalityStep convert(org.contextmapper.discovery.model.FunctionalityStep inputFunctionalityStep) {
        FunctionalityStep functionalityStep = ContextMappingDSLFactory.eINSTANCE.createFunctionalityStep();

        BoundedContext boundedContext = boundedContextMap.get(inputFunctionalityStep.getStepBoundedContext().getName());
        functionalityStep.setBoundedContext(boundedContext);
        boundedContext.getApplication().getServices().stream()
                .filter(s -> s.getName().equals(inputFunctionalityStep.getStepService().getName()))
                .findFirst().ifPresent(functionalityStep::setService);
        functionalityStep.getService().getOperations().stream()
                .filter(o -> o.getName().equals(inputFunctionalityStep.getStepOperation().getName()))
                .findFirst().ifPresent(functionalityStep::setOperation);

        return functionalityStep;
    }
}
