package com.tigo.ea.eir.provider.util;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.tigo.ea.eir.provider.config.EirConfig;

public class ConfigAppContext {

    private static AnnotationConfigApplicationContext ctx = null;

    public ConfigAppContext() {

    }

    public static AnnotationConfigApplicationContext getContext() {

	if (ctx == null) {
	    ctx = new AnnotationConfigApplicationContext(EirConfig.class);
	}
	return ctx;
    }

}
