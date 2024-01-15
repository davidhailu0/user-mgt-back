package com.hajj.hajj;

import com.hajj.hajj.model.HUjjaj;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@SpringBootApplication
public class HajjApplication {

	public static void main(String[] args) {

		String amount="200";
		String  draccount="0010934520101";
		String  naration= "transa";
		String paymentcode="ZQYAYW";

		String apiUrl="http://10.10.11.20/HajjFundTransfer/1.0.0/";
		String token="eyJ4NXQiOiJNV0l5TkRJNVlqRTJaV1kxT0RNd01XSTNOR1ptTVRZeU5UTTJOVFZoWlRnMU5UTTNaVE5oTldKbVpERTFPVEE0TldFMVlUaGxNak5sTldFellqSXlZUSIsImtpZCI6Ik1XSXlOREk1WWpFMlpXWTFPRE13TVdJM05HWm1NVFl5TlRNMk5UVmhaVGcxTlRNM1pUTmhOV0ptWkRFMU9UQTROV0UxWVRobE1qTmxOV0V6WWpJeVlRX1JTMjU2IiwidHlwIjoiYXQrand0IiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiI0NThkN2I0My0xOTU0LTRlNjQtYjI0NS1iNDg4YjAyYWNmNmMiLCJhdXQiOiJBUFBMSUNBVElPTiIsImF1ZCI6IlRJNHJlUXlheERiYUxhQVVfVWViMDk4d1ZJa2EiLCJuYmYiOjE3MDUwNjMzMzUsImF6cCI6IlRJNHJlUXlheERiYUxhQVVfVWViMDk4d1ZJa2EiLCJzY29wZSI6ImRlZmF1bHQiLCJpc3MiOiJodHRwczpcL1wvMTkyLjE2OC4xLjI5Ojk0NDhcL29hdXRoMlwvdG9rZW4iLCJleHAiOjEwMzQ1MDYzMzM1LCJpYXQiOjE3MDUwNjMzMzUsImp0aSI6IjdmZTFmZTYwLTllNGItNDMyNy04MmM3LTVhOTU1Mjg4YjBjMyIsImNsaWVudF9pZCI6IlRJNHJlUXlheERiYUxhQVVfVWViMDk4d1ZJa2EifQ.eQncaeeMZy_lCNan_dmWCm9ib_C9xFNAk0tYUfQQEhDO9J0tLvCDCQD-LhsPQWoLNNq6oCciQjC1vZnqTU2Tim0ws5zlEVIA_dUxNVv0-kzDShwQKbseasuVk2Czdsom7ucz9VJbtUf23P2pBbZ-mMlW4PSofBpn2mab6z1wl48Q3-0AdknC5wC8YjpHO69Ptk_MkKINecz4LIbQ_zNRCcLMs6sFX0tBmoiBrSV6HhyGlJ1Lu2bCejoRhtmmV-DWoDpH4FpViMerLWy1lV0qccHlLh1lwdS-3IsNdt0PJmL4lrJYefQ8fxBBDI04HSA0PwBAN8dm3hWim3hgqYizXg";
		RestTemplate restTemplate=new RestTemplate();
		HttpHeaders headers=new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);
		headers.setContentType(MediaType.APPLICATION_JSON);
		String jsonBody = "{ " +
				"\"Amount\":" +"\""+ amount + "\"" + ","+
		        "\"Draccount\":" +"\""+ draccount + "\"" + ","+
				"\"Narrative\": " +"\""+ naration + "\"" + ","+
				"\"PaymentCode\": " +"\""+ paymentcode + "\"" +

				"}";
		HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody,headers);
		try {
			ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<Map<String, Object>>() {});

			Map<String, Object> responseBody = responseEntity.getBody();

			if (responseBody != null) {
				Object MSGSTAT = responseBody.get("MSGSTAT");
				if ("FAILURE".equals(MSGSTAT)) {
					Object EDESC = responseBody.get("EDESC");
					String name = EDESC.toString();
					System.out.println("EDESC: " + name);
				}
				else
				{

			}
			}
			else {
				System.out.println("Response body is empty or null");
			}
		}
		catch (HttpClientErrorException ex) {
			// Handle unauthorized error
		  ex.getMessage();
		}




		SpringApplication.run(HajjApplication.class, args);
	}

}
