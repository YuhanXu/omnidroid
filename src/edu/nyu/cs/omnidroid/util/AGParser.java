package edu.nyu.cs.omnidroid.util;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import android.content.Context;

/**
 * Provides functionality to parse the Application Config
 * @author: Pradeep Varma
 */
public class AGParser {
  public static final String KEY_APPLICATION = "Application";
  public static final String KEY_PkgName="PkgName";
  public static final String KEY_ListenerClass="ListenerClass";
  public static final String KEY_EventName = "EventName";
  public static final String KEY_Filters = "Filters";
  public static final String KEY_ActionName = "ActionName";
  public static final String KEY_URIFields = "URIFields";
  public static final String KEY_ContentMap = "ContentMap";

  private FileOutputStream fout;
  private OutputStreamWriter osw;
  private FileInputStream FIn;
  private BufferedInputStream bis;
  private DataInputStream dis;
  private Context context;
  private static final String CONFIG_FILE = "AppConfig.txt";
  private static final int MODE_WRITE = android.content.Context.MODE_WORLD_WRITEABLE;
  private static final int MODE_APPEND = android.content.Context.MODE_APPEND;

  /**
   * Used to set the Context of the Class
   * 
   */
  public AGParser(Context context) {
    this.context = context;
  }

  /**
   * Opens Application Config for writing
   * 
   */
  private void OpenFileWrite(int mode) {
    try {
      fout = context.openFileOutput(CONFIG_FILE, mode);
      osw = new OutputStreamWriter(fout);
    } catch (FileNotFoundException e) {
      OmLogger.write(context, "Unable to Open App Config to write");
    }
  }

  /**
   * Opens Application Config for reading
   * 
   */
  private void OpenFileRead() {
    try {
      FIn = context.openFileInput(CONFIG_FILE);
      bis = new BufferedInputStream(FIn);
      dis = new DataInputStream(bis);
    } catch (FileNotFoundException e) {
      OmLogger.write(context, "Unable to Open Application Config to Read");
    }
  }

  /**
   * deletes the entire AppConfig
   * 
   */
  public void delete_all() {
    try {
      OpenFileWrite(MODE_WRITE);
      osw.write("");
      osw.flush();
      osw.close();
    } catch (Exception e) {
      OmLogger.write(context, "Could not delete AppConfig");
    }
  }

  /**
   * deletes a Record from App Config.
   * 
   * @param AppName
   *          Specify the Application Name.
   */
  @SuppressWarnings("finally")
  public boolean deleteApp(String AppName) {
    boolean deleted=false;
    try {
      // Opeing App Config in Read Mode
      OpenFileRead();
      String line;
      ArrayList<String> lines = new ArrayList<String>();
      String[] parts;
      // Navigate to the Application Record
      while ((line = dis.readLine()) != null) {
        parts = line.split(":", 2);
        if (parts[1].toString().equalsIgnoreCase(AppName)) {
          String[] dparts;
          while ((line = dis.readLine()) != null) {
            dparts = line.split(":", 2);
            // Ignore lines of the application to be deleted.
            if (dparts[0].toString().equalsIgnoreCase(KEY_APPLICATION))
              // Stop ignoring once the next application map is reached
              break;
          }
        }
        if (!line.equals(null))
          lines.add(line);
      }

      Iterator<String> i = lines.iterator();
      while (i.hasNext()) {
        write(i.next());// Writing new lines into AppConfig
      }
      deleted=true;
    } catch (Exception e) {
      OmLogger.write(context, "Could not delete Instance Record");
      deleted=false;
    }
    finally{
      return deleted;
    }
  }

  /**
   * Writes a Line into the Application Config File
   * 
   * @param AGLine
   *          AGLine of AGRecord should be one of the below Application:SMS
   *          EventName:SMS_RECEIVED,RECEIVED SMS Filters:S_Name,S_Ph_No,Text,Location
   *          ActionName:SMS_SEND,SEND SMS URIFields:R_NAME,R_Ph_No,Text Content Map: S_Name,SENDER
   *          NAME,STRING R_Name,RECEIVER NAME,STRING S_Ph_No,SENDER PHONE NUMBER,INT
   *          R_Ph_No,RECEIVER PHONE NUMBER,INT Text,Text,STRING Location,SMS Number,INT
   * @return Returns true if successful
   * 
   */
  @SuppressWarnings("finally")
  public boolean write(String AGLine) {
    boolean written=false;
    try {
      final String LineString = new String(AGLine + "\n");
      OpenFileWrite(MODE_APPEND);
      osw.write(LineString);
      osw.flush();
      osw.close();
      written=true;
    } catch (Exception e) {
      OmLogger.write(context, "Unable to write line in Application Config");
     written=false;    
    }
    finally{return written;}
      
   }

  /**
   * Reads the package name from the App Config
   * 
   * @param AppName
   *          Specify the Application
   * @return Returns String 
   */
  public String readPkgName(String AppName) {
    String PkgName=null;
    Boolean found = false;
    try {

      String ActualEvent;
      String DisplayEvent;
      String line;

      try{
      dis.close();
      bis.close();
      }catch(Exception e){}
      OpenFileRead();
      // Navigate to the Application Record
      while ((line = dis.readLine()) != null) {
        String[] parts = line.split(":", 2);
        if (parts[1].toString().equalsIgnoreCase(AppName)) {
          found = true;
          break;
        }
      }
      if (found == false) {
        OmLogger.write(context, "Application: " + AppName + " not present in App Config");
        return PkgName;
      }
      HashMap<String, String> HM = new HashMap<String, String>();
      while ((line = dis.readLine()) != null) {
        String[] parts = line.split(":", 2);
        // Check if the pointer reached the ContentMap Section of the Record
        if (parts[0].toString().equalsIgnoreCase(KEY_ContentMap))
          break;
        if (parts[0].toString().equalsIgnoreCase(KEY_PkgName)) {
          PkgName=parts[1].toString();
        }
      }
      dis.close();
      bis.close();
      return PkgName;
    } catch (Exception e) {
      OmLogger.write(context, "Unable to read Events from Application Config");
      e.printStackTrace();
      return PkgName;
    }
  }

  /**
   * Reads the ListenerClass from the App Config
   * 
   * @param AppName
   *          Specify the Application
   * @return Returns String 
   */
  public String readListenerClass(String AppName) {
    String ListerClass=null;
    Boolean found = false;
    try {

      String ActualEvent;
      String DisplayEvent;
      String line;
      try
      {
      dis.close();
      bis.close();
      }catch(Exception e){}
      OpenFileRead();
      // Navigate to the Application Record
      while ((line = dis.readLine()) != null) {
        String[] parts = line.split(":", 2);
        if (parts[1].toString().equalsIgnoreCase(AppName)) {
          found = true;
          break;
        }
      }
      if (found == false) {
        OmLogger.write(context, "Application: " + AppName + " not present in App Config");
        return ListerClass;
      }
      HashMap<String, String> HM = new HashMap<String, String>();
      while ((line = dis.readLine()) != null) {
        String[] parts = line.split(":", 2);
        // Check if the pointer reached the ContentMap Section of the Record
        if (parts[0].toString().equalsIgnoreCase(KEY_ContentMap))
          break;
        if (parts[0].toString().equalsIgnoreCase(KEY_ListenerClass)) {
          ListerClass=parts[1].toString();
        }
      }
      dis.close();
      bis.close();
      return ListerClass;
    } catch (Exception e) {
      OmLogger.write(context, "Unable to read Events from Application Config");
      e.printStackTrace();
      return ListerClass;
    }
  }

  
  /**
   * Reads the Events from the App Config
   * 
   * @param AppName
   *          Specify the Application
   * @return Returns ArrayList of Hashmaps containing Actual Name and Display Name
   */
  // TODO(Pradeep): I don't think this formats the data properly into a data structure.
  public ArrayList<HashMap<String, String>> readEvents(String AppName) {
    ArrayList<HashMap<String, String>> eArrayList = new ArrayList<HashMap<String, String>>();
    //ArrayList<StringMap> eArrayList = new ArrayList<StringMap>();
    Boolean found = false;
    try {

      String ActualEvent;
      String DisplayEvent;
      String line;

      OpenFileRead();
      // Navigate to the Application Record
      while ((line = dis.readLine()) != null) {
        try{
        String[] parts = line.split(":", 2);
        if (parts[1].toString().equalsIgnoreCase(AppName)) {
          found = true;
          break;
        }
        } catch(Exception e)
        {}
      }
      if (found == false) {
        OmLogger.write(context, "Application: " + AppName + " not present in App Config");
        return eArrayList;
      }

      HashMap<String, String> HM = new HashMap<String, String>();
      //StringMap SM = new StringMap();
      while ((line = dis.readLine()) != null) {
        String[] parts = line.split(":", 2);
        // Check if the pointer reached the ContentMap Section of the Record
        if (parts[0].toString().equalsIgnoreCase(KEY_ContentMap))
          break;
        if (parts[0].toString().equalsIgnoreCase(KEY_EventName)) {
          ActualEvent = parts[1].split(",")[0].toString();
          DisplayEvent = parts[1].split(",")[1].toString();
          //SM.set(ActualEvent,DisplayEvent);
          // Add Event to HashMap
          HM.put(ActualEvent, DisplayEvent);      
        }
      }
      eArrayList.add(HM);
      dis.close();
      bis.close();
      return eArrayList;
    } catch (Exception e) {
      OmLogger.write(context, "Unable to read Events from Application Config");
      e.printStackTrace();
      return eArrayList;
    }
  }
  
  /**
   * Reads the Actions from the App Config
   * 
   * @param AppName
   *          Specify the Application
   * @return Returns ArrayList of Hashmaps containing ActualName and Display Name
   */
  // TODO(Pradeep): I don't think this formats the data properly into a data structure.
  public ArrayList<HashMap<String, String>> readActions(String AppName) {
    ArrayList<HashMap<String, String>> aArrayList = new ArrayList<HashMap<String, String>>();
    Boolean found = false;
    try {
      OpenFileRead();
      String line;

      // Navigate to the Application Record
      while ((line = dis.readLine()) != null) {
       try
       {
        String[] parts = line.split(":", 2);
        if (parts[1].toString().equalsIgnoreCase(AppName)) {
          found = true;
          break;
        }
       }catch(Exception e)
       {}
      }
      if (found == false) {
        OmLogger.write(context, "Application: " + AppName + " not present in App Config");
        return aArrayList;
      }
      String ActualAction;
      String DisplayAction;
      HashMap<String, String> HM = new HashMap<String, String>();
      while ((line = dis.readLine()) != null) {
        String[] parts = line.split(":", 2);
        if (parts[0].toString().equalsIgnoreCase(KEY_ContentMap))
          break;
        if (parts[0].toString().equalsIgnoreCase(KEY_ActionName)) {
          ActualAction = parts[1].split(",")[0].toString();
          DisplayAction = parts[1].split(",")[1].toString();
          HM.put(ActualAction, DisplayAction);
        
        }
      }
      aArrayList.add(HM);
      dis.close();
      bis.close();
      return aArrayList;
    } catch (Exception e) {
      OmLogger.write(context, "Unable to read Events from Application Config");
      e.printStackTrace();
      return aArrayList;
    }
  }

  /**
   * Reads the Filters of an Event from the App Config
   * 
   * @param AppName
   *          Specify the Application
   * @param EventName
   *          Specify the EventName
   * @return Returns ArrayList of Strings containing Filters
   */
  public ArrayList<String> readFilters(String AppName, String EventName) {
    ArrayList<String> FilterList = new ArrayList<String>();
    Boolean found = false;
    try {
      OpenFileRead();
      String line;

      // Navigate to the Application Record
      while ((line = dis.readLine()) != null) {
        String[] parts = line.split(":", 2);
        if (parts[1].toString().equalsIgnoreCase(AppName)) {
          found = true;
          break;
        }
      }
      if (found == false) {
        OmLogger.write(context, "Application: " + AppName + " not present in App Config");
        return FilterList;
      }
      while ((line = dis.readLine()) != null) {
        String[] parts = line.split(":", 2);
        if (parts[0].toString().equalsIgnoreCase(KEY_ContentMap))
          break;
        if (parts[0].toString().equalsIgnoreCase(KEY_EventName)
            && parts[1].toString().split(",")[0].equalsIgnoreCase(EventName)) {
          line = dis.readLine();
          String[] fparts = line.split(":", 2);
          String[] filters = fparts[1].split(",");
          for (int i = 0; i < filters.length; i++) {
            FilterList.add(filters[i]);
          }
          break;
        }
      }
      dis.close();
      bis.close();
      return FilterList;
    } catch (Exception e) {
      OmLogger.write(context, "Unable to read Events from Application Config");
      e.printStackTrace();
      return FilterList;
    }
  }

  /**
   * Reads the URIFields of an Event from the App Config
   * 
   * @param AppName
   *          Specify the Application
   * @param ActionName
   *          Specify the ActionName
   * @return Returns ArrayList of Strings containing URIFields
   */
  public ArrayList<String> readURIFields(String AppName, String ActionName) {
    ArrayList<String> URIList = new ArrayList<String>();
    Boolean found = false;
    try {
      OpenFileRead();
      String line;

      // Navigate to the Application Record
      while ((line = dis.readLine()) != null) {
        String[] parts = line.split(":", 2);
        if (parts[1].toString().equalsIgnoreCase(AppName)) {
          found = true;
          break;
        }
      }
      if (found == false) {
        OmLogger.write(context, "Application: " + AppName + " not present in App Config");
        return URIList;
      }
      while ((line = dis.readLine()) != null) {
        String[] parts = line.split(":", 2);
        if (parts[0].toString().equalsIgnoreCase(KEY_ContentMap))
          break;
        if (parts[0].toString().equalsIgnoreCase(KEY_ActionName)
            && parts[1].toString().split(",")[0].equalsIgnoreCase(ActionName)) {
          line = dis.readLine();
          String[] fparts = line.split(":", 2);
          String[] URIs = fparts[1].split(",");
          for (int i = 0; i < URIs.length; i++) {
            URIList.add(URIs[i]);
          }
          break;
        }
      }
      dis.close();
      bis.close();
      return URIList;
    } catch (Exception e) {
      OmLogger.write(context, "Unable to read Events from Application Config");
      e.printStackTrace();
      return URIList;
    }
  }

  /**
   * Reads values from the AppConfig based on the Key
   * 
   * @param Key
   *          Specify the Key to be read. example ActionName, EventName
   * @return Returns values as ArrayList of Strings
   */
  public ArrayList<String> readLines(String key) {
    ArrayList<String> cols2 = new ArrayList<String>();

    String val;
    try {
      OpenFileRead();
      String line;

      while ((line = dis.readLine()) != null) {
        String[] parts = line.split(":", 2);
        if (parts[0].toString().equalsIgnoreCase(key)) {
          val = parts[1].toString();
          cols2.add(val);
        }
      }
      return cols2;
    } catch (Exception e) {
      OmLogger.write(context, "Unable to read Line from Application Config");
      return cols2;
    }
  }

  /**
   * Reads the ContentMap of an Application from the App Config
   * 
   * @param AppName
   *          Specify the Application
   * @return Returns ArrayList<String[]> of Strings containing Field Details.
   */
  public ArrayList<StringMap> readContentMap(String AppName) {
    ArrayList<StringMap> contentmap = new ArrayList<StringMap>();
    Boolean found = false;
    try {
      OpenFileRead();
      String line;
      // Navigate to the Application Record
      while ((line = dis.readLine()) != null) {
        String[] parts = line.split(":", 2);
        if (parts[1].toString().equalsIgnoreCase(AppName)) {
          found = true;
          break;
        }
      }
      if (found == false) {
        OmLogger.write(context, "Application: " + AppName + " not present in App Config");
        return contentmap;
      }
      while ((line = dis.readLine()) != null) {
        String[] parts = line.split(":", 2);
        if (parts[0].toString().equalsIgnoreCase(KEY_APPLICATION))
          break;
        if (parts[0].toString().equalsIgnoreCase(KEY_ContentMap)) {
          while ((line = dis.readLine()) != null) {
            String[] fmparts = line.split(",");
            contentmap.add(new StringMap(fmparts[0],fmparts[1]));
          }
        }
      }
      dis.close();
      bis.close();
      return contentmap;
    } catch (Exception e) {
      OmLogger.write(context, "Unable to read ContentMap from Application Config");
      e.printStackTrace();
      return contentmap;
    }
  }

}