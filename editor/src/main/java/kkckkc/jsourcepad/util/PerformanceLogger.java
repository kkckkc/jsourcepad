package kkckkc.jsourcepad.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class PerformanceLogger {
	private static Logger logger = LoggerFactory.getLogger(PerformanceLogger.class);
	
	private static final PerformanceLogger INSTANCE = new PerformanceLogger();
	private Map<String, Record> records = Maps.newHashMap();
	private Stack<String> stack = new Stack<String>();
	
	public static PerformanceLogger get() {
		return INSTANCE;
	}

	public void dump() {
	    List<String> keys = Lists.newArrayList(records.keySet());
	    Collections.sort(keys);
	    
	    for (String key : keys) {
	    	Record r = records.get(key);
	    	System.out.println(key + " = " + r.getCount() + " a " + r.getAvg() + "ms, total = " + r.getAccumulatedTime() + "ms");
	    }
    }
	
	public synchronized void enter(String name) {
		Record r = records.get(name);
		if (r == null) {
			r = new Record();
			records.put(name, r);
		}
		
		stack.push(name);
		r.enter();
	}
	
	public synchronized void exit() {
		String name = stack.pop();
		Record r = records.get(name);
		r.exit();
	}
	
	
	public static class Record {
		private long start = 0;
		
		private int count = 0;	
		private long accumulatedTime = 0;
		
		public void enter() {
	        start = System.nanoTime();
        }

		public long getAvg() {
	        return getAccumulatedTime() / count;
        }

		public void exit() {
	        accumulatedTime = (System.nanoTime() - start);
	        count++;
        }
		
		public long getAccumulatedTime() {
	        return accumulatedTime / 1000000;
        }
		
		public int getCount() {
	        return count;
        }
	}


	public void enter(Object o, String string) {
	    enter(o.getClass().getName() + "#" + string);
    }
}
