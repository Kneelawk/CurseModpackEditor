package com.kneelawk.modpackeditor.net

import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.impl.auth.BasicScheme
import org.apache.http.impl.client.BasicAuthCache
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import tornadofx.HttpClientEngine
import tornadofx.Rest
import java.net.URI

/**
 * Sets up TornatoFX's Rest client to use Apache HttpClient.
 */
fun setupRestEngine() {
    Rest.engineProvider = ::CustomHttpClientEngine
}

/**
 * The connection manager for the custom client.
 */
private val customConnectionManager = PoolingHttpClientConnectionManager().apply {
    maxTotal = 100
    defaultMaxPerRoute = 100
}

/**
 * Custom http client with builtin redirect url sanitization and a max of 100 total connections running at once.
 */
val customClient: CloseableHttpClient = HttpClients.custom()
        .setRedirectStrategy(RedirectUriSanitizer)
        .setConnectionManager(customConnectionManager)
        .build()

/**
 * Closes the custom client.
 */
fun shutdownCustomClient() {
    customClient.close()
}

/**
 * A custom Rest http client engine configured to use the multi-threaded pool and the redirect sanitizer.
 */
open class CustomHttpClientEngine(rest: Rest) : HttpClientEngine(rest) {
    override fun reset() {
        client = HttpClients.custom()
                .setRedirectStrategy(RedirectUriSanitizer)
                .setConnectionManager(customConnectionManager)
                .build()
        context = HttpClientContext.create()
    }

    override fun setBasicAuth(username: String, password: String) {
        requireNotNull(rest.baseURI) { "You must configure the baseURI first." }

        val uri = URI.create(rest.baseURI!!)

        val scheme = if (uri.scheme == null) "http" else uri.scheme
        val port = if (uri.port > -1) uri.port else if (scheme == "http") 80 else 443
        val host = HttpHost(uri.host, port, scheme)

        val credsProvider = BasicCredentialsProvider().apply {
            setCredentials(AuthScope(host), UsernamePasswordCredentials(username, password))
        }

        context.authCache = BasicAuthCache()
        context.authCache.put(host, BasicScheme())

        client = HttpClients.custom()
                .setRedirectStrategy(RedirectUriSanitizer)
                .setConnectionManager(customConnectionManager)
                .setDefaultCredentialsProvider(credsProvider)
                .build()
    }
}
