package org.bearstech.tracfetch;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

/**
 * Saves, restores and provides the list of tracs
 */
public class DataHolder {
	// Where to save the data
	private final String fileName = "save";
	private static DataHolder instance;

	private static List<Trac> tracs = null;

	/**
	 * Singleton
	 */
	public static DataHolder getInstance() {
		if (instance == null)
			instance = new DataHolder();
		return instance;
	}
	
	private DataHolder() {
		tracs = new ArrayList<Trac>();
	}

	public List<Trac> getTracs() {
		return tracs;
	}
	
	public void add(Trac t) {
		tracs.add(t);
	}
	
    public void save(Context ctx) {
        try {
            FileOutputStream fos = ctx.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(tracs);
            oos.flush();
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
	public synchronized void restore(Context ctx) {
        List<Trac> tmp = null;
        try {
            FileInputStream fis = ctx.openFileInput(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            tmp = (List<Trac>) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        } 
        
        /* If everything went well and if the list was existing: */
        if (tmp != null) {
            tracs = tmp;
        }
        for (Trac t : tracs) {
        	Log.d("TRAC - Restore", "url: " + t.getURL());
        }
    }

    /**
     * Convenience method. Quick'n'Dirty
     */
	public String[] getTracNames() {
		List<String> tmp = new ArrayList<String>();
		for (Trac t : tracs) {
			tmp.add(t.hostname);
		}
		return tmp.toArray(new String[tmp.size()]);
	}
}
