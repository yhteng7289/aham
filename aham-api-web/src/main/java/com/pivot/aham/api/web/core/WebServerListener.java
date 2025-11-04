package com.pivot.aham.api.web.core;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Component;
import com.pivot.aham.common.core.listener.ApplicationReadyListener;

@Component
public class WebServerListener extends ApplicationReadyListener {

	public void onApplicationEvent(ApplicationReadyEvent event) {
		super.onApplicationEvent(event);
	}
}