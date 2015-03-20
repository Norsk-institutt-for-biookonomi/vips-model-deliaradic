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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import no.bioforsk.vips.entity.ModelConfiguration;
import no.bioforsk.vips.entity.Result;
import no.bioforsk.vips.entity.ResultImpl;
import no.bioforsk.vips.entity.WeatherObservation;
import no.bioforsk.vips.i18n.I18nImpl;
import no.bioforsk.vips.model.ConfigValidationException;
import no.bioforsk.vips.model.Model;
import no.bioforsk.vips.model.ModelExcecutionException;
import no.bioforsk.vips.model.ModelId;
import no.bioforsk.vips.util.CommonNamespaces;
import no.bioforsk.vips.util.InvalidAggregationTypeException;
import no.bioforsk.vips.util.WeatherElements;
import no.bioforsk.vips.util.WeatherObservationListException;
import no.bioforsk.vips.util.WeatherUtil;

/**
 * Model controller for aggregated temperature model for invasion of
 * Delia radicum (NO: Liten kålflue) in fields
 * 
 * <pre>
 * Input parameters:
 *      Daily temperature mean
 *      Daily mean soil temperature, preferably at 10cm depth
 * </pre>
 * 
 * &copy; 2008-2015 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>: Programming
 * @author Tor J. Johansen <tor.johansen@nibio.no>: Model research
 */
public class DeliaRadicumModel extends I18nImpl implements Model{
    
    public final static ModelId MODEL_ID = new ModelId("DELIARADIC");
    
    private final Integer soilTempSumLowerThreshold = 140;
    private final Integer soilTempSumUpperThreshold = 160;
    private final Integer airTempSumLowerThreshold = 185;
    private final Integer airTempSumUpperThreshold = 210;
    private final Double dayDegreeBaseTemp = 4.0; // Same for air and soil temp
    
    private TimeZone timeZone;
    
    private List<WeatherObservation> TM; // Air temperature
    private List<WeatherObservation> TJM10; // Soil temperature at 10cm
    private DeliaRadicumModelDataMatrix dataMatrix;
    
    public DeliaRadicumModel()
    {
        // Setting the file name of the resource bundle
        super("no.nibio.vips.model.deliaradicummodel.texts");
    }

    @Override
    public List<Result> getResult() throws ModelExcecutionException {
        
        this.calculateTemperatureSum(DeliaRadicumModelDataMatrix.TMD, DeliaRadicumModelDataMatrix.TMDD4C);
        
        if(this.dataMatrix.getFirstDateWithParameterValue(DeliaRadicumModelDataMatrix.TJM10D) != null)
        {
            this.calculateTemperatureSum(DeliaRadicumModelDataMatrix.TJM10D, DeliaRadicumModelDataMatrix.TJM10DD4C);
        }
        
        List<Result> results = new ArrayList<>();
        Date currentDate = this.dataMatrix.getFirstDateWithValues();
        Date lastDate = this.dataMatrix.getLastDateWithValues();
        Calendar cal = Calendar.getInstance(this.timeZone);
        
        DecimalFormat dFormat = new DecimalFormat("###.##");
        while(currentDate.compareTo(lastDate) < 0)
        {
            Result result = new ResultImpl();
            result.setResultValidTime(currentDate);
            Double airTempSum = this.dataMatrix.getParamDoubleValueForDate(currentDate, DeliaRadicumModelDataMatrix.TMDD4C);

            result.setValue(CommonNamespaces.NS_WEATHER,
                    DeliaRadicumModelDataMatrix.TMDD4C,
                    dFormat.format(airTempSum)
            );
            
            Double soilTempSum = null;
            
            if(this.dataMatrix.getFirstDateWithParameterValue(DeliaRadicumModelDataMatrix.TJM10D) != null)
            {
                soilTempSum = this.dataMatrix.getParamDoubleValueForDate(currentDate, DeliaRadicumModelDataMatrix.TJM10DD4C);
                result.setValue(CommonNamespaces.NS_WEATHER,
                        DeliaRadicumModelDataMatrix.TJM10DD4C,
                        dFormat.format(soilTempSum)
                );
            }
            
            
            // Registering weather data
            result.setValue(
                    CommonNamespaces.NS_WEATHER, 
                    "TMD", 
                    dFormat.format(((WeatherObservation)this.dataMatrix.getParamValueForDate(currentDate, DeliaRadicumModelDataMatrix.TMD)).getValue())
            );
            
            WeatherObservation TJM10Obs = (WeatherObservation)this.dataMatrix.getParamValueForDate(currentDate, DeliaRadicumModelDataMatrix.TJM10D);
            result.setValue(
                    CommonNamespaces.NS_WEATHER, 
                    "TJM10D", 
                    TJM10Obs != null ? dFormat.format(TJM10Obs.getValue()) : null
            );
            
            // Setting threshold values
            result.setValue(this.getModelId().toString(), "TMDD4CLT", this.airTempSumLowerThreshold.toString());
            result.setValue(this.getModelId().toString(), "TMDD4CUT", this.airTempSumUpperThreshold.toString());
            if(this.dataMatrix.getFirstDateWithParameterValue(DeliaRadicumModelDataMatrix.TJM10D) != null)
            {
                result.setValue(this.getModelId().toString(), "TJM10DD4CLT", this.soilTempSumLowerThreshold.toString());
                result.setValue(this.getModelId().toString(), "TJM10DD4CUT", this.soilTempSumUpperThreshold.toString());
            }
            // Determining warning status
            Integer warningStatus = Result.WARNING_STATUS_NO_RISK;
            if( (   soilTempSum != null && soilTempSum >= this.soilTempSumLowerThreshold) 
                    || airTempSum >= this.airTempSumLowerThreshold
                    )
            {
                warningStatus = Result.WARNING_STATUS_MINOR_RISK;
            }
            if( (   soilTempSum != null && soilTempSum >= this.soilTempSumUpperThreshold) 
                    || airTempSum >= this.airTempSumUpperThreshold
                    )
            {
                warningStatus = Result.WARNING_STATUS_HIGH_RISK;
            }
            result.setWarningStatus(warningStatus);
            
            results.add(result);
            // Moving one day forward
            cal.setTime(currentDate);
            cal.add(Calendar.DATE, 1);
            
            currentDate = cal.getTime();
        }
        return results;
    }

    @Override
    public ModelId getModelId() {
        return DeliaRadicumModel.MODEL_ID;
    }

    @Override
    public String getModelName() {
        return this.getModelName(Model.DEFAULT_LANGUAGE);
    }

    @Override
    public String getModelName(String language) {
        return this.getText("name", language);
    }

    @Override
    public String getLicense() {
        return "Copyright (c) 2015 NIBIO <http://www.nibio.no/>. \n" +
            "\n" +
            "This file is part of DeliaRadicumModel. \n" + 
            "DeliaRadicumModel is free software: you can redistribute it and/or modify \n" + 
            "it under the terms of the NIBIO Open Source License as published by \n" + 
            "NIBIO, either version 1 of the License, or (at your option) any \n" +
            "later version. \n" +
            "\n" +
            "DeliaRadicumModel is distributed in the hope that it will be useful, \n" +
            "but WITHOUT ANY WARRANTY; without even the implied warranty of \n" +
            "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the \n" +
            "NIBIO Open Source License for more details. \n" +
            "" +
            "You should have received a copy of the NIBIO Open Source License \n" +
            "along with DeliaRadicumModel.  If not, see <http://www.nibio.no/licenses/>. \n";
    }

    @Override
    public String getCopyright() {
        return "(c) 2015 NIBIO (http://www.nibio.no/). Contact: post@nibio.no";
    }

    @Override
    public String getModelDescription() {
        return this.getModelDescription(Model.DEFAULT_LANGUAGE);
    }

    @Override
    public String getModelDescription(String language) {
        return this.getText("description", language);
    }

    @Override
    public String getWarningStatusInterpretation() {
        return this.getWarningStatusInterpretation(Model.DEFAULT_LANGUAGE);
    }

    @Override
    public String getWarningStatusInterpretation(String language) {
        return this.getText("statusInterpretation", language);
    }

    @Override
    public String getModelUsage() {
        return this.getModelUsage(Model.DEFAULT_LANGUAGE);
    }

    @Override
    public String getModelUsage(String language) {
        return this.getText("usage", language);
    }

    @Override
    public String getSampleConfig() {
        return  "{\n" +
                "\t\"loginInfo\":{\n" +
                "\t\t\"username\":\"example\",\n" +
                "\t\t\"password\":\"example\"\n" +
                "\t},\n" +
                "\t\"modelId\":\"" + MODEL_ID.toString() + "\",\n" +
                "\t\"configParameters\":{\n" +
                "\t\t\"observations\":[\n" +
                "\t\t{\n" +
                "\t\t\t\t\"timeMeasured\": \"2015-01-01T00:00:00+02:00\",\n" +
                "\t\t\t\t\"elementMeasurementTypeId\":\"TM\",\n" +
                "\t\t\t\t\"logIntervalId\":2,\n" +
                "\t\t\t\t\"value\":1.1\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\t\"timeMeasured\": \"2015-01-01T00:00:00+02:00\",\n" +
                "\t\t\t\t\"elementMeasurementTypeId\":\"TJM10\",\n" +
                "\t\t\t\t\"logIntervalId\":2,\n" +
                "\t\t\t\t\"value\":0.9\n" +
                "\t\t}\n" +
                "\t\t]\n" +
                "\t}\n" +
                "}\n";
    }

    /**
     * 
     * @param config
     * @throws ConfigValidationException 
     */
    @Override
    public void setConfiguration(ModelConfiguration config) throws ConfigValidationException {
        // Initialize the weather data collections
        this.TM = new ArrayList<>();
        this.TJM10 = new ArrayList<>();
        // Init data matrix
        this.dataMatrix = new DeliaRadicumModelDataMatrix();
        
        ObjectMapper mapper = new ObjectMapper();
        
        // Setting timezone
        this.timeZone = TimeZone.getTimeZone((String) config.getConfigParameter("timeZone"));
        
        // Importing weather data, creating collections
        // Can accept both hourly and daily data
        WeatherUtil wUtil = new WeatherUtil();
        List<WeatherObservation> observations = mapper.convertValue(config.getConfigParameter("observations"), new TypeReference<List<WeatherObservation>>(){});
        for(WeatherObservation o:observations)
        {
            switch(o.getElementMeasurementTypeId())
            {
                case WeatherElements.TEMPERATURE_MEAN:
                    if(o.getLogIntervalId().equals(WeatherObservation.LOG_INTERVAL_ID_1H))
                    {
                        this.TM.add(o);
                    }else {
                        o.setTimeMeasured(wUtil.pragmaticAdjustmentToMidnight(o.getTimeMeasured(), timeZone));
                        this.dataMatrix.setParamValueForDate(o.getTimeMeasured(), DeliaRadicumModelDataMatrix.TMD, o);
                    }break;
                case WeatherElements.SOIL_TEMPERATURE_10CM_MEAN:
                    if(o.getLogIntervalId().equals(WeatherObservation.LOG_INTERVAL_ID_1H))
                    {
                        this.TJM10.add(o);
                    }else {
                        o.setTimeMeasured(wUtil.pragmaticAdjustmentToMidnight(o.getTimeMeasured(), timeZone));
                        this.dataMatrix.setParamValueForDate(o.getTimeMeasured(), DeliaRadicumModelDataMatrix.TJM10D, o);
                    }break;
                default:
                    // Keep calm and continue importing data
                    break;
            }
        }
        
        // If we've received hourly weather data, create and store daily values
        // Air temperature
        if(dataMatrix.getFirstDateWithParameterValue(DeliaRadicumModelDataMatrix.TMD) == null)
        {
            try {
                List<WeatherObservation> dailyTemperatures = new WeatherUtil().getAggregatedDailyValues(
                        this.TM,
                        this.timeZone,
                        15,
                        WeatherUtil.AGGREGATION_TYPE_AVERAGE);
                for(WeatherObservation obs:dailyTemperatures)
                {
                    this.dataMatrix.setParamValueForDate(obs.getTimeMeasured(), DeliaRadicumModelDataMatrix.TMD, obs);
                }
            } catch (WeatherObservationListException | InvalidAggregationTypeException ex) {
                throw new ConfigValidationException(ex.getMessage());
            }
        }
        // Soil temperature
        if(dataMatrix.getFirstDateWithParameterValue(DeliaRadicumModelDataMatrix.TJM10D) == null)
        {
            try {
                List<WeatherObservation> dailyTemperatures = new WeatherUtil().getAggregatedDailyValues(
                        this.TJM10,
                        this.timeZone,
                        15,
                        WeatherUtil.AGGREGATION_TYPE_AVERAGE);
                if(dailyTemperatures != null)
                {
                    for(WeatherObservation obs:dailyTemperatures)
                    {
                        this.dataMatrix.setParamValueForDate(obs.getTimeMeasured(), DeliaRadicumModelDataMatrix.TJM10D, obs);
                    }
                }
            } catch (WeatherObservationListException | InvalidAggregationTypeException ex) {
                throw new ConfigValidationException(ex.getMessage());
            }
        }
        //System.out.println("DataMatrix");
        //System.out.println(this.dataMatrix.toString());
    }

    /**
     * Operates on the datamatrix
     * @param inputParameterName the parameter to sum
     * @param outputParameterName the result
     */
    private void calculateTemperatureSum(String inputParameterName, String outputParameterName) {
        Date today = this.dataMatrix.getFirstDateWithParameterValue(inputParameterName);
        Date lastDate = this.dataMatrix.getLastDateWithValues();
        Calendar cal = Calendar.getInstance(this.timeZone);
        
        Double sum = 0.0;
        while(today.compareTo(lastDate) < 0)
        {
            WeatherObservation todayTemp = (WeatherObservation)this.dataMatrix.getParamValueForDate(today, inputParameterName);
            sum += Math.max(0.0, todayTemp.getValue() - this.dayDegreeBaseTemp);
            this.dataMatrix.setParamDoubleValueForDate(today, outputParameterName, sum);
            cal.setTime(today);
            cal.add(Calendar.DATE, 1);
            today = cal.getTime();
        }
    }

}
