/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ubs.wmap.eisl.initilizationservice.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.ubs.wmap.eisl.housekeeping.TokenService;
import com.ubs.wmap.eisl.initilizationservice.exceptions.InvalidEislTokenException;
import com.ubs.wmap.eisl.initilizationservice.models.Payload;

@ActiveProfiles("test")
@RunWith(MockitoJUnitRunner.class)
public class IntitilizationServiceTest {

	@Mock
	RestTemplate restTemplate;

	@Mock
	TokenService tokenService;
	
	@Mock
	UriComponentsBuilder uriComponentsBuilder;

	@InjectMocks
	InitilizationServiceImpl initilizationServiceImpl;

	@Test
	public void eislTokenValidTest() {
		//Mockito.when(tokenService.isEISLTokenValid(ArgumentMatchers.anyString())).thenReturn(isValid);
		Mockito.when(tokenService.isEISLTokenValid(ArgumentMatchers.anyString())).thenReturn(true);
		assertTrue("valid", initilizationServiceImpl.validateEislToken("testEislToken"));
	}

	@Test
	public void generateEislTest() {
		String respEisl = "testEisl";
		Mockito.when(tokenService.init(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString())).thenReturn(respEisl);
		assertEquals("Success", respEisl,
				initilizationServiceImpl.generateEislToken("testUserId", "testServiceId", "testRole"));
	}

	@Test(expected = InvalidEislTokenException.class)
	public void InvalidEislException() {
		Mockito.when(tokenService.init(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString())).thenThrow(InvalidEislTokenException.class);
		initilizationServiceImpl.generateEislToken("testUserId", "testServiceId", "testRole");
	}

	@Test(expected = InvalidEislTokenException.class)
	public void InvalideislTokenTest() {
		Mockito.when(tokenService.isEISLTokenValid(ArgumentMatchers.anyString()))
				.thenThrow(InvalidEislTokenException.class);
		initilizationServiceImpl.validateEislToken("testEislToken");
	}

	@Test
	public void postRegistrationTest() {
		String basicToken = "testBasic";
		Payload payload = new Payload();
		String eislToken = "TestEisl";

		HttpHeaders headers = new HttpHeaders();
		headers.add("basicToken", basicToken);
		MultiValueMap<String, Object> request = new LinkedMultiValueMap<>();
		request.add("payload", payload);
		HttpEntity<Object> requestEntity = new HttpEntity<>(request, headers);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://localhost:8080")
				.queryParam("eislToken", eislToken);
		Mockito.when(UriComponentsBuilder.fromHttpUrl(ArgumentMatchers.anyString()).queryParam(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
		        .thenReturn(builder);
		Mockito.when(restTemplate.postForObject(builder.toUriString(), requestEntity, String.class)).thenReturn(eislToken);
		initilizationServiceImpl.postRegistration(eislToken, basicToken, payload);
	}

	@Test
	public void deleteRegistration() {
		String basicToken = "testBasic";
		String eislToken = "TestEisl";
		HttpHeaders headers = new HttpHeaders();
		headers.add("basicToken", basicToken);
		HttpEntity<String> requestEntity = new HttpEntity<>(headers);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:8084/eisl/resitraion/v1/registrations")
				.queryParam("eislToken", eislToken);
		initilizationServiceImpl.deleteRegistration(basicToken, eislToken);
	}

	@Test
	public void putRegistration() {
		
		String basicToken = "testBasic";
		Payload payload = new Payload();
		String eislToken = "TestEisl";
		
		HttpHeaders headers = new HttpHeaders();
        headers.add("basicToken", basicToken);
        MultiValueMap<String,Object> request = new LinkedMultiValueMap<>();
        HttpEntity<Object> requestEntity = new HttpEntity<>(request,headers);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://localhost:8080").queryParam("eislToken", eislToken);
        restTemplate.put(builder.toUriString(), String.class, requestEntity);
	}

}
