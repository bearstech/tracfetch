package org.bearstech.tracfetch;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.os.Bundle;

/**
 * Describes the content and attributes of a ticket
 */
public class Ticket implements Map<String, Object> {

	private static final long serialVersionUID = 1259599344351289466L;
	private Map<String, Object> attributes;
	// TODO a reference to the trac, maybe ?
	
	public Ticket(Map<String, Object> attrs) {
		this.attributes = attrs;
	}

	/**
	 * Convenience constructor to rebuild a ticket from a bundle ;
	 * used by TicketDetails.
	 * Must *definitely* find a way to pass objects from an Activity to another. 
	 */
	public Ticket(Bundle bundle) {
		attributes = new HashMap<String, Object>();
		for(String key : bundle.keySet()) {
			attributes.put(key, bundle.get(key));
		}
	}
	
	public Ticket(Integer id, Date date1, Date date2,
			Map<String, Object> attrs) {
		this.attributes = attrs;
		
		/*
		 * trac's xmlrpc response passes this data separately from other infos.
		 * but let's put them together to make it more consistent.
		 */
		attrs.put("id", id);
		attrs.put("created", date1);
		attrs.put("changed", date2);
	}

	public String get(String attr) {
		Object res = attributes.get(attr);
		if (res == null) {
			return "";
		}
		return res.toString();
	}

	public void clear() {
		attributes.clear();
	}

	public boolean containsKey(Object key) {
		return attributes.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return attributes.containsValue(value);
	}

	public Set<Map.Entry<String, Object>> entrySet() {
		return attributes.entrySet();
	}

	public boolean equals(Object object) {
		return attributes.equals(object);
	}

	public Object get(Object key) {
		return attributes.get(key);
	}

	public int hashCode() {
		return attributes.hashCode();
	}

	public boolean isEmpty() {
		return attributes.isEmpty();
	}

	public Set<String> keySet() {
		return attributes.keySet();
	}

	public Object put(String key, Object value) {
		return attributes.put(key, value);
	}

	public void putAll(Map<? extends String, ? extends Object> arg0) {
		attributes.putAll(arg0);
	}

	public Object remove(Object key) {
		return attributes.remove(key);
	}

	public int size() {
		return attributes.size();
	}

	public Collection<Object> values() {
		return attributes.values();
	}
}
