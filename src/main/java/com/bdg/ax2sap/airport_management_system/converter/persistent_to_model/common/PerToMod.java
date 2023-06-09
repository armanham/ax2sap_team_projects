package com.bdg.ax2sap.airport_management_system.converter.persistent_to_model.common;

import com.bdg.ax2sap.airport_management_system.validator.Validator;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class PerToMod<P, M> {

    public PerToMod() {
    }

    public abstract M getModelFrom(P persistent);

    public Collection<M> getModelListFrom(Collection<P> persistentList) {
        Validator.checkNull(persistentList);

        Set<M> modelList = new LinkedHashSet<>(persistentList.size());
        for (P tempPer : persistentList) {
            modelList.add(getModelFrom(tempPer));
        }

        return modelList;
    }
}