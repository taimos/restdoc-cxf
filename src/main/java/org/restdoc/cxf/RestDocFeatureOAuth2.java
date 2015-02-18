package org.restdoc.cxf;

import org.restdoc.api.GlobalHeader;
import org.restdoc.cxf.provider.IGlobalHeaderProvider;
import org.restdoc.server.ext.oauth2.OAuth2Extension;
import org.restdoc.server.impl.RestDocGenerator;

public abstract class RestDocFeatureOAuth2 extends RestDocFeature {
	
	@Override
	protected void customInit(final RestDocGenerator generator) {
		final OAuth2Extension oauth = new OAuth2Extension(this.getTokenURL(), this.getAuthURL(), this.getGrants());
		oauth.setClientaccess(this.getClientAccess());
		generator.registerGeneratorExtension(oauth);
		
		final IGlobalHeaderProvider oldHeaderProvider = this.getGlobalHeaderProvider();
		this.setGlobalHeaderProvider(new IGlobalHeaderProvider() {
			
			@Override
			public GlobalHeader getHeader() {
				GlobalHeader gh = null;
				if (oldHeaderProvider != null) {
					gh = oldHeaderProvider.getHeader();
				}
				if (gh == null) {
					gh = new GlobalHeader();
				}
				gh.request("Authorization", "Bearer: <oauth2 Token>", true);
				return gh;
			}
		});
	}
	
	protected abstract String getTokenURL();
	
	protected abstract String getAuthURL();
	
	protected abstract String[] getGrants();
	
	protected abstract String getClientAccess();
	
}
