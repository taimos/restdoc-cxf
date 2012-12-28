/**
 *
 */
package org.restdoc.cxf;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.Response;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.restdoc.api.GlobalHeader;
import org.restdoc.api.RestDoc;
import org.restdoc.server.impl.RestDocGenerator;
import org.slf4j.LoggerFactory;

/**
 * @author thoeger
 * 
 *         Copyright 2012, Taimos GmbH
 * 
 */
public abstract class RestDocFeature extends AbstractFeature {

	private static final String ENCODING = "UTF-8";

	private static final String HTTP_METHOD = "OPTIONS";

	private static final String FIELD_REQUEST_URI = "org.apache.cxf.request.uri";

	private static final String FIELD_REQUEST_METHOD = "org.apache.cxf.request.method";

	private final RestDocGenerator restDoc;

	/**
     * 
     */
	public RestDocFeature() {
		this.restDoc = new RestDocGenerator();
		this.restDoc.init(this.getClasses(), this.getHeader(), this.getBaseURL());
	}

	protected abstract String getBaseURL();

	protected abstract Class<?>[] getClasses();

	protected abstract GlobalHeader getHeader();

	@Override
	public void initialize(final Server server, final Bus bus) {
		server.getEndpoint().getInInterceptors().add(new AbstractPhaseInterceptor<Message>(Phase.READ) {

			@Override
			public void handleMessage(final Message message) throws Fault {
				final Response response = RestDocFeature.this.handleRequest(message);
				if (response != null) {
					message.getExchange().put(Response.class, response);
				}
			}
		});
	}

	Response handleRequest(final Message message) {
		final String verb = (String) message.get(RestDocFeature.FIELD_REQUEST_METHOD);
		if (!verb.equals(RestDocFeature.HTTP_METHOD)) {
			// Only handle OPTIONS calls
			return null;
		}
		final String path = (String) message.get(RestDocFeature.FIELD_REQUEST_URI);
		if (!path.startsWith(this.getBaseURL())) {
			// Only handle calls to API base path
			return null;
		}
		try {
			// Decode path as it may contain {xyz}
			final String decode = URLDecoder.decode(path, RestDocFeature.ENCODING);
			LoggerFactory.getLogger(this.getClass()).debug(String.format("RestDoc request: %s", decode));
			// find RestDoc and return it
			return Response.ok(this.restDoc.getRestDocStringForPath(decode), RestDoc.RESTDOC_MEDIATYPE).build();
		} catch (final UnsupportedEncodingException e) {
			LoggerFactory.getLogger(this.getClass()).warn("RestDoc exception: " + e.getMessage(), e);
		}
		throw new InternalServerErrorException();
	}
}