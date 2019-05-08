package com.erm.webflux.event;

import com.erm.webflux.data.Profile;
import org.springframework.context.ApplicationEvent;

public class ProfileCreatedEvent extends ApplicationEvent {
    public ProfileCreatedEvent(Profile source) {
        super(source);
    }
}
