package crunch;

import org.apache.crunch.MapFn;
import org.apache.crunch.PCollection;
import org.apache.crunch.PTable;
import org.apache.crunch.Pair;
import org.apache.crunch.fn.Aggregators;
import org.apache.crunch.types.PTypeFamily;
 

/**
 * stage of union, used for grouping values
 *
 */
public class Aggregate {

	// search and regroup all eligible points
 	 
	/** 
	 * Returns a PTable that regroup all associated PointVectors
	 * 
	 * need to search in database by Geohash  to find vectors in common
	 */  
	public static <S> PTable<S, Long> count (PCollection<S> collect) {  
	  // get the PTypeFamily that is associated with the PType for the collection.  
	  PTypeFamily tf = collect.getTypeFamily();  
	  return collect.parallelDo("Aggregate.count", new MapFn<S, Pair<S, Long>>() {  
	    public Pair<S, Long> map(S input) {  
	      return Pair.of(input, null);  
	    }  
	  }, tf.tableOf(collect.getPType(), tf.longs())).groupByKey()  
	      .combineValues(Aggregators.SUM_LONGS());  
	}
	 
}
