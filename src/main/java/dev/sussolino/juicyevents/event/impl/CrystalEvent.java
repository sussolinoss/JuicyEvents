package dev.sussolino.juicyevents.event.impl;

import dev.sussolino.juicyevents.Juicy;
import dev.sussolino.juicyevents.event.Event;

public class CrystalEvent extends Event {

    public CrystalEvent(Juicy instance) {
        super(instance, "crystal");
        this.addProcessors(new CrystalEventManager(this));
    }

}
