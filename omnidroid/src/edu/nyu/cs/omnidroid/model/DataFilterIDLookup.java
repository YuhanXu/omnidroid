/*******************************************************************************
 * Copyright 2009 OmniDroid - http://code.google.com/p/omnidroid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package edu.nyu.cs.omnidroid.model;

import java.util.HashMap;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import edu.nyu.cs.omnidroid.model.db.DataFilterDbAdapter;
import edu.nyu.cs.omnidroid.model.db.DataTypeDbAdapter;
import edu.nyu.cs.omnidroid.model.db.DbHelper;
import edu.nyu.cs.omnidroid.util.DualKey;

/**
 * This class can be used to query the database for dataFilterID efficiently.
 */
public class DataFilterIDLookup {

  private DataTypeDbAdapter dataTypeDbAdapter;
  private DataFilterDbAdapter dataFilterDbAdapter;
  private DbHelper omnidroidDbHelper;
  private HashMap<DualKey<String, String>, Long> dataFilterIDMap;

  public DataFilterIDLookup(Context context) {
    omnidroidDbHelper = new DbHelper(context);
    SQLiteDatabase database = omnidroidDbHelper.getWritableDatabase();
    dataTypeDbAdapter = new DataTypeDbAdapter(database);
    dataFilterDbAdapter = new DataFilterDbAdapter(database);
    dataFilterIDMap = new HashMap<DualKey<String, String>, Long>();
  }

  public void close() {
    omnidroidDbHelper.close();
  }

  /**
   * Query the dataFilterID with dataTypeName and dataFilterNames. This method is caching the result
   * into filterIDMap.
   * 
   * @param dataTypeName
   *          is name of the dataType it filters on
   * 
   * @param dataFilterName
   *          is name of the dataFilter
   * 
   * @return filterID that matches dataTypeName and dataFilterName or -1 if no match
   */
  public long getDataFilterID(String dataTypeName, String dataFilterName) {
    if (dataTypeName == null || dataFilterName == null) {
      throw new IllegalArgumentException("Arguments null.");
    }

    DualKey<String, String> key = new DualKey<String, String>(dataTypeName, dataFilterName);

    // Return it if the id is already cached.
    Long cachedDataFilterID = dataFilterIDMap.get(key);
    if (cachedDataFilterID != null) {
      return cachedDataFilterID;
    }

    // Try to find dataTypeID
    long dataTypeID = -1;
    Cursor cursor = dataTypeDbAdapter.fetchAll(dataTypeName, null);
    if (cursor.getCount() > 0) {
      cursor.moveToFirst();
      dataTypeID = cursor.getLong(cursor.getColumnIndex(DataTypeDbAdapter.KEY_DATATYPEID));
    }
    cursor.close();

    // Try to find dataFilterID
    long dataFilterID = -1;
    /* TODO(ehotou) now only support filters that have the same filterOn and compareWith 
    *  datatypes, need to change the second one when we support actually compareWithDataType.
    */
    cursor = dataFilterDbAdapter.fetchAll(dataFilterName, dataTypeID, dataTypeID);
    if (cursor.getCount() > 0) {
      cursor.moveToFirst();
      dataFilterID = cursor.getLong(cursor.getColumnIndex(DataFilterDbAdapter.KEY_DATAFILTERID));
    }
    cursor.close();

    // Cache it if the id is valid
    if (dataFilterID > 0) {
      dataFilterIDMap.put(key, dataFilterID);
    }

    return dataFilterID;
  }
}