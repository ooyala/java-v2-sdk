package com.ooyala.api;

import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;


public class SignatureGenerator {

  /**
   * Round-up time value.
   */
  public long roundUpTimeSeconds = 300L;
  /**
   * Value (in seconds) which indicates the (time) 'window' where the request remains valid
   */
  public long expirationWindowSeconds = 15L;
  String apiKey;
  String secretKey;
  MessageDigest digestProvider;


  public SignatureGenerator(String apiKey, String secretKey) throws NoSuchAlgorithmException {
    this.apiKey = apiKey;
    this.secretKey = secretKey;
    this.digestProvider = MessageDigest.getInstance("SHA-256");
  }

  /**
   * Generates the signature for a request, using a body in the request.
   * If the method is a GET, then it does not need the body. On the other hand
   * if it is a POST, PUT or PATCH, the body is a string with the parameters that
   * are going to be modified, or assigned to the resource.
   * This should be later added to the query parameters,
   * as the signature parameter of the desired requested URI.
   *
   * @param httpMethod  The method of the request (GET, POST, PUT, PATCH).
   * @param requestPath The path of the request (i.e. /v2/players).
   * @param parameters  The query parameters.
   * @param requestBody The body of the request, used for POST, PUT and PATCH.
   * @return The signature that should be added to the request URI as the signature parameter.
   * @throws NoSuchAlgorithmException if the SHA256 algorithm is not available.
   * @throws IOException
   */

  public String generateSignature(final String httpMethod, final String requestPath, Map<String,
      String> parameters, final String requestBody, final Long expirationTime) throws
      NoSuchAlgorithmException, IOException {

    parameters.put("api_key", apiKey);
    parameters.put("expires",
        String.format("%d", expirationTime == null ? getExpiration() : expirationTime));

    String stringToSign = secretKey + httpMethod.toUpperCase() + requestPath;
    stringToSign += HttpUtils.concatenateParams(new HashMap<String, String>(parameters), "");
    stringToSign += requestBody;

    digestProvider.reset();
    byte[] digest = digestProvider.digest(stringToSign.getBytes(HttpUtils.API_CHARSET));
    String signedInput = Base64.encodeBase64String(digest);

    signedInput = signedInput.replaceFirst("=*$", "");
    return HttpUtils.encodeURI(signedInput.substring(0, 43));
  }

  /**
   * Generates the signature for a request without a body
   *
   * @param httpMethod  The method of the request (GET, POST, PUT, PATCH).
   * @param requestPath The path of the request (i.e. /v2/players).
   * @param parameters  The query parameters.
   * @return The signature that should be added to the request URI as the signature parameter.
   * @throws NoSuchAlgorithmException if the SHA256 algorithm is not available.
   * @throws IOException
   */
  public String generateSignature(String httpMethod, String requestPath, Map<String, String>
      parameters) throws NoSuchAlgorithmException, IOException {
    return generateSignature(httpMethod, requestPath, parameters, null, null);
  }

  /**
   * Get expiration date (in seconds).
   *
   * @return the expiration date in seconds
   */
  public long getExpiration() {
    long nowPlusWindow = System.currentTimeMillis() / 1000 + expirationWindowSeconds;
    long roundUp = roundUpTimeSeconds - (nowPlusWindow % roundUpTimeSeconds);
    return (nowPlusWindow + roundUp);
  }
}
