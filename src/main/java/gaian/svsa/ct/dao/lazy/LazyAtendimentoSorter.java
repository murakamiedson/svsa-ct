package gaian.svsa.ct.dao.lazy;

import java.lang.reflect.Field;
import java.util.Comparator;

import org.apache.log4j.Logger;
import org.primefaces.model.SortOrder;

import gaian.svsa.ct.modelo.Atendimento;

public class LazyAtendimentoSorter implements Comparator<Atendimento> {

	private Logger log = Logger.getLogger(LazyAtendimentoSorter.class);
	
	private String sortField;
	private SortOrder sortOrder;

	/**
     * initializing sorting field and sorting order
     * @param sortField
     * @param sortOrder
     */
    public LazyAtendimentoSorter(String sortField, SortOrder sortOrder) {
        this.sortField = sortField;
        this.sortOrder = sortOrder;
        log.info("LazySorter = " + sortField + "-" + sortOrder);
    }

	/**
	 * Comparing object1 and object2 with reflection
	 * 
	 * @param object1
	 * @param object2
	 * @return
	 */
	@Override
	public int compare(Atendimento object1, Atendimento object2) {
		try {
			Field field1 = object1.getClass().getDeclaredField(this.sortField);
			Field field2 = object2.getClass().getDeclaredField(this.sortField);			
			
			field1.setAccessible(true);
			field2.setAccessible(true);
			Object value1 = field1.get(object1);
			Object value2 = field2.get(object2);

			@SuppressWarnings({ "unchecked", "rawtypes" })			
			int value = ((Comparable) value1).compareTo(value2);
			
			return SortOrder.ASCENDING.equals(sortOrder) ? value : -1 * value;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}	
	
}
