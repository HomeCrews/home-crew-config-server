package com.homecrew.configserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

/**
 * Refuses to start when a no-default secret was not supplied, or was supplied and does not work.
 *
 * <p>This class exists because the two properties it checks fail <em>silently</em> in two different
 * ways, and both failures look like the feature was never switched on.
 *
 * <p><strong>The placeholder never resolves.</strong> {@code encrypt.key} is bound by {@code
 * KeyProperties} and {@code spring.security.user.password} by {@code SecurityProperties.User}, both
 * {@code @ConfigurationProperties}. That binding ignores an unresolvable placeholder, so with
 * {@code ENCRYPT_KEY} unset the key becomes the literal fourteen-character string {@code $}{@code
 * {ENCRYPT_KEY}}, {@code EncryptorFactory} builds a perfectly valid AES encryptor out of it, and
 * nothing complains. It is the same trap that put a literal {@code $}{@code {JWT_SECRET}} into a
 * running service and prompted this migration.
 *
 * <p><strong>A wrong key does not throw.</strong> {@code CipherEnvironmentEncryptor} catches the
 * failure and renames the property to {@code invalid.<key>} instead, so the client receives no
 * property at all rather than a wrong one. {@code encrypt.fail-on-error} does not help: it is read
 * by the client-side {@code AbstractEnvironmentDecrypt}, not by this server.
 *
 * <p>{@code @Value} is the fix. It resolves through {@code PropertySourcesPlaceholderConfigurer},
 * which does not ignore unresolvable placeholders — so the same property that binds leniently above
 * throws here, at startup, before anything can serve a broken secret.
 */
// @Component, not @Configuration: this declares no @Bean methods, and a @Configuration class
// is CGLIB-proxied by default, which a final class cannot be. final is required because the
// constructor throws - SpotBugs' CT_CONSTRUCTOR_THROW flags a throwing constructor on a
// subclassable class as a finalizer-attack vector.
@Component
public final class RequiredSecrets {

  RequiredSecrets(
      @Value("${encrypt.key}") String encryptKey,
      @Value("${spring.security.user.password}") String basicAuthPassword,
      TextEncryptor textEncryptor) {

    require("ENCRYPT_KEY", encryptKey);
    require("CONFIG_CLIENT_PASSWORD", basicAuthPassword);

    // Catches what a presence check cannot: a blank key makes Spring hand back
    // Encryptors.noOpText(), which "encrypts" by returning its input - so every {cipher} value in
    // home-crew-config would be served in clear. A non-hex encrypt.salt fails here too, because
    // AesBytesEncryptor only calls Hex.decode on first use.
    String probe = "homecrew-encryption-self-test";
    String cipher = textEncryptor.encrypt(probe);
    if (probe.equals(cipher) || !probe.equals(textEncryptor.decrypt(cipher))) {
      throw new IllegalStateException(
          "encrypt.key does not round-trip. A blank key yields Encryptors.noOpText(), which would "
              + "serve every {cipher} value in clear. Check ENCRYPT_KEY and encrypt.salt.");
    }
  }

  private static void require(String name, String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalStateException(name + " is empty. It has no default, deliberately.");
    }
    if (value.startsWith("${")) {
      throw new IllegalStateException(
          name + " resolved to the literal placeholder " + value + " - the variable is not set.");
    }
  }
}
