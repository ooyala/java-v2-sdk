package com.ooyala.api;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class SignatureGeneratorTest {

  @Test
  public void testGenerateSignature() throws Exception {

    String apiKey = "test_api_key";
    String secretKey = "test_api_secret";
    Long expireTime = 1455595421L;

    String urlPath = "/v2/analytics/reports/account/performance/cities/2013-05-01...2013-08-02";
    Map<String, String> params = new HashMap<String, String>();
    params.put("limit", "25");
    params.put("order_by", "plays");

    SignatureGenerator signatureGenerator = new SignatureGenerator(apiKey, secretKey);


    assertEquals("iFu%2FtGCSlMds3VqcFht0a2OZwrjK4%2BPs689NujsDe8s",
        signatureGenerator.generateSignature("get", urlPath, params, "", expireTime));

    assertEquals("ualpt4pGZh9q78krXLxWOWer3lAEeY43s%2FAjawbZJeA", signatureGenerator
        .generateSignature("get", urlPath, params, "{'json': 'body', 'is': 'ok'}", expireTime));

    assertEquals("P5Ll1cxJhXhB8lMHk%2Bdaf9LAbooU7rrGRPLbrUZntXQ", signatureGenerator
        .generateSignature("get", urlPath, new HashMap<String, String>(),
            "{'json': 'body', 'is':" + " 'ok'}", expireTime));

    assertEquals("rA9t2taPoBlqmM3Zn2UXgkYHtm5zVf21i0an4cCsPw0", signatureGenerator
        .generateSignature("get", urlPath, new HashMap<String, String>(), "", expireTime));
  }

}
