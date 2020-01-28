package com.ithit.webdav.samples.springbootsample.controller;

import com.ithit.webdav.integration.servlet.HttpServletDavRequest;
import com.ithit.webdav.integration.servlet.HttpServletDavResponse;
import com.ithit.webdav.samples.springbootsample.impl.WebDavEngine;
import com.ithit.webdav.server.exceptions.DavException;
import com.ithit.webdav.server.exceptions.WebDavStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintStream;

import static com.ithit.webdav.samples.springbootsample.configuration.WebDavConfigurationProperties.WEBDAV_CONTEXT;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
public class SamplesController {

    WebDavEngine engine;

    @RequestMapping(value = WEBDAV_CONTEXT + "**", produces = MediaType.ALL_VALUE)
    public void webdav(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        performDavRequest(httpServletRequest, httpServletResponse);
    }

    @RequestMapping(value = WEBDAV_CONTEXT + "**", produces = MediaType.ALL_VALUE, method = {RequestMethod.OPTIONS})
    public void options(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        performDavRequest(httpServletRequest, httpServletResponse);
    }

    private void performDavRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        final Object rootAttribute = httpServletRequest.getAttribute("root");
        boolean isRoot = rootAttribute != null && (boolean) rootAttribute;
        HttpServletDavRequest davRequest = new HttpServletDavRequest(httpServletRequest) {

            @Override
            public String getServerPath() {
                return isRoot ? "/" : WEBDAV_CONTEXT;
            }

            @Override
            public String getRequestURI() {
                return isRoot ? "/" : super.getRequestURI();
            }
        };
        HttpServletDavResponse davResponse = new HttpServletDavResponse(httpServletResponse);
        try {
            engine.setServletRequest(davRequest);
            engine.service(davRequest, davResponse);
        } catch (DavException e) {
            if (e.getStatus() == WebDavStatus.INTERNAL_ERROR) {
                engine.getLogger().logError("Exception during request processing", e);
                if (engine.isShowExceptions())
                    e.printStackTrace(new PrintStream(davResponse.getOutputStream()));
            }
        }
    }

}