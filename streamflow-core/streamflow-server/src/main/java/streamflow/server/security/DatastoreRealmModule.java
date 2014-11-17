package streamflow.server.security;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.crypto.hash.Sha512Hash;

public class DatastoreRealmModule extends AbstractModule {

    @Override
    protected void configure() {
        // Credentials Matcher Configuration
        bind(CredentialsMatcher.class).to(HashedCredentialsMatcher.class);
    }
    
    @Provides
    public HashedCredentialsMatcher provideHashedCredentialsMatcher() {
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        matcher.setHashAlgorithmName(Sha512Hash.ALGORITHM_NAME);
        matcher.setHashIterations(2048);
        matcher.setStoredCredentialsHexEncoded(false);
        return matcher;
    }
}
