/*
 * Copyright (c) 2015 NIBIO <http://www.nibio.no/>. 
 * 
 * This file is part of DeliaRadicumModel.
 * DeliaRadicumModel is free software: you can redistribute it and/or modify
 * it under the terms of the NIBIO Open Source License as published by 
 * NIBIO, either version 1 of the License, or (at your option) any
 * later version.
 * 
 * DeliaRadicumModel is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * NIBIO Open Source License for more details.
 * 
 * You should have received a copy of the NIBIO Open Source License
 * along with DeliaRadicumModel.  If not, see <http://www.nibio.no/licenses/>.
 * 
 */
package no.nibio.vips.model.deliaradicummodel;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import static junit.framework.Assert.fail;
import no.bioforsk.vips.entity.ModelConfiguration;
import no.bioforsk.vips.entity.Result;
import no.bioforsk.vips.entity.WeatherObservation;
import no.bioforsk.vips.model.ModelId;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author treinar
 */
public class DeliaRadicumModelTest {
    
    public DeliaRadicumModelTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getResult method, of class DeliaRadicumModel.
     */
    @Test
    public void testAcceptance() throws Exception {
        System.out.println("testAcceptance");
        DeliaRadicumModel instance = new DeliaRadicumModel();
        ModelConfiguration config = this.getConfiguration("/weatherdata_both.json");
        instance.setConfiguration(config);
        List<Result> results = instance.getResult();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Oslo"));
        cal.set(2014, Calendar.MAY, 15, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date lastGreen = cal.getTime();
        cal.set(2014, Calendar.MAY,18,0,0,0);
        Date lastYellow = cal.getTime();
       
        for(Result result:results)
        {
            //System.out.println(result.getResultValidTime() + ": " + result.getWarningStatus());
            if(result.getResultValidTime().compareTo(lastGreen) <= 0)
            {
                assertEquals(Result.WARNING_STATUS_NO_RISK, result.getWarningStatus());
            }
            else if(result.getResultValidTime().compareTo(lastYellow) <= 0)
            {
                assertEquals(Result.WARNING_STATUS_MINOR_RISK, result.getWarningStatus());
            }
            else
            {
                assertEquals(Result.WARNING_STATUS_HIGH_RISK, result.getWarningStatus());   
            }
        }
        // Testing withour TJM10
        config = this.getConfiguration("/weatherdata_air.json");
        instance.setConfiguration(config);
        results = instance.getResult();
        
        cal.set(2014, Calendar.MAY, 18, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        lastGreen = cal.getTime();
        cal.set(2014, Calendar.MAY,20,0,0,0);
        lastYellow = cal.getTime();
       
        for(Result result:results)
        {
            //System.out.println(result.getResultValidTime() + ": " + result.getWarningStatus());
            if(result.getResultValidTime().compareTo(lastGreen) <= 0)
            {
                assertEquals(Result.WARNING_STATUS_NO_RISK, result.getWarningStatus());
            }
            else if(result.getResultValidTime().compareTo(lastYellow) <= 0)
            {
                assertEquals(Result.WARNING_STATUS_MINOR_RISK, result.getWarningStatus());
            }
            else
            {
                assertEquals(Result.WARNING_STATUS_HIGH_RISK, result.getWarningStatus());   
            }
        }
    }

    /**
     * Test of getModelId method, of class DeliaRadicumModel.
     */
    @Test
    public void testGetModelId() {
        System.out.println("getModelId");
        DeliaRadicumModel instance = new DeliaRadicumModel();
        ModelId expResult = new ModelId("DELIARADIC");
        ModelId result = instance.getModelId();
        assertEquals(expResult.toString(), result.toString());
    }

    /**
     * Test of getModelName method, of class DeliaRadicumModel.
     */
    @Test
    public void testGetModelName_0args() {
        System.out.println("getModelName");
        DeliaRadicumModel instance = new DeliaRadicumModel();
        String expResult = "";
        String result = instance.getModelName();
        
    }

    /**
     * Test of getModelName method, of class DeliaRadicumModel.
     */
    @Test
    public void testGetModelName_String() {
        System.out.println("getModelName");
        String language = "";
        DeliaRadicumModel instance = new DeliaRadicumModel();
        String expResult = "";
        String result = instance.getModelName(language);
        
    }

    /**
     * Test of getLicense method, of class DeliaRadicumModel.
     */
    @Test
    public void testGetLicense() {
        System.out.println("getLicense");
        DeliaRadicumModel instance = new DeliaRadicumModel();
        String expResult = "";
        String result = instance.getLicense();
        
    }

    /**
     * Test of getCopyright method, of class DeliaRadicumModel.
     */
    @Test
    public void testGetCopyright() {
        System.out.println("getCopyright");
        DeliaRadicumModel instance = new DeliaRadicumModel();
        String expResult = "";
        String result = instance.getCopyright();
        
    }

    /**
     * Test of getModelDescription method, of class DeliaRadicumModel.
     */
    @Test
    public void testGetModelDescription_0args() {
        System.out.println("getModelDescription");
        DeliaRadicumModel instance = new DeliaRadicumModel();
        String expResult = "";
        String result = instance.getModelDescription();
        
    }

    /**
     * Test of getModelDescription method, of class DeliaRadicumModel.
     */
    @Test
    public void testGetModelDescription_String() {
        System.out.println("getModelDescription");
        String language = "";
        DeliaRadicumModel instance = new DeliaRadicumModel();
        String expResult = "";
        String result = instance.getModelDescription(language);
        
    }

    /**
     * Test of getWarningStatusInterpretation method, of class DeliaRadicumModel.
     */
    @Test
    public void testGetWarningStatusInterpretation_0args() {
        System.out.println("getWarningStatusInterpretation");
        DeliaRadicumModel instance = new DeliaRadicumModel();
        String expResult = "";
        String result = instance.getWarningStatusInterpretation();
        
    }

    /**
     * Test of getWarningStatusInterpretation method, of class DeliaRadicumModel.
     */
    @Test
    public void testGetWarningStatusInterpretation_String() {
        System.out.println("getWarningStatusInterpretation");
        String language = "";
        DeliaRadicumModel instance = new DeliaRadicumModel();
        String expResult = "";
        String result = instance.getWarningStatusInterpretation(language);
        
    }

    /**
     * Test of getModelUsage method, of class DeliaRadicumModel.
     */
    @Test
    public void testGetModelUsage_0args() {
        System.out.println("getModelUsage");
        DeliaRadicumModel instance = new DeliaRadicumModel();
        String expResult = "";
        String result = instance.getModelUsage();
        
    }

    /**
     * Test of getModelUsage method, of class DeliaRadicumModel.
     */
    @Test
    public void testGetModelUsage_String() {
        System.out.println("getModelUsage");
        String language = "";
        DeliaRadicumModel instance = new DeliaRadicumModel();
        String expResult = "";
        String result = instance.getModelUsage(language);
        
    }

    /**
     * Test of getSampleConfig method, of class DeliaRadicumModel.
     */
    @Test
    public void testGetSampleConfig() {
        System.out.println("getSampleConfig");
        DeliaRadicumModel instance = new DeliaRadicumModel();
        String expResult = "";
        String result = instance.getSampleConfig();
        
    }

    /**
     * Test of setConfiguration method, of class DeliaRadicumModel.
     */
    @Test
    public void testSetConfiguration() throws Exception {
        System.out.println("setConfiguration");
        ModelConfiguration config = this.getConfiguration("/weatherdata_both.json");
        DeliaRadicumModel instance = new DeliaRadicumModel();
        instance.setConfiguration(config);
        
    }
    
    private ModelConfiguration getConfiguration(String fileName)
    {
        try {
            ModelConfiguration config = new ModelConfiguration();
            config.setModelId(DeliaRadicumModel.MODEL_ID.toString());
            
            
            config.setConfigParameter("timeZone", "Europe/Oslo");
            BufferedInputStream inputStream = new BufferedInputStream(this.getClass().getResourceAsStream(fileName));
            JsonFactory f = new MappingJsonFactory();
            JsonParser jp = f.createParser(inputStream);
            JsonNode all = jp.readValueAsTree();
            List<WeatherObservation> observations = new ArrayList<>();
            ObjectMapper mapper = new ObjectMapper();

            Date firstDate = null;
            Date lastDate = null;
            if(all.isArray())
            {
                for(JsonNode node : all){
                    Date timeMeasured = (Date)mapper.convertValue(node.get("timeMeasured").asText(), new TypeReference<Date>(){});
                    if(firstDate == null || firstDate.compareTo(timeMeasured) > 0)
                    {
                        firstDate = timeMeasured;
                    }
                    if(lastDate == null || lastDate.compareTo(timeMeasured) < 0)
                    {
                        lastDate = timeMeasured;
                    }
                    //System.out.println(node.toString());
                    WeatherObservation observation = new WeatherObservation();
                    observation.setTimeMeasured(timeMeasured);
                    observation.setLogIntervalId(node.get("logIntervalId").asInt());
                    observation.setElementMeasurementTypeId(node.get("elementMeasurementTypeId").asText());
                    observation.setValue(node.get("value").asDouble());
                    observations.add(observation);
                }

            }
            else
            {
                fail("Data input from file is not a JSON array");
            }
            config.setConfigParameter("observations", observations);

            
            return config;
        } catch (IOException ex) {
            return null;
        } 
    }
}
