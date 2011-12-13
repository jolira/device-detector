/**
 * 
 */
package com.jolira.detector;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.wurfl.core.Device;
import net.sourceforge.wurfl.core.WURFLHolder;
import net.sourceforge.wurfl.core.WURFLManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author jfk
 * 
 */
@WebServlet(name = "detectort", urlPatterns = { "/*" })
public class DetectorServlet extends HttpServlet {
    private static final long serialVersionUID = 3359106917458718755L;

    private static void close(final Closeable out) {
        try {
            out.close();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static PrintWriter open(final HttpServletResponse response) {
        ServletOutputStream out;

        try {
            out = response.getOutputStream();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        return new PrintWriter(out);
    }

    private final GsonBuilder gsonBuilder = new GsonBuilder();

    private synchronized Gson create() {
        return gsonBuilder.create();
    }

    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) {
        final Map<?, ?> capabilities = getCapabilities(request);
        final PrintWriter writer = open(response);

        response.setContentType("application/json");

        final Gson gson = create();

        try {
            gson.toJson(capabilities, writer);
        } finally {
            close(writer);
        }
    }

    private Map<?, ?> getCapabilities(final HttpServletRequest request) {
        final ServletContext context = request.getServletContext();
        final WURFLHolder wurflHolder = (WURFLHolder) context.getAttribute("net.sourceforge.wurfl.core.WURFLHolder");
        final WURFLManager wurfl = wurflHolder.getWURFLManager();
        final Device device = wurfl.getDeviceForRequest(request);
        @SuppressWarnings("rawtypes")
        final Map capabilities = device.getCapabilities();

        return capabilities;
    }
}
