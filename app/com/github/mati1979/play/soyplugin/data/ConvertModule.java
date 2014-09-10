package com.github.mati1979.play.soyplugin.data;

import play.api.Configuration;
import play.api.Environment;
import play.api.inject.Binding;
import play.api.inject.Module;
import scala.collection.Seq;

/**
 * Created by mati on 10/09/2014.
 */
public class ConvertModule extends Module {

    @Override
    public Seq<Binding<?>> bindings(final Environment environment, final Configuration configuration) {
        if (configuration.getBoolean("play.soy.view.module.data.convert").apply(true).get()) {
            return seq(
                    bind(ToSoyDataConverter.class).to(ReflectionToSoyDataConverter.class)
            );
        }

        return seq();
    }

}
