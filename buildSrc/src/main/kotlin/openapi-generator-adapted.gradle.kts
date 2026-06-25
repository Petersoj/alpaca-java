import com.google.common.base.CaseFormat.LOWER_CAMEL
import com.google.common.base.CaseFormat.LOWER_HYPHEN
import com.google.common.base.CaseFormat.UPPER_CAMEL

plugins {
    java
    id("org.openapi.generator")
}

val jacksonVersion = "3.2.0"
val jacksonAnnotationsVersion = "2.22"

dependencies {
    implementation("tools.jackson.core:jackson-core:$jacksonVersion")
    implementation("tools.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonAnnotationsVersion")
}

val extension = extensions.create("openApiGeneratorAdapted", OpenApiGeneratorAdaptedExtension::class)
tasks.openApiGenerate.configure {
    inputSpec = file("openapi.json")
    generatorName = "java"
    val outputPackageName = "${project.group}.${project.name}".replace("-", "")
    configOptions.put("invokerPackage", outputPackageName)
    val apiPackageName = "api"
    configOptions.put("apiPackage", "$outputPackageName.$apiPackageName")
    configOptions.put("modelPackage", "$outputPackageName.model")
    configOptions.put("library", "native")
    configOptions.put("documentationProvider", "none")
    configOptions.put("useJackson3", "true")
    configOptions.put("openApiNullable", "false")
    cleanupOutput = true
    skipValidateSpec = true
    generateModelTests = false
    generateModelDocumentation = false
    generateApiTests = false
    generateApiDocumentation = false
    val projectName = project.name
    val projectDescription = project.description
    val environmentUrlProduction = extension.environmentUrlProduction.get()
    val environmentUrlDevelopment = extension.environmentUrlDevelopment.get()
    doLast {
        val srcMainJava = outputDir.get().asFile.resolve("src/main/java/")
        srcMainJava.walkTopDown().filter { it.isFile }.forEach {
            it.writeText(it.readText()
                    .replace("@javax.annotation.Nullable", "@org.jspecify.annotations.Nullable")
                    .replace("@javax.annotation.Nonnull", "@org.jspecify.annotations.NonNull")
                    .replace("@javax.annotation.Generated", "@javax.annotation.processing.Generated"))
        }
        val outputPackageDirectory = srcMainJava.resolve(outputPackageName.replace('.', '/'))
        val apiClassNamesOfMethodNames = outputPackageDirectory.resolve(apiPackageName).list()
                .map { it.replace(".java", "") }
                .associateBy { UPPER_CAMEL.to(LOWER_CAMEL, it.substring(0, it.length - 3)) }
        val className = "${LOWER_HYPHEN.to(UPPER_CAMEL, projectName)}Api"
        outputPackageDirectory.resolve("${className}.java").writeText("""
        package ${outputPackageName};

        import com.google.errorprone.annotations.concurrent.LazyInit;
        import net.jacobpeterson.alpacajava.common.AlpacaHeader;
        import net.jacobpeterson.alpacajava.common.ApiEnvironment;
        import ${outputPackageName}.${apiPackageName}.*;
        import org.jspecify.annotations.NullMarked;
        import org.jspecify.annotations.Nullable;
        import tools.jackson.databind.ObjectMapper;

        import java.io.InputStream;
        import java.net.http.HttpClient;
        import java.net.http.HttpRequest;
        import java.net.http.HttpResponse;
        import java.util.function.Consumer;

        import static com.google.common.net.HttpHeaders.ACCEPT_ENCODING;
        import static com.google.common.net.HttpHeaders.AUTHORIZATION;
        import static java.time.Duration.ofSeconds;
        import static net.jacobpeterson.alpacajava.common.AlpacaHeader.API_KEY_ID;
        import static net.jacobpeterson.alpacajava.common.AlpacaHeader.API_SECRET_KEY;
        import static net.jacobpeterson.alpacajava.common.AlpacaHeader.AUTHORIZATION_TOKEN_BEARER_PREFIX;
        import static net.jacobpeterson.alpacajava.common.ApiEnvironment.PRODUCTION;

        /**
         * {@link ${className}}: $projectDescription
         */
        @NullMarked
        public class $className implements AutoCloseable {

            /**
             * The URL for {@link ApiEnvironment#PRODUCTION}: <code>"$environmentUrlProduction"</code>
             */
            public static final String ENVIRONMENT_URL_PRODUCTION ="$environmentUrlProduction";

            /**
             * The URL for {@link ApiEnvironment#DEVELOPMENT}: <code>"$environmentUrlDevelopment"</code>
             */
            public static final String ENVIRONMENT_URL_DEVELOPMENT = "$environmentUrlDevelopment";

            private final HttpClient httpClient;
            private final ApiClient apiClient;
            ${apiClassNamesOfMethodNames.entries.joinToString(separator = "\n    ") { (methodName, apiClassName) ->
                "private @Nullable @LazyInit $apiClassName $methodName;" }}

            /**
             * Calls {@link #${className}(HttpClient, ObjectMapper, String, String, String, ApiEnvironment, Consumer, Consumer)}
             * with everything set to <code>null</code> except <code>authenticationKeyID</code>,
             * <code>authenticationSecretKey</code>, and <code>apiEnvironment</code>.
             */
            public ${className}(final @Nullable String authenticationKeyID, final @Nullable String authenticationSecretKey,
                    final @Nullable ApiEnvironment apiEnvironment) {
                this(null, null, authenticationKeyID, authenticationSecretKey, null, apiEnvironment, null, null);
            }

            /**
             * Calls {@link #${className}(HttpClient, ObjectMapper, String, String, String, ApiEnvironment, Consumer, Consumer)}
             * with everything set to <code>null</code> except <code>authorizationBearerToken</code> and
             * <code>apiEnvironment</code>.
             */
            public ${className}(final @Nullable String authorizationBearerToken, final @Nullable ApiEnvironment apiEnvironment) {
                this(null, null, null, null, authorizationBearerToken, apiEnvironment, null, null);
            }

            /**
             * Instantiates a new {@link ${className}}.
             *
             * @param httpClient               the {@link HttpClient}, or <code>null</code> to use a new default instance
             * @param objectMapper             the {@link ObjectMapper}, or <code>null</code> to use a new default instance
             * @param authenticationKeyID      the {@link AlpacaHeader#API_KEY_ID} value
             * @param authenticationSecretKey  the {@link AlpacaHeader#API_SECRET_KEY} value
             * @param authorizationBearerToken the {@link AlpacaHeader#AUTHORIZATION_TOKEN_BEARER_PREFIX} suffix value
             * @param apiEnvironment           the {@link ApiEnvironment}. If {@link ApiEnvironment#PRODUCTION}, then
             *                                 {@link #ENVIRONMENT_URL_PRODUCTION} is used. If
             *                                 {@link ApiEnvironment#DEVELOPMENT}, then {@link #ENVIRONMENT_URL_DEVELOPMENT}
             *                                 is used.
             * @param requestInterceptor       the {@link HttpRequest.Builder} {@link Consumer} to invoke before sending a
             *                                 request
             * @param responseInterceptor      the {@link HttpResponse} {@link Consumer} to invoke before processing a response
             */
            public ${className}(final @Nullable HttpClient httpClient, final @Nullable ObjectMapper objectMapper,
                    final @Nullable String authenticationKeyID, final @Nullable String authenticationSecretKey,
                    final @Nullable String authorizationBearerToken, final @Nullable ApiEnvironment apiEnvironment,
                    final @Nullable Consumer<HttpRequest.Builder> requestInterceptor,
                    final @Nullable Consumer<HttpResponse<InputStream>> responseInterceptor) {
                this.httpClient = httpClient != null ? httpClient :
                        HttpClient.newBuilder().connectTimeout(ofSeconds(10)).build();
                final var baseUri = apiEnvironment == PRODUCTION ? ENVIRONMENT_URL_PRODUCTION : ENVIRONMENT_URL_DEVELOPMENT;
                apiClient = new ApiClient(null, objectMapper != null ? objectMapper :
                        ApiClient.createDefaultObjectMapper(), null) {

                    @Override
                    public HttpClient getHttpClient() {
                        // Re-use `HttpClient` instead of building a new one for each tag
                        return ${className}.this.httpClient;
                    }

                    @Override
                    public String getBaseUri() {
                        return baseUri;
                    }
                };
                apiClient.setReadTimeout(ofSeconds(10));
                apiClient.setRequestInterceptor(builder -> {
                    builder.header(ACCEPT_ENCODING, "gzip");
                    if (authenticationKeyID != null) {
                        builder.header(API_KEY_ID, authenticationKeyID);
                    }
                    if (authenticationSecretKey != null) {
                        builder.header(API_SECRET_KEY, authenticationSecretKey);
                    }
                    if (authorizationBearerToken != null) {
                        builder.header(AUTHORIZATION, AUTHORIZATION_TOKEN_BEARER_PREFIX + authorizationBearerToken);
                    }
                    if (requestInterceptor != null) {
                        requestInterceptor.accept(builder);
                    }
                });
                apiClient.setResponseInterceptor(responseInterceptor);
            }

            @Override
            public void close() {
                httpClient.close();
            }

            ${apiClassNamesOfMethodNames.entries.joinToString(separator = "\n") { (methodName, apiClassName) -> """
            /**
             * @return {@link ${apiClassName}} internally-cached instance
             */
            public $apiClassName ${methodName}() {
                if (${methodName} == null) {
                    $methodName = new ${apiClassName}(apiClient);
                }
                return ${methodName};
            }
            """.trimIndent() }}
        }
        """.trimIndent())
    }
}
tasks.withType(JavaCompile::class).configureEach {
    dependsOn(tasks.openApiGenerate)
}
tasks.withType(Jar::class).configureEach {
    dependsOn(tasks.openApiGenerate)
}
sourceSets {
    main {
        java {
            srcDir(openApiGenerate.outputDir.file("src/main/java/"))
        }
    }
}

tasks.withType(Javadoc::class).configureEach {
    options {
        (this as StandardJavadocDocletOptions).links(
                // TODO uncomment once Jackson Core published Javadoc artifact is fixed
                // "https://javadoc.io/doc/tools.jackson.core/jackson-core/$jacksonVersion",
                "https://javadoc.io/doc/tools.jackson.core/jackson-databind/$jacksonVersion",
                "https://javadoc.io/doc/com.fasterxml.jackson.core/jackson-annotations/$jacksonAnnotationsVersion")
    }
}
