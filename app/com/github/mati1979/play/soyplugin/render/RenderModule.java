package com.github.mati1979.play.soyplugin.render;

import play.api.Configuration;
import play.api.Environment;
import play.api.inject.Binding;
import play.api.inject.Module;
import scala.collection.Seq;

/**
 * Created by mati on 10/09/2014.
 */
public class RenderModule extends Module {

    @Override
    public Seq<Binding<?>> bindings(final Environment environment, final Configuration configuration) {
        if (configuration.getBoolean("play.soy.view.module.render").apply(true).get()) {
            return seq(
                    bind(TemplateRenderer.class).to(DefaultTemplateRenderer.class)
            );
        }

        return seq();
    }

}
