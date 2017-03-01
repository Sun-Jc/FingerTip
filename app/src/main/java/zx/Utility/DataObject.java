package zx.Utility;
import java.util.List;

public class DataObject implements java.io.Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = -2185143337180863373L;
		private List<String> datelist = null;
		private List<String> resultlist = null;
		
		public List<String> getDateList(){
			return datelist;
		}
		public List<String> getResultList(){
			return resultlist;
		}
		public void setList(List<String> datelist, List<String> resultlist){
			this.datelist = datelist;
			this.resultlist = resultlist;
		}
	}
