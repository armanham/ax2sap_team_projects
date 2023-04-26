package com.bdg.converter.persistent_to_model;

import com.bdg.model.AddressMod;
import com.bdg.persistent.AddressPer;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class PerToModAddress extends PerToMod<AddressPer, AddressMod> {

    @Override
    public AddressMod getModelFrom(AddressPer persistent) {
        checkNull(persistent);

        AddressMod model = new AddressMod();
        model.setId(persistent.getId());
        model.setCountry(persistent.getCountry());
        model.setCity(persistent.getCity());
        return model;
    }

    @Override
    public Collection<AddressMod> getModelListFrom(Collection<AddressPer> persistentList) {
        checkNull(persistentList);

        Set<AddressMod> addressModSet = new LinkedHashSet<>(persistentList.size());
        for (AddressPer tempAddressPer : persistentList) {
            addressModSet.add(getModelFrom(tempAddressPer));
        }
        return addressModSet;
    }

    private static void checkNull(Object persistent) {
        if (persistent == null) {
            throw new NullPointerException("Passed null value as 'persistent': ");
        }
    }
}
