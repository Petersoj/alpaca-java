import com.google.common.base.CaseFormat.LOWER_CAMEL
import com.google.common.base.CaseFormat.LOWER_HYPHEN
import com.google.common.base.CaseFormat.UPPER_CAMEL
import com.google.common.base.Splitter

plugins {
    `java-library`
    id("org.openapi.generator")
}

val jacksonVersion = "3.2.0"
val jacksonAnnotationsVersion = "2.22"

dependencies {
    api(project(":module:common"))

    api("tools.jackson.core:jackson-core:$jacksonVersion")
    api("tools.jackson.core:jackson-databind:$jacksonVersion")
    api("com.fasterxml.jackson.core:jackson-annotations:$jacksonAnnotationsVersion")
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
        srcMainJava.walkTopDown().filter { it.isFile }.forEach { sourceFile ->
            var source = sourceFile.readText()
                    .replaceFirst("\nimport", """
                    import org.jspecify.annotations.*;
                    import net.jacobpeterson.alpacajava.common.sse.*;
                    import com.google.common.collect.ImmutableMap;
                    import java.util.Arrays;
                    import java.util.Locale;
                    import
                    """.trimIndent())
                    .replace("@javax.annotation.Nullable", "@Nullable")
                    .replace("@javax.annotation.Nonnull", "@NonNull")
                    .replace("@javax.annotation.Generated", "@javax.annotation.processing.Generated")
                    .replace("if (memberVarResponseInterceptor != null) {\n        " +
                            "memberVarResponseInterceptor.accept(localVarResponse);\n      }\n      " +
                            "InputStream localVarResponseBody = null;\n      try {",
                            "InputStream localVarResponseBody = null;\n      try {\n        " +
                                    "if (memberVarResponseInterceptor != null) {\n          " +
                                    "memberVarResponseInterceptor.accept(localVarResponse);\n        }")
            Regex("@JsonCreator\\n {2}public static ([\\w_]*)[\\s\\S]*?\\n {2}}").find(source)?.run {
                val type = groupValues[1]
                source = source.replaceRange(range.first, range.last + 1,  """
                public static final ImmutableMap<String, $type> VALUES_OF_UPPERCASED_STRINGS = Arrays.stream(values())
                        .collect(ImmutableMap.toImmutableMap(value -> value.toString().toUpperCase(Locale.ROOT), e -> e));
                @JsonCreator
                public static $type fromValue(String value) {
                    return VALUES_OF_UPPERCASED_STRINGS.get(value.toUpperCase(Locale.ROOT));
                }
                """.trimIndent())
            }
            val headersCall = ".headers()"
            source = source.replace("$headersCall.map()", headersCall)
            val httpRequestNewBuilder = "HttpRequest.newBuilder()"
            source = source.replace(httpRequestNewBuilder,
                    "$httpRequestNewBuilder.header(\"Accept-Encoding\", \"gzip\")")
            var acceptCommaTextEventStreamIndex = 0
            while (source.indexOf("\"Accept\", \"text/event-stream\"", acceptCommaTextEventStreamIndex + 1)
                            .also { acceptCommaTextEventStreamIndex = it } != -1) {
                val privateHttpRequestBuilder = "private HttpRequest.Builder "
                val methodNameStartIndex = source.lastIndexOf(privateHttpRequestBuilder,
                        acceptCommaTextEventStreamIndex)
                val methodName = source.substring(methodNameStartIndex + privateHttpRequestBuilder.length,
                        source.indexOf("RequestBuilder(", methodNameStartIndex))
                val methodIndex = source.indexOf(" $methodName(")
                val methodJavadocAndAnnotations = source.substring(source.lastIndexOf("/**", methodIndex),
                        source.lastIndexOf("public", methodIndex))
                val methodLine = source.substring(source.lastIndexOf('\n', methodIndex) + 1,
                        source.indexOf('\n', methodIndex))
                val methodReturnType = Regex(" (?:List<)?([\\w_]*)>? $methodName").find(methodLine)!!.groupValues[1]
                val methodArguments = Regex("$methodName\\((.*)\\)").find(methodLine)!!.groupValues[1]
                val methodArgumentNames = Splitter.on(", ").split(methodArguments)
                        .joinToString { it.substring(it.lastIndexOf(' ')) }
                val methodEndBrace = "\n  }"
                val methodRemove = IntRange(source.lastIndexOf("/**", methodIndex),
                        source.indexOf(methodEndBrace, source.indexOf(methodEndBrace, methodIndex) + 1) +
                                methodEndBrace.length)
                source = source.removeRange(methodRemove)
                acceptCommaTextEventStreamIndex -= methodRemove.last - methodRemove.first
                val methodHttpInfoIndex = source.indexOf(" ${methodName}WithHttpInfo(")
                val methodHttpInfoRemove = IntRange(source.lastIndexOf("/**", methodHttpInfoIndex),
                        source.indexOf(methodEndBrace, source.indexOf(methodEndBrace, methodHttpInfoIndex) + 1) +
                                methodEndBrace.length)
                val sseMethods = """
                $methodJavadocAndAnnotations
                public SseResponse $methodName($methodArguments, SseListener<$methodReturnType> sseListener)
                        throws ApiException {
                    return $methodName($methodArgumentNames, sseListener, null);
                }

                $methodJavadocAndAnnotations
                public SseResponse $methodName($methodArguments, SseListener<$methodReturnType> sseListener,
                        @Nullable Map<String, String> headers) throws ApiException {
                    try {
                        var response = memberVarHttpClient.send(
                                ${methodName}RequestBuilder($methodArgumentNames, headers)
                                        .setHeader("Accept-Encoding", "identity").build(),
                                HttpResponse.BodyHandlers.ofInputStream());
                        try {
                            if (memberVarResponseInterceptor != null) {
                                memberVarResponseInterceptor.accept(response);
                            }
                            if (response.statusCode() / 100 != 2) {
                                throw getApiException("$methodName", response);
                            }
                            return new SseResponse(memberVarHttpClient, response, sseListener, s -> {
                                try {
                                    return memberVarObjectMapper.readValue(s, new TypeReference<$methodReturnType>() {}); 
                                } catch (JacksonException e) {
                                    throw new ApiException(e);
                                }
                            });
                        } catch (Throwable throwable) {
                            response.body().close();
                            throw throwable;
                        }
                    } catch (IOException e) {
                        throw new ApiException(e);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new ApiException(e);
                    }
                }
                """.trimIndent()
                source = source.replaceRange(methodHttpInfoRemove, sseMethods)
                acceptCommaTextEventStreamIndex -= methodHttpInfoRemove.last - methodHttpInfoRemove.first
                acceptCommaTextEventStreamIndex += sseMethods.length
            }
            sourceFile.writeText(source)
        }
        val outputPackageDirectory = srcMainJava.resolve(outputPackageName.replace('.', '/'))
        outputPackageDirectory.resolve("ApiClient.java").apply {
            val apiClientConstructor = "public ApiClient() {"
            writeText(readText().replace(apiClientConstructor,
                    "public ApiClient(ObjectMapper mapper) { this.mapper = mapper; }$apiClientConstructor"))
        }
        outputPackageDirectory.resolve("ApiException.java").apply {
            writeText(readText().replace("extends Exception {", "extends RuntimeException {"))
        }
        outputPackageDirectory.resolve("ApiResponse.java").apply {
            writeText(readText().replace("Map<String, List<String>>", "java.net.http.HttpHeaders"))
        }
        val apiClassNamesOfMethodNames = outputPackageDirectory.resolve(apiPackageName).list()
                .map { it.replace(".java", "") }
                .associateBy { UPPER_CAMEL.to(LOWER_CAMEL, it.substring(0, it.length - 3)) }
        val className = "${LOWER_HYPHEN.to(UPPER_CAMEL, projectName)}Api"
        outputPackageDirectory.resolve("${className}.java").writeText("""
        package ${outputPackageName};

        import com.google.errorprone.annotations.concurrent.LazyInit;
        import net.jacobpeterson.alpacajava.common.ApiHeader;
        import net.jacobpeterson.alpacajava.common.ApiEnvironment;
        import ${outputPackageName}.${apiPackageName}.*;
        import org.jspecify.annotations.NullMarked;
        import org.jspecify.annotations.Nullable;
        import tools.jackson.databind.ObjectMapper;
        import com.google.common.net.HttpHeaders;

        import java.io.InputStream;
        import java.net.http.HttpClient;
        import java.net.http.HttpRequest;
        import java.net.http.HttpResponse;
        import java.util.function.Consumer;

        import static java.time.Duration.ofSeconds;
        import static java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor;

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
             * with everything set to <code>null</code> except <code>authorizationToken</code> and <code>apiEnvironment</code>.
             */
            public ${className}(final @Nullable String authorizationToken, final @Nullable ApiEnvironment apiEnvironment) {
                this(null, null, null, null, authorizationToken, apiEnvironment, null, null);
            }

            /**
             * Instantiates a new {@link ${className}}.
             *
             * @param httpClient               the {@link HttpClient}, or <code>null</code> to use a new default instance
             * @param objectMapper             the {@link ObjectMapper}, or <code>null</code> to use a new default instance
             * @param authenticationKeyID      the {@link ApiHeader#API_KEY_ID} value
             * @param authenticationSecretKey  the {@link ApiHeader#API_SECRET_KEY} value
             * @param authorizationToken       the {@link HttpHeaders#AUTHORIZATION} value (should start with
             *                                 {@link ApiHeader#AUTHORIZATION_BASIC_PREFIX} or
             *                                 {@link ApiHeader#AUTHORIZATION_BEARER_PREFIX})
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
                    final @Nullable String authorizationToken, final @Nullable ApiEnvironment apiEnvironment,
                    final @Nullable Consumer<HttpRequest.Builder> requestInterceptor,
                    final @Nullable Consumer<HttpResponse<InputStream>> responseInterceptor) {
                this.httpClient = httpClient != null ? httpClient : HttpClient.newBuilder()
                        .connectTimeout(ofSeconds(10))
                        .executor(newVirtualThreadPerTaskExecutor())
                        .build();
                final var baseUri = apiEnvironment == ApiEnvironment.PRODUCTION ? ENVIRONMENT_URL_PRODUCTION :
                        ENVIRONMENT_URL_DEVELOPMENT;
                apiClient = new ApiClient(objectMapper != null ? objectMapper : ApiClient.createDefaultObjectMapper()) {

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
                    if (authenticationKeyID != null) {
                        builder.header(ApiHeader.API_KEY_ID, authenticationKeyID);
                    }
                    if (authenticationSecretKey != null) {
                        builder.header(ApiHeader.API_SECRET_KEY, authenticationSecretKey);
                    }
                    if (authorizationToken != null) {
                        builder.header(HttpHeaders.AUTHORIZATION, authorizationToken);
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
