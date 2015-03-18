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

import no.bioforsk.vips.util.DateMap;

/**
 * @copyright 2015 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
public class DeliaRadicumModelDataMatrix extends DateMap{
    // Aggregated values
    public final static String TMDD = "TMDD";
    public final static String TJM10DD = "TJM10DD";
    // Weather data stored for convenience (putting into result object)
    public final static String TMD = "TMD";
    public final static String TJM10D = "TJM10D";
    
    public DeliaRadicumModelDataMatrix()
    {
        super();
    }
}
