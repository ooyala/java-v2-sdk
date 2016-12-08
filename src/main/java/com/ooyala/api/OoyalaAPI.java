/**
 * Copyright 2011 © Ooyala, Inc.  All rights reserved.
 * <p/>
 * Ooyala, Inc. (“Ooyala”) hereby grants permission, free of charge, to any person or entity
 * obtaining a copy of the software code provided in source code format via this webpage and
 * direct links contained within this webpage and any associated documentation (collectively, the
 * "Software"), to use, copy, modify, merge, and/or publish the Software and, subject to
 * pass-through of all terms and conditions hereof, permission to transfer, distribute and
 * sublicense the Software; all of the foregoing subject to the following terms and conditions:
 * <p/>
 * 1.  The above copyright notice and this permission notice shall be included in all copies or
 * portions of the Software.
 * <p/>
 * 2.   For purposes of clarity, the Software does not include any APIs, but instead consists of
 * code that may be used in conjunction with APIs that may be provided by Ooyala pursuant to a
 * separate written agreement subject to fees.
 * <p/>
 * 3.   Ooyala may in its sole discretion maintain and/or update the Software.  However, the
 * Software is provided without any promise or obligation of support, maintenance or update.
 * <p/>
 * 4.  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, TITLE, AND NONINFRINGEMENT.  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, RELATING TO, ARISING FROM, IN CONNECTION WITH, OR INCIDENTAL TO THE SOFTWARE OR THE
 * USE OR OTHER DEALINGS IN THE SOFTWARE.
 * <p/>
 * 5.   TO THE MAXIMUM EXTENT PERMITTED BY APPLICABLE LAW, (i) IN NO EVENT SHALL OOYALA BE LIABLE
 * FOR ANY CONSEQUENTIAL, INCIDENTAL, INDIRECT, SPECIAL, PUNITIVE, OR OTHER DAMAGES WHATSOEVER
 * (INCLUDING, WITHOUT LIMITATION, DAMAGES FOR LOSS OF BUSINESS PROFITS, BUSINESS INTERRUPTION,
 * LOSS OF BUSINESS INFORMATION, OR OTHER PECUNIARY LOSS) RELATING TO, ARISING FROM, IN
 * CONNECTION WITH, OR INCIDENTAL TO THE SOFTWARE OR THE USE OF OR INABILITY TO USE THE SOFTWARE,
 * EVEN IF OOYALA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES, AND (ii) OOYALA’S TOTAL
 * AGGREGATE LIABILITY RELATING TO, ARISING FROM, IN CONNECTION WITH, OR INCIDENTAL TO THE
 * SOFTWARE SHALL BE LIMITED TO THE ACTUAL DIRECT DAMAGES INCURRED UP TO MAXIMUM AMOUNT OF FIFTY
 * DOLLARS ($50).
 */

package com.ooyala.api;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONValue;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The OoyalaAPI class implements methods to call the V2 Ooyala API.
 * <p>
 * It uses the Simple.JSON Java library to parse JSON (see: http://code.google.com/p/json-simple/)
 * <p>
 * Please keep in mind that when creating your HashMap to send the JSON body, align to using Maps
 * and Lists as the Simple.JSON library indicates.
 */
public class OoyalaAPI {

  /**
   * the Secret key
   */
  private String secretKey;

  /**
   * The API key
   */
  private String apiKey;
  /**
   * Base URL to Ooyala API
   */
  private String baseURL;
  /**
   * Represents the HTTP Status Code from the last response
   */
  private int responseCode;
  /**
   * The request's content type
   */
  private String contentType;
  private SignatureGenerator signatureGenerator;

  /**
   * Constructor with keys
   *
   * @param apiKey    The API key
   * @param secretKey The secret key
   */
  public OoyalaAPI(String apiKey, String secretKey) throws NoSuchAlgorithmException {
    this.secretKey = secretKey;
    this.apiKey = apiKey;
    this.signatureGenerator = new SignatureGenerator(apiKey, secretKey);
    baseURL = "https://api.ooyala.com";
    contentType = "application/json";
  }

  /**
   * Gets the Secret key
   *
   * @return the secret key
   */
  public String getSecretKey() {
    return secretKey;
  }

  /**
   * Sets the secret key
   *
   * @param secretKey The secret key to be set
   */
  public void setSecretKey(String secretKey) {
    this.secretKey = secretKey;
  }

  /**
   * Gets the API key
   *
   * @return the API key
   */
  public String getAPIKey() {
    return apiKey;
  }

  /**
   * Sets the API key
   *
   * @param apiKey The secret key to be set
   */
  public void setAPIKey(String apiKey) {
    this.apiKey = apiKey;
  }

  /**
   * Gets the URL where requests are sent
   *
   * @return the URL
   */
  public String getBaseURL() {
    return baseURL;
  }

  /**
   * Sets the URL where requests are sent
   *
   * @param baseURL The URL to be set
   * @return
   */
  public void setBaseURL(String baseURL) {
    this.baseURL = baseURL;
  }

  /**
   * Get the response code from previous request
   *
   * @return
   */
  public int getResponseCode() {
    return responseCode;
  }

  /**
   * Get the request's content type
   *
   * @return The request's content type
   */
  public String getContentType() {
    return contentType;
  }

  /**
   * Set the request's content type
   *
   * @param contentType The request's content type
   */
  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  /**
   * Response Handler
   *
   * @return
   */
  private ResponseHandler<String> createResponseHandler() {
    return new ResponseHandler<String>() {
      public String handleResponse(HttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        responseCode = response.getStatusLine().getStatusCode();
        if (entity != null) {
          return EntityUtils.toString(entity);
        } else {
          return null;
        }
      }
    };
  }

  /**
   * Sends a Request to the URL using the indicating HTTP method, content type and the array of
   * bytes as body
   *
   * @param httpMethod  The HTTPMethod
   * @param url         The URL where the request is made
   * @param requestBody The request's body as an array of bytes
   * @return The response from the server as an object of class Object. Must be casted to either
   * a LinkedList<String> or an HashMap<String, Object>
   * @throws IOException
   * @throws HttpStatusCodeException
   */
  public Object sendRequest(String httpMethod, String url, byte[] requestBody) throws
      IOException, HttpStatusCodeException {
    HttpRequestBase method = getHttpMethod(httpMethod, url, new ByteArrayEntity(requestBody));
    return executeRequest(method);
  }

  /**
   * Creates a request to a given path using the indicated HTTP-Method with a (string) body
   *
   * @param httpMethod  The HTTP method (verb)
   * @param requestPath The request path
   * @param parameters  The query parameters
   * @param requestBody The request's body
   * @return The response from the server as an object of class Object. Must be casted to either
   * a LinkedList<String> or an HashMap<String, Object>
   * @throws NoSuchAlgorithmException
   * @throws IOException
   * @throws HttpStatusCodeException
   */
  @SuppressWarnings("rawtypes")
  public Object sendRequest(String httpMethod, String requestPath, Map<String, String>
      parameters, Map<String, Object> requestBody) throws NoSuchAlgorithmException, IOException,
      HttpStatusCodeException {
    String jsonBody = "";

    if (requestBody != null && !requestBody.keySet().isEmpty()) {
      jsonBody = JSONValue.toJSONString((Map) requestBody);
    }

    String url = generateURLWithAuthenticationParameters(httpMethod,
        getFixedRequestPath(requestPath), parameters, jsonBody);

    HttpRequestBase method = getHttpMethod(httpMethod, url,
        new StringEntity(jsonBody, HttpUtils.API_CHARSET));
    return executeRequest(method);
  }

  /**
   * Creates a request to a given path (requestPath) using the indicated HTTP-Method
   * (HTTPMethod) wit neither
   * parameters nor a body (requestBody)
   *
   * @param httpMethod  The HTTP method
   * @param requestPath the request path
   * @return The response from the server as an object of class Object. Must be casted to either
   * a LinkedList<String> or an HashMap<String, Object>
   * @throws IOException
   * @throws NoSuchAlgorithmException
   * @throws HttpStatusCodeException
   */
  public Object sendRequest(String httpMethod, String requestPath) throws IOException,
      NoSuchAlgorithmException, HttpStatusCodeException {
    return sendRequest(httpMethod, getFixedRequestPath(requestPath), new HashMap<String, String>(),
        new HashMap<String, Object>());
  }

  /**
   * Creates a request to a given path using the indicated HTTP-Method with a (byte array) body
   *
   * @param httpMethod  The HTTP method (verb)
   * @param requestPath The request path
   * @param parameters  The query parameters
   * @param requestBody The request's body
   * @return The response from the server as an object of class Object. Must be casted to either
   * a LinkedList<String> or an HashMap<String, Object>
   * @throws NoSuchAlgorithmException
   * @throws IOException
   * @throws HttpStatusCodeException
   */
  public Object sendRequest(String httpMethod, String requestPath, Map<String, String>
      parameters, byte[] requestBody) throws NoSuchAlgorithmException, IOException,
      HttpStatusCodeException {
    String url = generateURLWithAuthenticationParameters(httpMethod,
        getFixedRequestPath(requestPath), parameters,
        new String(requestBody, HttpUtils.API_CHARSET));
    HttpRequestBase method = getHttpMethod(httpMethod, url, new ByteArrayEntity(requestBody));
    return executeRequest(method);
  }


  /**
   * Creates an instance of HttpRequestBase's subclass (HttpGet, HttpDelete, etc).
   *
   * @param httpMethod The HTTPMethod string name
   * @param url        The URL
   * @param entity     the entity carrying the request's content
   * @return An instance of a HttpRequestBase's subclass
   */
  private HttpRequestBase getHttpMethod(String httpMethod, String url, AbstractHttpEntity entity) {
    HttpRequestBase method = null;
    entity.setContentType(contentType);

    /* create the method object */
    if (httpMethod.toLowerCase().contentEquals("get")) {
      method = new HttpGet(url);
    } else if (httpMethod.toLowerCase().contentEquals("delete")) {
      method = new HttpDelete(url);
    } else {
      if (httpMethod.toLowerCase().contentEquals("post")) {
        method = new HttpPost(url);
        ((HttpPost) method).setEntity(entity);
      } else if (httpMethod.toLowerCase().contentEquals("patch")) {
        method = new HttpPatch(url);
        ((HttpPatch) method).setEntity(entity);
      } else if (httpMethod.toLowerCase().contentEquals("put")) {
        method = new HttpPut(url);
        ((HttpPut) method).setEntity(entity);
      }
    }
    return method;
  }

  /**
   * Generate the URL including the authentication parameters
   *
   * @param httpMethod  The HTTP Method
   * @param requestPath The request's path
   * @param parameters  The query parameters
   * @param requestBody The string request body
   * @return
   * @throws NoSuchAlgorithmException
   * @throws IOException
   */
  @SuppressWarnings("unchecked")
  private String generateURLWithAuthenticationParameters(String httpMethod, String requestPath,
                                                         Map<String, String> parameters, String
                                                               requestBody) throws
      NoSuchAlgorithmException, IOException {


    HashMap<String, String> parametersWithAuthentication = new HashMap<String, String>(parameters);

    String signature = signatureGenerator
        .generateSignature(httpMethod.toUpperCase(), requestPath, parametersWithAuthentication,
            requestBody, null);
    parametersWithAuthentication.put("signature", signature);
    return buildURL(httpMethod, requestPath, parametersWithAuthentication);
  }

  /**
   * Executes the request
   *
   * @param method The class containing the type of request (HttpGet, HttpDelete, etc)
   * @return The response from the server as an object of class Object. Must be casted to
   * either a LinkedList<String> or an HashMap<String, Object>
   * @throws IOException
   * @throws HttpStatusCodeException
   */
  @SuppressWarnings("unchecked")
  private Object executeRequest(HttpRequestBase method) throws IOException,
      HttpStatusCodeException {

    HttpClient httpclient = new DefaultHttpClient();
    String response = httpclient.execute(method, createResponseHandler());
    if (!isResponseOK()) {
      throw new HttpStatusCodeException(response, getResponseCode());
    }

    if (response.isEmpty()) {
      return null;
    }

    JSONParser parser = new JSONParser();

    ContainerFactory containerFactory = new ContainerFactory() {
      @SuppressWarnings("rawtypes")
      public java.util.Map createObjectContainer() {
        return new LinkedHashMap();
      }

      @SuppressWarnings("rawtypes")
      public List creatArrayContainer() {
        return new LinkedList();
      }
    };

    Object json = null;

    try {
      json = parser.parse(response, containerFactory);
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return json;
  }

  /**
   * URI encodes non-authentication values
   *
   * @param parameters The hashtable containing values to URI encode
   * @return
   * @throws UnsupportedEncodingException
   */
  private HashMap<String, String> makeURIValues(HashMap<String, String> parameters) throws
      UnsupportedEncodingException {
    HashMap<String, String> uriParameters = new HashMap<String, String>();
    Set<String> set = parameters.keySet();
    for (String key : set) {
      String value = parameters.get(key);

      /* just URI encode non-authentication params*/
      if (!(key.equals("expires") || key.equals("api_key") || key.equals("signature"))) {
        uriParameters.put(key, HttpUtils.encodeURI(parameters.get(key)));
      } else {
        uriParameters.put(key, value);
      }
    }
    return uriParameters;
  }

  /**
   * Builds the URL for a given request. In the process, it includes the api_key, expires and
   * signature parameters
   *
   * @param httpMethod  The HTTP method
   * @param requestPath The request path
   * @param parameters  The query parameters
   * @return The URL for a request.
   * @throws java.security.NoSuchAlgorithmException if the SHA256 algorithm is not
   *                                                available.
   * @throws java.io.UnsupportedEncodingException   if the Base64 encoder is not able
   *                                                to generate an output.
   */
  public String buildURL(String httpMethod, String requestPath, HashMap<String, String>
      parameters) throws java.security.NoSuchAlgorithmException, java.io
      .UnsupportedEncodingException {
    return String.format("%s%s?%s", baseURL, requestPath,
        HttpUtils.concatenateParams(makeURIValues(parameters), "&"));
  }

  /**
   * Sends a POST request
   *
   * @param requestPath The request path
   * @param requestBody The request's body
   * @return The response from the server as an object of class Object. Must be casted to either
   * a LinkedList<String> or an HashMap<String, Object>
   * @throws IOException
   * @throws NoSuchAlgorithmException
   * @throws HttpStatusCodeException
   */
  public Object postRequest(String requestPath, Map<String, Object> requestBody) throws
      IOException, NoSuchAlgorithmException, HttpStatusCodeException {
    return sendRequest("POST", requestPath, new HashMap<String, String>(), requestBody);
  }

  /**
   * Sends a GET request
   *
   * @param requestPath The request path
   * @param parameters  hashtable containing query parameters
   * @return The response from the server as an object of class Object. Must be casted to either
   * a LinkedList<String> or an HashMap<String, Object>
   * @throws IOException
   * @throws NoSuchAlgorithmException
   * @throws HttpStatusCodeException
   */
  public Object getRequest(String requestPath, Map<String, String> parameters) throws
      IOException, NoSuchAlgorithmException, HttpStatusCodeException {
    return sendRequest("GET", requestPath, parameters, new HashMap<String, Object>());
  }

  /**
   * Sends a GET request
   *
   * @param requestPath The request path
   * @return The response from the server as an object of class Object. Must be casted to
   * either a LinkedList<String> or an HashMap<String, Object>
   * @throws IOException
   * @throws NoSuchAlgorithmException
   * @throws HttpStatusCodeException
   */
  public Object getRequest(String requestPath) throws IOException, NoSuchAlgorithmException,
      HttpStatusCodeException {
    return sendRequest("GET", requestPath);
  }

  /**
   * Sends a PUT request
   *
   * @param requestPath The request path
   * @param requestBody The request's body
   * @return The response from the server as an object of class Object. Must be casted to
   * either a LinkedList<String> or an HashMap<String, Object>
   * @throws IOException
   * @throws NoSuchAlgorithmException
   * @throws HttpStatusCodeException
   */
  public Object putRequest(String requestPath, Map<String, Object> requestBody) throws
      IOException, NoSuchAlgorithmException, HttpStatusCodeException {
    return sendRequest("PUT", requestPath, new HashMap<String, String>(), requestBody);
  }

  /**
   * Sends a DELETE request
   *
   * @param requestPath The request path
   * @return The response from the server as an object of class Object. Must be casted to
   * either a LinkedList<String> or an HashMap<String, Object>
   * @throws IOException
   * @throws NoSuchAlgorithmException
   * @throws HttpStatusCodeException
   */
  public Object deleteRequest(String requestPath) throws IOException, NoSuchAlgorithmException,
      HttpStatusCodeException {
    return sendRequest("DELETE", requestPath);
  }

  /**
   * Sends a PATCH request
   *
   * @param requestPath The request path
   * @param requestBody The patch to be sent
   * @return The response from the server as an object of class Object. Must be casted to
   * either a LinkedList<String> or an HashMap<String, Object>
   * @throws IOException
   * @throws NoSuchAlgorithmException
   * @throws HttpStatusCodeException
   */
  public Object patchRequest(String requestPath, Map<String, Object> requestBody) throws
      IOException, NoSuchAlgorithmException, HttpStatusCodeException {
    return sendRequest("PATCH", requestPath, new HashMap<String, String>(), requestBody);
  }

  /**
   * This is to keep the api backward compatible, the /v2/ prefix was not needed in earlier
   * versions, and baseURL
   * itself had /v2/ as part of it. But this wouldn't work with v3 api urls. So now requestPath
   * needs to have /v2/
   * or /v3/ in it. If it is not present we add /v2/ prefix
   */
  private String getFixedRequestPath(String requestPath) {

    if (!requestPath.startsWith("/v2/") && !requestPath.startsWith("/v3/")) {
      requestPath = "/v2/" + requestPath;
    }
    return requestPath;
  }

  /**
   * Indicates if a request was successful
   *
   * @return
   */
  public boolean isResponseOK() {
    return ((responseCode >= 200) && (responseCode < 400));
  }

  /**
   * HttpPatch class which allows PATCH requests
   */
  private class HttpPatch extends HttpPost {
    public HttpPatch(String string) {
      super(string);
    }

    public String getMethod() {
      return "PATCH";
    }
  }
}
