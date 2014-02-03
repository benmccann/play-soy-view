package com.github.mati1979.play.soyplugin.ajax.url;

import java.io.IOException;
import java.util.Collection;

import com.google.common.base.Optional;
import play.mvc.Http;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 03.07.13
 * Time: 20:16
 */
public interface TemplateUrlComposer {

    Optional<String> compose(Http.Request request, String soyTemplateFileName) throws IOException;

    Optional<String> compose(Http.Request request, Collection<String> soyTemplateFileNames) throws IOException;

}
