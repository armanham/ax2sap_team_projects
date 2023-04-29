package com.bdg.converter.persistent_to_model;

import com.bdg.converter.persistent_to_model.common.PerToMod;
import com.bdg.model.PassengerMod;
import com.bdg.persistent.PassengerPer;
import com.bdg.validator.Validator;

public class PerToModPassenger extends PerToMod<PassengerPer, PassengerMod> {

    private final static PerToModAddress PER_TO_MOD_ADDRESS = new PerToModAddress();


    @Override
    public PassengerMod getModelFrom(PassengerPer persistent) {
        Validator.checkNull(persistent);

        PassengerMod model = new PassengerMod();
        model.setId(persistent.getId());
        model.setName(persistent.getName());
        model.setPhone(persistent.getPhone());
        model.setAddress(PER_TO_MOD_ADDRESS.getModelFrom(persistent.getAddress()));

        return model;
    }
}