/*
 * Copyright (c) 2015 NIBIO <http://www.nibio.no/>. 
 * 
 * This file is part of DeliaRadicumModel.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 
 */

package no.nibio.vips.model.deliaradicummodel;

import no.nibio.vips.util.DateMap;

/**
 * @copyright 2015 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
public class DeliaRadicumModelDataMatrix extends DateMap{
    // Aggregated values
    public final static String TMDD4C = "TMDD4C";
    public final static String TJM10DD4C = "TJM10DD4C";
    // Weather data stored for convenience (putting into result object)
    public final static String TMD = "TMD";
    public final static String TJM10D = "TJM10D";
    
    public DeliaRadicumModelDataMatrix()
    {
        super();
    }
}
