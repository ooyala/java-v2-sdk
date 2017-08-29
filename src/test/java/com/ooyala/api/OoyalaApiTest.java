package com.ooyala.api;

import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class OoyalaApiTest {
  private String apikey = System.getProperty("ooyala_apikey");
  private String apisecret = System.getProperty("ooyala_apisecret");

  @Test
  public void testV2Assets() throws Exception {
    OoyalaAPI ooyalaApiClient = new OoyalaAPI(apikey, apisecret);
    Object performanceTotal = ooyalaApiClient.getRequest("assets");
    System.out.println(performanceTotal);
  }

  @Ignore(value = "This test fails due to 'Internal API connection error'")
  @Test
  public void testV2AccountPerformance() throws Exception {
    OoyalaAPI ooyalaApiClient = new OoyalaAPI(apikey, apisecret);
    Object performanceTotal = ooyalaApiClient
        .getRequest("analytics/reports/account/performance/total/2014-01-01...2014-05-01");
    System.out.println(performanceTotal);
  }

  @Ignore(value = "This test fails due to 'Internal API connection error'")
  @Test
  public void testV2AssetPerformance() throws Exception {
    OoyalaAPI ooyalaApiClient = new OoyalaAPI(apikey, apisecret);
    Map<String, String> params = new HashMap<String, String>();
    params.put("limit", "25");
    params.put("limit", "25");
    params.put("limit", "25");
    params.put("order_by", "plays");
    Object performanceTotal = ooyalaApiClient
        .getRequest("/v2/analytics/reports/account/performance/cities/2013-05-01...2013-08-02",
            params);
    System.out.println(performanceTotal);
  }

  @Test
  public void testV3AnalyticsReport() throws Exception {
    OoyalaAPI ooyalaApiClient = new OoyalaAPI(apikey, apisecret);
    Map<String, String> params = new HashMap<String, String>();
    params.put("report_type", "performance");
    params.put("start_date", "2016-06-01");
    params.put("end_date", "2016-06-02");
    params.put("limit", "25");
    params.put("order_by", "plays");
    Object performanceTotal = ooyalaApiClient.getRequest("/v3/analytics/reports", params);
    System.out.println(performanceTotal);
  }
}
